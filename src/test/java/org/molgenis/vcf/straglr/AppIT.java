package org.molgenis.vcf.straglr;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.SpringApplication;
import org.springframework.util.ResourceUtils;

class AppIT {

  @TempDir Path sharedTempDir;

  @Test
  void test() throws IOException {
    String inputFile = ResourceUtils.getFile("classpath:example.tsv").toString();
    String referenceFile = ResourceUtils.getFile("classpath:example.fasta.gz").toString();
    String bedFile = ResourceUtils.getFile("classpath:example.bed").toString();
    String outputFile = sharedTempDir.resolve("example.vcf").toString();

    String[] args = {
      "-i",
      inputFile,
      "-b",
      bedFile,
      "-r",
      referenceFile,
      "-o",
      outputFile,
      "-s",
      "SAMPLE",
      "-c",
      "chrX"
    };
    SpringApplication.run(App.class, args);

    String outputVcf = Files.readString(Path.of(outputFile));

    Path expectedOutputFile = ResourceUtils.getFile("classpath:example.vcf").toPath();
    String expectedOutputVcf = Files.readString(expectedOutputFile).replaceAll("\\R", "\n");

    assertEquals(expectedOutputVcf, outputVcf);
  }

  @Test
  void testNoHaploid() throws IOException {
    String inputFile = ResourceUtils.getFile("classpath:example.tsv").toString();
    String referenceFile = ResourceUtils.getFile("classpath:example.fasta.gz").toString();
    String bedFile = ResourceUtils.getFile("classpath:example.bed").toString();
    String outputFile = sharedTempDir.resolve("example.vcf").toString();

    String[] args = {
      "-i", inputFile, "-b", bedFile, "-r", referenceFile, "-o", outputFile, "-s", "SAMPLE",
    };
    SpringApplication.run(App.class, args);

    String outputVcf = Files.readString(Path.of(outputFile));

    Path expectedOutputFile = ResourceUtils.getFile("classpath:example.vcf").toPath();
    String expectedOutputVcf = Files.readString(expectedOutputFile).replaceAll("\\R", "\n");

    assertEquals(expectedOutputVcf, outputVcf);
  }
}
