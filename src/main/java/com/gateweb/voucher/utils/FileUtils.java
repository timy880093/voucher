package com.gateweb.voucher.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@Slf4j
public class FileUtils {

  public static Properties readResourceFile(String classPath) {
    final Resource resourceFile = new ClassPathResource(classPath);
    final Properties prop = new Properties();
    try (InputStream inputStream = resourceFile.getInputStream()) {
      prop.load(inputStream);
    } catch (IOException e) {
      log.error(e.getMessage());
    }
    return prop;
  }
}
