package com.gateweb.voucher.model.dto;

public enum LogStatus {
  PROCESSING(0),
  SUCCESS(1),
  READ_ERROR(2),
  VALIDATE_ERROR(3),
  EXCEPTION(9),
  ;

  public final int status;

  LogStatus(int status) {
    this.status = status;
  }
}
