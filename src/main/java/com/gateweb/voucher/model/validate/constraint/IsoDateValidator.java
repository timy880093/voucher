package com.gateweb.voucher.model.validate.constraint;


import com.gateweb.voucher.model.validate.annotation.IsoDate;
import com.gateweb.voucher.utils.DateTimeConverter;
import com.gateweb.voucher.utils.voucher.VoucherLogic;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

// 僅驗證單一欄位
public class IsoDateValidator implements ConstraintValidator<IsoDate, String> { // 註解類型、欄位類型

  @Override
  public boolean isValid(String date, ConstraintValidatorContext context) {
    if (date == null) return false;

    final String formatDate = DateTimeConverter.toYYYYMMDD(date);
    return VoucherLogic.isValidDate(formatDate);
  }
}
