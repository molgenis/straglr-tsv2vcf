package org.molgenis.vcf.straglr.model;

public record Read(String readName, String actualRepeat, String copyNumber, String size,
                   String readStart, String strand, String allele, ReadStatus readStatus) {

}
