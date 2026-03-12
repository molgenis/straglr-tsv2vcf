package org.molgenis.vcf.straglr.model;

/**
 * Unique identifier for a locus
 *
 * @param contig contig identifier
 * @param start 1 based start position
 * @param stop 1 based, inclusive, stop position
 */
public record LocusKey(String contig, int start, int stop) {}
