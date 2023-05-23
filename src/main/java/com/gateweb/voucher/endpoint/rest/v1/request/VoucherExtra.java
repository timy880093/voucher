package com.gateweb.voucher.endpoint.rest.v1.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VoucherExtra {

  private String action;
  private String filename;
  private String yearMonth;
  private String owner;
  private int userId;
  private Long logId;
}
