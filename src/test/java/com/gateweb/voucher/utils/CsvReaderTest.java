package com.gateweb.voucher.utils;

import com.gateweb.voucher.endpoint.rest.v1.request.VoucherGwRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.List;

class CsvReaderTest {
  @Test
  void test_read_csv_gw() throws IOException {
    final ClassPathResource resource = new ClassPathResource("/example/import-gw.csv");
    final List<VoucherGwRequest> requestData =
        CsvReader.readByPosition(resource.getInputStream(), VoucherGwRequest.class, 1);
    Assertions.assertThat(requestData).isNotEmpty();
  }

}
