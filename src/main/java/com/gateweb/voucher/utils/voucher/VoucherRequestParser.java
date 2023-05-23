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

  public static String parseVoucherNumber(
      String invoiceNumber, String commonNumber, String typeCode, String taxType) {
    if (VoucherLogic.isOutputExport(typeCode, taxType)) return invoiceNumber;
    else return StringUtils.isBlank(invoiceNumber) ? commonNumber : invoiceNumber;
  }

  public static String parseOutputNumber(String commonNumber, String typeCode, String taxType) {
    return VoucherLogic.isOutputExport(typeCode, taxType) ? commonNumber : null;
  }

  public static String parseBuyer(String buyer) {
    return StringUtils.isBlank(buyer) ? "0000000000" : buyer;
  }

  public static String parseAmount(String amount) {
    return StringUtils.isBlank(amount) ? "0" : amount;
  }

  public static String parseTaxRate(String taxType) {
    switch (taxType) {
      case "1":
      case "9":
        return "0.05";
      case "2":
      case "3":
        return "0";
      default:
        /* parse 特種稅，ex:
        1-25  => 0.25
        應-15 => 0.15
         */
        if (StringUtils.contains(taxType, "-") && StringUtils.startsWithAny(taxType, "1", "應")) {
          final String rateString = StringUtils.split(taxType, "")[1];
          if (NumberUtils.isParsable(rateString))
            return new BigDecimal(rateString)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP)
                .toPlainString();
        }
        return "0";
    }
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
