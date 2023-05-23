package com.gateweb.voucher.model.validate.constraint;

import static com.gateweb.voucher.model.dto.VoucherColumns.*;
import static com.gateweb.voucher.utils.voucher.VoucherLogic.*;

import com.gateweb.voucher.model.VoucherCore;
import com.gateweb.voucher.model.validate.annotation.SalesAmount;
import com.gateweb.voucher.utils.ValidatorUtils;
import java.math.BigDecimal;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.data.util.Pair;

public class SalesAmountValidator implements ConstraintValidator<SalesAmount, VoucherCore> {

  /** 實現驗證內容 */
  @Override
  public boolean isValid(final VoucherCore v, ConstraintValidatorContext context) {

    boolean isValid;
    if (isCancel(v.getStatus())) return true;

    final BigDecimal salesAmountDigit = new BigDecimal(v.getSalesAmount());
    final String taxType = v.getTaxType();

    isValid =
        isTaxable(taxType) // 應稅
                || isSpecialTax(taxType) // 特種稅
                || isMixedTax(taxType) // 混和稅
            ? salesAmountDigit.compareTo(BigDecimal.ZERO) >= 0 // >= 0 (含0元發票)
            : salesAmountDigit.compareTo(BigDecimal.ZERO) == 0; // 其餘 = 0

    if (!isValid) {
      ValidatorUtils.addError(
          context, Pair.of(SALES_AMOUNT, v.getSalesAmount()), Pair.of(TAX_TYPE, taxType));
    }
    return isValid;
  }
}
