package org.molgenis.vcf.straglr;

import static java.util.Objects.hash;
import static java.util.Objects.requireNonNull;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
class AppCommandLineRunner implements CommandLineRunner {

  private static final Logger LOGGER = LoggerFactory.getLogger(AppCommandLineRunner.class);

  private static final int STATUS_MISC_ERROR = 1;
  private static final int STATUS_COMMAND_LINE_USAGE_ERROR = 64;

  private final String appName;
  private final String appVersion;
  private final CommandLineParser commandLineParser;

  AppCommandLineRunner(
      @Value("${app.name}") String appName, @Value("${app.version}") String appVersion) {
    this.appName = requireNonNull(appName);
    this.appVersion = requireNonNull(appVersion);

    this.commandLineParser = new DefaultParser();
  }

  @Override
  public void run(String... args) {
    if (args.length == 1
        && (args[0].equals("-" + AppCommandLineOptions.OPT_VERSION)
            || args[0].equals("--" + AppCommandLineOptions.OPT_VERSION_LONG))) {
      LOGGER.info("{} {}", appName, appVersion);
      return;
    }

    CommandLine commandLine = getCommandLine(args);
    AppCommandLineOptions.validateCommandLine(commandLine);

    try {
      Path inputPath = Path.of(commandLine.getOptionValue(AppCommandLineOptions.OPT_INPUT));
      Path inputBed = Path.of(commandLine.getOptionValue(AppCommandLineOptions.OPT_BED));
      Path inputReference = Path.of(commandLine.getOptionValue(AppCommandLineOptions.OPT_REFERENCE));
      List<String> haploidContigs = Collections.emptyList();
      if(commandLine.hasOption(AppCommandLineOptions.OPT_HAPLOID)) {
       haploidContigs = Arrays.asList(commandLine.getOptionValue(AppCommandLineOptions.OPT_HAPLOID).split(","));
      }
      String sampleName = commandLine.getOptionValue(AppCommandLineOptions.OPT_SAMPLE);
      StraglrTsv2Vcf.run(inputPath, inputBed, inputReference, haploidContigs, getOutput(commandLine), sampleName);

    } catch (Exception e) {
      LOGGER.error(e.getLocalizedMessage(), e);
      System.exit(STATUS_MISC_ERROR);
    }
  }

  private CommandLine getCommandLine(String[] args) {
    CommandLine commandLine = null;
    try {
      commandLine = commandLineParser.parse(AppCommandLineOptions.getAppOptions(), args);
    } catch (ParseException e) {
      logException(e);
      System.exit(STATUS_COMMAND_LINE_USAGE_ERROR);
    }
    return commandLine;
  }

  private Path getOutput(CommandLine commandLine) {
    Path outputPath;
    if (commandLine.hasOption(AppCommandLineOptions.OPT_OUTPUT)) {
      outputPath = Path.of(commandLine.getOptionValue(AppCommandLineOptions.OPT_OUTPUT));
    } else {
      String output;
        output =
            commandLine
                .getOptionValue(AppCommandLineOptions.OPT_INPUT)
                .replace(".tsv", "out.vcf.gz");
      outputPath = Path.of(output);
    }
    return outputPath;
  }

  @SuppressWarnings("java:S106")
  private void logException(ParseException e) {
    LOGGER.error(e.getLocalizedMessage(), e);

    // following information is only logged to system out
    System.out.println();
    HelpFormatter formatter = new HelpFormatter();
    formatter.setOptionComparator(null);
    String cmdLineSyntax = "java -jar " + appName + ".jar";
    formatter.printHelp(cmdLineSyntax, AppCommandLineOptions.getAppOptions(), true);
    System.out.println();
    formatter.printHelp(cmdLineSyntax, AppCommandLineOptions.getAppVersionOptions(), true);
  }
}
