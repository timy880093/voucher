package com.gateweb.voucher.model.validate.constraint;

import static com.gateweb.voucher.model.dto.VoucherColumns.*;
import static com.gateweb.voucher.utils.voucher.VoucherLogic.*;

import com.gateweb.voucher.model.VoucherCore;
import com.gateweb.voucher.model.validate.annotation.Buyer;
import com.gateweb.voucher.utils.ValidatorUtils;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.util.Pair;

public class BuyerValidator implements ConstraintValidator<Buyer, VoucherCore> {

  @Override
  public boolean isValid(final VoucherCore v, ConstraintValidatorContext context) {

    boolean isValid = false;
    String message = "";
    final String buyer = v.getBuyer();
    final String typeCode = v.getTypeCode();
    final String taxType = v.getTaxType();
    final String consolidationMark = v.getConsolidationMark();
    final String zeroTaxMark = v.getZeroTaxMark();
    final String status = v.getStatus();

    if (isOutput(typeCode) && isCancel(status)) { // 銷項作廢時不檢查
      return true;
    }

    final boolean isValidTaxIdNumber = checkTaxIdNumber(buyer);
    final boolean isConsolidation = chekckIsConsolidation(consolidationMark);
    final boolean isB2C = isB2C(buyer);
    final boolean isOutput = isOutput(typeCode);
    final boolean isEmptyOrValidTaxIdNumber = isB2C || isValidTaxIdNumber;
    boolean checkBuyerIsConsolidation;
    checkBuyerIsConsolidation =
        isConsolidation // 若彙加
            ? isB2C // 填 0000000000 or 留空
            : isEmptyOrValidTaxIdNumber; // 填 0000000000 or 留空 or 有效統編

    if (StringUtils.length(buyer) <= 10) { // 限10字元內
      if (isOutput) { // 銷項
        // 銷項出口
        if (isOutputExport(typeCode, taxType)) {
          if (StringUtils.equalsAny(zeroTaxMark, "4", "5", "6", "7", "8", "9")) { // 若零稅率註記 = 4~9
            if (isValidTaxIdNumber) // 必填有效統編
            return true;
            else message = "IE0602";

          } else { // 若零稅率註記 = 1~3 ，checkBuyerIsConsolidation
            if (checkBuyerIsConsolidation) return true;
            else message = "IE0603";
          }
        } else { // 非出口格式代號；或 是出口格式代號，且非零稅的，checkBuyerIsConsolidation
          if (checkBuyerIsConsolidation) return true;
          else message = "IE0603";
        }
      } else { // 進項全部、其他
        isValid = isValidTaxIdNumber;
      }
    }

    if (!isValid) {
      ValidatorUtils.addError(
          context,
          message,
          Pair.of(BUYER, buyer),
          Pair.of(TYPE_CODE, typeCode),
          Pair.of(TAX_TYPE, taxType),
          Pair.of(CONSOLIDATION_MARK, consolidationMark),
          Pair.of(ZERO_TAX_MARK, zeroTaxMark),
          Pair.of(INVOICE_STATUS, status));
    }
    return isValid;
  }
}
