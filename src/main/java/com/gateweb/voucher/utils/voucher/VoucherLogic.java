package com.gateweb.voucher.utils.voucher;

import com.gateweb.voucher.utils.DateTimeConverter;
import com.gateweb.voucher.utils.FileUtils;
import com.gateweb.voucher.utils.ReflectUtils;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unchecked")
public class VoucherLogic {

  private static final Logger logger = LoggerFactory.getLogger(VoucherLogic.class);

  static {
    init();
  }

  public static String[] gwColumnFormat;
  public static String[] mofColumnFormat;
  public static Pattern patternInvoiceNumber;
  public static Pattern patternOtherCertificate10ForExport;
  public static Pattern patternOtherCertificate10;
  public static Pattern patternPublicUtilities10;
  public static Pattern patternCustomsCollection14;
  public static Pattern patternExportCertificate14;
  public static Pattern patternExportDeclaration16;
  public static Pattern patternQuantity;
  public static Pattern patternTwbid;
  public static String[] typeCodeOfTaxAmountZero; // 零稅額的格式代號
  private static final String TWBID_WEIGHT = "12121241";

  /** 初始化驗證參數 */
  public static void init() {
    final Properties prop = FileUtils.readResourceFile("/static/voucher-logic.properties");
    gwColumnFormat = prop.getProperty("format.gw").split(",");
    mofColumnFormat = prop.getProperty("format.mof").split(",");
    patternInvoiceNumber = Pattern.compile(prop.getProperty("invoiceNumber"));
    patternOtherCertificate10ForExport =
        Pattern.compile(prop.getProperty("otherCertificate10ForExport"));
    patternOtherCertificate10 = Pattern.compile(prop.getProperty("otherCertificate10"));
    patternPublicUtilities10 = Pattern.compile(prop.getProperty("publicUtilities10"));
    patternCustomsCollection14 = Pattern.compile(prop.getProperty("customsCollection14"));
    patternExportCertificate14 = Pattern.compile(prop.getProperty("exportCertificate14"));
    patternExportDeclaration16 = Pattern.compile(prop.getProperty("exportDeclaration16"));
    patternQuantity = Pattern.compile(prop.getProperty("quantity"));
    patternTwbid = Pattern.compile(prop.getProperty("twbid"));
    typeCodeOfTaxAmountZero = StringUtils.split(prop.getProperty("twbid"));
  }

  public static final String ADMIN_TAX_ID = "24549210";
  private static final DecimalFormat df = new DecimalFormat("#,##0");

  private static final String[] OUTPUT_TYPECODE =
      new String[] {"31", "32", "33", "34", "35", "36", "37", "38"};
  private static final String[] INPUT_TYPECODE =
      new String[] {"21", "22", "23", "24", "25", "26", "27", "28", "29"};
  private static final String[] INVOICE_TYPECODE =
      new String[] {"21", "22", "25", "26", "27", "28", "31", "32", "35", "36", "37"};
  private static final String[] ALLOWANCE_TYPECODE =
      new String[] {"23", "24", "29", "33", "34", "38"};
  private static final String[] OUTPUT_INVOICE_TYPECODE =
      new String[] {"31", "32", "35", "36", "37"};
  private static final String[] INPUT_INVOICE_TYPECODE =
      new String[] {"21", "22", "25", "26", "27", "28"};
  private static final String[] OUTPUT_ALLOWANCE_TYPECODE = new String[] {"33", "34", "38"};
  private static final String[] INPUT_ALLOWANCE_TYPECODE = new String[] {"23", "24", "29"};
  private static final String[] EXPORT_TYPECODE = new String[] {"31", "32", "35", "36"};
  private static final String[] TRACK_EXIST_TYPECODE =
      new String[] {"21", "22", "25", "26", "27", "31", "32", "35", "37"};

  public static final Pattern patternTaxable = Pattern.compile("^(應.*|1)$");
  public static final Pattern patternSpecial = Pattern.compile("^(特.*|1)[-_](25|15|2|1|5)$");
  public static final Pattern patternMixedTax = Pattern.compile("^((混合|混和).*|9)$");
  public static final Pattern patternZeroTax = Pattern.compile("^(零.*|2)$");
  public static final Pattern patternFreeTax = Pattern.compile("^(免.*|3)$");
  //    public static final Pattern patternZeroTaxAndFreeTax = Pattern.compile("^((零|免).*|2|3)$");
  // date
  //    public static final Pattern patternDate =
  // Pattern.compile("^((((19|20)\\d{2})([-\\/]|)(0?(1|[3-9])|1[012])([-\\/]|)(0?[1-9]|[12]\\d|30))|(((19|20)\\d{2})([-\\/]|)(0?[13578]|1[02])-31)|(((19|20)\\d{2})([-\\/]|)0?2([-\\/]|)(0?[1-9]|1\\d|2[0-8]))|((((19|20)([13579][26]|[2468][048]|0[48]))|(2000))([-\\/]|)0?2([-\\/]|)29))$");

  /* 驗證 typeCode  */

  public static boolean isTypeCodeValid(String typeCode) {
    return isInput(typeCode) || isOutput(typeCode);
  }

  public static boolean isInvoice(String typeCode) {
    return StringUtils.equalsAny(typeCode, INVOICE_TYPECODE);
  }

  public static boolean isAllowance(String typeCode) {
    return StringUtils.equalsAny(typeCode, ALLOWANCE_TYPECODE);
  }

  public static boolean isInput(String typeCode) {
    return StringUtils.equalsAny(typeCode, INPUT_TYPECODE);
  }

  public static boolean isOutput(String typeCode) {
    return StringUtils.equalsAny(typeCode, OUTPUT_TYPECODE);
  }

  public static boolean isEguiInvoice(String typeCode) {
    return "35".equals(typeCode);
  }

  public static boolean isOutputExport(String typeCode, String taxType) {
    return StringUtils.equalsAny(typeCode, EXPORT_TYPECODE) && checkIsZeroTax(taxType);
  }

  public static boolean isIssued(String status) {
    return "2".equals(status) || StringUtils.contains(status, "開立");
  }

  public static boolean isCancel(String status) {
    return "3".equals(status) || StringUtils.contains(status, "作廢");
  }

  public static boolean isVoid(String status) {
    return "4".equals(status) || StringUtils.contains(status, "註銷");
  }

  public static boolean isTrackExist(String typeCode) {
    return StringUtils.equalsAny(typeCode, TRACK_EXIST_TYPECODE);
  }

  /* 驗證 invoiceNumber & commonNumber */

  public static boolean isValidInvoiceNumber(String invoiceNumber) {
    return StringUtils.isNotBlank(invoiceNumber)
        && patternInvoiceNumber.matcher(invoiceNumber).matches();
  }

  public static boolean isValidOtherCertificate10ForExport(String invoiceNumber) {
    return patternOtherCertificate10ForExport.matcher(invoiceNumber).matches();
  }

  public static boolean isValidOtherCertificate10(String commonNumber) {
    return patternOtherCertificate10.matcher(commonNumber).matches();
  }

  public static boolean isValidPublicUtilities10(String commonNumber) {
    return patternPublicUtilities10.matcher(commonNumber).matches();
  }

  public static boolean isValidCustomsCollection14(String commonNumber) {
    return patternCustomsCollection14.matcher(commonNumber).matches();
  }

  public static boolean isValidExportCertificate14(String commonNumber) {
    return patternExportCertificate14.matcher(commonNumber).matches();
  }

  public static boolean isValidExportDeclaration16(String commonNumber) {
    return patternExportDeclaration16.matcher(commonNumber).matches();
  }

  /* 驗證 taxType */

  public static boolean isTaxable(String taxType) {
    return patternTaxable.matcher(taxType).matches();
  }

  public static boolean isTaxableOrMixedTax(String taxType) {
    return isTaxable(taxType) || isMixedTax(taxType);
  }

  public static boolean isSpecialTax(String taxType) {
    return patternSpecial.matcher(taxType).matches();
  }

  public static boolean isMixedTax(String taxType) {
    return patternMixedTax.matcher(taxType).matches();
  }

  public static boolean checkIsMixedTaxWithAmount(String taxType, BigDecimal amount) {
    return isMixedTax(taxType) && amount.compareTo(BigDecimal.ZERO) > 0;
  }

  public static boolean checkIsZeroTax(String taxType) {
    return patternZeroTax.matcher(taxType).matches();
  }

  public static boolean checkIsFreeTax(String taxType) {
    return patternFreeTax.matcher(taxType).matches();
  }

  public static boolean checkIsZeroTaxOrFreeTax(String taxType) {
    return checkIsZeroTax(taxType) || checkIsFreeTax(taxType);
  }

  /* 驗證 consolidation */

  public static boolean chekckIsConsolidation(String consolidationMark) {
    return "A".equals(consolidationMark);
  }

  public static boolean checkIsValidQuantity(String quantity) {
    return patternQuantity.matcher(quantity).matches();
  }

  /* 驗證 buyer & businessNo */
  public static boolean isB2C(String buyer) {
    return StringUtils.isBlank(buyer) || StringUtils.equals(buyer, "0000000000");
  }

  public static boolean checkTaxIdNumber(String businessNo) {
    if (!patternTwbid.matcher(businessNo.trim()).matches()) {
      return false;
    }
    int tmp, sum = 0;
    boolean is7thEq7 = false;
    for (int i = 0; i < 8; i++) {
      tmp = (businessNo.charAt(i) - '0') * (TWBID_WEIGHT.charAt(i) - '0');
      sum += (tmp / 10) + (tmp % 10); // 取出十位數和個位數相加
      if (i == 6 && businessNo.charAt(i) == '7') { // 第七個數是否為七
        is7thEq7 = true;
      }
    }
    return is7thEq7 ? (sum % 5) == 0 || ((sum + 1) % 5) == 0 : (sum % 5) == 0;
  }

  /* 驗證 Amount */

  public static boolean checkTaxAmountWithGeneral(BigDecimal taxAmount, BigDecimal salesAmount) {
    // 相減的絕對值 <= 5
    return checkTaxAmountAndSalesAmount(taxAmount, salesAmount, new BigDecimal("0.05"), 5);
  }

  public static boolean checkTaxAmountAndSalesAmount(
      BigDecimal taxAmount, BigDecimal salesAmount, BigDecimal taxRate, int tolerance) {
    final BigDecimal tax = taxAmount.setScale(0, RoundingMode.HALF_UP).abs();
    final BigDecimal sales = salesAmount.multiply(taxRate).setScale(0, RoundingMode.HALF_UP).abs();
    return tax.subtract(sales).abs().compareTo(new BigDecimal(tolerance)) <= 0; // 相減的絕對值 <= 5
  }

  public static boolean isAmountZero(BigDecimal taxAmount) {
    return taxAmount.compareTo(BigDecimal.ZERO) == 0;
  }

  /**
   * 含稅價，稅額為 0 的格式代號
   *
   * @param typeCode
   * @return
   */
  public static boolean isTaxAmountWithZeroTax(String typeCode) {
    return StringUtils.equalsAny(typeCode, typeCodeOfTaxAmountZero);
  }

  public static boolean isCustomsClearanceMark(String customsClearanceMark) {
    return StringUtils.equalsAny(customsClearanceMark, "1", "2");
  }

  public static boolean isZeroTaxMark(String zeroTaxMark) {
    return StringUtils.equalsAny(zeroTaxMark, "1", "2", "3", "4", "5", "6", "7", "8", "9");
  }

  public static boolean isValidDate(String date) {
    try {
      final LocalDate localDate = DateTimeConverter.toLocalDate(date);
      return localDate.getYear() >= 2000;
    } catch (Exception e) {
      return false;
    }
  }

  public static boolean checkFillingAndOriginalYearMonth(
      String filingYearMonth, String voucherYearMonth, String typeCode, boolean checkFilling) {

    final LocalDate fillingLocalDate = DateTimeConverter.toLocalDate(filingYearMonth);
    final LocalDate originalLocalDate = DateTimeConverter.toLocalDate(voucherYearMonth);
    final Period period = Period.between(originalLocalDate, fillingLocalDate);
    final int monthDiff = period.getMonths();
    if (monthDiff < 0) { // 開票期別不可晚於申報期別
      return false;
    }

    final int filingYearMonthInt = fillingLocalDate.getMonthValue();
    if (isInput(typeCode)) {
      // 進項，可申報10年內發票或折讓
      return monthDiff <= 10 * 12;
    } else {
      if (isAllowance(typeCode)) { // 銷項折讓
        if (checkFilling) { // FIXME PCHOME 客製化，放寬
          return filingYearMonthInt % 2 == 1
              ? monthDiff <= 5 // 單數，按月申報，可申報當月 & 前 2 期
              : monthDiff <= 6; // 雙數，按期申報，可申報當期 & 前 2 期
        } else { // 原始的
          return filingYearMonthInt % 2 == 1
              ? monthDiff <= 2 // 單數，按月申報，可申報當月 & 前 1 期
              : monthDiff <= 3; // 雙數，按期申報，可申報當期 & 前 1 期
        }
      } else { // 銷項發票
        return filingYearMonthInt % 2 == 1 // 單數，按月申報
            ? monthDiff == 0 // 單數，按月申報，只能申報當月
            : monthDiff <= 1; // 雙數，按期申報，只能申報當期
      }
    }
  }

  /* parse param */

  public static String parseNumber(String number) {
    return StringUtils.isNotBlank(number)
        ? NumberUtils.createBigDecimal(number).stripTrailingZeros().toString()
        : "0";
  }

  public static String taxTypeToChiness(String taxType) {
    switch (taxType) {
      case "1":
        return "應稅";
      case "2":
        return "零稅";
      case "3":
        return "免稅";
      case "4":
        return "特種稅";
      case "9":
        return "混合稅";
    }
    return taxType;
  }

  public static String parseTaxType(String taxType) {
    if (StringUtils.equals(taxType, "1") || StringUtils.contains(taxType, "應")) {
      return "1";
    } else if (StringUtils.equals(taxType, "2") || StringUtils.contains(taxType, "零")) {
      return "2";
    } else if (StringUtils.equals(taxType, "3") || StringUtils.contains(taxType, "免")) {
      return "3";
    } else if (StringUtils.startsWith(taxType, "1-") || StringUtils.contains(taxType, "特")) {
      return "4";
    } else if (StringUtils.equals(taxType, "9") || StringUtils.contains(taxType, "混")) {
      return "9";
    }
    return "";
  }

  public static int parseStatus(String value) {
    if (isIssued(value)) return 2;
    else if (isCancel(value)) return 3;
    else if (isVoid(value)) return 4;
    else return -1;
  }

  public static Integer parseDeductionCode(String deductionCode) {
    if (StringUtils.equalsAny(deductionCode, "1", "可扣抵之進貨及費用")) return 1;
    else if (StringUtils.equalsAny(deductionCode, "2", "可扣抵之固定資產")) return 2;
    else if (StringUtils.equalsAny(deductionCode, "3", "不可扣抵之進貨及費用")) return 3;
    else if (StringUtils.equalsAny(deductionCode, "4", "不可扣抵之固定資產")) return 4;
    else {
      if (StringUtils.startsWith(deductionCode, "可扣抵")) return 1;
      else if (StringUtils.startsWith(deductionCode, "不可扣抵")) return 3;
      else return null;
    }
  }

  public static boolean ignoreCheckTax(String checkTax) {
    return StringUtils.equalsAny(checkTax, "false", "F", "否");
  }

  public static boolean isOwnerContains(Set<String> acceptBusinessNos, String businessNo) {
    return acceptBusinessNos.contains(businessNo) || acceptBusinessNos.contains("24549210");
  }

  // 檢驗銷項是否與 GW 關網發票重複 (多筆)
  public static boolean isRepeatWithInvoiceMain(
      String invoiceNumber, String voucherDate, String typeCode, Set<String> repeatNumbers) {
    final String actualYearMonth =
        DateTimeConverter.toEvenYearMonth(voucherDate); // invoiceMain 只取偶數月
    if (!isValidInvoiceNumber(invoiceNumber) // 無效發票號碼不檢查
        || isInput(typeCode) // 進項不檢查
        || isAllowance(typeCode)
        || StringUtils.length(actualYearMonth) != 5) { // 折讓不檢查
      return false;
    }
    return repeatNumbers.contains(invoiceNumber);
  }

  // 檢驗外部發票字軌 true => 有效
  public static boolean isValidTrack(
      String invoiceNumber, String voucherDate, String typeCode, Map<String, Boolean> trackMap) {
    if (!isValidInvoiceNumber(invoiceNumber) // 無效發票號碼不檢查
        || !isValidDate(voucherDate) // 無效發票日期
        || isAllowance(typeCode) // 折讓不檢查
        || !isTrackExist(typeCode) // 若該格式查無可用字軌，代表不需檢查
    ) {
      return true;
    }
    final String track = invoiceNumber.substring(0, 2);
    final String evenYearMonth = DateTimeConverter.toEvenYearMonth(voucherDate);
    return isTrackExist(trackMap, evenYearMonth, track, typeCode);
  }

  public static boolean isTrackExist(
      Map<String, Boolean> trackMap, String yearMonth, String track, String typeCode) {
    final String key = genTrackCacheKey(yearMonth, track, typeCode);
    return trackMap.getOrDefault(key, false);
  }

  public static String genTrackCacheKey(String yearMonth, String track, String typeCode) {
    return StringUtils.joinWith("_", yearMonth, track, typeCode);
  }

  /* ----- Converter ----- */

  public static int parseTaxTypeInt(String taxType) {
    if (isSpecialTax(taxType) || isTaxable(taxType)) {
      return 1;
    } else if (checkIsZeroTax(taxType)) {
      return 2;
    } else if (checkIsFreeTax(taxType)) {
      return 3;
    } else if (isSpecialTax(taxType)) {
      return 4;
    } else if (isMixedTax(taxType)) {
      return 9;
    }
    return -1;
  }

  public static BigDecimal parseCancelAmount(String status, String amount) {
    return isCancel(status) ? BigDecimal.ZERO : new BigDecimal(amount);
  }

  public static String parseDate(String date) {
    return DateTimeConverter.toYYYYMMDD(date);
  }

  //  public static <T extends InvoiceExternalBase> List<T> excelToList(
  //      InputStream fileInput, Class<T> format) throws Exception {
  //    if (format == InvoiceExternalGW.class) {
  //      return AsposeCellUtils.read(fileInput, gwColumnFormat, format);
  //    } else if (format == InvoiceExternalMOF.class) {
  //      return AsposeCellUtils.read(fileInput, mofColumnFormat, format);
  //    }
  //    return null; // 只接受 GW、MOF 兩種格式
  //  }

  public static Object getOriginalValue(String field, Object obj) {
    if (obj == null) {
      return null;
    }
    if (obj instanceof String
        || obj instanceof Integer
        || obj instanceof Float
        || obj instanceof BigDecimal
        || obj instanceof Date
        || obj instanceof LocalDate
        || obj instanceof LocalDateTime
        || obj instanceof Boolean) {
      return obj;
    }
    return ReflectUtils.invokeGetter(obj, field);
  }

  public static List<String> getOutputInvoiceTypeCodes() {
    return new ArrayList<>(Arrays.asList(OUTPUT_INVOICE_TYPECODE));
  }

  public static List<String> getInputInvoiceTypeCodes() {
    return new ArrayList<>(Arrays.asList(INPUT_INVOICE_TYPECODE));
  }

  public static List<String> getOutputAllowanceTypeCodes() {
    return new ArrayList<>(Arrays.asList(OUTPUT_ALLOWANCE_TYPECODE));
  }

  public static List<String> getInputAllowanceTypeCodes() {
    return new ArrayList<>(Arrays.asList(INPUT_ALLOWANCE_TYPECODE));
  }

  public static String[] getDateRange(String yearMonth) {
    final String yyyymm = DateTimeConverter.rocToAd(yearMonth);
    //    final String start = Integer.parseInt(yyyymm) - 1 + "01";
    //    final String end = yyyymm + "31";
    // 10 年內 ~ 未來 1 年內，都算重複
    final String start = Integer.parseInt(yyyymm) - 1000 + "01";
    final String end = Integer.parseInt(yyyymm) + 100 + "31";
    return new String[] {start, end};
  }

  public static boolean isOwnerValid(Set<String> businessNos, String businessNo) {
    // 檢查 source，只能匯自己 or 子公司的資料
    return businessNos.contains(businessNo) || businessNos.contains(ADMIN_TAX_ID);
  }

  public static boolean isTrackValid(
      String invoiceNumber, String yearMonth, String typeCode, Map<String, Boolean> trackMap) {
    // 檢查年度字軌 (invoice_external_track)
    if (!isValidInvoiceNumber(invoiceNumber) // 無效發票號碼不檢查
        || !isTrackExist(typeCode) // 若該格式查無可用字軌，代表不需檢查
        || isAllowance(typeCode) // 折讓不檢查
    ) return true;
    final String track = invoiceNumber.substring(0, 2);
    final String key = StringUtils.joinWith("_", yearMonth, track, typeCode);
    return trackMap.getOrDefault(key, false);
  }

  public static boolean isInvoiceNumberUnique(
      Set<String> existNumbers, String invoiceNumber, String typeCode) {
    // 檢查重號 (invoice_main)
    return !(isEguiInvoice(typeCode)
        && isValidInvoiceNumber(invoiceNumber)
        && existNumbers.contains(invoiceNumber));
  }

  //  public static List<String> getFields(String messageCode) {
  //    switch (messageCode) {
  //      case "IE0101":
  //      case "IE0102":
  //      case "IE0103":
  //      case "IE0105":
  //      case "IE0108":
  //        return Arrays.asList(INVOICE_NUMBER);
  //      case "IE0104":
  //        return Arrays.asList(INVOICE_NUMBER, INVOICE_DATE, TYPE_CODE);
  //      case "IE0106":
  //      case "IE0109":
  //        return Arrays.asList(INVOICE_NUMBER, TYPE_CODE);
  //      case "IE0107":
  //        return Arrays.asList(
  //            INVOICE_NUMBER, TYPE_CODE, CUSTOMS_CLEARANCE_MARK, TAX_TYPE, ZERO_TAX_MARK);
  //      case "IE0201":
  //      case "IE0202":
  //        return Arrays.asList(INVOICE_NUMBER, COMMOM_NUMBER);
  //      case "IE0203":
  //      case "IE0204":
  //        return Arrays.asList(COMMOM_NUMBER, TYPE_CODE, CUSTOMS_CLEARANCE_MARK, TAX_TYPE);
  //      case "IE0205":
  //      case "IE0206":
  //        return Arrays.asList(COMMOM_NUMBER);
  //      case "IE0301":
  //        return Arrays.asList(TYPE_CODE);
  //      case "IE0401":
  //        return Arrays.asList(INVOICE_STATUS);
  //      case "IE0402":
  //        return Arrays.asList(INVOICE_STATUS, TYPE_CODE);
  //      case "IE0501":
  //        return Arrays.asList(INVOICE_DATE);
  //      case "IE0502":
  //        return Arrays.asList(INVOICE_DATE, YEAR_MONTH);
  //      case "IE0601":
  //        return Arrays.asList(BUYER, TYPE_CODE);
  //      case "IE0602":
  //        return Arrays.asList(BUYER, TYPE_CODE, CUSTOMS_CLEARANCE_MARK, TAX_TYPE, ZERO_TAX_MARK);
  //      case "IE0603":
  //        return Arrays.asList(BUYER, CONSOLIDATION_MARK);
  //      case "IE0604":
  //        return Arrays.asList(BUYER, TYPE_CODE);
  //      case "IE0701":
  //        return Arrays.asList(BUYER_NAME, TYPE_CODE);
  //      case "IE0702":
  //        return Arrays.asList(BUYER_NAME, TYPE_CODE, BUYER, CONSOLIDATION_MARK);
  //      case "IE0801":
  //      case "IE0802":
  //      case "IE0803":
  //      case "IE0805":
  //      case "IE0806":
  //        return Arrays.asList(SELLER, TYPE_CODE);
  //      case "IE0804":
  //        return Arrays.asList(SELLER, TYPE_CODE, COMMOM_NUMBER, CONSOLIDATION_MARK);
  //      case "IE0901":
  //        return Arrays.asList(SELLER_NAME, TYPE_CODE, CONSOLIDATION_MARK);
  //      case "IE0902":
  //        return Arrays.asList(SELLER_NAME, TYPE_CODE);
  //      case "IE1001":
  //        return Arrays.asList(SALES_AMOUNT, TAX_TYPE);
  //      case "IE1101":
  //        return Arrays.asList(TAX_AMOUNT, SALES_AMOUNT);
  //      case "IE1102":
  //        return Arrays.asList(TAX_AMOUNT, SALES_AMOUNT, INVOICE_NUMBER, TYPE_CODE);
  //      case "IE1103":
  //      case "IE1104":
  //        return Arrays.asList(TAX_AMOUNT, SALES_AMOUNT, TYPE_CODE);
  //      case "IE1105":
  //      case "IE1106":
  //        return Arrays.asList(TAX_AMOUNT, SALES_AMOUNT, TYPE_CODE, CONSOLIDATION_MARK, BUYER);
  //      case "IE1107":
  //        return Arrays.asList(TAX_AMOUNT, TYPE_CODE);
  //      case "IE1201":
  //        return Arrays.asList(
  //            TOTAL_AMOUNT, SALES_AMOUNT, ZERO_TAX_MARK, FREE_TAX_SALES_AMOUNT, TAX_AMOUNT);
  //      case "IE1301":
  //      case "IE1302":
  //      case "IE1303":
  //      case "IE1304":
  //      case "IE1305":
  //        return Arrays.asList(TAX_TYPE, TYPE_CODE);
  //      case "IE1401":
  //        return Arrays.asList(FREE_TAX_SALES_AMOUNT, TAX_TYPE);
  //      case "IE1501":
  //        return Arrays.asList(ZERO_TAX_SALES_AMOUNT, TAX_TYPE);
  //      case "IE1601":
  //        return Arrays.asList(DEDUCTION_CODE, TYPE_CODE, TAX_TYPE);
  //      case "IE1602":
  //        return Arrays.asList(DEDUCTION_CODE, TYPE_CODE);
  //      case "IE1701":
  //      case "IE1702":
  //      case "IE1703":
  //      case "IE1704":
  //        return Arrays.asList(CONSOLIDATION_MARK, TYPE_CODE);
  //      case "IE1801":
  //      case "IE1802":
  //        return Arrays.asList(CONSOLIDATION_QUANTITY, CONSOLIDATION_MARK, TYPE_CODE);
  //      case "IE1901":
  //      case "IE1902":
  //        return Arrays.asList(CUSTOMS_CLEARANCE_MARK, TYPE_CODE, TAX_TYPE);
  //      case "IE2001":
  //        return Arrays.asList(CURRENCY);
  //      case "IE2101":
  //      case "IE2102":
  //        return Arrays.asList(ZERO_TAX_MARK, TYPE_CODE, CUSTOMS_CLEARANCE_MARK, TAX_TYPE);
  //      case "IE2201":
  //      case "IE2202":
  //        return Arrays.asList(OUTPUT_DATE, TYPE_CODE, CUSTOMS_CLEARANCE_MARK, TAX_TYPE);
  //      case "IE2301":
  //        return Arrays.asList(BUSINESS_UNIT);
  //      case "IE2401":
  //        return Arrays.asList(RELATE_NUMBER);
  //      case "IE2501":
  //        return Arrays.asList(YEAR_MONTH);
  //      case "IE2601":
  //        return Arrays.asList(SOURCE);
  //    }
  //    return new ArrayList<>();
  //  }

  //  public static Object getOriginalValues(String messageCode, Object originalData) {
  //    List<String> fieldList = new ArrayList<>(getFields(messageCode));
  //    InvoiceExternalGW newData = new InvoiceExternalGW();
  //
  //    fieldList.forEach(
  //        field -> {
  //          if (originalData == null) {
  //            ReflectUtils.invokeSetter(newData, field, null);
  //            return;
  //          }
  //          if (originalData instanceof String
  //              || originalData instanceof Integer
  //              || originalData instanceof Float
  //              || originalData instanceof BigDecimal
  //              || originalData instanceof Date
  //              || originalData instanceof LocalDate
  //              || originalData instanceof LocalDateTime
  //              || originalData instanceof Boolean
  //              || originalData instanceof Timestamp) {
  //            ReflectUtils.invokeSetter(newData, field, originalData);
  //            return;
  //          }
  //          Object value = ReflectUtils.invokeGetter(originalData, field);
  //          ReflectUtils.invokeSetter(newData, field, value);
  //        });
  //    try {
  //      return OBJECT_MAPPER.writeValueAsString(newData);
  //    } catch (JsonProcessingException e) {
  //      logger.error(e.getMessage());
  //      return "";
  //    }
  //  }

}
