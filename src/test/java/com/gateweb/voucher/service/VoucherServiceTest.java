package com.gateweb.voucher.service;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.gateweb.voucher.component.TrackCacheProvider;
import com.gateweb.voucher.endpoint.rest.v1.request.FileTypes;
import com.gateweb.voucher.endpoint.rest.v1.request.VoucherDefaultRequest;
import com.gateweb.voucher.endpoint.rest.v1.request.VoucherExtra;
import com.gateweb.voucher.endpoint.rest.v1.request.VoucherRequest;
import com.gateweb.voucher.model.VoucherCore;
import com.gateweb.voucher.model.dto.ErrorInfo;
import com.gateweb.voucher.model.entity.VoucherEntity;
import com.gateweb.voucher.repository.*;
import com.gateweb.voucher.utils.voucher.VoucherLogic;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import javax.validation.Validation;
import javax.validation.Validator;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.io.ClassPathResource;

public class VoucherServiceTest {
  private static VoucherService instance;
  private static CompanyDao companyDao;
  private static InvoiceMainDao invoiceMainDao;
  private static VoucherRepository voucherRepository;
  private static VoucherDataTableRepository voucherDataTableRepository;
  private static VoucherDao voucherDao;
  private static TrackCacheProvider trackCacheProvider;
  private static ObjectMapper objectMapper;
  private static XmlMapper xmlMapper;

  @BeforeAll
  static void init() {
    companyDao = mock(CompanyDao.class);
    invoiceMainDao = mock(InvoiceMainDao.class);
    trackCacheProvider = mock(TrackCacheProvider.class);
    voucherRepository = mock(VoucherRepository.class);
    voucherDataTableRepository = mock(VoucherDataTableRepository.class);
    voucherDao = mock(VoucherDao.class);
    final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    instance =
        new VoucherService(
            voucherRepository,
            voucherDataTableRepository,
            voucherDao,
            companyDao,
            invoiceMainDao,
            trackCacheProvider,
            validator);

    //    instance = mockVoucherService();
    objectMapper = new ObjectMapper();
    xmlMapper = new XmlMapper();
  }

  @AfterAll
  static void destroy() {}

  @Test
  void test_readFile_csv() throws Exception {
    final ClassPathResource resource =
        new ClassPathResource("/example/import-gw.csv", getClass().getClassLoader());
    final List<VoucherRequest> results = instance.readFile(resource, FileTypes.GW);
    Assertions.assertThat(results).isNotEmpty();
  }

  @Test
  void test_readFile_xlsx() throws Exception {
    final ClassPathResource resource = new ClassPathResource("/example/import-gw.xlsx");
    final List<VoucherRequest> results = instance.readFile(resource, FileTypes.GW);
    Assertions.assertThat(results).isNotEmpty();
  }

  @Test
  void test_convert_from_excel() throws Exception {
    final ClassPathResource resource = new ClassPathResource("/example/import-gw.xlsx");
    final List<VoucherRequest> requestData = instance.readFile(resource, FileTypes.GW);
    final VoucherExtra extra = mockExtra();

    final List<VoucherCore> voucherCores = instance.convert(requestData, extra);

    Assertions.assertThat(voucherCores).isNotEmpty();
  }

  @Test
  void test_convert_from_json() throws IOException {
    final ClassPathResource resource = new ClassPathResource("/example/import-data.json");
    final List<VoucherDefaultRequest> requestData =
        objectMapper.readValue(
            resource.getInputStream(), new TypeReference<List<VoucherDefaultRequest>>() {});
    final VoucherExtra extra = mockExtra();

    final List<VoucherCore> voucherCores = instance.convert(requestData, extra);
    System.out.println(voucherCores.get(0));
    Assertions.assertThat(voucherCores).isNotEmpty();
  }

  @Test
  void test_convert_from_xml() throws IOException {
    final ClassPathResource resource = new ClassPathResource("/example/import-data.xml");
    final List<VoucherDefaultRequest> requestData =
        xmlMapper.readValue(
            resource.getInputStream(), new TypeReference<List<VoucherDefaultRequest>>() {});
    final VoucherExtra extra = mockExtra();

    final List<VoucherCore> voucherCores = instance.convert(requestData, extra);
    System.out.println(voucherCores.get(0));
    Assertions.assertThat(voucherCores).isNotEmpty();
  }

  @Test
  void test_validate() throws IOException {
    final VoucherExtra extra = mockExtra();
    final List<VoucherCore> voucherCores = mockVoucherCores(extra);

    final List<ErrorInfo> errorInfos = instance.validate(voucherCores);

    Assertions.assertThat(errorInfos).isEmpty();
  }

  @Test
  void test_checkLogic() throws IOException {
    final String businessNo = "24549210";
    final VoucherExtra extra = mockExtra();
    final List<VoucherCore> voucherCores = mockVoucherCores(extra);
    voucherCores.forEach(v -> v.setOwner(businessNo));
    Mockito.when(companyDao.findBusinessNos(anyInt()))
        .thenReturn(Collections.singleton(businessNo));
    Mockito.when(invoiceMainDao.findExistInvoiceNumbers(any(), any()))
        .thenReturn(Collections.emptySet());
    Mockito.when(trackCacheProvider.getTrackMap()).thenReturn(mockTrackMap(voucherCores));

    final List<ErrorInfo> errorInfos = instance.checkLogic(voucherCores, extra);

    Assertions.assertThat(errorInfos).isEmpty();
  }

  @Test
  void test_save() throws IOException {
    final VoucherExtra extra = mockExtra();
    final List<VoucherCore> voucherCores = mockVoucherCores(extra);
    final List<VoucherEntity> voucherEntities =
        voucherCores.stream().map(VoucherEntity::fromCore).collect(Collectors.toList());
    Mockito.when(voucherDao.findByRepeatKey(any())).thenReturn(voucherEntities);
    Mockito.doNothing().when(voucherDao).deleteByIds(anyList());
    Mockito.when(voucherRepository.saveAll(anyList())).thenReturn(voucherEntities);

    final List<VoucherEntity> results = instance.save(voucherCores);
    Assertions.assertThat(results).isNotEmpty();
  }

  //  public static VoucherService mockVoucherService() {
  //    final VoucherRepository voucherRepository = mock(VoucherRepository.class);
  //    final VoucherDao voucherDao = mock(VoucherDao.class);
  //    final CompanyDao companyDao = mock(CompanyDao.class);
  //    final InvoiceMainDao invoiceMainDao = mock(InvoiceMainDao.class);
  //    final TrackCacheProvider trackCacheProvider = mock(TrackCacheProvider.class);
  //    final Validator validator = mock(Validator.class);
  //    return new VoucherService(
  //        voucherRepository, voucherDataTableRepository, voucherDao, companyDao, invoiceMainDao,
  // trackCacheProvider, validator);
  //  }

  VoucherExtra mockExtra() {
    return VoucherExtra.builder()
        .owner("24549210")
        .userId(-1)
        .yearMonth("11202")
        .action("test")
        .logId(-1L)
        .build();
  }

  Map<String, Boolean> mockTrackMap(List<VoucherCore> voucherCores) {
    return voucherCores.stream()
        .distinct()
        .filter(v -> VoucherLogic.isTrackExist(v.getTypeCode()))
        .collect(
            Collectors.toMap(
                v ->
                    StringUtils.joinWith(
                        "_",
                        v.getVoucherYearMonth(),
                        v.getVoucherNumber().substring(0, 2),
                        v.getTypeCode()),
                v -> true,
                (older, newer) -> older, // 可選, 用於合併重複值
                HashMap::new));
  }

  private List<VoucherCore> mockVoucherCores(VoucherExtra extra) throws IOException {
    final ClassPathResource resource = new ClassPathResource("/example/import-data.json");
    final List<VoucherDefaultRequest> requestData =
        objectMapper.readValue(
            resource.getInputStream(), new TypeReference<List<VoucherDefaultRequest>>() {});
    return instance.convert(requestData, extra);
  }
}
