//package com.gateweb.voucher.model.entity;
//
//import com.gateweb.voucher.model.VoucherCore;
//import com.gateweb.voucher.utils.voucher.VoucherLogic;
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import javax.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import org.apache.commons.lang3.StringUtils;
//
//@Data
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//@Table(name = "voucher")
//@Entity
//public class VoucherEntity {
//
//  @Id
//  @GeneratedValue(strategy = GenerationType.IDENTITY)
//  private long id;
//
//  private String key;
//  private String owner;
//  private String filingYearMonth;
//  private String voucherYearMonth;
//  private String voucherNumber;
//  private String exportNumber;
//  private String typeCode;
//  private int status;
//  private String voucherDate;
//  private String buyer;
//  private String buyerName;
//  private String seller;
//  private String sellerName;
//  private int taxType;
//  private BigDecimal taxRate;
//  private BigDecimal salesAmount;
//  private BigDecimal taxAmount;
//  private BigDecimal zeroTaxSalesAmount;
//  private BigDecimal freeTaxSalesAmount;
//  private BigDecimal totalAmount;
//  private Integer deductionCode;
//  private String consolidationMark;
//  private Integer consolidationQuantity;
//  private String currency;
//  private Integer customsClearanceMark;
//  private Integer zeroTaxMark;
//  private String exportDate;
//  private int creatorId;
//  private LocalDateTime createDate;
//  private int modifierId;
//  private LocalDateTime modifyDate;
//  private long logId;
//  private String logIdHistory;
//  private String uid;
//  private String remark1;
//  private String remark2;
//  private String remark3;
//  private String remark4;
//  private String remark5;
//
//  public static VoucherEntity fromCore(VoucherCore v) {
//    return VoucherEntity.builder()
//        .key(v.getKey())
//        .owner(v.getOwner())
//        .filingYearMonth(v.getFilingYearMonth())
//        .voucherYearMonth(v.getVoucherYearMonth())
//        .voucherDate(VoucherLogic.parseDate(v.getVoucherDate()))
//        .voucherNumber(v.getVoucherNumber())
//        .exportNumber(v.getExportNumber())
//        .typeCode(v.getTypeCode())
//        .status(VoucherLogic.parseStatus(v.getStatus()))
//        .buyer(v.getBuyer())
//        .buyerName(v.getBuyerName())
//        .seller(v.getSeller())
//        .sellerName(v.getSellerName())
//        .taxType(VoucherLogic.parseTaxType(v.getTaxType()))
//        .taxRate(new BigDecimal(v.getTaxRate()))
//        .salesAmount(new BigDecimal(v.getSalesAmount()))
//        .taxAmount(new BigDecimal(v.getTaxAmount()))
//        .zeroTaxSalesAmount(new BigDecimal(v.getZeroTaxSalesAmount()))
//        .freeTaxSalesAmount(new BigDecimal(v.getFreeTaxSalesAmount()))
//        .totalAmount(new BigDecimal(v.getTotalAmount()))
//        .deductionCode(VoucherLogic.parseDeductionCode(v.getDeductionCode()))
//        .consolidationMark(v.getConsolidationMark())
//        .consolidationQuantity(Integer.getInteger(v.getConsolidationQuantity()))
//        .currency(v.getCurrency())
//        .customsClearanceMark(Integer.getInteger(v.getCustomsClearanceMark()))
//        .zeroTaxMark(Integer.getInteger(v.getZeroTaxMark()))
//        .exportDate(VoucherLogic.parseDate(v.getExportDate()))
//        .creatorId(v.getCreatorId())
//        .createDate(LocalDateTime.now())
//        .modifierId(v.getModifierId())
//        .modifyDate(LocalDateTime.now())
//        .logId(v.getLogId())
//        .logIdHistory(String.valueOf(v.getLogId())) // default , 後面補上
//        .uid(v.getUid())
//        .remark1(v.getRemark1())
//        .remark2(v.getRemark2())
//        .remark3(v.getRemark3())
//        .remark4(v.getRemark4())
//        .remark5(v.getRemark5())
//        .build();
//  }
//
//  String getTrackKey() {
//    return StringUtils.joinWith("_", voucherYearMonth, voucherNumber, typeCode);
//  }
//}
