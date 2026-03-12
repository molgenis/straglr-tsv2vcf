package org.molgenis.vcf.straglr.utils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vcf.straglr.model.Read;

@ExtendWith(MockitoExtension.class)
class RepeatUnitUtilsTest {

  @ParameterizedTest
  @CsvSource({
    "A, A, true",
    "A, C, false",
    "R, A, true",
    "R, T, false",
    "N, T, true",
    "ACG, CG, false",
    "ACG, ACG, true",
    "ACG, GAC, true",
    "ACG, CGA, true",
    "RY, AT, true"
  })
  void testIsMatch_SameLength_MatchesWithShiftOrIUPAC(
      String catalog, String called, boolean expected) {
    boolean result = RepeatUnitUtils.isMatch(catalog, called);
    assertEquals(expected, result, catalog + " vs " + called);
  }

  @Test
  void testGetMostFrequentActualRepeat_MultipleReads() {
    Read read1 = mock(Read.class);
    when(read1.actualRepeat()).thenReturn("ATAT");
    Read read2 = mock(Read.class);
    when(read2.actualRepeat()).thenReturn("ATAT");
    Read read3 = mock(Read.class);
    when(read3.actualRepeat()).thenReturn("TATA");
    List<Read> reads = Arrays.asList(read1, read2, read3);

    String result = RepeatUnitUtils.getMostFrequentActualRepeat(reads);
    assertEquals("ATAT", result);
  }

  @Test
  void testGetMostFrequentActualRepeat_WithCommas() {
    Read read1 = mock(Read.class);
    when(read1.actualRepeat()).thenReturn("AT,AT");
    Read read2 = mock(Read.class);
    when(read2.actualRepeat()).thenReturn("AT,AT");
    Read read3 = mock(Read.class);
    when(read3.actualRepeat()).thenReturn("TATA");
    List<Read> reads = Arrays.asList(read1, read2, read3);

    String result = RepeatUnitUtils.getMostFrequentActualRepeat(reads);
    assertEquals("AT_AT", result);
  }

  @Test
  void testGetRepeatUnitsWithCounts_Multiple() {
    Read read1 = mock(Read.class);
    when(read1.actualRepeat()).thenReturn("AT,AT");
    Read read2 = mock(Read.class);
    when(read2.actualRepeat()).thenReturn("AT,AT");
    Read read3 = mock(Read.class);
    when(read3.actualRepeat()).thenReturn("TATA");
    List<Read> reads = Arrays.asList(read1, read2, read3);

    List<String> result = RepeatUnitUtils.getRepeatUnitsWithCounts(reads);
    assertEquals(2, result.size());
    assertTrue(result.contains("AT_AT(2)"));
    assertTrue(result.contains("TATA(1)"));
  }
}
