package com.gateweb.voucher.endpoint.rest.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gateweb.voucher.endpoint.rest.v1.response.VoucherResponse;
import com.gateweb.voucher.model.dto.ErrorInfo;
import com.gateweb.voucher.usecase.DownloadTrack;
import com.gateweb.voucher.usecase.ImportTrack;
import com.gateweb.voucher.utils.ResponseGenerator;
import java.util.List;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
public class V1TrackController {

  private final ImportTrack importTrack;
  private final DownloadTrack downloadTrack;
  private final ObjectMapper objectMapper;

  public V1TrackController(
      ImportTrack importTrack, DownloadTrack downloadTrack, ObjectMapper objectMapper) {
    this.importTrack = importTrack;
    this.downloadTrack = downloadTrack;
    this.objectMapper = objectMapper;
  }

  @PostMapping(value = "/v1/track")
  public ResponseEntity<?> importFile(
      HttpServletRequest request,
      @RequestParam("file") MultipartFile file,
      @RequestHeader(
              value = "Accept",
              required = false,
              defaultValue = MediaType.APPLICATION_JSON_VALUE)
          String accept) {
    log.info("Request : {}?{}", request.getRequestURI(), request.getQueryString());
    ResponseEntity response;
    try {
      if (!Objects.requireNonNull(file.getOriginalFilename()).toLowerCase().endsWith(".csv"))
        throw new Exception("只支援 csv 副檔名，請確認檔案格式");
      final List<ErrorInfo> errors = importTrack.run(file.getResource());
      if (errors.isEmpty()) {
        response = ResponseEntity.ok("ok");
        log.info("Response ok : {}", response);
      } else {
        response = ResponseEntity.badRequest().body(objectMapper.writeValueAsString(errors));
        log.warn("Response failed : {}", response);
      }
    } catch (Exception e) {
      response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
      log.error("Response error : {}", response);
    }
    return ResponseGenerator.parseContentType(response, accept);
  }

  @GetMapping(value = {"/v1/track", "/v1/track/{year}"})
  public ResponseEntity<Resource> downloadTrackCsv(
      HttpServletRequest request, @PathVariable(value = "year", required = false) String year) {
    log.info("Request : {}?{}", request.getRequestURI(), request.getQueryString());
    final String trackCsv = downloadTrack.run(year);
    final String filename = "track_" + StringUtils.defaultIfBlank(year, "all") + ".csv";
    return ResponseGenerator.downloadString(filename, "application/csv", trackCsv);
  }

  @GetMapping(value = "/v1/track/example")
  public ResponseEntity<Resource> downloadTrackImportSample(HttpServletRequest request) {
    log.info("Request : {}?{}", request.getRequestURI(), request.getQueryString());
    final String resourcePath = "example/import_track.csv";
    final String filename = "import_track_example.csv";
    return ResponseGenerator.downloadResourcesFile(filename, "application/csv", resourcePath);
  }

  @ExceptionHandler
  public ResponseEntity<VoucherResponse> exception(Exception e) {
    final VoucherResponse response = VoucherResponse.error(e.getMessage(), null);
    return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(response);
  }
}
