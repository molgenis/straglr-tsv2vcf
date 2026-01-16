package org.molgenis.vcf.straglr;

import static java.lang.String.format;

import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

class AppCommandLineOptions {

  static final String OPT_INPUT = "i";
  static final String OPT_INPUT_LONG = "input";
  static final String OPT_BED = "b";
  static final String OPT_BED_LONG = "bed";
  static final String OPT_REFERENCE = "r";
  static final String OPT_REFERENCE_LONG = "reference";
  static final String OPT_HAPLOID = "h";
  static final String OPT_HAPLOID_LONG = "haploid_contigs";
  static final String OPT_SAMPLE = "s";
  static final String OPT_SAMPLE_LONG = "sample";
  static final String OPT_OUTPUT = "o";
  static final String OPT_OUTPUT_LONG = "output";
  static final String OPT_FORCE = "f";
  static final String OPT_FORCE_LONG = "force";
  static final String OPT_VERSION = "v";
  static final String OPT_VERSION_LONG = "version";
  private static final Options APP_OPTIONS;
  private static final Options APP_VERSION_OPTIONS;

  static {
    Options appOptions = new Options();
    appOptions.addOption(
        Option.builder(OPT_INPUT)
            .hasArg(true)
            .longOpt(OPT_INPUT_LONG)
            .desc("Straglr tsv file.")
            .required()
            .build());
    appOptions.addOption(
        Option.builder(OPT_BED)
            .hasArg(true)
            .longOpt(OPT_BED_LONG)
            .desc("bed catalog file (.bed).")
            .required()
            .build());
    appOptions.addOption(
        Option.builder(OPT_REFERENCE)
            .hasArg(true)
            .longOpt(OPT_REFERENCE_LONG)
            .desc("Reference sequence file (.fna).")
            .required()
            .build());
    appOptions.addOption(
        Option.builder(OPT_HAPLOID)
            .hasArg(true)
            .longOpt(OPT_HAPLOID_LONG)
            .desc("Comma separated list of haploid contigs.")
            .build());
    appOptions.addOption(
        Option.builder(OPT_SAMPLE)
            .hasArg(true)
            .longOpt(OPT_SAMPLE_LONG)
            .desc("Sample name to be used in the VCF.")
            .required()
            .build());
    appOptions.addOption(
        Option.builder(OPT_OUTPUT)
            .hasArg(true)
            .longOpt(OPT_OUTPUT_LONG)
            .desc("Output file (.tsv).")
            .build());
    appOptions.addOption(
        Option.builder(OPT_FORCE)
            .longOpt(OPT_FORCE_LONG)
            .desc("Override the output file if it already exists.")
            .build());

    APP_OPTIONS = appOptions;
    Options appVersionOptions = new Options();
    appVersionOptions.addOption(
        Option.builder(OPT_VERSION)
            .required()
            .longOpt(OPT_VERSION_LONG)
            .desc("Print version.")
            .build());
    APP_VERSION_OPTIONS = appVersionOptions;
  }

  private AppCommandLineOptions() {}

  static Options getAppOptions() {
    return APP_OPTIONS;
  }

  static Options getAppVersionOptions() {
    return APP_VERSION_OPTIONS;
  }

  static void validateCommandLine(CommandLine commandLine) {
    validateInput(commandLine);
    validateOutput(commandLine);
  }

  private static void validateInput(CommandLine commandLine) {
    validateFile(commandLine, OPT_INPUT, ".tsv");
    validateFile(commandLine, OPT_REFERENCE, ".fna");
    validateFile(commandLine, OPT_BED, ".bed");
  }

  private static void validateFile(CommandLine commandLine, String option, String extension) {
    if (commandLine.hasOption(option)) {
      Path inputPath = Path.of(commandLine.getOptionValue(option));
      if (!Files.exists(inputPath)) {
        throw new IllegalArgumentException(
            format("Input file '%s' does not exist.", inputPath));
      }
      if (Files.isDirectory(inputPath)) {
        throw new IllegalArgumentException(
            format("Input file '%s' is a directory.", inputPath));
      }
      if (!Files.isReadable(inputPath)) {
        throw new IllegalArgumentException(
            format("Input file '%s' is not readable.", inputPath));
      }
      String inputPathStr = inputPath.toString();
      if (!inputPathStr.endsWith(extension)) {
        throw new IllegalArgumentException(
            format("Input file '%s' is not a %s file.", inputPathStr, extension));
      }
    }
  }

  private static void validateOutput(CommandLine commandLine) {
    if (!commandLine.hasOption(OPT_OUTPUT)) {
      return;
    }

    Path outputPath = Path.of(commandLine.getOptionValue(OPT_OUTPUT));

    if (!commandLine.hasOption(OPT_FORCE) && Files.exists(outputPath)) {
      throw new IllegalArgumentException(
          format("Output file '%s' already exists", outputPath));
    }
  }
}
