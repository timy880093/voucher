package com.gateweb.voucher.endpoint.rest.v1.request;

import static com.gateweb.voucher.utils.voucher.VoucherRequestParser.*;
import static com.gateweb.voucher.utils.voucher.VoucherRequestParser.parseOutputNumber;

import com.gateweb.voucher.model.VoucherCore;
import com.gateweb.voucher.utils.DateTimeConverter;
import com.gateweb.voucher.utils.voucher.VoucherRequestParser;
import lombok.Data;

@Data
public class VoucherDefaultRequest extends VoucherRequest {

  private String invoiceNumber;
  private String commonNumber;
  private String typeCode;
  private String invoiceStatus;
  private String invoiceDate;
  private String buyer;
  private String buyerName;
  private String seller;
  private String sellerName;
  private String salesAmount;
  private String zeroTaxSalesAmount;
  private String freeTaxSalesAmount;
  private String taxAmount;
  private String totalAmount;
  private String taxType;
  private String deductionCode;
  private String consolidationMark;
  private String consolidationQuantity;
  private String customsClearanceMark;
  private String currency;
  private String zeroTaxMark;
  private String outputDate;
  private String businessUnit;
  private String relateNumber;
  private String yearMonth;
  private String taxRate;
  private String source;
  private String uid;

  public VoucherCore toDomain() {
    return VoucherCore.builder()
        .voucherNumber(parseVoucherNumber(invoiceNumber, commonNumber, typeCode, taxType))
        .exportNumber(parseOutputNumber(commonNumber, typeCode, taxType))
        .typeCode(typeCode)
        .status(parseStatus())
        .voucherDate(invoiceDate)
        .buyer(parseBuyer(buyer))
        .buyerName(buyerName)
        .seller(seller)
        .sellerName(sellerName)
        .salesAmount(parseAmount(salesAmount))
        .zeroTaxSalesAmount(parseAmount(zeroTaxSalesAmount))
        .freeTaxSalesAmount(parseAmount(freeTaxSalesAmount))
        .taxAmount(parseAmount(taxAmount))
        .totalAmount(parseAmount(totalAmount))
        .taxType(taxType)
        .taxRate(parseTaxRate(taxType))
        .deductionCode(deductionCode)
        .consolidationMark(consolidationMark)
        .consolidationQuantity(consolidationQuantity)
        .customsClearanceMark(customsClearanceMark)
        .currency(currency)
        .zeroTaxMark(zeroTaxMark)
        .exportDate(outputDate)
        // 可能 yyyyMM
        .filingYearMonth(parseFilingYearMonth(yearMonth))
        .voucherYearMonth(DateTimeConverter.toEvenYearMonth(invoiceDate))
        .uid(uid)
        .remark1(businessUnit)
        .remark2(relateNumber)
        .remark3(source)
        //        .remark4(null)
        //        .remark5(null)
        .build();
  }

  @Override
  public String parseOwner() {
    return VoucherRequestParser.parseOwner(typeCode, buyer, seller);
  }
  
  String parseStatus(){
    // FIXME api 規格對外 1-開立、2-作廢、3-註銷，這段很難更改
    switch (invoiceStatus){
      case "1":
        return "2";
              case "2":
        return "3";
              case "3":
        return "4";
      default:
        return invoiceStatus;
    }
  }
}
