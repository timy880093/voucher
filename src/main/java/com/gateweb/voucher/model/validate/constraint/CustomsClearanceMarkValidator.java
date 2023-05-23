package com.gateweb.voucher.model.validate.constraint;

import static com.gateweb.voucher.model.dto.VoucherColumns.*;

import com.gateweb.voucher.model.VoucherCore;
import com.gateweb.voucher.model.validate.annotation.CustomsClearanceMark;
import com.gateweb.voucher.utils.ValidatorUtils;
import com.gateweb.voucher.utils.voucher.VoucherLogic;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.util.Pair;

public class CustomsClearanceMarkValidator
    implements ConstraintValidator<CustomsClearanceMark, VoucherCore> {

  /** 實現驗證內容 */
  @Override
  public boolean isValid(final VoucherCore v, ConstraintValidatorContext context) {

    boolean isValid = false;
    final String customsClearanceMark = v.getCustomsClearanceMark();
    final String taxType = v.getTaxType();

    // 欄位限制1位以內
    if (StringUtils.length(customsClearanceMark) <= 1) {
      isValid =
          VoucherLogic.checkIsZeroTax(taxType)
              ? StringUtils.equalsAny(customsClearanceMark, "1", "2")
              : StringUtils.length(customsClearanceMark) == 0;
    }

    if (!isValid) {
      ValidatorUtils.addError(
          context,
          "IE1901",
          Pair.of(CUSTOMS_CLEARANCE_MARK, customsClearanceMark),
          Pair.of(TAX_TYPE, taxType));
    }
    return isValid;
  }
}
