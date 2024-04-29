package com.gateweb.voucher.model.validate.constraint;

import static com.gateweb.voucher.model.dto.VoucherColumns.*;
import static com.gateweb.voucher.utils.voucher.VoucherLogic.*;

import com.gateweb.voucher.model.VoucherCore;
import com.gateweb.voucher.model.validate.annotation.Seller;
import com.gateweb.voucher.utils.ValidatorUtils;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.util.Pair;

public class SellerValidator implements ConstraintValidator<Seller, VoucherCore> {

  @Override
  public boolean isValid(VoucherCore v, ConstraintValidatorContext context) {

    boolean isValid = false;
    final String seller = v.getSeller();
    final String typeCode = v.getTypeCode();
    final String consolidationMark = v.getConsolidationMark();
    final String voucherNumber = v.getInvoiceNumber();

    final boolean isInput = isInput(typeCode);
    final boolean isConsolidation = chekckIsConsolidation(consolidationMark);
    final boolean isValidTaxIdNumber = checkTaxIdNumber(seller);
    final boolean isEmptyOrValidTaxIdNumber = StringUtils.isBlank(seller) || isValidTaxIdNumber;

    // 限8字元內
    if (StringUtils.length(seller) <= 8) {
      if (isInput) { // 進項
        switch (typeCode) {
          case "26":
          case "28":
          case "29":
            // 進項26必彙加，28、29為海關，可不填
            isValid = isEmptyOrValidTaxIdNumber;
            break;
          case "25":
            isValid =
                isConsolidation // 若彙加
                    ? isEmptyOrValidTaxIdNumber // 為空或是有效統編
                    : isValidTaxIdNumber; // 否則，只能是有效統編
            break;
          case "22":
          case "27":
            isValid =
                isConsolidation || StringUtils.isBlank(voucherNumber) // 若彙加 or 無發票號碼
                    ? isEmptyOrValidTaxIdNumber // 為空或是有效統編
                    : isValidTaxIdNumber; // 否則，只能是有效統編
            break;
          default: // 21、23、24
            isValid = isValidTaxIdNumber;
        }
      } else { // 銷項全部、其他
        isValid = isValidTaxIdNumber;
      }
    }

    if (!isValid) {
      ValidatorUtils.addError(
          context,
          Pair.of(SELLER, seller),
          Pair.of(TYPE_CODE, typeCode),
          Pair.of(CONSOLIDATION_MARK, consolidationMark),
          Pair.of(VOUCHER_NUMBER, voucherNumber));
    }
    return isValid;
  }
}
