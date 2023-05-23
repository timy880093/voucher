package com.gateweb.voucher.model.validate.constraint;

import static com.gateweb.voucher.model.dto.VoucherColumns.*;

import com.gateweb.voucher.model.VoucherCore;
import com.gateweb.voucher.model.validate.annotation.InvoiceDate;
import com.gateweb.voucher.utils.ValidatorUtils;
import com.gateweb.voucher.utils.voucher.VoucherLogic;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.util.Pair;

public class InvoiceDateValidator implements ConstraintValidator<InvoiceDate, VoucherCore> {

  //  @Value("${external.checkFilling:false}")
  // FIXME 目前都放寬 2-3 期
  boolean checkFilling = true;

  /** 實現驗證內容 */
  @Override
  public boolean isValid(final VoucherCore v, ConstraintValidatorContext context) {

    boolean isValid = false;
    final String filingYearMonth = v.getFilingYearMonth(); // yyyMM
    final String voucherYearMonth = v.getVoucherYearMonth();
    final String typeCode = v.getTypeCode();

    if (StringUtils.isNumeric(filingYearMonth) && StringUtils.isNumeric(voucherYearMonth)) {
      isValid =
          VoucherLogic.checkFillingAndOriginalYearMonth(
              filingYearMonth, voucherYearMonth, typeCode, checkFilling);
    }

    if (!isValid) {
      ValidatorUtils.addError(
          context,
          Pair.of(FILING_YEAR_MONTH, filingYearMonth),
          Pair.of(VOUCHER_DATE, v.getVoucherDate()));
    }
    return isValid;
  }
}
