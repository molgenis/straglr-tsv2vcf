package org.molgenis.vcf.straglr.model;

public enum ReadStatus {
  FULL,
  PARTIAL,
  SKIPPED,
  FAILED;

  public static ReadStatus fromTsv(String tsvValue) {
    if (tsvValue == null || tsvValue.trim().isEmpty()) {
      return ReadStatus.SKIPPED;
    }

    String clean = tsvValue.trim().toUpperCase();
    if (clean.startsWith("FULL") || clean.startsWith("COMPLETE")) {
      return ReadStatus.FULL;
    }
    if (clean.startsWith("PARTIAL") || clean.startsWith("INCOMPLETE")) {
      return ReadStatus.PARTIAL;
    }
    if (clean.startsWith("SKIP") || clean.startsWith("IGNOR")) {
      return ReadStatus.SKIPPED;
    }
    return ReadStatus.FAILED;
  }
}
