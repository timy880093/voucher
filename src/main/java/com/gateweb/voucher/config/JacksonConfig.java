package com.gateweb.voucher.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;

@Configuration
public class JacksonConfig {

  public JacksonConfig() {
  }

  @Bean
  public Jackson2ObjectMapperBuilder jacksonBuilder() {
    return new Jackson2ObjectMapperBuilder()
        .indentOutput(true) // Pretty print
        .featuresToEnable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES) // 解析大小寫不敏感
        .featuresToDisable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES) // 忽略多餘字段
        .dateFormat(new SimpleDateFormat("yyyy-MM-dd")) // 格式化日期
        .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Date轉Timestamp
  }

  @Bean
  @Primary
  @Qualifier("objectMapper")
  public ObjectMapper objectMapper() {
    return jacksonBuilder().build();
  }

  @Bean
  @Qualifier("xmlMapper")
  public XmlMapper xmlMapper() {
    final XmlMapper xmlMapper = jacksonBuilder().createXmlMapper(true).build();
    return (XmlMapper)
        xmlMapper.configure(
            ToXmlGenerator.Feature.WRITE_XML_DECLARATION,
            true); // 增加 xml header 第一行 version、encoding
  }

  // json converter
  @Bean
  public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
    final MappingJackson2HttpMessageConverter jsonConverter =
        new MappingJackson2HttpMessageConverter(objectMapper());
    jsonConverter.setDefaultCharset(StandardCharsets.UTF_8);
    return jsonConverter;
  }

  // xml converter
  @Bean
  public MappingJackson2XmlHttpMessageConverter mappingJackson2XmlHttpMessageConverter() {
    final MappingJackson2XmlHttpMessageConverter xmlConverter =
        new MappingJackson2XmlHttpMessageConverter(xmlMapper());
    xmlConverter.setDefaultCharset(StandardCharsets.UTF_8);
    return xmlConverter;
  }

}
