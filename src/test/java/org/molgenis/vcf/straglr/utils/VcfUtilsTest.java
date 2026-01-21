package org.molgenis.vcf.straglr.utils;

import htsjdk.samtools.SAMSequenceDictionary;
import htsjdk.samtools.SAMSequenceRecord;
import htsjdk.samtools.reference.IndexedFastaSequenceFile;
import htsjdk.samtools.reference.ReferenceSequence;
import htsjdk.variant.variantcontext.*;
import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFHeaderLine;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vcf.straglr.model.Locus;
import org.molgenis.vcf.straglr.model.Read;
import org.molgenis.vcf.straglr.model.ReadStatus;
import org.molgenis.vcf.straglr.model.LocusKey;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

@ExtendWith(MockitoExtension.class)
class VcfUtilsTest {

  @Test
  void testCreateStrVcfLine_SkippedReads_ReturnsNull() {
    IndexedFastaSequenceFile fasta = mock(IndexedFastaSequenceFile.class);
    Locus locus = mock(Locus.class);
    LocusKey key = new LocusKey("chr1", 100, 103);
    Read skipped = mock(Read.class);
    when(skipped.readStatus()).thenReturn(ReadStatus.SKIPPED);
    List<Read> reads = List.of(skipped);
    VariantContext result = VcfUtils.createStrVcfLine(key, reads, List.of(), fasta, Map.of(key, locus), "SAMPLE");
    assertNull(result);
  }

  @Test
  void testCreateStrVcfLine_HaploidContig_SingleAllele() {
    IndexedFastaSequenceFile fasta = mock(IndexedFastaSequenceFile.class);
    Locus locus = mock(Locus.class);
    when(locus.catalogRepeatUnit()).thenReturn("AT");
    when(locus.identifier()).thenReturn("STR1");
    LocusKey key = new LocusKey("chr1", 100, 103);
    ReferenceSequence refSeq = mock(ReferenceSequence.class);
    when(refSeq.getBaseString()).thenReturn("A");
    when(fasta.getSubsequenceAt(eq("chr1"), eq(100L), eq(100L))).thenReturn(refSeq);

    Read read = mock(Read.class);
    when(read.allele()).thenReturn("10.0");
    when(read.readStatus()).thenReturn(ReadStatus.FULL);
    when(read.actualRepeat()).thenReturn("AT");
    List<Read> reads = List.of(read);
    List<String> haploidContigs = List.of("chr1");

    VariantContext vc = VcfUtils.createStrVcfLine(key, reads, haploidContigs, fasta, Map.of(key, locus), "SAMPLE");
    assertNotNull(vc);
    assertEquals("PASS", vc.getFilters().stream().findFirst().get());
    assertEquals(1, vc.getGenotypes().size());
    assertEquals(103, vc.getAttributeAsInt("END", 0));
    assertTrue((Boolean) vc.getAttribute("RU_MATCH"));
  }

  @Test
  void testCreateStrVcfLine_DiploidContig_DuplicateAllele() {
    IndexedFastaSequenceFile fasta = mock(IndexedFastaSequenceFile.class);
    Locus locus = mock(Locus.class);
    when(locus.catalogRepeatUnit()).thenReturn("AT");
    when(locus.identifier()).thenReturn("STR1");
    LocusKey key = new LocusKey("chr1", 100, 103);

    ReferenceSequence refSeq = mock(ReferenceSequence.class);
    when(refSeq.getBaseString()).thenReturn("A");
    when(fasta.getSubsequenceAt(eq("chr1"), eq(100L), eq(100L))).thenReturn(refSeq);

    Read read = mock(Read.class);
    when(read.allele()).thenReturn("10.0");
    when(read.readStatus()).thenReturn(ReadStatus.FULL);
    when(read.actualRepeat()).thenReturn("AT");
    List<Read> reads = List.of(read);
    List<String> diploidContigs = List.of();  // chr1 not haploid

    VariantContext vc = VcfUtils.createStrVcfLine(key, reads, diploidContigs, fasta, Map.of(key, locus), "SAMPLE");
    assertEquals(2, vc.getGenotype(SAMPLE_NAME).getAlleles().size());  // 0/0
  }

  @Test
  void testCreateStrVcfLine_WithFilters() {
    IndexedFastaSequenceFile fasta = mock(IndexedFastaSequenceFile.class);
    Locus locus = mock(Locus.class);
    when(locus.catalogRepeatUnit()).thenReturn("AT");
    when(locus.identifier()).thenReturn("STR1");
    LocusKey key = new LocusKey("chr1", 100, 103);
    Read full = mock(Read.class);
    when(full.allele()).thenReturn("10.0");
    when(full.readStatus()).thenReturn(ReadStatus.FULL);
    when(full.actualRepeat()).thenReturn("AT");
    Read partial = mock(Read.class);
    when(partial.allele()).thenReturn("12.0");
    when(partial.readStatus()).thenReturn(ReadStatus.FAILED);
    when(partial.actualRepeat()).thenReturn("AT");
    List<Read> reads = List.of(full, partial);

    ReferenceSequence refSeq = mock(ReferenceSequence.class);
    when(refSeq.getBaseString()).thenReturn("A");
    when(fasta.getSubsequenceAt(anyString(), anyLong(), anyLong())).thenReturn(refSeq);

    VariantContext vc = VcfUtils.createStrVcfLine(key, reads, List.of(), fasta, Map.of(key, locus), "SAMPLE");
    assertEquals("FAILED", vc.getFilters().stream().findFirst().get());
  }

  @Test
  void testParseAlleleInt() throws Exception {
    assertEquals(10, VcfUtils.parseAlleleInt("10.0"));
  }

  @Test
  void testVariantComparator() {
    VariantContext vc1 = new VariantContextBuilder("test", "chr1", 100, 100, List.of(Allele.REF_A)).make();
    VariantContext vc2 = new VariantContextBuilder("test", "chr1", 200, 200, List.of(Allele.REF_A)).make();
    VariantContext vc3 = new VariantContextBuilder("test", "chr2", 100, 100, List.of(Allele.REF_A)).make();

    Comparator<VariantContext> comp = VcfUtils.variantComparator();
    assertEquals(-1, comp.compare(vc1, vc2));
    assertEquals(-1, comp.compare(vc1, vc3));
    assertEquals(1, comp.compare(vc3, vc1));
  }

  @Test
  void testCreateStrVcfHeader_ContigHeaders() {
    SAMSequenceRecord seq1 = new SAMSequenceRecord("chr1", 1000);
    SAMSequenceDictionary dict = new SAMSequenceDictionary(List.of(seq1));

    VCFHeader header = VcfUtils.createStrVcfHeader(List.of(), dict, "SAMPLE");
    assertEquals(1, header.getContigLines().size());
    assertEquals("chr1", header.getContigLines().getFirst().getID());
  }

  @Test
  void testAddAltHeaders() {
    Allele alt1 = Allele.create("<STR10>");
    Allele alt2 = Allele.create("<STR12>");
    VariantContext vc = new VariantContextBuilder("test", "chr1", 100, 100, List.of(Allele.REF_A, alt1, alt2)).make();

    Set<VCFHeaderLine> lines = new HashSet<>();
    VcfUtils.addAltHeaders(lines, List.of(vc));
    assertEquals(2, lines.size());
    assertTrue(lines.stream().anyMatch(l -> l.getKey().equals("ALT") && l.toString().contains("Short tandem repeat (STR) allele of length 12.")));
  }

  private static final String SAMPLE_NAME = "SAMPLE";
}
