package com.gateweb.voucher.model.validate.constraint;

import static com.gateweb.voucher.model.dto.VoucherColumns.*;
import static com.gateweb.voucher.utils.voucher.VoucherLogic.isMixedTax;
import static com.gateweb.voucher.utils.voucher.VoucherLogic.checkIsZeroTaxOrFreeTax;

import com.gateweb.voucher.model.VoucherCore;
import com.gateweb.voucher.model.validate.annotation.DeductionCode;
import com.gateweb.voucher.utils.ValidatorUtils;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.util.Pair;

public class DeductionCodeValidator implements ConstraintValidator<DeductionCode, VoucherCore> {

  /** 實現驗證內容 */
  @Override
  public boolean isValid(final VoucherCore v, ConstraintValidatorContext context) {

    boolean isValid;
    final String deductionCode = v.getDeductionCode();
    final String taxType = v.getTaxType();

    isValid =
        checkIsZeroTaxOrFreeTax(taxType) || isMixedTax(taxType)
            ? StringUtils.equalsAny(deductionCode, "3", "4")
            : StringUtils.equalsAny(deductionCode, "1", "2", "3", "4");

    if (!isValid) {
      ValidatorUtils.addError(
          context, "IE1601", Pair.of(DEDUCTION_CODE, deductionCode), Pair.of(TAX_TYPE, taxType));
    }
    return isValid;
  }
}
