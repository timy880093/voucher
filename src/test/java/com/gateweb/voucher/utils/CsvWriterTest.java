package com.gateweb.voucher.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class CsvWriterTest {

  @Test
  void test_writeToString_withBom() throws IOException {
    List<String[]> data = new ArrayList<>();
    data.add(new String[] {"Name", "Age", "City"});
    data.add(new String[] {"John", "25", "New York"});
    data.add(new String[] {"Jane", "30", "Los Angeles"});
    final String result = CsvWriter.writeToString(data, true);
    Assertions.assertThat(result).isNotBlank();
  }
}
