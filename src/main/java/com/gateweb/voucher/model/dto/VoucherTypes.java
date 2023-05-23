package com.gateweb.voucher.model.dto;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public enum VoucherTypes {
  INPUT_INVOICE(new HashSet<>(Arrays.asList("21", "22", "25", "26", "27", "28"))),
  INPUT_ALLOWANCE(new HashSet<>(Arrays.asList("23", "24", "29"))),
  OUTPUT_INVOICE(new HashSet<>(Arrays.asList("31", "32", "35", "36", "37"))),
  OUTPUT_ALLOWANCE(new HashSet<>(Arrays.asList("33", "34", "38")));

  private final Set<String> typeCodes;

  VoucherTypes(Set<String> typeCodes) {
    this.typeCodes = typeCodes;
  }
}
