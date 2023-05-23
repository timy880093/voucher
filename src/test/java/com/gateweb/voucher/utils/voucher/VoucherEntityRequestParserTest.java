package com.gateweb.voucher.utils.voucher;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class VoucherRequestParserTest {

  @ParameterizedTest
  @CsvSource({"1,1", ",0", " ,0", "a,0"})
  void test_parseInteger(String value, int expect) throws Exception {
    String field = "f";

    if (StringUtils.isBlank(value) || NumberUtils.isDigits(value)) {
      final int result = VoucherRequestParser.parseInteger(field, value);
      Assertions.assertThat(result).isEqualTo(expect);
    } else {
      final Throwable throwable =
          Assertions.catchThrowable(() -> VoucherRequestParser.parseInteger(field, value));
      Assertions.assertThat(throwable)
          .isInstanceOf(Exception.class)
          .hasMessageContaining("parseInteger error");
    }
  }

  @ParameterizedTest
  @CsvSource({"1.1,1.1", ",0", " ,0", "a,0"})
  void test_Bigdecimal(String value, BigDecimal expect) throws Exception {
    String field = "f";

    if (StringUtils.isBlank(value) || NumberUtils.isParsable(value)) {
      final BigDecimal result = VoucherRequestParser.parseBigdecimal(field, value);
      Assertions.assertThat(result).isEqualTo(expect);
    } else {
      final Throwable throwable =
          Assertions.catchThrowable(() -> VoucherRequestParser.parseBigdecimal(field, value));
      Assertions.assertThat(throwable)
          .isInstanceOf(Exception.class)
          .hasMessageContaining("parseBigdecimal error");
    }
  }
}
