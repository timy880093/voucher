package com.gateweb.voucher.endpoint.rest.v1.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VoucherResponse {
  private String code;
  private String message;
  private String json;

  public static VoucherResponse success(String message, String json) {
    return VoucherResponse.builder().code("200").message(message).json(json).build();
  }

  public static VoucherResponse error(String message, String json) {
    return VoucherResponse.builder().code("400").message(message).json(json).build();
  }
}
