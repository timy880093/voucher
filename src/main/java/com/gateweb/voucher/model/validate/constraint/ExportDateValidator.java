package com.gateweb.voucher.model.validate.constraint;

import static com.gateweb.voucher.model.dto.VoucherColumns.*;

import com.gateweb.voucher.model.VoucherCore;
import com.gateweb.voucher.model.validate.annotation.ExportDate;
import com.gateweb.voucher.utils.ValidatorUtils;
import com.gateweb.voucher.utils.voucher.VoucherLogic;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.util.Pair;

public class ExportDateValidator implements ConstraintValidator<ExportDate, VoucherCore> {

  /** 實現驗證內容 */
  @Override
  public boolean isValid(final VoucherCore v, ConstraintValidatorContext context) {

    boolean isValid = false;
    final String exportDate = v.getExportDate();
    final String taxType = v.getTaxType();
    final String zeroTaxMark = v.getZeroTaxMark();

    final boolean isValidDate = VoucherLogic.isValidDate(exportDate);
    final boolean isEmptyOrValidDate = StringUtils.isBlank(exportDate) || isValidDate;

    // 欄位限制10
    if (StringUtils.length(exportDate) <= 10) {
      // 銷項31、32、35、36，零稅出口
      if (VoucherLogic.checkIsZeroTax(taxType)) {
        isValid = StringUtils.equals(zeroTaxMark, "4") ? isEmptyOrValidDate : isValidDate;
      } else { // 銷項31、32、35、36，非零稅出口
        isValid = StringUtils.length(exportDate) == 0;
      }
    }

    if (!isValid) {
      ValidatorUtils.addError(context, "IE2201"
          , Pair.of(EXPORT_DATE, exportDate)
          , Pair.of(TAX_TYPE, taxType), Pair.of(ZERO_TAX_MARK, zeroTaxMark));
    }
    return isValid;
  }
}
