package com.gateweb.voucher.model.validate.constraint;

import static com.gateweb.voucher.model.dto.VoucherColumns.*;

import com.gateweb.voucher.model.VoucherCore;
import com.gateweb.voucher.model.validate.annotation.ExportNumber;
import com.gateweb.voucher.utils.ValidatorUtils;
import com.gateweb.voucher.utils.voucher.VoucherLogic;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.util.Pair;

public class ExportNumberValidator implements ConstraintValidator<ExportNumber, VoucherCore> {

  /** 實現驗證內容 */
  @Override
  public boolean isValid(final VoucherCore v, ConstraintValidatorContext context) {
    boolean isValid = false;
    String message = "";
    final String exportNumber = v.getExportNumber();
    final String typeCode = v.getTypeCode();
    final String taxType = v.getTaxType();
    final String customsClearanceMark = v.getCustomsClearanceMark();

    if (VoucherLogic.isOutputExport(typeCode, taxType)) {
      if ("1".equals(customsClearanceMark))
        if (VoucherLogic.isValidExportCertificate14(exportNumber)) isValid = true;
        else message = "IE0203";
      else if ("2".equals(customsClearanceMark))
        if (VoucherLogic.isValidExportDeclaration16(exportNumber)) isValid = true;
        else message = "IE0204";
      else isValid = true; // customsClearanceMark 給別的地方驗證

    } else {
      if (StringUtils.isBlank(exportNumber)) isValid = true;
      else message = "IE0205";
    }

    if (!isValid) {
      ValidatorUtils.addError(
          context,
          message,
          Pair.of(EXPORT_NUMBER, exportNumber),
          Pair.of(TYPE_CODE, typeCode),
          Pair.of(TAX_TYPE, taxType),
          Pair.of(CUSTOMS_CLEARANCE_MARK, customsClearanceMark));
    }
    return isValid;
  }
}
