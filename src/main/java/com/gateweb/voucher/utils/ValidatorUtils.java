package com.gateweb.voucher.utils;

import static java.util.stream.Collectors.toList;

import com.gateweb.voucher.model.dto.ErrorInfo;
import com.gateweb.voucher.model.dto.VoucherColumns;
import java.util.*;
import java.util.stream.Collectors;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintViolation;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.data.util.Pair;

public class ValidatorUtils {

  private static final String SEPARATOR = "|||";

  /**
   * 以下方法在 類級約束 且 驗證失敗 才需要加上這段，需多加boolean變數如isValid搭配判斷使用 例如 isValid() 傳入的是，BeanObject(自定義) 和
   * ConstraintValidatorContext 因為傳入整個 Bean類別，預設不知道是哪個欄位錯誤，若驗證失敗： 1. 需先使用
   * disableDefaultConstraintViolation將預設值清空 2. 再 Build MessageTemplate，並使用 addPropertyNode添加錯誤欄位名，再
   * addConstraintViolation()加回驗證器
   *
   * @param context
   * @param message
   */
  public static boolean addConstraintViolation(
      ConstraintValidatorContext context, String message, String field) {
    // reset message
    context.disableDefaultConstraintViolation();
    // add new message
    context
        .buildConstraintViolationWithTemplate(message)
        .addPropertyNode(field)
        .addConstraintViolation();
    return false;
  }

  public static String genFieldValues(Pair... filedValuePairs) {
    return Arrays.stream(filedValuePairs)
        .map(
            p -> {
              final Object field =
                  p.getFirst() instanceof VoucherColumns
                      ? ((VoucherColumns) p.getFirst()).colName()
                      : p.getFirst();
              return field + ": " + p.getSecond();
            })
        //        .map(p -> p.getFirst() + ": " + p.getSecond())
        .collect(Collectors.joining(SEPARATOR));
  }

  /*
  以下方法在 類級約束 且 驗證失敗 才需要加上這段，需多加boolean變數如isValid搭配判斷使用
  例如 isValid() 傳入的是，BeanObject(自定義) 和 ConstraintValidatorContext
  因為傳入整個 Bean類別，預設不知道是哪個欄位錯誤，若驗證失敗：
  1. 需先使用 disableDefaultConstraintViolation將預設值清空
  2. 再 Build MessageTemplate，並使用 addPropertyNode添加錯誤欄位名，再 addConstraintViolation()加回驗證器
  */
  public static void addError(
      ConstraintValidatorContext context, String message, Pair... fieldValuePairs) {
    final String newMessage =
        StringUtils.defaultIfBlank(message, context.getDefaultConstraintMessageTemplate());
    String fieldValue = genFieldValues(fieldValuePairs);
    if (!fieldValue.contains(SEPARATOR)) fieldValue += SEPARATOR; // 為了給後面 toError 作判斷
    addConstraintViolation(context, newMessage, fieldValue);
  }

  public static void addError(ConstraintValidatorContext context, Pair... fieldValuePairs) {
    addError(context, null, fieldValuePairs);
  }

  public static List<ErrorInfo> toError(String key, Set<ConstraintViolation<Object>> cvs) {
    return cvs.stream().map(v -> toError(key, v)).collect(toList());
  }

  public static ErrorInfo toError(String key, ConstraintViolation<Object> cv) {
    final String field = cv.getPropertyPath().toString();
    final String message = cv.getMessage();
    final Object value = cv.getInvalidValue();
    return field.contains(SEPARATOR)
        ? ErrorInfo.create(key, message, StringUtils.split(field, SEPARATOR))
        : ErrorInfo.create(key, message, Pair.of(field, value));
  }

  public static boolean isNumberAll(String... numbers) {
    for (String number : numbers) {
      if (!NumberUtils.isParsable(number)) return false;
    }
    return true;
  }
}
