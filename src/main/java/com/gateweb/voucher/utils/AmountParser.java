package com.gateweb.voucher.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class AmountParser {
  private static final DecimalFormat thousandsFormatter = new DecimalFormat("#,##0"); // 千分位

  public static String formatThousands(BigDecimal amount) {
    return thousandsFormatter.format(amount.stripTrailingZeros());
  }
}
