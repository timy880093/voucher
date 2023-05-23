package com.gateweb.voucher.endpoint.rest.v1;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gateweb.voucher.endpoint.rest.v1.response.VoucherResponse;
import com.gateweb.voucher.model.entity.VoucherEntity;
import com.gateweb.voucher.usecase.DeleteVoucher;
import com.gateweb.voucher.usecase.DownloadVoucher;
import com.gateweb.voucher.usecase.SearchVoucher;
import com.gateweb.voucher.utils.ResponseGenerator;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class V1VoucherOtherController {

  private final DeleteVoucher deleteVoucher;
  private final DownloadVoucher downloadVoucher;
  private final SearchVoucher searchVoucher;
  private final ObjectMapper objectMapper;

  public V1VoucherOtherController(
      DeleteVoucher deleteVoucher,
      DownloadVoucher downloadVoucher,
      SearchVoucher searchVoucher,
      ObjectMapper objectMapper) {
    this.deleteVoucher = deleteVoucher;
    this.downloadVoucher = downloadVoucher;
    this.searchVoucher = searchVoucher;
    this.objectMapper = objectMapper;
  }

  @DeleteMapping(value = "/v1/voucher", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> delete(
      HttpServletRequest request,
      @RequestParam("ids") String ids,
      @RequestParam("userId") Integer userId) {
    log.info("Request : {}/{}", request.getRequestURI(), request.getQueryString());
    final List<String> keys = deleteVoucher.execute(ids, userId);
    final String message = keys.isEmpty() ? "id not exists, not working" : "delete ok : " + keys;
    log.info("Response : {}", message);
    return ResponseEntity.ok(message);
  }

  // 使用 Get 可能因 URL 過長導致解析失敗
  @PostMapping(value = "/v1/voucher/search")
  public DataTablesOutput<VoucherEntity> search(
      HttpServletRequest request, @RequestBody JsonNode body) {
    log.info("Request : {}/{}", request.getRequestURI(), request.getQueryString());

    final String yearMonth = body.get("yearMonth").asText();
    final String businessNos = body.get("businessNos").asText();
    final DataTablesInput input =
        objectMapper.convertValue(body.get("dataTablesInput"), DataTablesInput.class);
    final Set<String> businessNoSet =
        new HashSet<>(Arrays.asList(StringUtils.split(businessNos, ",")));
    final Set<String> yearMonths = new HashSet<>(Arrays.asList(StringUtils.split(yearMonth, ",")));

    final DataTablesOutput<VoucherEntity> exList =
        searchVoucher.execute(input, businessNoSet, yearMonths);
    log.debug("{} records", exList.getData().size());
    return exList;
  }

  @GetMapping(value = "/v1/voucher/csv")
  public ResponseEntity<Resource> downloadCsv(
      HttpServletRequest request,
      @RequestParam("yearMonths") String yearMonths,
      @RequestParam("businessNos") String businessNos)
      throws IOException {
    log.info("Request : {}/{}", request.getRequestURI(), request.getQueryString());
    final String csv = downloadVoucher.execute(yearMonths, businessNos);
    // TODO
    final String filename = String.format("voucher_%s_%s.csv", yearMonths, businessNos);
    return ResponseGenerator.downloadString(filename, "application/csv", csv);
  }

  @ExceptionHandler
  public ResponseEntity<VoucherResponse> exception(Exception e) {
    final VoucherResponse response = VoucherResponse.error(e.getMessage(), null);
    return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(response);
  }
}
