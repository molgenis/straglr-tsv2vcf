package org.molgenis.vcf.straglr.model;

import com.opencsv.bean.CsvBindByPosition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BedLine {
  @CsvBindByPosition(
      position = 0,
      required = true)
  String chrom;
  @CsvBindByPosition(
      position = 1,
      required = true)
  int start;
  @CsvBindByPosition(
      position = 2,
      required = true)
  int stop;
  @CsvBindByPosition(
      position = 3,
      required = true)
  String straglrRu;
  @CsvBindByPosition(
      position = 4,
      required = true)
  String gene;
  @CsvBindByPosition(
      position = 5,
      required = true)
  String locusId;
  @CsvBindByPosition(
      position = 6)
  String repeatUnit;
}
