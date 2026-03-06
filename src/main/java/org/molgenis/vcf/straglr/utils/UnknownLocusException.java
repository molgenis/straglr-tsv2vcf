package org.molgenis.vcf.straglr.utils;

import static java.lang.String.format;

import java.io.Serial;
import org.molgenis.vcf.straglr.model.LocusKey;

public class UnknownLocusException extends RuntimeException {
  @Serial private static final long serialVersionUID = 1L;

  public UnknownLocusException(LocusKey locusKey) {
    super(format("Unknown locus for key '%s'.", locusKey.toString()));
  }
}
