package org.molgenis.vcf.straglr.utils;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.molgenis.vcf.straglr.model.CatalogLine;
import org.molgenis.vcf.straglr.model.CatalogRepeatLocus;
import org.molgenis.vcf.straglr.model.LocusKey;
import org.molgenis.vcf.straglr.model.StraglrTsvLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtils {
  private static final Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);

  private FileUtils() {}

  public static List<StraglrTsvLine> readTsv(Path input) {
    int offset = calculateOffset(input);
    return readStraglrOutputFile(input, StraglrTsvLine.class, offset);
  }

  private static int calculateOffset(Path input) {
    int skipLines = 0;
    try (BufferedReader br = Files.newBufferedReader(input, StandardCharsets.UTF_8)) {
      String line;
      while ((line = br.readLine()) != null) {
        if (!line.trim().startsWith("#")) {
          if (skipLines == 0) {
            throw new IllegalStateException("No header lines found for straglr tsv.");
          }
          return skipLines - 1; // First non-# is header
        }
        skipLines++;
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    throw new IllegalStateException("No data lines found for straglr tsv.");
  }

  public static Map<LocusKey, CatalogRepeatLocus> readLoci(Path input) {
    List<CatalogLine> bedLines = readStraglrOutputFile(input, CatalogLine.class, 0);
    Map<LocusKey, CatalogRepeatLocus> lookup = new HashMap<>();

    for (CatalogLine line : bedLines) {
      // BED is 0-based, VCF is 1-based
      LocusKey key = new LocusKey(line.getChrom(), line.getStart() + 1, line.getStop() + 1);
      lookup.put(
          key,
          new CatalogRepeatLocus(
              line.getChrom(),
              line.getStart() + 1,
              line.getStop() + 1,
              line.getRepeatUnit() == null ? line.getStraglrRu() : line.getRepeatUnit(),
              line.getLocusId()));
    }

    return lookup;
  }

  private static <T> List<T> readStraglrOutputFile(Path input, Class<T> type, int offset) {
    try (Reader reader = Files.newBufferedReader(input, StandardCharsets.UTF_8)) {

      CsvToBean<T> csv =
          new CsvToBeanBuilder<T>(reader)
              .withSkipLines(offset)
              .withSeparator('\t')
              .withType(type)
              .withThrowExceptions(false)
              .withIgnoreQuotations(true)
              .build();

      List<T> result = csv.parse();
      handleCsvParseExceptions(csv.getCapturedExceptions());
      return result;

    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  static void handleCsvParseExceptions(List<CsvException> exceptions) {
    exceptions.forEach(
        ex -> {
          if (!ex.getLine()[0].startsWith("#")) {
            throw new IllegalStateException(
                String.format(
                    "CSV parse error at line %s: %s", ex.getLineNumber(), ex.getMessage()));
          }
        });
  }
}
