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
public class StraglrLine {
  @CsvBindByName(
      column = "#chrom",
      required = true)
  String chrom;

  @CsvBindByName(
      column = "start",
      required = true)
  int start;

  @CsvBindByName(
      column = "end",
      required = true)
  int end;

  @CsvBindByName(
      column = "target_repeat",
      required = true)
  String targetRepeat;

  @CsvBindByName(
      column = "locus",
      required = true)
  String locus;

  @CsvBindByName(
      column = "coverage",
      required = true)
  int coverage;

  @CsvBindByName(
      column = "genotype",
      required = true)
  String genotype;

  @CsvBindByName(
      column = "read_name",
      required = true)
  String readName;

  @CsvBindByName(
      column = "actual_repeat",
      required = true)
  String actualRepeat;

  @CsvBindByName(
      column = "copy_number",
      required = true)
  String copyNumber;

  @CsvBindByName(
      column = "size",
      required = true)
  String size;

  @CsvBindByName(
      column = "read_start",
      required = true)
  String readStart;

  @CsvBindByName(
      column = "strand",
      required = true)
  String strand;

  @CsvBindByName(
      column = "allele",
      required = true)
  String allele;

  @CsvBindByName(
      column = "read_status",
      required = true)
  String readStatus;
}
