//package com.gateweb.voucher.usecase;
//
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.mock;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.gateweb.voucher.component.LockProvider;
//import com.gateweb.voucher.endpoint.rest.v1.request.VoucherDefaultRequest;
//import com.gateweb.voucher.endpoint.rest.v1.request.VoucherExtra;
//import com.gateweb.voucher.model.dto.ErrorInfo;
//import com.gateweb.voucher.model.dto.LogStatus;
//import com.gateweb.voucher.model.entity.VoucherLogEntity;
//import com.gateweb.voucher.service.LogService;
//import com.gateweb.voucher.service.VoucherService;
//import com.gateweb.voucher.service.VoucherServiceTest;
//import java.util.ArrayList;
//import java.util.List;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.core.io.ClassPathResource;
//
//// @SpringBootTest
//class ImportVoucherTest {
//  private static ImportVoucher instance;
//  private static ObjectMapper objectMapper;
//  private static LockProvider lockProvider;
//  private static LogService logService;
//
//  @BeforeAll
//  static void init() {
//    objectMapper = new ObjectMapper();
//    lockProvider = mock(LockProvider.class);
//    logService = mock(LogService.class);
//    final VoucherService voucherService = VoucherServiceTest.mockVoucherService();
//    instance = new ImportVoucher(lockProvider, voucherService, logService, objectMapper);
//  }
//
//  @AfterAll
//  static void destory() {}
//
//  @Test
//  void test_importData() throws Exception {
//    final ClassPathResource resource = new ClassPathResource("/example/import-data.json");
//    final List<VoucherDefaultRequest> requestData =
//        objectMapper.readValue(
//            resource.getInputStream(), new TypeReference<List<VoucherDefaultRequest>>() {});
//    final VoucherExtra extra = mockExtra();
//    Mockito.doNothing().when(lockProvider).lock(anyString());
//    Mockito.doNothing().when(lockProvider).unlock(anyString());
//    Mockito.doNothing().when(logService).saveProcessing(any(), any());
//    Mockito.doNothing().when(logService).updateSuccess(any());
//    Mockito.doNothing().when(logService).updateValidateError(any(), any());
//    Mockito.doNothing().when(logService).updateException(any(), any());
//
//    instance.importData(requestData, extra, false);
//  }
//
//  @Test
//  void test_execute() throws Exception {
//    final ClassPathResource resource = new ClassPathResource("/example/import-data.json");
//    final List<VoucherDefaultRequest> requestData =
//        objectMapper.readValue(
//            resource.getInputStream(), new TypeReference<List<VoucherDefaultRequest>>() {});
//    final VoucherExtra extra = mockExtra();
//    final VoucherLogEntity log = VoucherLogEntity.create(extra, LogStatus.PROCESSING);
//    Mockito.doNothing().when(lockProvider).lock(anyString());
//    Mockito.doNothing().when(lockProvider).unlock(anyString());
//    Mockito.when(logService.saveProcessing(any(), any())).thenReturn(log);
//    Mockito.doNothing().when(logService).updateSuccess(any());
//    Mockito.doNothing().when(logService).updateValidateError(any(), any());
//    Mockito.doNothing().when(logService).updateException(any(), any());
//    Mockito.when(instance.execute(any(), any(), anyBoolean())).thenReturn(new ArrayList<>());
//
//    final List<ErrorInfo> execute = instance.execute(requestData, extra, false);
//
//    Assertions.assertThat(execute).isEmpty();
//  }
//
//  VoucherExtra mockExtra() {
//    return VoucherExtra.builder()
//        .owner("24549210")
//        .userId(-1)
//        .yearMonth("11202")
//        .action("test")
//        .logId(-1L)
//        .build();
//  }
//}
