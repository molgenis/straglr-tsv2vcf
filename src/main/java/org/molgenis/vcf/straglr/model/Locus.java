package org.molgenis.vcf.straglr.model;

/**
 * @param chrom contig identifier
 * @param start 1 based start position, based on straglr bed file
 * @param stop 1 based stop position, based on straglr bed file
 * @param catalogRepeatUnit Repeat unit as specified in the straglr bed file
 * @param identifier Locus identifier from straglr bed file
 */
public record Locus(String chrom, int start, int stop, String catalogRepeatUnit,
                    String identifier) {

}
