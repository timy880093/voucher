package com.gateweb.voucher.endpoint.rest.v1.request;

import com.gateweb.voucher.model.VoucherCore;

public abstract class VoucherRequest {
  public abstract VoucherCore toDomain();

  public abstract String parseOwner();
}
