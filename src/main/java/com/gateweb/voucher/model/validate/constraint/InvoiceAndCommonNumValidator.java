package com.gateweb.voucher.model.validate.constraint;

import static com.gateweb.voucher.model.dto.VoucherColumns.*;

import com.gateweb.voucher.model.VoucherCore;
import com.gateweb.voucher.model.validate.annotation.InvoiceAndCommonNum;
import com.gateweb.voucher.utils.ValidatorUtils;
import com.gateweb.voucher.utils.voucher.VoucherLogic;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;

public class InvoiceAndCommonNumValidator
    implements ConstraintValidator<InvoiceAndCommonNum, VoucherCore> {

  private static Logger logger = LoggerFactory.getLogger(InvoiceAndCommonNumValidator.class);

  /** 初始參數,獲取註解中的值 */
  //    @Override
  //    public void initialize(final InvoiceAndCommonNum constraintAnnotation) {
  //        invoiceNumber = constraintAnnotation.invoiceNumber();
  //        commonNumber = constraintAnnotation.commonNumber();
  //        taxType = constraintAnnotation.taxType();
  //        typeCode = constraintAnnotation.typeCode();
  //        customsClearanceMark = constraintAnnotation.customsClearanceMark();
  //    }

  /** 實現驗證內容 */
  @Override
  public boolean isValid(final VoucherCore ie, ConstraintValidatorContext context) {

    boolean isValid = false;
    String message = context.getDefaultConstraintMessageTemplate();

    final String invoiceNumberStr = ie.getInvoiceNumber();
    final String commonNumberStr = ie.getCommonNumber();
    final String taxTypeStr = ie.getTaxType();
    final String typeCodeStr = ie.getTypeCode();
    final String customsClearanceMarkStr = ie.getCustomsClearanceMark();

    final boolean isInvoiceNumOnly =
        StringUtils.isNotBlank(invoiceNumberStr) && StringUtils.isBlank(commonNumberStr);
    final boolean isCommonNumOnly =
        StringUtils.isNotBlank(commonNumberStr) && StringUtils.isBlank(invoiceNumberStr);

    if (isInvoiceNumOnly) {
      return true; // 若只有發票號碼，不檢驗
    }

    try {
      if (StringUtils.length(commonNumberStr) <= 16) { // 出口
        switch (typeCodeStr) {
          case "22":
          case "27":
          case "24":
          case "37":
          case "38":
          case "34":
            // FIXME 34 只有發票號碼 (可能是折讓 32 的發票號碼、也可能折讓 36 的其他憑證號碼)
            // 只有發票號碼 or 只有其他憑證號碼 10 碼
            if ((isCommonNumOnly && VoucherLogic.isValidOtherCertificate10(commonNumberStr))
                || isInvoiceNumOnly) {
              isValid = true;
            } else {
              message = "IE0201";
            }
            break;
          case "25":
            // 只有發票號碼 or 只有公用事業號碼 10 碼
            if ((isCommonNumOnly && VoucherLogic.isValidPublicUtilities10(commonNumberStr))
                || isInvoiceNumOnly) { // 公用事業10碼
              isValid = true;
            } else {
              message = "IE0202";
            }
            break;
          case "21":
          case "23":
          case "26":
          case "33":
            // 只有發票號碼
            if (isInvoiceNumOnly) {
              isValid = true;
            } else {
              message = "IE0205";
            }
            break;
          case "28":
          case "29":
            if (isCommonNumOnly
                && VoucherLogic.isValidCustomsCollection14(commonNumberStr)) { // 公用事業10碼
              isValid = true;
            } else {
              message = "IE0206";
            }
            break;
          case "31":
          case "32":
          case "35":
          case "36": // 其他憑證號碼放在 invoiceNumber
            if (VoucherLogic.checkIsZeroTax(taxTypeStr)) { // 出口
              if (StringUtils.equals(customsClearanceMarkStr, "1")) { // 非經海關，14碼出口文件
                if (VoucherLogic.isValidExportCertificate14(commonNumberStr)) {
                  isValid = true;
                } else {
                  message = "IE0203";
                }
              } else if (StringUtils.equals(customsClearanceMarkStr, "2")) { // 經海關，16碼出口報單
                if (VoucherLogic.isValidExportDeclaration16(commonNumberStr)) {
                  isValid = true;
                } else {
                  message = "IE0204";
                }
              }
            } else { // 非出口
              if (isInvoiceNumOnly) {
                isValid = true;
              } else {
                message = "IE0205";
              }
            }
            break;
        }
      }
    } catch (Exception e) {
      logger.error(
          "InvoiceNumber = {} , CommonNumber = {} ,Validate error: {} ",
          ie.getInvoiceNumber(),
          ie.getCommonNumber(),
          e.getMessage());
    }

    if (!isValid) {
      ValidatorUtils.addError(
          context,
          message,
          Pair.of(INVOICE_NUMBER, invoiceNumberStr),
          Pair.of(COMMON_NUMBER, commonNumberStr));
    }
    return isValid;
  }
}
