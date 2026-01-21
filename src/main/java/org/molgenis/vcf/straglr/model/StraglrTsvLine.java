package org.molgenis.vcf.straglr.model;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * @param chrom - chromosome name
 * @param start - start coordinate of locus
 * @param end - end coordinate of locus
 * @param target_repeat - consensus(shortest) repeat motif from genome scan or target motif in
 *     genotyping
 * @param locus - locus in UCSC format (chrom:start-end)
 * @param coverage - coverage depth of locus
 * @param genotype - copy numbers (default) or sizes (--genotype_in_size) of each allele detected
 *     for given locus, separate by semi-colon(";") if multiple alleles detected, with number of
 *     support reads in bracket following each allele copy number/size. An example of a heterozygous
 *     allele in size: 990.8(10);30.9(10) (Alleles preceded by > indicate minimum values, as full
 *     alleles are not captured in any support reads)
 * @param actual_repeat - actual repeat motif detected in mapped read
 * @param read_name - mapped read name
 * @param copy_number - number of copies of repeat in allele
 * @param size - size of allele
 * @param read_start - start position of repeat in support read
 * @param strand - strand of reference genome from which read originates
 * @param allele - allele to which support read is assigned
 * @param read_status - classification of mapped read
 */
public class StraglrTsvLine {
  @CsvBindByName(column = "#chrom", required = true)
  String chrom;

  @CsvBindByName(column = "start", required = true)
  int start;

  @CsvBindByName(column = "end", required = true)
  int end;

  @CsvBindByName(column = "target_repeat", required = true)
  String targetRepeat;

  @CsvBindByName(column = "locus", required = true)
  String locus;

  @CsvBindByName(column = "coverage", required = true)
  int coverage;

  @CsvBindByName(column = "genotype", required = true)
  String genotype;

  @CsvBindByName(column = "read_name", required = true)
  String readName;

  @CsvBindByName(column = "actual_repeat", required = true)
  String actualRepeat;

  @CsvBindByName(column = "copy_number", required = true)
  String copyNumber;

  @CsvBindByName(column = "size", required = true)
  String size;

  @CsvBindByName(column = "read_start", required = true)
  String readStart;

  @CsvBindByName(column = "strand", required = true)
  String strand;

  @CsvBindByName(column = "allele", required = true)
  String allele;

  @CsvBindByName(column = "read_status", required = true)
  String readStatus;
}
