package org.molgenis.vcf.straglr.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.molgenis.vcf.straglr.model.Read;

public class StatisticsUtils {

  public static String calculateConfidenceInterval(List<Read> reads, double confidenceLevel) {
    if (confidenceLevel < 0 || confidenceLevel > 1) {
      throw new IllegalArgumentException(
          String.format(
              "Confidence level '%s' is not allows, please provide a value between 0 and 1.",
              confidenceLevel));
    }
    double[] ruCounts = reads.stream().mapToDouble(Read::copyNumber).toArray();

    if (ruCounts.length < 2) {
      return "NA";
    }

    DescriptiveStatistics stats = new DescriptiveStatistics(ruCounts);
    double mean = stats.getMean();
    double sem = stats.getStandardDeviation() / Math.sqrt((double) stats.getN());
    TDistribution tDist = new TDistribution(stats.getN() - 1);
    double margin = tDist.inverseCumulativeProbability((1 + confidenceLevel) / 2) * sem;

    double lower = mean - margin;
    double upper = mean + margin;

    DecimalFormat df = new DecimalFormat("0.0", new DecimalFormatSymbols(Locale.getDefault()));
    return String.format("%s-%s", df.format(lower), df.format(upper));
  }
}
