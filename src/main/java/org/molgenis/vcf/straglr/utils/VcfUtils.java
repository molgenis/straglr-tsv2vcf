package org.molgenis.vcf.straglr.utils;

import static org.molgenis.vcf.straglr.utils.RepeatUnitUtils.getMostFrequentActualRepeat;
import static org.molgenis.vcf.straglr.utils.RepeatUnitUtils.getRepeatUnitsWithCounts;
import static org.molgenis.vcf.straglr.utils.StatisticsUtils.calculateConfidenceInterval;

import htsjdk.samtools.SAMSequenceDictionary;
import htsjdk.samtools.SAMSequenceRecord;
import htsjdk.samtools.reference.ReferenceSequence;
import htsjdk.samtools.reference.ReferenceSequenceFile;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.GenotypeBuilder;
import htsjdk.variant.variantcontext.GenotypesContext;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.variantcontext.writer.VariantContextWriterBuilder;
import htsjdk.variant.vcf.VCFAltHeaderLine;
import htsjdk.variant.vcf.VCFContigHeaderLine;
import htsjdk.variant.vcf.VCFFilterHeaderLine;
import htsjdk.variant.vcf.VCFFormatHeaderLine;
import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFHeaderLine;
import htsjdk.variant.vcf.VCFHeaderLineCount;
import htsjdk.variant.vcf.VCFHeaderLineType;
import htsjdk.variant.vcf.VCFHeaderVersion;
import htsjdk.variant.vcf.VCFInfoHeaderLine;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.molgenis.vcf.straglr.model.CatalogRepeatLocus;
import org.molgenis.vcf.straglr.model.LocusKey;
import org.molgenis.vcf.straglr.model.Read;
import org.molgenis.vcf.straglr.model.ReadStatus;

public class VcfUtils {

  public static VariantContext createStrVcfLine(
      LocusKey locusKey,
      List<Read> inputReads,
      List<String> haploidContigs,
      ReferenceSequenceFile fasta,
      Map<LocusKey, CatalogRepeatLocus> locusIdLookup,
      String sampleName) {

    List<Read> reads =
        inputReads.stream().filter(r -> r.readStatus() != ReadStatus.SKIPPED).toList();

    if (reads.isEmpty()) {
      return null;
    }

    int dp = reads.size();
    List<String> filters = collectFilters(reads);
    Allele refAllele = createRefAllele(locusKey, fasta);

    Map<String, List<Read>> alleleCounts = getReadsPerAllele(reads);
    int[] ad = alleleCounts.values().stream().mapToInt(List::size).toArray();

    Set<Allele> alleles =
        alleleCounts.keySet().stream()
            .map(
                count ->
                    Allele.create(String.format("<STR%s>", Math.round(Float.parseFloat(count)))))
            .collect(Collectors.toSet());
    List<Allele> genotypeAlleles =
        determineGenotypeAlleles(alleles, locusKey.contig(), haploidContigs);

    GenotypesContext genotypes =
        GenotypesContext.create(
            new GenotypeBuilder(sampleName).alleles(genotypeAlleles).DP(dp).AD(ad).make());

    Map<String, Object> attributes =
        buildAttributes(locusKey, reads, locusIdLookup.get(locusKey), alleleCounts.keySet());

    List<Allele> allAlleles = new ArrayList<>();
    allAlleles.add(refAllele);
    allAlleles.addAll(alleles);

    return new VariantContextBuilder(
            "Straglr", locusKey.contig(), locusKey.start(), locusKey.stop(), allAlleles)
        .attributes(attributes)
        .genotypes(genotypes)
        .filter(filters.isEmpty() ? "PASS" : String.join(";", filters))
        .make();
  }

  private static Allele createRefAllele(LocusKey locusKey, ReferenceSequenceFile fasta) {
    String base =
        fasta
            .getSubsequenceAt(locusKey.contig(), locusKey.start(), locusKey.start())
            .getBaseString();
    return Allele.create(base, true);
  }

  private static Map<String, List<Read>> getReadsPerAllele(List<Read> reads) {
    return reads.stream()
        .collect(
            Collectors.groupingBy(
                Read::allele,
                Collectors.mapping(read -> read, Collectors.toList()) // List<Read> per allele
                ));
  }

  private static List<String> collectFilters(List<Read> reads) {
    return reads.stream()
        .map(Read::readStatus)
        .filter(status -> status != ReadStatus.FULL && status != ReadStatus.PARTIAL)
        .map(ReadStatus::toString)
        .distinct()
        .toList();
  }

  private static List<Allele> determineGenotypeAlleles(
      Set<Allele> alleles, String contig, List<String> haploidContigs) {

    List<Allele> genotypeAlleles = new ArrayList<>(alleles);

    // Diploid contig but only one allele -> duplicate
    if (genotypeAlleles.size() == 1 && !haploidContigs.contains(contig)) {
      genotypeAlleles.add(genotypeAlleles.getFirst());
    }

    return genotypeAlleles;
  }

  private static Map<String, Object> buildAttributes(
      LocusKey locusKey,
      List<Read> reads,
      CatalogRepeatLocus catalogRepeatLocus,
      Set<String> repeatUnitCounts) {

    String actualRu = getMostFrequentActualRepeat(reads);
    Map<String, List<Read>> readsByAllele =
        reads.stream().collect(Collectors.groupingBy(Read::allele));

    String confidenceIntervals =
        readsByAllele.values().stream()
            .map(readList -> calculateConfidenceInterval(readList, 0.95))
            .collect(Collectors.joining(","));

    Map<String, Object> attributes = new LinkedHashMap<>();
    attributes.put("END", locusKey.stop());
    attributes.put("RU_CALL", actualRu);
    attributes.put("RU_CAT", catalogRepeatLocus.catalogRepeatUnit());
    attributes.put("RU_SEEN", getRepeatUnitsWithCounts(reads));
    attributes.put("REPID", catalogRepeatLocus.identifier());
    attributes.put(
        "RU_MATCH", RepeatUnitUtils.isMatch(catalogRepeatLocus.catalogRepeatUnit(), actualRu));
    attributes.put("RU_NR", String.join(",", repeatUnitCounts));
    attributes.put("RU_CI", confidenceIntervals);

    return attributes;
  }

  static int parseAlleleInt(String alleleStr) {
    return Math.round(Float.parseFloat(alleleStr.trim()));
  }

  public static Comparator<VariantContext> variantComparator() {
    return Comparator.comparing(VariantContext::getContig)
        .thenComparingInt(VariantContext::getStart)
        .thenComparingInt(VariantContext::getEnd);
  }

  public static VCFHeader createStrVcfHeader(
      List<VariantContext> variants, SAMSequenceDictionary dict, String sampleName) {

    Set<VCFHeaderLine> headerLines = new HashSet<>();

    addAltHeaders(headerLines, variants);
    addFilterHeaders(headerLines, variants);
    addInfoHeaders(headerLines);
    addFormatHeaders(headerLines);

    VCFHeader header = new VCFHeader(VCFHeaderVersion.VCF4_2, headerLines, Set.of(sampleName));

    addContigHeaders(header, dict);
    return header;
  }

  static void addAltHeaders(Set<VCFHeaderLine> headerLines, List<VariantContext> variants) {
    variants.stream()
        .flatMap(vc -> vc.getAlternateAlleles().stream())
        .map(Allele::getDisplayString)
        .filter(alt -> alt.startsWith("<STR"))
        .collect(Collectors.toSet())
        .forEach(
            alt -> {
              int repeatLen = Math.round(Float.parseFloat(alt.substring(4, alt.length() - 1)));
              headerLines.add(
                  new VCFAltHeaderLine(
                      String.format(
                          "##ALT=<ID=STR%s,Description=\"Short tandem repeat (STR) allele\" of length %d.>",
                          repeatLen, repeatLen),
                      VCFHeaderVersion.VCF4_2));
            });
  }

  private static void addFilterHeaders(
      Set<VCFHeaderLine> headerLines, List<VariantContext> variants) {
    variants.stream()
        .flatMap(vc -> vc.getFilters().stream())
        .distinct()
        .forEach(filter -> headerLines.add(new VCFFilterHeaderLine(filter)));
  }

  private static void addInfoHeaders(Set<VCFHeaderLine> headerLines) {
    headerLines.add(new VCFInfoHeaderLine("END", 1, VCFHeaderLineType.Integer, "End position"));
    headerLines.add(
        new VCFInfoHeaderLine(
            "RU_CALL", 1, VCFHeaderLineType.String, "Most frequent actual repeat motif"));
    headerLines.add(
        new VCFInfoHeaderLine("RU_CAT", 1, VCFHeaderLineType.String, "Catalog repeat motif"));
    headerLines.add(
        new VCFInfoHeaderLine("REPID", 1, VCFHeaderLineType.String, "Locus identifier."));
    headerLines.add(
        new VCFInfoHeaderLine(
            "RU_MATCH",
            0,
            VCFHeaderLineType.Flag,
            "RU call matches catalog (allowing shift/IUPAC)"));
    headerLines.add(
        new VCFInfoHeaderLine(
            "RU_SEEN",
            VCFHeaderLineCount.UNBOUNDED,
            VCFHeaderLineType.String,
            "All RUs encountered with read counts"));
    headerLines.add(
        new VCFInfoHeaderLine(
            "RU_NR",
            VCFHeaderLineCount.A,
            VCFHeaderLineType.String,
            "Number of repeat units per allele."));
    headerLines.add(
        new VCFInfoHeaderLine(
            "RU_CI",
            VCFHeaderLineCount.A,
            VCFHeaderLineType.String,
            "95% confidence interval per allele. 'NA' if less than 2 reads were present."));
  }

  private static void addFormatHeaders(Set<VCFHeaderLine> headerLines) {
    headerLines.add(new VCFFormatHeaderLine("GT", 1, VCFHeaderLineType.String, "Genotype"));
    headerLines.add(new VCFFormatHeaderLine("DP", 1, VCFHeaderLineType.Integer, "Read depth"));
    headerLines.add(
        new VCFFormatHeaderLine(
            "AD", VCFHeaderLineCount.UNBOUNDED, VCFHeaderLineType.Integer, "Allelic depths"));
  }

  private static void addContigHeaders(VCFHeader header, SAMSequenceDictionary dict) {
    for (SAMSequenceRecord seq : dict.getSequences()) {
      Map<String, String> contigInfo =
          Map.of(
              "ID", seq.getSequenceName(),
              "length", String.valueOf(seq.getSequenceLength()));
      header.addMetaDataLine(
          new VCFContigHeaderLine(contigInfo, dict.getSequenceIndex(seq.getSequenceName())));
    }
  }

  public static VariantContextWriter createVcfWriter(
      Path outputVcf,
      String sampleName,
      ReferenceSequenceFile fasta,
      List<VariantContext> variants) {
    SAMSequenceDictionary dict = new SAMSequenceDictionary();

    ReferenceSequence seq;
    while ((seq = fasta.nextSequence()) != null) {
      String name = seq.getName();
      int length = seq.length();
      dict.addSequence(new SAMSequenceRecord(name, length));
    }

    VCFHeader header = createStrVcfHeader(variants, dict, sampleName);

    VariantContextWriter writer =
        new VariantContextWriterBuilder()
            .setOutputFile(outputVcf.toFile())
            .setReferenceDictionary(dict)
            .build();

    writer.writeHeader(header);

    return writer;
  }
}
