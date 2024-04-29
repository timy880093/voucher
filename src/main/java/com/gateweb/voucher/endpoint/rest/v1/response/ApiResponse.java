package com.gateweb.voucher.endpoint.rest.v1.response;

import lombok.Data;

import java.util.Collection;

@Data
public class ApiResponse {
  String status;
  String message;
  Collection<?> data;
}
