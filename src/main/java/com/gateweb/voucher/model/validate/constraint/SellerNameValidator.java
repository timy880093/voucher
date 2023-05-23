package com.gateweb.voucher.model.validate.constraint;

import static com.gateweb.voucher.model.dto.VoucherColumns.*;

import com.gateweb.voucher.model.VoucherCore;
import com.gateweb.voucher.model.validate.annotation.SellerName;
import com.gateweb.voucher.utils.ValidatorUtils;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.util.Pair;

public class SellerNameValidator implements ConstraintValidator<SellerName, VoucherCore> {

  @Override
  public boolean isValid(VoucherCore v, ConstraintValidatorContext context) {

    final String sellerName = v.getSellerName();
    boolean isValid = StringUtils.length(sellerName) <= 60;

    if (!isValid) {
      ValidatorUtils.addError(context, Pair.of(SELLER_NAME, sellerName));
    }
    return isValid;
  }
}
