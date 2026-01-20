package org.molgenis.vcf.straglr.utils;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvException;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.molgenis.vcf.straglr.model.BedLine;
import org.molgenis.vcf.straglr.model.Locus;
import org.molgenis.vcf.straglr.model.StraglrLine;
import org.molgenis.vcf.straglr.model.VariantKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtils {
  private static final Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);
  public static List<StraglrLine> readTsv(Path input) {
    return readDelimitedFile(input, StraglrLine.class, 1);
  }

  public static Map<VariantKey, Locus> readBed(Path input) {
    List<BedLine> bedLines = readDelimitedFile(input, BedLine.class, 0);
    Map<VariantKey, Locus> lookup = new HashMap<>();

    for (BedLine line : bedLines) {
      // BED is 0-based, VCF is 1-based
      VariantKey key = new VariantKey(
          line.getChrom(),
          line.getStart() + 1,
          line.getStop() + 1
      );
      lookup.put(key, new Locus(line.getChrom(), line.getStart() + 1, line.getStop() + 1,
          line.getRepeatUnit() == null ? line.getStraglrRu() : line.getRepeatUnit(), line.getLocusId()));
    }

    return lookup;
  }

  private static <T> List<T> readDelimitedFile(Path input, Class<T> type, int offset) {
    try (Reader reader =
        new BufferedReader(new InputStreamReader(new FileInputStream(input.toFile())))) {

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
    exceptions.forEach(ex -> {
      if (!ex.getLine()[0].startsWith("#")) {
        LOGGER.error("CSV parse error at line {}: {}",
            ex.getLineNumber(), ex.getMessage());
      }
    });
  }

}
