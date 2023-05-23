package com.gateweb.voucher.utils;

import com.opencsv.bean.*;
import io.micrometer.core.instrument.util.IOUtils;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class CsvReader {
  public static <T> List<T> readByPosition(InputStream fileInput, Class<T> format, int skipLine) {
    String content = IOUtils.toString(fileInput, StandardCharsets.UTF_8);
    content = prepareContent(content);
    final Reader reader = new StringReader(content);
    final CsvToBean<T> ctb = csvToBeanByColumnPosition(reader, format, skipLine);
    return ctb.parse();
  }

  public static <T> List<T> readByHeaderName(String content, Class<T> format) {
    content = prepareContent(content);
    final StringReader reader = new StringReader(content);
    final CsvToBean<T> ctb = csvToBeanByHeaderColumnName(reader, format);
    return ctb.parse();
  }

  private static <T> CsvToBean<T> csvToBeanByColumnPosition(
      Reader reader, Class<T> format, int skipLine) {
    // 設置 ColumnPosition，根據欄位順序讀取，需在欲讀取的物件上設置 @CsvBindByPosition 配合使用
    final ColumnPositionMappingStrategy<T> strategy = new ColumnPositionMappingStrategy<>();
    strategy.setType(format);
    final CsvToBeanBuilder<T> builder = builder(reader, strategy);
    if (skipLine > 0) builder.withSkipLines(skipLine);
    return builder.build();
  }

  private static <T> CsvToBean<T> csvToBeanByHeaderColumnName(Reader reader, Class<T> format) {
    // 設置 HeaderColumnName，根據欄位名稱讀取，需在欲讀取的物件上設置 @CsvBindByName 配合使用
    final HeaderColumnNameMappingStrategy<T> strategy = new HeaderColumnNameMappingStrategy<>();
    strategy.setType(format);
    final CsvToBeanBuilder<T> builder = builder(reader, strategy);
    return builder.build();
  }

  private static <T> CsvToBeanBuilder<T> builder(Reader reader, MappingStrategy strategy) {
    return new CsvToBeanBuilder<T>(reader)
        .withMappingStrategy(strategy) // 讀取的 strategy (position、headerName)
        .withIgnoreLeadingWhiteSpace(true); // 忽略第一個雙引號前的空白 (BOM?)
  }

  private static String prepareContent(String content) {
    content = content.replace("\uFEFF", ""); // remove BOM
    content = StringUtils.substringBefore(content, ",,,,,,,"); // remove empty row
    return StringUtils.upperCase(StringUtils.substringBefore(content, System.lineSeparator()))
        + System.lineSeparator()
        + StringUtils.substringAfter(content, System.lineSeparator()); // case toUpper
  }
}
