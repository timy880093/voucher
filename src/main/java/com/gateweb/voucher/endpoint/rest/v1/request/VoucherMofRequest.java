package com.gateweb.voucher.endpoint.rest.v1.request;

import com.gateweb.voucher.model.VoucherCore;
import com.gateweb.voucher.utils.DateTimeConverter;
import com.gateweb.voucher.utils.voucher.VoucherRequestParser;
import lombok.Data;

@Data
public class VoucherMofRequest extends VoucherRequest {

  private String invoiceNumber;
  private String deductionCode;
  private String typeCode;
  private String invoiceStatus;
  private String invoiceDate;
  private String buyer;
  private String buyerName;
  private String seller;
  private String sellerName;
  private String sendDate;
  private String salesAmount;
  private String zeroTaxSalesAmount;
  private String freeTaxSalesAmount;
  private String taxAmount;
  private String totalAmount;
  private String taxType;
  //  private float taxRate = 0.05f;

  // TODO
  public static final String[] headers =
      ("invoiceNumber,commonNumber,typeCode,invoiceStatus,invoiceDate,buyer,buyerName,seller"
              + ",sellerName,sendDate,salesAmount,zeroTaxSalesAmount,freeTaxSalesAmount,taxAmount"
              + ",totalAmount,taxType,deductionCode,consolidationMark,consolidationQuantity"
              + ",customsClearanceMark,currency,zeroTaxMark,outputDate")
          .split(",");

  @Override
  public VoucherCore toDomain() {
    return VoucherCore.builder()
        .voucherNumber(invoiceNumber)
        .typeCode(typeCode)
        .status(invoiceStatus)
        .voucherDate(invoiceDate)
        .buyer(VoucherRequestParser.parseBuyer(buyer))
        .buyerName(buyerName)
        .seller(seller)
        .sellerName(sellerName)
        .salesAmount(VoucherRequestParser.parseAmount(salesAmount))
        .zeroTaxSalesAmount(VoucherRequestParser.parseAmount(zeroTaxSalesAmount))
        .freeTaxSalesAmount(VoucherRequestParser.parseAmount(freeTaxSalesAmount))
        .taxAmount(VoucherRequestParser.parseAmount(taxAmount))
        .totalAmount(VoucherRequestParser.parseAmount(totalAmount))
        .taxType(taxType)
        .taxRate(VoucherRequestParser.parseTaxRate(taxType))
        .deductionCode(
            VoucherRequestParser.parseDeductionCode(deductionCode, typeCode)) // 若表內沒填，預設 1
        .voucherYearMonth(DateTimeConverter.toEvenYearMonth(invoiceDate))
        //                .uid(uid)
        //        .remark1(businessUnit)
        //        .remark2(relateNumber)
        //        .remark3(source)
        //        .remark4(null)
        //        .remark5(null)
        .build();
  }

  @Override
  public String parseOwner() {
    return VoucherRequestParser.parseOwner(typeCode, buyer, seller);
  }
}
