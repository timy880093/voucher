package com.gateweb.voucher.utils;

import java.time.LocalDate;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.util.Locale;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@UtilityClass
@Slf4j
public class DateTimeConverter {

  final DateTimeFormatter ACCEPT_FORMAT =
      new DateTimeFormatterBuilder()
          .parseCaseInsensitive()
          .appendPattern("[uuuuMMdd][uuuu-M-d][uuuu/M/d][M-d-uuuu][M/d/uuuu]") // 可接受解析的格式
          .toFormatter(Locale.getDefault()) // 預設時區
          .withChronology(IsoChronology.INSTANCE) // ISO 日期格式，要使用 uuuu(年) 取代 yyyy(年分)
          .withResolverStyle(ResolverStyle.STRICT); // 嚴謹解析，例如 0229 不是每年都有

  /**
   * @param dateOrYearMonth : [uuuuMMdd][uuuu-M-d][uuuu/M/d][M-d-uuuu][M/d/uuuu] ,
   *     [yyyMM][yyyyMM][yyyMMdd]
   * @return localDate
   */
  public LocalDate toLocalDate(String dateOrYearMonth) {
    if (StringUtils.isBlank(dateOrYearMonth)) return null;
    final String str =
        StringUtils.containsAny(dateOrYearMonth, "-", "/")
            ? dateOrYearMonth
            : parseDate(dateOrYearMonth);
    try {
      return LocalDate.parse(str, ACCEPT_FORMAT);
    } catch (Exception e) {
      log.error("date({}) , toLocalDate error : {}", dateOrYearMonth, e.getMessage());
      return null;
    }
  }

  String parseDate(String dateOrYearMonth) {
    switch (dateOrYearMonth.length()) {
      case 5:
        return rocToAd(dateOrYearMonth) + "01";
      case 7:
        return rocToAd(dateOrYearMonth);
      case 6:
        return dateOrYearMonth + "01";
      default:
        return dateOrYearMonth;
    }
  }

  public String toYYYYMMDD(LocalDate localDate) {
    return localDate == null ? "" : localDate.format(DateTimeFormatter.BASIC_ISO_DATE);
  }

  public String toYYYYMMDD(String date) {
    return toYYYYMMDD(toLocalDate(date));
  }

  public String toYYYMM(LocalDate localDate) {
    return localDate == null
        ? ""
        : (localDate.getYear() - 1911) * 100 + localDate.getMonthValue() + "";
  }

  public String toYYYMM(String date) {
    return toYYYMM(toLocalDate(date));
  }

  public String toEvenYearMonth(LocalDate localDate) {
    final int yyyMM = Integer.parseInt(toYYYMM(localDate));
    return "" + (yyyMM % 2 == 1 ? yyyMM + 1 : yyyMM);
  }

  public String toEvenYearMonth(String date) {
    return toEvenYearMonth(toLocalDate(date));
  }

  public String rocToAd(String roc) {
    if (StringUtils.isBlank(roc)) return "";
    final String str = roc.trim();
    final int yyyy = Integer.parseInt(StringUtils.substring(str, 0, 3)) + 1911;
    return yyyy + StringUtils.substring(str, 3);
  }

  public String adToRoc(String ad) {
    if (StringUtils.isBlank(ad)) return "";
    final String str = ad.trim();
    final int yyy = Integer.parseInt(StringUtils.substring(str, 0, 4)) - 1911;
    return yyy + StringUtils.substring(str, 4);
  }
}
