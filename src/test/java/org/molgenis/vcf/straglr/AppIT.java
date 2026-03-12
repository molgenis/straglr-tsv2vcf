package org.molgenis.vcf.straglr;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.util.ResourceUtils;

@ExtendWith(OutputCaptureExtension.class)
class AppIT {

  @TempDir Path sharedTempDir;

  @Test
  void test() throws IOException {
    String inputFile = ResourceUtils.getFile("classpath:example.tsv").toString();
    String referenceFile = ResourceUtils.getFile("classpath:example.fasta.gz").toString();
    String lociFile = ResourceUtils.getFile("classpath:example_loci.tsv").toString();
    String outputFile = sharedTempDir.resolve("example.vcf").toString();

    String[] args = {
      "-i",
      inputFile,
      "-l",
      lociFile,
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
    String lociFile = ResourceUtils.getFile("classpath:example_loci.tsv").toString();
    String outputFile = sharedTempDir.resolve("example.vcf").toString();

    String[] args = {
      "-i", inputFile, "-l", lociFile, "-r", referenceFile, "-o", outputFile, "-s", "SAMPLE",
    };
    SpringApplication.run(App.class, args);

    String outputVcf = Files.readString(Path.of(outputFile));

    Path expectedOutputFile = ResourceUtils.getFile("classpath:example.vcf").toPath();
    String expectedOutputVcf = Files.readString(expectedOutputFile).replaceAll("\\R", "\n");

    assertEquals(expectedOutputVcf, outputVcf);
  }

  @Test
  void testEmptyInput() throws IOException {
    String inputFile = ResourceUtils.getFile("classpath:empty.tsv").toString();
    String referenceFile = ResourceUtils.getFile("classpath:example.fasta.gz").toString();
    String lociFile = ResourceUtils.getFile("classpath:example_loci.tsv").toString();
    String outputFile = sharedTempDir.resolve("example.vcf").toString();

    String[] args = {
      "-i", inputFile, "-l", lociFile, "-r", referenceFile, "-o", outputFile, "-s", "SAMPLE",
    };
    SpringApplication.run(App.class, args);

    String outputVcf = Files.readString(Path.of(outputFile));

    Path expectedOutputFile = ResourceUtils.getFile("classpath:empty.vcf").toPath();
    String expectedOutputVcf = Files.readString(expectedOutputFile).replaceAll("\\R", "\n");

    assertEquals(expectedOutputVcf, outputVcf);
  }

  @Test
  void testVersion(CapturedOutput output) {
    String[] args = {"-v"};
    SpringApplication.run(App.class, args);
    assertEquals("straglrTsv2Vcf 1.2.0\n", output.getAll().replaceAll("\\R", "\n"));
  }

  @Test
  void testHelp(CapturedOutput output) throws IOException {
    Path expectedOutput = ResourceUtils.getFile("classpath:helpOutput.txt").toPath();
    String expectedOutputString = Files.readString(expectedOutput).replaceAll("\\R", "\n");
    String[] args = {"-h"};
    SpringApplication.run(App.class, args);
    assertEquals(
        expectedOutputString.replaceAll("\\R", "\n"),
        output
            .getAll()
            .replaceAll("\\R", "\n")
            .lines()
            .map(String::stripTrailing)
            .collect(Collectors.joining("\n")));
  }
}
