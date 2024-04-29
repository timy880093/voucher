package com.gateweb.voucher.model;

import com.gateweb.voucher.endpoint.rest.v1.request.VoucherExtra;
import com.gateweb.voucher.model.validate.annotation.*;
import com.gateweb.voucher.model.validate.type.*;
import com.gateweb.voucher.utils.DateTimeConverter;
import com.gateweb.voucher.utils.voucher.VoucherLogic;
import java.util.UUID;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@InvoiceAndCommonNum(groups = {TypeInput.class, TypeOutput.class})
@InvoiceDate(
    message = "IE0502",
    groups = {TypeInput.class, TypeOutput.class})
@Buyer.List({
  @Buyer(
      message = "IE0601",
      groups = {TypeInput.class}),
  @Buyer(
      message = "IE0602",
      groups = {TypeOutput.class})
})
@BuyerName.List({
  @BuyerName(
      message = "IE0701",
      groups = {TypeInput.class}),
  @BuyerName(
      message = "IE0702",
      groups = {TypeOutput.class})
})
@Seller.List({
  @Seller(
      message = "IE0801",
      groups = {TypeOutput.class}),
  @Seller(
      message = "IE0802",
      groups = {Type26.class, Type28.class, Type29.class}),
  @Seller(
      message = "IE0803",
      groups = {Type25.class}),
  @Seller(
      message = "IE0804",
      groups = {Type22.class, Type27.class}),
  @Seller(
      message = "IE0805",
      groups = {Type21.class, Type23.class, Type24.class})
})
@SellerName.List({
  @SellerName(
      message = "IE0901",
      groups = {TypeInput.class}),
  @SellerName(
      message = "IE0902",
      groups = {TypeOutput.class})
})
@SalesAmount(
    message = "IE1001",
    groups = {TypeInput.class, TypeOutput.class})
@TaxAmount(
    message = "Unexpected TaxAmount Validation",
    groups = {TypeInput.class, TypeOutput.class})
@TotalAmount(
    message = "IE1201",
    groups = {TypeInput.class, TypeOutput.class})
@FreeTaxSalesAmount(
    message = "IE1401",
    groups = {TypeInput.class, TypeOutput.class})
@ZeroTaxSalesAmount(
    message = "IE1501",
    groups = {TypeInput.class, TypeOutput.class})
@DeductionCode(
    message = "Unexpected DeductionCode Validation",
    groups = {TypeInput.class})
@ConsolidationQuantity(
    message = "IE1801",
    groups = {Type22.class, Type25.class, Type31.class, Type32.class, Type35.class, Type36.class})
// 銷項零稅出口
@CustomsClearanceMark(
    message = "Unexpected CustomsClearanceMark Validation",
    groups = {Type31.class, Type32.class, Type35.class, Type36.class})
@ZeroTaxMark(
    message = "Unexpected ZeroTaxMark Validation",
    groups = {Type31.class, Type32.class, Type35.class, Type36.class})
@OutputDate(
    message = "Unexpected OutputDate Validation",
    groups = {Type31.class, Type32.class, Type35.class, Type36.class})
public class VoucherCore {
  private String key;
  private String owner;

  @Pattern(message = "IE2501", regexp = "^1\\d{2}(0[1-9]|1[0-2])$")
  private String filingYearMonth; // YYYMM

  @Pattern(message = "IE2501", regexp = "^1\\d{2}(0[1-9]|1[0-2])$")
  private String voucherYearMonth; // YYYMM

  @Pattern(
      message = "IE0101",
      regexp = "^([A-Z]{2}[0-9]{8})$",
      groups = {
        Type21.class,
        Type26.class,
        Type23.class,
        Type31.class,
        Type32.class,
        Type35.class,
        Type33.class
      })
  @Size(
      message = "IE0102",
      max = 0,
      groups = {Type28.class, Type29.class})
  @Pattern(
      message = "IE0103",
      regexp = "^([A-Z]{2}[0-9]{8}|)$",
      groups = {Type22.class, Type25.class, Type27.class, Type24.class, Type37.class, Type38.class})
  @Pattern(message = "IE0106", regexp = "^((?![A-Z]{2}[0-9]{8})\\w){0,10}$", groups = Type36.class)
  @Pattern(
      message = "IE0109",
      regexp = "^([A-Z]{2}[0-9]{8}|((?![A-Z]{2}[0-9]{0,8})\\w){0,10})$",
      groups = Type34.class)
  // 34 可能有 32 的發票號碼、36 的其他憑證號碼
  @Size(
      message = "IE0108",
      max = 10,
      groups = {TypeInput.class, TypeOutput.class})
  private String invoiceNumber;

  private String commonNumber;

  private String typeCode;

  @Pattern(
      message = "IE0403",
      groups = {TypeInput.class},
      regexp = "^(開立.*|2)$")
  @Pattern(
      message = "IE0401",
      groups = {
        Type31.class,
        Type32.class,
        Type33.class,
        Type34.class,
        Type36.class,
        Type37.class,
        Type38.class
      },
      regexp = "^((開立|作廢).*|2|3)$")
  @Pattern(
      message = "IE0402",
      groups = {Type35.class},
      regexp = "^((開立|作廢|註銷).*|2|3|4)$")
  private String status;

  @IsoDate(
      message = "IE0501",
      groups = {TypeInput.class, TypeOutput.class})
  private String voucherDate;

  private String buyer;
  private String buyerName;
  private String seller;
  private String sellerName;

  @Pattern(
      message = "IE1301",
      regexp = "^(應.*|1)$",
      groups = {Type29.class})
  @Pattern(
      message = "IE1302",
      regexp = "^((應|免|混合|混和).*|1|3|9)$",
      groups = {Type28.class})
  @Pattern(
      message = "IE1304",
      regexp = "^((應|零|免|混合|混和).*|1|2|3|9)$",
      groups = {
        Type21.class, Type22.class, Type25.class, Type26.class, Type27.class, Type23.class,
        Type24.class, Type31.class, Type32.class, Type35.class, Type36.class, Type33.class,
        Type34.class
      })
  @Pattern(
      message = "IE1305",
      regexp = "^((特.*|1)[-_](25|15|2|1|5)|應.*|1|免.*|3)$",
      groups = {Type37.class, Type38.class})
  private String taxType;

  @Pattern(
      message = "taxRate error",
      regexp = "^(0|0.01|0.02|0.05|0.15|0.25)$",
      groups = {TypeInput.class, TypeOutput.class})
  private String taxRate;

  @Pattern(
      message = "salesAmount error",
      regexp = "^[0-9]+(|[.][0-9]+)$",
      groups = {TypeInput.class, TypeOutput.class})
  private String salesAmount;

  @Pattern(
      message = "taxAmount error",
      regexp = "^[0-9]+(|[.][0-9]+)$",
      groups = {TypeInput.class, TypeOutput.class})
  private String taxAmount;

  @Pattern(
      message = "zeroTaxSalesAmount error",
      regexp = "^[0-9]+(|[.][0-9]+)$",
      groups = {TypeInput.class, TypeOutput.class})
  private String zeroTaxSalesAmount;

  @Pattern(
      message = "freeTaxSalesAmount error",
      regexp = "^[0-9]+(|[.][0-9]+)$",
      groups = {TypeInput.class, TypeOutput.class})
  private String freeTaxSalesAmount;

  @Pattern(
      message = "totalAmount error",
      regexp = "^[0-9]+(|[.][0-9]+)$",
      groups = {TypeInput.class, TypeOutput.class})
  private String totalAmount;

  @Size(
      message = "IE1602",
      max = 0,
      groups = {TypeOutput.class})
  private String deductionCode;

  @Size(
      message = "IE1701",
      max = 0,
      groups = {
        Type21.class,
        Type28.class,
        Type23.class,
        Type24.class,
        Type29.class,
        Type37.class,
        Type33.class,
        Type34.class,
        Type38.class
      })
  @Pattern(
      message = "IE1702",
      regexp = "^(A|)$",
      groups = {Type22.class, Type31.class, Type32.class, Type35.class, Type36.class})
  @Pattern(
      message = "IE1703",
      regexp = "^(A|B|)$",
      groups = {Type25.class})
  @Pattern(
      message = "IE1704",
      regexp = "^(A)$",
      groups = {Type26.class, Type27.class})
  private String consolidationMark;

  private String consolidationQuantity;

  @Size(
      message = "IE1902",
      max = 0,
      groups = {TypeInput.class, Type33.class, Type34.class, Type37.class, Type38.class})
  private String customsClearanceMark;

  @Pattern(
      message = "IE2001",
      regexp =
          "^(TWD|USD|HKD|GBP|AUD|CAD|SGD|CHF|JPY|ZAR|SEK|NZD|THB|PHP|IDR|EUR|KRW|VND|MYR|CNY)$",
      groups = {TypeInput.class, TypeOutput.class})
  private String currency;

  @Size(
      message = "IE2102",
      max = 0,
      groups = {TypeInput.class, Type33.class, Type34.class, Type37.class, Type38.class})
  private String zeroTaxMark;

  @Size(
      message = "IE2202",
      max = 0,
      groups = {TypeInput.class, Type33.class, Type34.class, Type37.class, Type38.class})
  private String outputDate;

  private int creatorId;
  private int modifierId;
  private long logId;
  //  private String logIdHistory;
  private String uid; // 彈性

  @Size(
      message = "IE2301",
      max = 30,
      groups = {TypeInput.class, TypeOutput.class})
  private String remark1; // 彈性

  @Size(
      message = "IE2401",
      max = 30,
      groups = {TypeInput.class, TypeOutput.class})
  private String remark2; // 彈性

  @Size(
      message = "IE2601",
      max = 30,
      groups = {TypeInput.class, TypeOutput.class})
  private String remark3; // 彈性

  private String remark4; // 彈性
  private String remark5; // 彈性

  public VoucherCore combine(VoucherExtra extra) {
    setKey(genKey());
    // 若號碼為空，要隨機 gen 號碼，避免日後沒有 key 可供查詢
    setInvoiceNumber(
        StringUtils.defaultIfBlank(
            invoiceNumber, "gw" + UUID.randomUUID().toString().substring(0, 8)));
    setCommonNumber(StringUtils.defaultIfBlank(commonNumber, ""));
    setVoucherYearMonth(DateTimeConverter.toEvenYearMonth(voucherDate));
    setCurrency(StringUtils.defaultIfBlank(currency, "TWD"));
    setOwner(extra.getOwner());
    setCreatorId(extra.getUserId());
    setModifierId(extra.getUserId());
    setLogId(extra.getLogId());
    setFilingYearMonth(StringUtils.defaultIfBlank(getFilingYearMonth(), extra.getYearMonth()));

    return this;
  }

  String genKey() {
    final String inputOrOutput = VoucherLogic.isInput(typeCode) ? "input" : "output";
    final String invoiceOrAllowance = VoucherLogic.isInvoice(typeCode) ? "invoice" : "allowance";
    final String allowanceUid = VoucherLogic.isInvoice(typeCode) ? "" : uid;
    return String.join(
        "|",
        inputOrOutput,
        invoiceOrAllowance,
        voucherYearMonth.substring(0, 3),
        invoiceNumber,
        commonNumber == null ? "" : commonNumber,
        allowanceUid);
  }
}
