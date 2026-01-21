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
/**
 * @param chrom contig identifier
 * @param start 0 based start position
 * @param stop 0-based stop position
 * @param straglrRu the repeat unit provided to straglr
 * @param gene the gene of the locus
 * @param locusId the identifier of the locus
 * @param repeatUnit the repeat unit to match with if "-" was provided to straglr
 */
public class CatalogLine {
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
