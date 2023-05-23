package com.gateweb.voucher.utils;

import com.gateweb.voucher.endpoint.rest.v1.request.VoucherGwRequest;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

class ExcelReaderTest {

  @Test
  void test_readGw_excel() throws Exception {
    final ClassPathResource resource = new ClassPathResource("/example/import-gw.xlsx");
    final List<VoucherGwRequest> requestData =
        ExcelReader.read(
            resource.getInputStream(), VoucherGwRequest.headers, VoucherGwRequest.class);
    Assertions.assertThat(requestData).isNotEmpty();
  }

  @Test
  void test_readGw_csv() throws Exception {
    final ClassPathResource resource = new ClassPathResource("/example/import-gw.csv");
    final List<VoucherGwRequest> requestData =
        ExcelReader.read(
            resource.getInputStream(), VoucherGwRequest.headers, VoucherGwRequest.class);
    Assertions.assertThat(requestData).isNotEmpty();
  }
}
