package com.gateweb.voucher.endpoint.rest.v1.request;

import static com.gateweb.voucher.utils.voucher.VoucherRequestParser.*;

import com.gateweb.voucher.model.VoucherCore;
import com.gateweb.voucher.utils.DateTimeConverter;
import com.gateweb.voucher.utils.voucher.VoucherRequestParser;
import com.opencsv.bean.CsvBindByPosition;
import lombok.Data;

@Data
public class VoucherGwRequest extends VoucherRequest {

  @CsvBindByPosition(position = 0) // 一般Web匯入用此方式，照順序讀
  private String invoiceNumber;

  @CsvBindByPosition(position = 1)
  private String commonNumber;

  @CsvBindByPosition(position = 2)
  private String typeCode;

  @CsvBindByPosition(position = 3)
  private String invoiceStatus;

  @CsvBindByPosition(position = 4)
  private String invoiceDate;

  @CsvBindByPosition(position = 5)
  private String buyer;

  @CsvBindByPosition(position = 6)
  private String buyerName;

  @CsvBindByPosition(position = 7)
  private String seller;

  @CsvBindByPosition(position = 8)
  private String sellerName;

  @CsvBindByPosition(position = 9)
  private String sendDate;

  @CsvBindByPosition(position = 10)
  private String salesAmount;

  @CsvBindByPosition(position = 11)
  private String zeroTaxSalesAmount;

  @CsvBindByPosition(position = 12)
  private String freeTaxSalesAmount;

  @CsvBindByPosition(position = 13)
  private String taxAmount;

  @CsvBindByPosition(position = 14)
  private String totalAmount;

  @CsvBindByPosition(position = 15)
  private String taxType;

  @CsvBindByPosition(position = 16)
  private String deductionCode;

  @CsvBindByPosition(position = 17)
  private String consolidationMark;

  @CsvBindByPosition(position = 18)
  private String consolidationQuantity;

  @CsvBindByPosition(position = 19)
  private String customsClearanceMark;

  @CsvBindByPosition(position = 20)
  private String currency;

  @CsvBindByPosition(position = 21)
  private String zeroTaxMark;

  @CsvBindByPosition(position = 22)
  private String outputDate;
  //  private String yearMonth;
  //  private String taxRate;
  //  private Integer userId;
  public static final String[] headers =
      ("invoiceNumber,commonNumber,typeCode,invoiceStatus,invoiceDate,buyer,buyerName,seller"
              + ",sellerName,sendDate,salesAmount,zeroTaxSalesAmount,freeTaxSalesAmount,taxAmount"
              + ",totalAmount,taxType,deductionCode,consolidationMark,consolidationQuantity"
              + ",customsClearanceMark,currency,zeroTaxMark,outputDate")
          .split(",");

  @Override
  public VoucherCore toDomain() {
    return VoucherCore.builder()
        .invoiceNumber(invoiceNumber)
        .commonNumber(commonNumber)
        .typeCode(typeCode)
        .status(invoiceStatus)
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
        .deductionCode(parseDeductionCode(deductionCode, typeCode)) // 若表內沒填，預設 1
        .consolidationMark(consolidationMark)
        .consolidationQuantity(consolidationQuantity)
        .customsClearanceMark(customsClearanceMark)
        .currency(currency)
        .zeroTaxMark(zeroTaxMark)
        .outputDate(outputDate)
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
