package com.gateweb.voucher.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gateweb.voucher.config.JacksonConfig;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@Slf4j
public class ResponseGenerator {

  private static final ObjectMapper objectMapper = new JacksonConfig().objectMapper();

  public static String genTaxDownloadFilePath(String prefix, String suffix) { // 檔名 附檔名

    try {
      // 於C:\Users\XXX\AppData\Local\Temp 建立暫存檔案 用完可刪除 fixme 現在會建立在tomcat/temp路徑下 尚未找出原因
      Path base =
          Files.createTempFile(prefix + "_", suffix); // createTempFile 依據檔名 + 隨機亂數 + 副檔名 建立暫存檔
      return base.toString();
    } catch (IOException e) {
      log.error(e.getMessage());
    }
    return "";
  }

  public static String genJson(String absolutePath, String suffix, String fileName) { // 暫存路徑 副檔名 檔名
    Map<String, String> map = new HashMap<>();
    map.put("base", absolutePath);
    map.put("suffix", suffix);
    map.put("fileName", fileName);

    String jsonObj = "";
    try {
      jsonObj = objectMapper.writeValueAsString(map);
    } catch (JsonProcessingException e) {
      log.error(e.getMessage());
    }
    return jsonObj;
  }

  public static String genJson(String absolutePath) { // 暫存路徑 副檔名 檔名
    final String fileName = Paths.get(absolutePath).getFileName().toString();
    return genJson(
        absolutePath,
        "." + StringUtils.substringAfterLast(fileName, "."),
        StringUtils.substringBeforeLast(fileName, "_"));
  }

  public static Map<String, String> genMap(
      String base, String suffix, String fileName) { // 暫存路徑 副檔名 檔名
    Map<String, String> map = new HashMap<>();
    map.put("base", base);
    map.put("suffix", suffix);
    map.put("fileName", fileName);

    return map;
  }

  public static void appendTextToFile(String base, String content) throws Exception {
    appendTextToFile(base, content.getBytes());
  }

  public static void appendTextToFile(String base, byte[] bytes) throws Exception {
    try {
      Files.write(Paths.get(base), bytes, StandardOpenOption.APPEND);
    } catch (IOException e) {
      throw new Exception("寫檔錯誤" + e.getMessage());
    }
  }

  public static void deleteTempFile(String base) {
    try {
      Files.deleteIfExists(Paths.get(base));
      log.debug("Delete file : {}", base);
    } catch (IOException e) {
      log.error(e.getMessage());
    }
  }

  public static ResponseEntity<Resource> download(
      String filename, String mediaType, Resource resource) {
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
        .contentType(MediaType.parseMediaType(mediaType))
        .body(resource);
  }

  public static ResponseEntity<Resource> downloadResourcesFile(
      String filename, String mediaType, String resourcePath) {
    final Resource resource = new ClassPathResource(resourcePath);
    return download(filename, mediaType, resource);
  }

  public static ResponseEntity<Resource> downloadString(
      String filename, String mediaType, String content) {
    final Resource resource = new ByteArrayResource(content.getBytes(StandardCharsets.UTF_8));
    return download(filename, mediaType, resource);
  }

  public static ResponseEntity<?> parseContentType(ResponseEntity<?> response, String accept) {
    return ResponseEntity.status(response.getStatusCode())
        .contentType(
            StringUtils.isBlank(accept)
                ? Objects.requireNonNull(response.getHeaders().getContentType())
                : MediaType.valueOf(accept))
        .body(response.getBody());
  }
}
