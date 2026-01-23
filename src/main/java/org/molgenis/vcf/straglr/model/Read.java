package org.molgenis.vcf.straglr.model;

public record Read(
    String readName,
    String locus,
    String actualRepeat,
    double copyNumber,
    double size,
    int readStart,
    String strand,
    String allele,
    ReadStatus readStatus) {}
