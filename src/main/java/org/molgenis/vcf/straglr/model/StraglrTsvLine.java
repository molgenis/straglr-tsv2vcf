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
public class StraglrTsvLine {
  /** chromosome name */
  @CsvBindByName(column = "#chrom", required = true)
  String chrom;

  /** 0-based start coordinate of locus */
  @CsvBindByName(column = "start", required = true)
  int start;

  /** 0-based end coordinate of locus */
  @CsvBindByName(column = "end", required = true)
  int end;

  /** consensus(shortest) repeat motif from genome scan or target motif in genotyping */
  @CsvBindByName(column = "target_repeat", required = true)
  String targetRepeat;

  /** locus in UCSC format (chrom:start-end) */
  @CsvBindByName(column = "locus", required = true)
  String locus;

  /** coverage depth of locus */
  @CsvBindByName(column = "coverage", required = true)
  int coverage;

  /**
   * copy numbers (default) or sizes (--genotype_in_size) of each allele detected for given locus,
   * separate by semi-colon(";") if multiple alleles detected, with number of support reads in
   * bracket following each allele copy number/size. An example of a heterozygous allele in size:
   * 990.8(10);30.9(10) (Alleles preceded by > indicate minimum values, as full alleles are not
   * captured in any support reads)
   */
  @CsvBindByName(column = "genotype", required = true)
  String genotype;

  /** mapped read name */
  @CsvBindByName(column = "read_name", required = true)
  String readName;

  /** actual repeat motif detected in mapped read */
  @CsvBindByName(column = "actual_repeat", required = true)
  String actualRepeat;

  /** number of copies of repeat in allele */
  @CsvBindByName(column = "copy_number", required = true)
  String copyNumber;

  /** size of allele */
  @CsvBindByName(column = "size", required = true)
  String size;

  /** start position of repeat in support read */
  @CsvBindByName(column = "read_start", required = true)
  String readStart;

  /** strand of reference genome from which read originates */
  @CsvBindByName(column = "strand", required = true)
  String strand;

  /** allele to which support read is assigned */
  @CsvBindByName(column = "allele", required = true)
  String allele;

  /** classification of mapped read */
  @CsvBindByName(column = "read_status", required = true)
  String readStatus;
}
