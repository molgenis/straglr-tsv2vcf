package org.molgenis.vcf.straglr.utils;

import static org.junit.jupiter.api.Assertions.*;
import static org.molgenis.vcf.straglr.model.ReadStatus.FULL;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vcf.straglr.model.Read;

@ExtendWith(MockitoExtension.class)
class StatisticsUtilsTest {

  @ParameterizedTest
  @MethodSource("provider")
  void testCalculateConfidenceInterval(List<Read> reads, double confidence, String expected) {
    assertEquals(expected, StatisticsUtils.calculateConfidenceInterval(reads, confidence));
  }

  // validated the outcomes using: https://www.statskingdom.com/confidence-interval-calculator.html
  static Stream<Arguments> provider() {
    Read read1 = new Read("read1", "A", 5, -1, 1, "+", "xx", FULL);
    Read read2 = new Read("read2", "A", 6, -1, 1, "+", "xx", FULL);
    Read read3 = new Read("read3", "A", 7, -1, 1, "+", "xx", FULL);
    Read read4 = new Read("read4", "A", 8, -1, 1, "+", "xx", FULL);
    Read read5 = new Read("read5", "A", 12, -1, 1, "+", "xx", FULL);
    Read read6 = new Read("read6", "A", 14, -1, 1, "+", "xx", FULL);
    Read read7 = new Read("read7", "A", 20, -1, 1, "+", "xx", FULL);
    return Stream.of(
        Arguments.of(List.of(read1, read1), 0.95, "5.0-5.0"),
        Arguments.of(List.of(read1, read2, read3), 0.95, "3.5-8.5"),
        Arguments.of(List.of(read1, read2, read6), 0.95, "-3.9-20.6"),
        Arguments.of(List.of(read4, read5, read6, read7), 0.95, "5.5-21.5"),
        Arguments.of(List.of(read1, read2, read3), 0.99, "0.3-11.7"));
  }

  @Test
  void testInvalidConfidenceLevel() {
    List<Read> reads =
        List.of(
            new Read("r1", "AAA", 6.0, 100.0, 1, "+", "TEST", FULL),
            new Read("r2", "AAA", 7.8, 100.0, 1, "+", "TEST", FULL));
    assertThrows(
        IllegalArgumentException.class,
        () -> StatisticsUtils.calculateConfidenceInterval(reads, -0.1));
  }

  @Test
  void testLessThanTwoReads() {
    assertEquals("NA", StatisticsUtils.calculateConfidenceInterval(List.of(), 0.95));
    List<Read> oneRead = List.of(new Read("r1", "AAA", 6.0, 100.0, 1, "+", "TEST", FULL));
    assertEquals("NA", StatisticsUtils.calculateConfidenceInterval(oneRead, 0.95));
  }
}
