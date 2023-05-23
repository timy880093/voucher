package com.gateweb.voucher.model.dto;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.util.Pair;

@Data
@AllArgsConstructor
public class ErrorInfo {
  private String key;
  private String message;
  private List<String> values;

  public static ErrorInfo create(String key, String message, Pair... fieldValuePairs) {
    return new ErrorInfo(
        key,
        message,
        Arrays.stream(fieldValuePairs)
            .map(p -> p.getFirst() + ": " + p.getSecond())
            .collect(Collectors.toList()));
  }

  public static ErrorInfo create(String key, String message, String[] fieldValuePairs) {
    return new ErrorInfo(key, message, Arrays.stream(fieldValuePairs).collect(Collectors.toList()));
  }
}
