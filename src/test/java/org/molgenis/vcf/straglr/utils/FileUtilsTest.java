package org.molgenis.vcf.straglr.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vcf.straglr.model.CatalogRepeatLocus;
import org.molgenis.vcf.straglr.model.LocusKey;
import org.molgenis.vcf.straglr.model.StraglrTsvLine;

@ExtendWith(MockitoExtension.class)
class FileUtilsTest {

  @TempDir Path tempDir;

  @Test
  void testReadTsv() throws IOException {
    String tsv =
        """
      #header line that should be skipped
      #chrom\tstart\tend\ttarget_repeat\tlocus\tcoverage\tgenotype\tread_name\tactual_repeat\tcopy_number\tsize\tread_start\tstrand\tallele\tread_status
      chr1\t149390802\t149390841\tGGC\tchr1:149390802-149390841\t33\t20.0(19);8.5(14)\t04d31d3d-91e4-4a6a-9e95-2c9d50c99295\tGGC\t20.3\t61\t3881\t-\t20.0\tfull
      """;

    Path file = tempDir.resolve("input.tsv");
    Files.writeString(file, tsv);

    List<StraglrTsvLine> result = FileUtils.readTsv(file);

    assertEquals(1, result.size());
    StraglrTsvLine line = result.getFirst();
    assertEquals("chr1", line.getChrom());
    assertEquals(149390802, line.getStart());
    assertEquals(149390841, line.getEnd());
    assertEquals("20.0", line.getAllele());
    assertEquals("3881", line.getReadStart());
    assertEquals("full", line.getReadStatus());
    assertEquals("GGC", line.getTargetRepeat());
    assertEquals("GGC", line.getActualRepeat());
  }

  @Test
  void testReadLoci() throws IOException {
    String bed =
        "chrX\t67545316\t67545385\t-\tAR\tAR\tGCA\n"
            + "chr12\t6936716\t6936773\t-\tATN1\tATN1\tCNG";

    Path file = tempDir.resolve("regions.bed");
    Files.writeString(file, bed);

    Map<LocusKey, CatalogRepeatLocus> lookup = FileUtils.readLoci(file);

    assertEquals(2, lookup.size());

    LocusKey key1 = new LocusKey("chrX", 67545317, 67545386);
    LocusKey key2 = new LocusKey("chr12", 6936717, 6936774);

    assertTrue(lookup.containsKey(key1));
    assertTrue(lookup.containsKey(key2));

    CatalogRepeatLocus catalogRepeatLocus = lookup.get(key1);
    assertEquals("chrX", catalogRepeatLocus.chrom());
    assertEquals(67545317, catalogRepeatLocus.start());
    assertEquals(67545386, catalogRepeatLocus.stop());
    assertEquals("GCA", catalogRepeatLocus.catalogRepeatUnit());
    assertEquals("AR", catalogRepeatLocus.identifier());
  }
}
