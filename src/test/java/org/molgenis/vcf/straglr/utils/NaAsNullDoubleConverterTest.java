package org.molgenis.vcf.straglr.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NaAsNullDoubleConverterTest {

  private final NaAsNullDoubleConverter converter = new NaAsNullDoubleConverter();

  @ParameterizedTest
  @ValueSource(strings = {"NA", "na", "Na"})
  void convertNa(String input) throws Exception {
    assertNull(converter.convert(input));
  }

  @Test
  void convertDecimal() throws Exception {
    assertEquals(1.1, converter.convert("1.1"));
  }

  @Test
  void convertInteger() throws Exception {
    assertEquals(1.0, converter.convert("1"));
  }
}