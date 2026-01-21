package org.molgenis.vcf.straglr;

import static org.molgenis.vcf.straglr.utils.FileUtils.readBed;
import static org.molgenis.vcf.straglr.utils.FileUtils.readTsv;
import static org.molgenis.vcf.straglr.utils.VcfUtils.createVcfWriter;
import static org.molgenis.vcf.straglr.utils.VcfUtils.variantComparator;

import htsjdk.samtools.reference.BlockCompressedIndexedFastaSequenceFile;
import htsjdk.samtools.reference.ReferenceSequenceFile;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
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
import org.molgenis.vcf.straglr.model.StraglrTsvLine;
import org.molgenis.vcf.straglr.model.LocusKey;
import org.molgenis.vcf.straglr.utils.VcfUtils;

public final class StraglrTsv2Vcf {

  private StraglrTsv2Vcf() {
    // utility class
  }

  public static void run(Path inputTsv, Path inputBed, Path inputReference,
      List<String> haploidContigs, Path outputVcf, String sampleName) {

    List<StraglrTsvLine> straglrTsvLines = readTsv(inputTsv);
    Map<LocusKey, List<Read>> readsPerLocus = parseLoci(straglrTsvLines);
    Map<LocusKey, Locus> locusLookup = readBed(inputBed);

    try (ReferenceSequenceFile fasta =
        new BlockCompressedIndexedFastaSequenceFile(inputReference)) {

      List<VariantContext> variants = readsPerLocus.entrySet().stream()
          .map(entry ->
              VcfUtils.createStrVcfLine(entry.getKey(), entry.getValue(), haploidContigs, fasta,
                  locusLookup, sampleName))
          .sorted(variantComparator())
          .toList();

      try(VariantContextWriter writer = createVcfWriter(outputVcf, sampleName, fasta, variants)) {
        variants.forEach(writer::add);
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private static Map<LocusKey, List<Read>> parseLoci(List<StraglrTsvLine> lines) {
    Map<LocusKey, List<Read>> loci = new HashMap<>();

    for (StraglrTsvLine line : lines) {
      LocusKey locus = new LocusKey(
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
