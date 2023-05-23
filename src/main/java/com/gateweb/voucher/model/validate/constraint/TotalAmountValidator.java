package com.gateweb.voucher.model.validate.constraint;

import static com.gateweb.voucher.model.dto.VoucherColumns.*;
import static com.gateweb.voucher.utils.voucher.VoucherLogic.isCancel;

import com.gateweb.voucher.model.VoucherCore;
import com.gateweb.voucher.model.validate.annotation.TotalAmount;
import com.gateweb.voucher.utils.ValidatorUtils;
import java.math.BigDecimal;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.data.util.Pair;

public class TotalAmountValidator implements ConstraintValidator<TotalAmount, VoucherCore> {

  /** 實現驗證內容 */
  @Override
  public boolean isValid(final VoucherCore v, ConstraintValidatorContext context) {

    boolean isValid = false;
    if (isCancel(v.getStatus())) return true;

    if (ValidatorUtils.isNumberAll(
        v.getTotalAmount(),
        v.getSalesAmount(),
        v.getZeroTaxSalesAmount(),
        v.getFreeTaxSalesAmount(),
        v.getTaxAmount())) {
      final BigDecimal totalAmountDigit = new BigDecimal(v.getTotalAmount());
      final BigDecimal salesAmountDigit = new BigDecimal(v.getSalesAmount());
      final BigDecimal zeroTaxSalesAmountDigit = new BigDecimal(v.getZeroTaxSalesAmount());
      final BigDecimal freeTaxSalesAmountDigit = new BigDecimal(v.getFreeTaxSalesAmount());
      final BigDecimal taxAmountDigit = new BigDecimal(v.getTaxAmount());

      isValid =
          totalAmountDigit.compareTo(
                  salesAmountDigit.add(
                      zeroTaxSalesAmountDigit.add(freeTaxSalesAmountDigit.add(taxAmountDigit))))
              == 0;
    }

    if (!isValid) {
      ValidatorUtils.addError(
          context,
          Pair.of(ZERO_TAX_SALES_AMOUNT, v.getZeroTaxSalesAmount()),
          Pair.of(FREE_TAX_SALES_AMOUNT, v.getFreeTaxSalesAmount()),
          Pair.of(SALES_AMOUNT, v.getSalesAmount()),
          Pair.of(TAX_AMOUNT, v.getTaxAmount()),
          Pair.of(TOTAL_AMOUNT, v.getTotalAmount()));
    }
    return isValid;
  }
}
