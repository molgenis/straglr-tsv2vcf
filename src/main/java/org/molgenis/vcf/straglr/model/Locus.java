package org.molgenis.vcf.straglr.model;

public record Locus(String chrom, int start, int stop, String catalogRepeatUnit,
                    String identifier) {

}
