package com.gateweb.voucher.model.validate.constraint;

import static com.gateweb.voucher.model.dto.VoucherColumns.*;
import static com.gateweb.voucher.utils.voucher.VoucherLogic.*;

import com.gateweb.voucher.model.VoucherCore;
import com.gateweb.voucher.model.validate.annotation.TaxAmount;
import com.gateweb.voucher.utils.ValidatorUtils;
import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.data.util.Pair;

public class TaxAmountValidator implements ConstraintValidator<TaxAmount, VoucherCore> {

  /** 實現驗證內容 */
  @Override
  public boolean isValid(final VoucherCore v, ConstraintValidatorContext context) {

    boolean isValid = false;
    String message = "";
    final String taxType = v.getTaxType();
    final String typeCode = v.getTypeCode();
    final String buyer = v.getBuyer();
    final String voucherNumber = v.getInvoiceNumber();
    final String consolidationMark = v.getConsolidationMark();

    //    final boolean ignoreCheckTax = VoucherLogic.ignoreCheckTax(v.getCheckTax());
    //    if (ignoreCheckTax) return true; // 若不檢查稅額，return true
    if (!ValidatorUtils.isNumberAll(
        v.getTaxAmount(), v.getSalesAmount(), v.getConsolidationQuantity())) {
      // 交給其他檢查
      return true;
    }

    final BigDecimal consolidationQuantityDigit = new BigDecimal(v.getConsolidationQuantity());
    final BigDecimal taxAmountDigit = new BigDecimal(v.getTaxAmount());
    final BigDecimal salesAmountDigit = new BigDecimal(v.getSalesAmount());

    final boolean isTaxAmountZero = isAmountZero(taxAmountDigit);
    final boolean isTaxAmountGeneral = checkTaxAmountWithGeneral(taxAmountDigit, salesAmountDigit);
    final boolean isPerTaxAmountLessThan500 =
        chekckIsConsolidation(consolidationMark)
            ? taxAmountDigit
                    .divide(consolidationQuantityDigit, RoundingMode.HALF_UP)
                    .compareTo(new BigDecimal(500))
                <= 0
            : taxAmountDigit.compareTo(new BigDecimal(500)) <= 0;

    // 使用divide方法必須判斷被除值!=0
    // 需要在 convert 處判斷 status = 作廢時，金額全部轉 0 嗎?
    if (isCancel(v.getStatus())) {
      if (isTaxAmountZero) return true;
      else message = "IE1109";
    }

    if (isTaxableOrMixedTax(taxType)) {
      // 應稅 or 混合稅時 且 稅額不為 0 的TaxType
      switch (typeCode) {
        case "22":
          isValid =
              isValidInvoiceNumber(voucherNumber)
                  ? isTaxAmountZero
                  : isTaxAmountZero || isTaxAmountGeneral;
          if (!isValid) message = "IE1102";
          break;
        case "21":
        case "25":
        case "28":
          isValid = isTaxAmountZero || isTaxAmountGeneral;
          if (!isValid) message = "IE1104";
          break;
        case "26":
          isValid = isTaxAmountGeneral && isPerTaxAmountLessThan500; // 判斷 一般稅額 & 單張稅<=500
          if (!isValid) message = "IE1103";
          break;
        case "27":
          isValid =
              isValidInvoiceNumber(voucherNumber)
                  ? isTaxAmountZero
                  : isTaxAmountZero || (isTaxAmountGeneral && isPerTaxAmountLessThan500);
          if (!isValid) message = "IE1104";
          break;
        case "31":
        case "35":
          if (chekckIsConsolidation(consolidationMark)) {
            isValid = isTaxAmountZero;
          } else {
            isValid = isB2C(buyer) ? isTaxAmountZero : isTaxAmountGeneral;
          }
          if (!isValid) message = "IE1105";
          break;
        case "36":
          if (chekckIsConsolidation(consolidationMark)) {
            isValid = isTaxAmountZero || isTaxAmountGeneral;
          } else {
            isValid = isB2C(buyer) ? isTaxAmountZero : isTaxAmountGeneral;
          }
          if (!isValid) message = "IE1106";
          break;
        case "32":
        case "37":
        case "38":
          isValid = isTaxAmountZero;
          if (!isValid) message = "IE1107";
          break;
        default: // 23、24、29，33、34
          isValid = isTaxAmountGeneral;
          if (!isValid) message = "IE1101";
      }
    } else { // 零稅、免稅
      isValid = isTaxAmountZero;
      if (!isValid) message = "IE1108";
    }

    if (!isValid) {
      ValidatorUtils.addError(
          context,
          message,
          Pair.of(TAX_TYPE, taxType),
          Pair.of(TYPE_CODE, typeCode),
          Pair.of(BUYER, buyer),
          Pair.of(VOUCHER_NUMBER, voucherNumber),
          Pair.of(CONSOLIDATION_MARK, consolidationMark),
          Pair.of(CONSOLIDATION_QUANTITY, v.getConsolidationQuantity()),
          Pair.of(SALES_AMOUNT, v.getSalesAmount()),
          Pair.of(TAX_AMOUNT, v.getTaxAmount()));
    }
    return isValid;
  }
}
