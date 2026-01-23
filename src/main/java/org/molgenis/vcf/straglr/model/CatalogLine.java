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
public class CatalogLine {

  /** chrom contig identifier */
  @CsvBindByPosition(position = 0, required = true)
  String chrom;

  /** start 0 based start position */
  @CsvBindByPosition(position = 1, required = true)
  int start;

  /** stop 0-based, non-inclusive, stop position */
  @CsvBindByPosition(position = 2, required = true)
  int stop;

  /** straglrRu the repeat unit provided to straglr */
  @CsvBindByPosition(position = 3, required = true)
  String straglrRu;

  /** gene the gene of the locus */
  @CsvBindByPosition(position = 4, required = true)
  String gene;

  /** locusId the identifier of the locus */
  @CsvBindByPosition(position = 5, required = true)
  String locusId;

  /** repeatUnit the repeat unit to match with if "-" was provided to straglr */
  @CsvBindByPosition(position = 6)
  String repeatUnit;
}
