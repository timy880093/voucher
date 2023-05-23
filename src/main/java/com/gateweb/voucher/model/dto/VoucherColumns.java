package com.gateweb.voucher.model.dto;

public enum VoucherColumns {
  YEAR_MONTH("yearMonth"),
  INVOICE_NUMBER("invoiceNumber"),
  COMMON_NUMBER("commonNumber"),
  VOUCHER_NUMBER("voucherNumber"),
  EXPORT_NUMBER("exportNumber"),
  TYPE_CODE("typeCode"),
  INVOICE_STATUS("invoiceStatus"),
  VOUCHER_DATE("voucherDate"),
  FILING_YEAR_MONTH("filingYearMonth"),
  BUYER("buyer"),
  BUYER_NAME("buyerName"),
  SELLER("seller"),
  SELLER_NAME("sellerName"),
  TAX_TYPE("taxType"),
  TAX_RATE("taxRate"),
  SALES_AMOUNT("salesAmount"),
  TAX_AMOUNT("salesAmount"),
  TOTAL_AMOUNT("totalAmount"),
  ZERO_TAX_SALES_AMOUNT("zeroTaxSalesAmount"),
  FREE_TAX_SALES_AMOUNT("freeTaxSalesAmount"),
  DEDUCTION_CODE("deductionCode"),
  CONSOLIDATION_MARK("consolidationMark"),
  CONSOLIDATION_QUANTITY("consolidationQuantity"),
  CURRENCY("currency"),
  CUSTOMS_CLEARANCE_MARK("customsClearanceMark"),
  ZERO_TAX_MARK("zeroTaxMark"),
  OUTPUT_DATE("outputDate"),
  EXPORT_DATE("exportDate");

  private String colName;

  VoucherColumns(String colName) {
    this.colName = colName;
  }

  public String colName() {
    return colName;
  }
}
