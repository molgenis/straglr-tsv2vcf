package org.molgenis.vcf.straglr.utils;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import org.jspecify.annotations.Nullable;

public class NaAsNullIntegerConverter extends AbstractBeanField {

  @Override
  @Nullable
  public Object convert(String value) throws CsvDataTypeMismatchException {
    if (value == null) {
      return null;
    }
    String trimmed = value.trim();
    if (trimmed.isEmpty() || trimmed.equalsIgnoreCase("NA")) {
      return null;
    }
    try {
      return Integer.valueOf(trimmed);
    } catch (NumberFormatException e) {
      throw new CsvDataTypeMismatchException(value, Double.class, e.getMessage());
    }
  }
}