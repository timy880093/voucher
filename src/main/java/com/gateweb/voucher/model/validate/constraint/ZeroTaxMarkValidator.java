package com.gateweb.voucher.model.validate.constraint;

import static com.gateweb.voucher.model.dto.VoucherColumns.*;

import com.gateweb.voucher.model.VoucherCore;
import com.gateweb.voucher.model.validate.annotation.ZeroTaxMark;
import com.gateweb.voucher.utils.ValidatorUtils;
import com.gateweb.voucher.utils.voucher.VoucherLogic;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.util.Pair;

public class ZeroTaxMarkValidator implements ConstraintValidator<ZeroTaxMark, VoucherCore> {

  /** 實現驗證內容 */
  @Override
  public boolean isValid(final VoucherCore v, ConstraintValidatorContext context) {

    boolean isValid;
    final String zeroTaxMark = v.getZeroTaxMark();
    final String taxType = v.getTaxType();

    isValid =
        VoucherLogic.checkIsZeroTax(taxType)
            ? StringUtils.equalsAny(zeroTaxMark, "1", "2", "3", "4", "5", "6", "7", "8", "9")
            : StringUtils.length(zeroTaxMark) == 0;

    if (!isValid) {
      ValidatorUtils.addError(
          context, "IE2101", Pair.of(ZERO_TAX_MARK, zeroTaxMark), Pair.of(TAX_TYPE, taxType));
    }
    return isValid;
  }
}
