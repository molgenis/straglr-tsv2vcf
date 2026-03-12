package org.molgenis.vcf.straglr;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.molgenis.vcf.straglr.model.LocusKey;
import org.molgenis.vcf.straglr.model.Read;
import org.molgenis.vcf.straglr.model.ReadStatus;
import org.molgenis.vcf.straglr.model.StraglrTsvLine;

class StraglrTsv2VcfTest {

  @Test
  void testParse() {
    StraglrTsvLine strLine =
        StraglrTsvLine.builder()
            .allele("22.0")
            .chrom("TEST")
            .start(123)
            .end(456)
            .locus("TEST:123-456")
            .actualRepeat("ABC")
            .coverage(30)
            .readStart(123)
            .targetRepeat("DEF")
            .copyNumber(22.0)
            .size(22)
            .readStatus("full")
            .readName("name")
            .strand("strand")
            .build();
    List<StraglrTsvLine> testLines = new ArrayList<>();
    testLines.add(strLine);

    Map<LocusKey, List<Read>> result = StraglrTsv2Vcf.parseLoci(testLines);

    assertEquals(1, result.size());
    assertEquals(
        List.of(new Read("name", "ABC", 22.0, 22, 123, "strand", "22.0", ReadStatus.FULL)),
        result.get(new LocusKey("TEST", 124, 456)));
  }

  @Test
  void testParsePartial() {
    StraglrTsvLine strLine =
        StraglrTsvLine.builder()
            .allele("22.0")
            .chrom("TEST")
            .start(123)
            .end(456)
            .locus("TEST:123-456")
            .actualRepeat("ABC")
            .coverage(30)
            .readStart(123)
            .targetRepeat("DEF")
            .copyNumber(22.0)
            .size(22)
            .readStatus("partial (not complete)")
            .readName("name")
            .strand("strand")
            .build();
    List<StraglrTsvLine> testLines = new ArrayList<>();
    testLines.add(strLine);

    Map<LocusKey, List<Read>> result = StraglrTsv2Vcf.parseLoci(testLines);

    assertEquals(1, result.size());
    assertEquals(
        List.of(new Read("name", "ABC", 22.0, 22, 123, "strand", "22.0", ReadStatus.PARTIAL)),
        result.get(new LocusKey("TEST", 124, 456)));
  }

  @Test
  void testParseFailed() {
    StraglrTsvLine strLine =
        StraglrTsvLine.builder()
            .allele("22.0")
            .chrom("TEST")
            .start(123)
            .end(456)
            .locus("TEST:123-456")
            .actualRepeat("ABC")
            .coverage(30)
            .readStart(123)
            .targetRepeat("DEF")
            .copyNumber(22.0)
            .size(22)
            .readStatus("failed (not good)")
            .readName("name")
            .strand("strand")
            .build();
    List<StraglrTsvLine> testLines = new ArrayList<>();
    testLines.add(strLine);

    Map<LocusKey, List<Read>> result = StraglrTsv2Vcf.parseLoci(testLines);

    assertEquals(1, result.size());
    assertEquals(
        List.of(new Read("name", "ABC", 22.0, 22, 123, "strand", "22.0", ReadStatus.FAILED)),
        result.get(new LocusKey("TEST", 124, 456)));
  }

  @Test
  void testParseSkipped() {
    StraglrTsvLine strLine =
        StraglrTsvLine.builder()
            .allele("22.0")
            .chrom("TEST")
            .start(123)
            .end(456)
            .locus("TEST:123-456")
            .actualRepeat("ABC")
            .coverage(30)
            .readStart(123)
            .targetRepeat("DEF")
            .copyNumber(22.0)
            .size(22)
            .readStatus("skipped (unmatched ru)")
            .readName("name")
            .strand("strand")
            .build();
    List<StraglrTsvLine> testLines = new ArrayList<>();
    testLines.add(strLine);

    Map<LocusKey, List<Read>> result = StraglrTsv2Vcf.parseLoci(testLines);

    assertEquals(1, result.size());
    assertEquals(
        List.of(new Read("name", "ABC", 22.0, 22, 123, "strand", "22.0", ReadStatus.SKIPPED)),
        result.get(new LocusKey("TEST", 124, 456)));
  }

  @Test
  void testParseIgnoresNAAllele() {
    StraglrTsvLine strLine =
        StraglrTsvLine.builder()
            .allele("NA")
            .chrom("TEST")
            .start(123)
            .end(456)
            .locus("TEST:123-456")
            .actualRepeat("ABC")
            .coverage(30)
            .readStart(123)
            .targetRepeat("DEF")
            .copyNumber(22.0)
            .size(22)
            .readStatus("failed")
            .build();
    List<StraglrTsvLine> testLines = new ArrayList<>();
    testLines.add(strLine);

    Map<LocusKey, List<Read>> result = StraglrTsv2Vcf.parseLoci(testLines);

    assertTrue(result.isEmpty());
  }
}
