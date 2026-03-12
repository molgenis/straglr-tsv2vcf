package org.molgenis.vcf.straglr.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.molgenis.vcf.straglr.model.Read;

public class RepeatUnitUtils {
  private RepeatUnitUtils() {}

  private static final Map<Character, Set<Character>> IUPAC_EXPANSION = new HashMap<>();

  static {
    IUPAC_EXPANSION.put('A', Set.of('A'));
    IUPAC_EXPANSION.put('C', Set.of('C'));
    IUPAC_EXPANSION.put('G', Set.of('G'));
    IUPAC_EXPANSION.put('T', Set.of('T'));
    IUPAC_EXPANSION.put('R', Set.of('A', 'G'));
    IUPAC_EXPANSION.put('Y', Set.of('C', 'T'));
    IUPAC_EXPANSION.put('S', Set.of('G', 'C'));
    IUPAC_EXPANSION.put('W', Set.of('A', 'T'));
    IUPAC_EXPANSION.put('K', Set.of('G', 'T'));
    IUPAC_EXPANSION.put('M', Set.of('A', 'C'));
    IUPAC_EXPANSION.put('B', Set.of('C', 'G', 'T'));
    IUPAC_EXPANSION.put('D', Set.of('A', 'G', 'T'));
    IUPAC_EXPANSION.put('H', Set.of('A', 'C', 'T'));
    IUPAC_EXPANSION.put('V', Set.of('A', 'C', 'G'));
    IUPAC_EXPANSION.put('N', Set.of('A', 'C', 'G', 'T'));
  }

  public static boolean isMatch(String catalogRU, String calledRU) {
    if (catalogRU == null || calledRU == null) return false;
    catalogRU = catalogRU.toUpperCase(Locale.ROOT);
    calledRU = calledRU.toUpperCase(Locale.ROOT);

    if (catalogRU.length() != calledRU.length()) return false;
    int n = catalogRU.length();
    if (n == 0) return true;

    // Try all  shifts of called RU against catalog RU
    for (int shift = 0; shift < n; shift++) {
      boolean matches = true;
      for (int i = 0; i < n; i++) {
        char cat = catalogRU.charAt(i);
        char called = calledRU.charAt((i + shift) % n);

        // called RU must be ACGT
        if (!"ACGT".contains(String.valueOf(called))) return false;
        Set<Character> allowed = IUPAC_EXPANSION.get(cat);
        if (allowed == null || !allowed.contains(called)) {
          matches = false;
          break;
        }
      }
      if (matches) return true;
    }
    return false;
  }

  static String getMostFrequentActualRepeat(List<Read> reads) {
    return reads.stream()
        .map(Read::actualRepeat)
        .filter(Objects::nonNull)
        .collect(Collectors.groupingBy(actual -> actual.replace(",", "_"), Collectors.counting()))
        .entrySet()
        .stream()
        .max(Map.Entry.comparingByValue())
        .map(Map.Entry::getKey)
        .orElseThrow();
  }

  static List<String> getRepeatUnitsWithCounts(List<Read> reads) {
    return reads.stream()
        .map(Read::actualRepeat)
        .filter(Objects::nonNull)
        .collect(Collectors.groupingBy(actual -> actual.replace(",", "_"), Collectors.counting()))
        .entrySet()
        .stream()
        .map(e -> e.getKey() + "(" + e.getValue() + ")")
        .toList();
  }
}
