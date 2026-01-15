package org.molgenis.vcf.straglr.model;

public enum ReadStatus {
  FULL,
  SKIPPED_NOT_SPANNING,
  FAILED_MOTIF_SIZE_OUT_OF_RANGE,
  FAILED_UNMATCHED_MOTIF;

  public static ReadStatus fromTsv(String tsvValue) {
    String clean = tsvValue.trim()
        .replace(" ", "_")
        .replace("(", "")
        .replace(")", "")
        .toUpperCase();

    return valueOf(clean);
  }
}