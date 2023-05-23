package com.gateweb.voucher.model.validate.constraint;

import static com.gateweb.voucher.model.dto.VoucherColumns.*;

import com.gateweb.voucher.model.VoucherCore;
import com.gateweb.voucher.model.validate.annotation.ConsolidationQuantity;
import com.gateweb.voucher.utils.ValidatorUtils;
import com.gateweb.voucher.utils.voucher.VoucherLogic;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.util.Pair;

public class ConsolidationQuantityValidator
    implements ConstraintValidator<ConsolidationQuantity, VoucherCore> {

  /** 實現驗證內容 */
  @Override
  public boolean isValid(final VoucherCore v, ConstraintValidatorContext context) {

    boolean isValid;
    String message = "";
    final String consolidationQuantity = v.getConsolidationQuantity();
    final String consolidationMark = v.getConsolidationMark();

    if (VoucherLogic.chekckIsConsolidation(consolidationMark)) { // 彙加
      // 數量需為數字且長度 <= 4，但不可 = 0
      isValid = VoucherLogic.checkIsValidQuantity(consolidationQuantity);
      if (!isValid) message = "IE1801";

    } else { // 單張
      isValid =
          StringUtils.isBlank(consolidationQuantity)
              || StringUtils.equalsAny(consolidationQuantity, "0", "1"); // 否則必須為空或0
      if (!isValid) message = "IE1802";
    }

    if (!isValid) {
      ValidatorUtils.addError(
          context,
          message,
          Pair.of(CONSOLIDATION_QUANTITY, consolidationQuantity),
          Pair.of(CONSOLIDATION_MARK, consolidationMark));
    }
    return isValid;
  }
}
