package com.gateweb.voucher.endpoint.rest.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gateweb.voucher.endpoint.rest.v1.request.*;
import com.gateweb.voucher.endpoint.rest.v1.response.VoucherResponse;
import com.gateweb.voucher.model.dto.ErrorInfo;
import com.gateweb.voucher.usecase.ImportVoucher;
import com.gateweb.voucher.utils.ResponseGenerator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
public class V1VoucherImportController {

  private final ImportVoucher importVoucher;
  private final ObjectMapper objectMapper;

  public V1VoucherImportController(ImportVoucher importVoucher, ObjectMapper objectMapper) {
    this.importVoucher = importVoucher;
    this.objectMapper = objectMapper;
  }

  @PostMapping(value = "/v1/voucher/file/{type}")
  public <V extends VoucherRequest> ResponseEntity<?> importFile(
      HttpServletRequest request,
      @PathVariable("type") String type,
      @RequestParam("yearMonth") String yearMonth,
      @RequestParam("userId") Integer userId,
      @RequestParam("file") MultipartFile file,
      @RequestParam(value = "validate", defaultValue = "true") Boolean validate,
      @RequestHeader(
              value = "Accept",
              required = false,
              defaultValue = MediaType.APPLICATION_JSON_VALUE)
          String accept)
      throws Exception {
    final VoucherExtra extra =
        VoucherExtra.builder()
            .action("import-" + type)
            .yearMonth(yearMonth)
            .userId(userId)
            .filename(file.getOriginalFilename())
            .build();
    final FileTypes fileTypes = FileTypes.fromType(type);
    final List<VoucherRequest> requestData =
        importVoucher.readFile(file.getResource(), fileTypes, extra);
    final ResponseEntity<?> response = importVoucher(request, requestData, extra, validate);
    return ResponseGenerator.parseContentType(response, accept);
  }

  @PostMapping(
      value = "/v1/voucher",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> importJson(
      HttpServletRequest request,
      @RequestParam(value = "userId") Integer userId,
      @RequestParam(value = "validate", defaultValue = "true") Boolean validate,
      @RequestBody List<VoucherDefaultRequest> voucherRequests) {
    final VoucherExtra voucherExtra =
        VoucherExtra.builder().action("import-default-json").userId(userId).build();
    return importVoucher(request, voucherRequests, voucherExtra, validate);
  }

  @PostMapping(
      value = "/v1/voucher",
      consumes = MediaType.APPLICATION_XML_VALUE,
      produces = MediaType.APPLICATION_XML_VALUE)
  public ResponseEntity<?> importXml(
      HttpServletRequest request,
      @RequestParam(value = "userId") Integer userId,
      @RequestParam(value = "validate", defaultValue = "true") Boolean validate,
      @RequestBody List<VoucherDefaultRequest> voucherRequests) {
    final VoucherExtra voucherExtra =
        VoucherExtra.builder().action("import-default-xml").userId(userId).build();
    return importVoucher(request, voucherRequests, voucherExtra, validate);
  }

  // TODO test?
  @PostMapping(value = "/v1/voucher", consumes = MediaType.TEXT_PLAIN_VALUE)
  public ResponseEntity<?> importCsv(
      HttpServletRequest request,
      @RequestParam(value = "userId") Integer userId,
      @RequestParam(value = "validate", defaultValue = "true") Boolean validate,
      @RequestBody List<VoucherDefaultRequest> voucherRequests,
      @RequestHeader(
              value = "Accept",
              required = false,
              defaultValue = MediaType.APPLICATION_JSON_VALUE)
          String accept) {
    final VoucherExtra voucherExtra =
        VoucherExtra.builder().action("import-default-csv").userId(userId).build();
    final ResponseEntity<?> response =
        importVoucher(request, voucherRequests, voucherExtra, validate);
    return ResponseGenerator.parseContentType(response, accept);
  }

  private <V extends VoucherRequest> ResponseEntity<?> importVoucher(
      HttpServletRequest request,
      List<V> voucherRequests,
      VoucherExtra voucherExtra,
      boolean validate) {
    log.info("Request : {}?{}", request.getRequestURI(), request.getQueryString());
    ResponseEntity<String> response;
    try {
      final List<ErrorInfo> errors = importVoucher.run(voucherRequests, voucherExtra, validate);
      if (errors.isEmpty()) {
        response = ResponseEntity.ok("ok");
        log.info("Response ok : {}", response);
      } else {
        response = ResponseEntity.badRequest().body(objectMapper.writeValueAsString(errors));
        log.warn("Response failed : {}", response);
      }
    } catch (Exception e) {
      response = ResponseEntity.badRequest().body(e.getMessage());
      log.error("Response error : {}", response);
    }
    return response;
  }

  @ExceptionHandler
  public ResponseEntity<VoucherResponse> exception(Exception e) {
    final VoucherResponse response = VoucherResponse.error(e.getMessage(), null);
    return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(response);
  }

  /*
  import excel
  import csv
  import json
  import xml
   */

}
