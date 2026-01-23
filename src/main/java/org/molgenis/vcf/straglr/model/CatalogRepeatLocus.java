package org.molgenis.vcf.straglr.model;

/**
 * @param chrom contig identifier
 * @param start 1 based start position, based on straglr loci (catalog) file
 * @param stop 1 based stop position, based on straglr loci (catalog) file
 * @param catalogRepeatUnit Repeat unit as specified in the straglr loci (catalog) file
 * @param identifier Locus identifier from straglr loci (catalog) file
 */
public record CatalogRepeatLocus(
    String chrom, int start, int stop, String catalogRepeatUnit, String identifier) {}
