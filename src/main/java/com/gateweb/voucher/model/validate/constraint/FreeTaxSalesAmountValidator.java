package com.gateweb.voucher.model.validate.constraint;

import static com.gateweb.voucher.model.dto.VoucherColumns.*;
import static com.gateweb.voucher.utils.voucher.VoucherLogic.*;

import com.gateweb.voucher.model.VoucherCore;
import com.gateweb.voucher.model.validate.annotation.FreeTaxSalesAmount;
import com.gateweb.voucher.utils.ValidatorUtils;
import java.math.BigDecimal;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.data.util.Pair;

public class FreeTaxSalesAmountValidator
    implements ConstraintValidator<FreeTaxSalesAmount, VoucherCore> {

  /** 實現驗證內容 */
  @Override
  public boolean isValid(final VoucherCore v, ConstraintValidatorContext context) {

    boolean isValid = false;
    if (isCancel(v.getStatus())) return true;

    if (NumberUtils.isParsable(v.getFreeTaxSalesAmount())) {
      final BigDecimal zeroTaxSalesAmountDigit = new BigDecimal(v.getZeroTaxSalesAmount());
      final BigDecimal freeTaxSalesAmountDigit = new BigDecimal(v.getFreeTaxSalesAmount());
      final String taxType = v.getTaxType();
      if (checkIsFreeTax(taxType)) {
        isValid = freeTaxSalesAmountDigit.compareTo(BigDecimal.ZERO) >= 0; // 免稅或混和稅時 > 0
      } else if (isMixedTax(taxType)) {
        if (freeTaxSalesAmountDigit.compareTo(BigDecimal.ZERO) > 0
            && zeroTaxSalesAmountDigit.compareTo(BigDecimal.ZERO) == 0) isValid = true;
        else return true;

      } else {
        isValid = freeTaxSalesAmountDigit.compareTo(BigDecimal.ZERO) == 0; // 否則 = 0
      }
      //       FIXME 目前混和稅強制等於應稅+免稅，看日後是否需修改
      //      isValid =
      //          checkIsFreeTax(taxType) || checkIsMixedTax(taxType)
      //              ? freeTaxSalesAmountDigit.compareTo(BigDecimal.ZERO) >= 0 // 免稅或混和稅時 > 0
      //              : freeTaxSalesAmountDigit.compareTo(BigDecimal.ZERO) == 0; // 否則 = 0
    }

    if (!isValid) {
      ValidatorUtils.addError(
          context,
          Pair.of(ZERO_TAX_SALES_AMOUNT, v.getZeroTaxSalesAmount()),
          Pair.of(FREE_TAX_SALES_AMOUNT, v.getFreeTaxSalesAmount()),
          Pair.of(TAX_TYPE, v.getTaxType()));
    }
    return isValid;
  }
}
