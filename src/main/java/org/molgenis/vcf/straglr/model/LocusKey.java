package org.molgenis.vcf.straglr.model;

/**
 * @param contig contig identifier
 * @param start 1 based start position
 * @param stop 1 based stop position
 */
public record LocusKey(String contig, int start, int stop) {
}
