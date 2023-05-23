package com.gateweb.voucher.service;

import static com.gateweb.voucher.model.dto.VoucherColumns.*;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;

import com.gateweb.voucher.component.TrackCacheProvider;
import com.gateweb.voucher.endpoint.rest.v1.request.*;
import com.gateweb.voucher.model.VoucherCore;
import com.gateweb.voucher.model.dto.ErrorInfo;
import com.gateweb.voucher.model.entity.VoucherEntity;
import com.gateweb.voucher.model.validate.type.*;
import com.gateweb.voucher.repository.*;
import com.gateweb.voucher.utils.*;
import com.gateweb.voucher.utils.voucher.VoucherLogic;
import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import javax.persistence.criteria.Predicate;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class VoucherService {

  private final VoucherRepository voucherRepository;
  private final VoucherDataTableRepository voucherDataTableRepository;
  private final VoucherDao voucherDao;
  private final CompanyDao companyDao;
  private final InvoiceMainDao invoiceMainDao;
  private final TrackCacheProvider trackCacheProvider;
  private final Validator validator;

  public VoucherService(
      VoucherRepository voucherRepository,
      VoucherDataTableRepository voucherDataTableRepository,
      VoucherDao voucherDao,
      CompanyDao companyDao,
      InvoiceMainDao invoiceMainDao,
      TrackCacheProvider trackCacheProvider,
      Validator validator) {
    this.voucherRepository = voucherRepository;
    this.voucherDataTableRepository = voucherDataTableRepository;
    this.voucherDao = voucherDao;
    this.companyDao = companyDao;
    this.invoiceMainDao = invoiceMainDao;
    this.trackCacheProvider = trackCacheProvider;
    this.validator = validator;
  }

  private static final String[] GW_DOWNLOAD_CSV_COLUMN = {
    "voucherNumber",
    "exportNumber",
    "filingYearMonth",
    "typeCode",
    "status",
    "voucherDate",
    "buyer",
    "buyerName",
    "seller",
    "sellerName",
    "salesAmount",
    "taxAmount",
    "totalAmount",
    "taxType",
    "freeTaxSalesAmount",
    "zeroTaxSalesAmount",
    "deductionCode",
    "consolidationMark",
    "consolidationQuantity",
    "customsClearanceMark",
    "currency",
    "zeroTaxMark",
    "exportDate"
  };

  public List<ErrorInfo> validate(List<VoucherCore> voucherCores) {
    final List<ErrorInfo> errors = new ArrayList<>();
    for (VoucherCore voucherCore : voucherCores) {
      final Set<ConstraintViolation<Object>> violations;
      switch (voucherCore.getTypeCode()) {
          /* 進項-發票相關(格式代號21、22、25、26、27、28) */
        case ("21"):
          violations = validator.validate(voucherCore, TypeInput.class, Type21.class);
          break;
        case ("22"):
          violations = validator.validate(voucherCore, TypeInput.class, Type22.class);
          break;
        case ("25"):
          violations = validator.validate(voucherCore, TypeInput.class, Type25.class);
          break;
        case ("26"):
          violations = validator.validate(voucherCore, TypeInput.class, Type26.class);
          break;
        case ("27"):
          violations = validator.validate(voucherCore, TypeInput.class, Type27.class);
          break;
        case ("28"):
          violations = validator.validate(voucherCore, TypeInput.class, Type28.class);
          break;
          /* 進項-退回或折讓相關(格式代號23、24、29) */
        case ("23"):
          violations = validator.validate(voucherCore, TypeInput.class, Type23.class);
          break;
        case ("24"):
          violations = validator.validate(voucherCore, TypeInput.class, Type24.class);
          break;
        case ("29"):
          violations = validator.validate(voucherCore, TypeInput.class, Type29.class);
          break;
          /* 銷項-發票相關(格式代號31、32、35、36、37) */
        case ("31"):
          violations = validator.validate(voucherCore, TypeOutput.class, Type31.class);
          break;
        case ("32"):
          violations = validator.validate(voucherCore, TypeOutput.class, Type32.class);
          break;
        case ("35"):
          violations = validator.validate(voucherCore, TypeOutput.class, Type35.class);
          break;
        case ("36"):
          violations = validator.validate(voucherCore, TypeOutput.class, Type36.class);
          break;
        case ("37"):
          violations = validator.validate(voucherCore, TypeOutput.class, Type37.class);
          break;
          /* 銷項-退回或折讓相關(格式代號33、34、38) */
        case ("33"):
          violations = validator.validate(voucherCore, TypeOutput.class, Type33.class);
          break;
        case ("34"):
          violations = validator.validate(voucherCore, TypeOutput.class, Type34.class);
          break;
        case ("38"):
          violations = validator.validate(voucherCore, TypeOutput.class, Type38.class);
          break;
        default:
          violations = new HashSet<>();
      }

      final List<ErrorInfo> objects =
          violations.isEmpty()
              ? new ArrayList<>()
              : ValidatorUtils.toError(voucherCore.getKey(), violations);
      errors.addAll(objects);
    }
    return errors;
  }

  public List<ErrorInfo> checkLogic(List<VoucherCore> voucherCores, VoucherExtra extra) {
    final List<ErrorInfo> errors = new ArrayList<>();
    final Set<String> businessNos = companyDao.findBusinessNos(extra.getUserId());
    final Set<String> existNumbers = getExistNumbers(voucherCores);
    for (VoucherCore v : voucherCores) {
      addIfError(errors, checkOwner(v, businessNos));
      addIfError(errors, checkTrack(v));
      addIfError(errors, checkInvoiceNumberUnique(v, existNumbers));
    }
    return errors;
  }

  public <V extends VoucherRequest> List<VoucherCore> convert(
      List<V> requestData, VoucherExtra extra) {
    return requestData.stream().map(v -> v.toDomain().combine(extra)).collect(Collectors.toList());
  }

  @Transactional(rollbackFor = Exception.class)
  public List<VoucherEntity> save(List<VoucherCore> voucherCores) {
    final List<VoucherEntity> voucherEntities =
        voucherCores.stream().map(VoucherEntity::fromCore).collect(Collectors.toList());
    final List<VoucherEntity> repeatList = voucherDao.findByRepeatKey(voucherEntities);
    final List<VoucherEntity> newEntities = parseEntities(voucherEntities, repeatList);
    voucherDao.deleteByIds(
        repeatList.stream().map(VoucherEntity::getId).collect(Collectors.toList()));
    return voucherRepository.saveAll(newEntities);
  }

  private List<VoucherEntity> parseEntities(
      List<VoucherEntity> voucherEntities, List<VoucherEntity> repeatList) {
    final Map<String, VoucherEntity> repeatMap =
        repeatList.stream()
            .collect(
                Collectors.toMap(VoucherEntity::getKey, v -> v, (oldValue, newValue) -> newValue));
    voucherEntities.forEach(
        it -> {
          final VoucherEntity history = repeatMap.getOrDefault(it.getKey(), null);
          if (history == null) return;
          // LogIdHistory : (之前的,現在的)
          it.setLogIdHistory(history.getLogIdHistory() + "," + it.getLogIdHistory());
          it.setCreatorId(history.getCreatorId());
          it.setCreateDate(history.getCreateDate());
        });
    return voucherEntities;
  }

  private Set<String> getExistNumbers(List<VoucherCore> voucherCores) {
    final List<VoucherCore> eguiVoucherCores =
        voucherCores.stream()
            .filter(
                v ->
                    VoucherLogic.isEguiInvoice(v.getTypeCode())
                        && VoucherLogic.isValidInvoiceNumber(v.getVoucherNumber()))
            .collect(Collectors.toList());
    final Map<String, Set<String>> outputInvoiceMap =
        eguiVoucherCores.stream()
            .collect(
                groupingBy(
                    v -> DateTimeConverter.toEvenYearMonth(v.getVoucherDate()),
                    Collectors.mapping(VoucherCore::getVoucherNumber, toSet())));
    return outputInvoiceMap.keySet().stream()
        .map(ym -> invoiceMainDao.findExistInvoiceNumbers(ym, outputInvoiceMap.get(ym)))
        .flatMap(Set::stream)
        .collect(toSet());
  }

  ErrorInfo checkOwner(VoucherCore v, Set<String> businessNos) {
    return !VoucherLogic.isOwnerValid(businessNos, v.getOwner())
        ? ErrorInfo.create(
            v.getKey(),
            "owner error : 此單據不屬於匯入者",
            Pair.of(TYPE_CODE, v.getTypeCode()),
            Pair.of(BUYER, v.getBuyer()),
            Pair.of(SELLER, v.getSeller()))
        : null;
  }

  ErrorInfo checkTrack(VoucherCore v) {
    return !VoucherLogic.isTrackValid(
            v.getVoucherNumber(),
            v.getVoucherYearMonth(),
            v.getTypeCode(),
            trackCacheProvider.getTrackMap())
        ? ErrorInfo.create(
            v.getKey(),
            "invoiceNumber track error : 字軌不符",
            Pair.of(INVOICE_NUMBER, v.getVoucherNumber()),
            Pair.of(VOUCHER_DATE, v.getVoucherDate()),
            Pair.of(TYPE_CODE, v.getTypeCode()))
        : null;
  }

  ErrorInfo checkInvoiceNumberUnique(VoucherCore v, Set<String> existNumbers) {
    return !VoucherLogic.isInvoiceNumberUnique(existNumbers, v.getVoucherNumber(), v.getTypeCode())
        ? ErrorInfo.create(
            v.getKey(),
            "invoiceNumber error : 與 Egui 重複",
            Pair.of(INVOICE_NUMBER, v.getVoucherNumber()))
        : null;
  }

  void addIfError(List<ErrorInfo> errorInfos, ErrorInfo errorInfo) {
    if (errorInfo != null) errorInfos.add(errorInfo);
  }

  public <V extends VoucherRequest> List<V> readFile(Resource resource, FileTypes type)
      throws Exception {
    switch (StringUtils.substringAfterLast(resource.getFilename(), ".").toLowerCase()) {
      case "xlsx":
      case "xls":
      case "xlsm":
        return readExcel(resource, type);
      case "csv":
        return readCsv(resource, type);
      default:
        throw new Exception("extension error , only accept [csv,xlsx,xls,xlsm]");
    }
  }

  <V extends VoucherRequest> List<V> readExcel(Resource resource, FileTypes type) throws Exception {
    switch (type) {
      case GW:
        return (List<V>)
            ExcelReader.read(
                resource.getInputStream(), VoucherGwRequest.headers, VoucherGwRequest.class);
      case MOF:
        return (List<V>)
            ExcelReader.read(
                resource.getInputStream(), VoucherMofRequest.headers, VoucherMofRequest.class);
      default:
        throw new Exception("type error , only accept [gw,mof]");
    }
  }

  <V extends VoucherRequest> List<V> readCsv(Resource resource, FileTypes type) throws Exception {
    switch (type) {
      case GW:
        return (List<V>)
            CsvReader.readByPosition(resource.getInputStream(), VoucherGwRequest.class, 1);
      case MOF:
        return (List<V>)
            CsvReader.readByPosition(resource.getInputStream(), VoucherMofRequest.class, 1);
      default:
        throw new Exception("type error , only accept [gw,mof]");
    }
  }

  public List<String> deleteReturnKeys(List<Long> ids) {
    final List<String> keys = voucherDao.findKeyByIds(ids);
    if (!keys.isEmpty()) voucherDao.deleteByIds(ids);
    return keys;
  }

  public DataTablesOutput<VoucherEntity> search(
      DataTablesInput input, Set<String> yearMonths, Set<String> businessNos) {
    final Specification<VoucherEntity> conditionSpec = getConditionSpec(yearMonths, businessNos);
    return voucherDataTableRepository.findAll(input, conditionSpec, conditionSpec);
  }

  Specification<VoucherEntity> getConditionSpec(Set<String> yearMonths, Set<String> businessNos) {
    return (root, query, criteriaBuilder) -> {
      final Predicate ownerIn = root.get("owner").in(businessNos);
      final Predicate yearMonthIn = root.get("yearMonth").in(yearMonths);
      return criteriaBuilder.and(yearMonthIn, ownerIn);
    };
  }

  public List<VoucherEntity> findDataBySeek(
      Collection<String> yearMonths, Collection<String> businessNos) {
    List<VoucherEntity> entities = new LinkedList<>();
    boolean dataExist = true;
    long id = 0L;
    do {
      final List<VoucherEntity> seekData =
          voucherRepository.findDataBySeek(id, yearMonths, businessNos, 100000);
      if (seekData.isEmpty()) {
        dataExist = false;
      } else {
        entities.addAll(seekData);
        log.debug("process record : {}", entities.size());
        id = seekData.get(seekData.size() - 1).getId();
        log.debug("process last id : {}", id);
      }
    } while (dataExist);
    log.debug("total record : {}", entities.size());
    return entities;
  }

  public String writeCsvString(List<VoucherEntity> voucherEntities) throws IOException {
    final List<String[]> arrayList =
        voucherEntities.stream()
            .map(
                d -> {
                  if (d.getStatus() == 3 || d.getStatus() == 4) {
                    d.setSalesAmount(BigDecimal.ZERO);
                    d.setZeroTaxSalesAmount(BigDecimal.ZERO);
                    d.setFreeTaxSalesAmount(BigDecimal.ZERO);
                    d.setTaxAmount(BigDecimal.ZERO);
                    d.setTotalAmount(BigDecimal.ZERO);
                  }
                  return new String[] {
                    d.getVoucherNumber(),
                    d.getExportNumber(),
                    d.getFilingYearMonth(),
                    d.getTypeCode(),
                    String.valueOf(d.getStatus()),
                    d.getVoucherDate(),
                    d.getBuyer(),
                    d.getBuyerName(),
                    d.getSeller(),
                    d.getSellerName(),
                    AmountParser.formatThousands(d.getSalesAmount()),
                    AmountParser.formatThousands(d.getTaxAmount()),
                    AmountParser.formatThousands(d.getTotalAmount()),
                    String.valueOf(d.getTaxType()),
                    AmountParser.formatThousands(d.getFreeTaxSalesAmount()),
                    AmountParser.formatThousands(d.getZeroTaxSalesAmount()),
                    String.valueOf(d.getDeductionCode()),
                    d.getConsolidationMark(),
                    String.valueOf(d.getConsolidationQuantity()),
                    String.valueOf(d.getCustomsClearanceMark()),
                    d.getCurrency(),
                    String.valueOf(d.getZeroTaxMark()),
                    d.getExportDate()
                  };
                })
            .collect(Collectors.toList());
    arrayList.add(0, GW_DOWNLOAD_CSV_COLUMN);
    return CsvWriter.writeToString(arrayList, true);
  }
}
