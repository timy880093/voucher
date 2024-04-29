package com.gateweb.voucher.utils.voucher;

import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@UtilityClass
public class VoucherRequestParser {
  private final Logger logger = LoggerFactory.getLogger(VoucherRequestParser.class);

  public static String parseFilingYearMonth(String yearMonth) {
    return StringUtils.length(yearMonth) == 6 && NumberUtils.isDigits(yearMonth)
        ? String.valueOf(Integer.parseInt(yearMonth) - 191100)
        : yearMonth;
  }

  public static String parseBuyer(String buyer) {
    return StringUtils.isBlank(buyer) ? "0000000000" : buyer;
  }

  public static String parseAmount(String amount) {
    return StringUtils.isBlank(amount) ? "0" : amount;
  }

  public static String parseTaxRate(String taxType) {
    if (VoucherLogic.isTaxable(taxType) || VoucherLogic.isMixedTax(taxType)) {
      return "0.05";
    } else if (VoucherLogic.isSpecialTax(taxType)) {
      final String rateString = StringUtils.split(taxType, "")[1];
      return NumberUtils.isParsable(rateString)
          ? new BigDecimal(rateString)
              .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP)
              .toPlainString()
          : "0";
    } else return "0";
  }

  public static String parseDeductionCode(String deductionCode, String typeCode) {
    if (VoucherLogic.isOutput(typeCode)) return null;
    return StringUtils.isBlank(deductionCode) ? "1" : deductionCode;
  }

  public static Integer parseDeductionCode(String value) {
    switch (value) {
      case "1":
      case "可扣抵":
        return 1;
      case "3":
      case "不可扣抵":
        return 3;
      default:
        logger.warn("parseDeductionCode({}) error", value);
        return -1;
    }
  }

  public static String parseOwner(String typeCode, String buyer, String seller) {
    return StringUtils.startsWith(typeCode, "2") ? buyer : seller;
  }

  public static int parseInteger(String field, String value) throws Exception {
    try {
      return Integer.parseInt(StringUtils.defaultIfBlank(value, "0"));
    } catch (NumberFormatException e) {
      throw new Exception(
          String.format("parseInteger error %s(%s) : %s", field, value, e.getMessage()));
    }
  }

  public static BigDecimal parseBigdecimal(String field, String value) throws Exception {
    try {
      return StringUtils.isBlank(value) ? BigDecimal.ZERO : new BigDecimal(value);
    } catch (Exception e) {
      throw new Exception(
          String.format("parseBigdecimal error %s(%s) : %s", field, value, e.getMessage()));
    }
  }
}
