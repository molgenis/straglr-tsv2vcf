package org.molgenis.vcf.straglr.utils;

import java.util.List;
import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.molgenis.vcf.straglr.model.Read;

public class StatisticsUtils {

  public static String calculateREPCI(List<Read> reads, float confidenceLevel, String locus) {
    double[] ruCounts = reads.stream().mapToDouble(Read::copyNumber).toArray();

    if (ruCounts.length < 2) {
      throw new IllegalStateException(
          String.format(
              "Too little copies '%s' for position '%s'.",
              ruCounts.length, locus));
    }

    DescriptiveStatistics stats = new DescriptiveStatistics(ruCounts);
    double mean = stats.getMean();
    double sem = stats.getStandardDeviation() / Math.sqrt(stats.getN());
    TDistribution tDist = new TDistribution(stats.getN() - 1);
    double margin = tDist.inverseCumulativeProbability(confidenceLevel) * sem;

    int lower = (int) Math.round(mean - margin);
    int upper = (int) Math.round(mean + margin);

    return lower + "-" + upper;
  }
}
