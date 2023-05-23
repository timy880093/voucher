package com.gateweb.voucher.model.validate.constraint;

import static com.gateweb.voucher.model.dto.VoucherColumns.*;

import com.gateweb.voucher.model.VoucherCore;
import com.gateweb.voucher.model.validate.annotation.BuyerName;
import com.gateweb.voucher.utils.ValidatorUtils;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.util.Pair;

public class BuyerNameValidator implements ConstraintValidator<BuyerName, VoucherCore> {

  /** 實現驗證內容 */
  @Override
  public boolean isValid(final VoucherCore v, ConstraintValidatorContext context) {
    
    final String buyerName = v.getBuyerName();
    boolean isValid = StringUtils.length(buyerName) <= 60;
    
    if (!isValid) {
      ValidatorUtils.addError(context, Pair.of(BUYER_NAME,buyerName));
    }
    return isValid;
  }
}
