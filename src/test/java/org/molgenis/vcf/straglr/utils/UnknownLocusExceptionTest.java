package org.molgenis.vcf.straglr.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.molgenis.vcf.straglr.model.LocusKey;

class UnknownLocusExceptionTest {

  private final LocusKey LocusKey = new LocusKey("chr1", 123, 456);

  @Test
  void getMessage() {
    assertEquals(
        "Unknown locus for key 'LocusKey[contig=chr1, start=123, stop=456]'.",
        new UnknownLocusException(LocusKey).getMessage());
  }
}
