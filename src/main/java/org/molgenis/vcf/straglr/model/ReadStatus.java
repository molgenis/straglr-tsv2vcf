package org.molgenis.vcf.straglr.model;

import java.util.Locale;

public enum ReadStatus {
  FULL,
  PARTIAL,
  SKIPPED,
  FAILED;

  public static ReadStatus fromTsv(String tsvValue) {
    if (tsvValue == null || tsvValue.trim().isEmpty()) {
      return ReadStatus.SKIPPED;
    }

    String clean = tsvValue.trim().toUpperCase(Locale.ROOT);
    if (clean.startsWith("FULL")) {
      return ReadStatus.FULL;
    }
    if (clean.startsWith("PARTIAL")) {
      return ReadStatus.PARTIAL;
    }
    if (clean.startsWith("SKIPPED")) {
      return ReadStatus.SKIPPED;
    }
    return ReadStatus.FAILED;
  }
}
