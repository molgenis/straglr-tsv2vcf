package org.molgenis.vcf.straglr;

import static org.molgenis.vcf.straglr.utils.FileUtils.readBed;
import static org.molgenis.vcf.straglr.utils.FileUtils.readTsv;
import static org.molgenis.vcf.straglr.utils.VcfUtils.createStrVcfHeader;
import static org.molgenis.vcf.straglr.utils.VcfUtils.variantComparator;

import htsjdk.samtools.reference.IndexedFastaSequenceFile;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.variantcontext.writer.VariantContextWriterBuilder;
import htsjdk.variant.vcf.VCFHeader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.molgenis.vcf.straglr.model.Locus;
import org.molgenis.vcf.straglr.model.Read;
import org.molgenis.vcf.straglr.model.ReadStatus;
import org.molgenis.vcf.straglr.model.StraglrLine;
import org.molgenis.vcf.straglr.model.VariantKey;
import org.molgenis.vcf.straglr.utils.VcfUtils;

public final class StraglrTsv2Vcf {

  private StraglrTsv2Vcf() {
    // utility class
  }

  public static void run(Path inputTsv, Path inputBed, Path inputReference,
      List<String> haploidContigs, Path outputVcf, String sampleName) {

    List<StraglrLine> straglrLines = readTsv(inputTsv);
    Map<VariantKey, List<Read>> readsPerLocus = parseLoci(straglrLines);
    Map<VariantKey, Locus> locusLookup = readBed(inputBed);

    try (IndexedFastaSequenceFile fasta =
        new IndexedFastaSequenceFile(inputReference.toFile())) {

      List<VariantContext> variants = readsPerLocus.entrySet().stream()
          .map(entry ->
              VcfUtils.createStrVcfLine(entry.getKey(), entry.getValue(), haploidContigs, fasta,
                  locusLookup, sampleName))
          .sorted(variantComparator())
          .toList();

      VCFHeader header = createStrVcfHeader(variants, fasta, sampleName);

      try (VariantContextWriter writer =
          new VariantContextWriterBuilder()
              .setOutputFile(outputVcf.toFile())
              .build()) {

        writer.writeHeader(header);
        variants.forEach(writer::add);
      }

    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private static Map<VariantKey, List<Read>> parseLoci(List<StraglrLine> lines) {
    Map<VariantKey, List<Read>> loci = new HashMap<>();

    for (StraglrLine line : lines) {
      VariantKey locus = new VariantKey(
          line.getChrom(),
          line.getStart() + 1,
          line.getEnd() + 1
      );

      if ("NA".equals(line.getAllele())) {
        continue;
      }

      Read read = new Read(
          line.getReadName(),
          line.getActualRepeat(),
          line.getCopyNumber(),
          line.getSize(),
          line.getReadStart(),
          line.getStrand(),
          line.getAllele(),
          ReadStatus.fromTsv(line.getReadStatus())
      );

      loci.computeIfAbsent(locus, l -> new ArrayList<>()).add(read);
    }

    return loci;
  }
}
