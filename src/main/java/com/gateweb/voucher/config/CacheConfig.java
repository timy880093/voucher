package com.gateweb.voucher.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class CacheConfig {

  public Caffeine caffeineConfig() {
    return Caffeine.newBuilder().expireAfterWrite(30, TimeUnit.DAYS);
  }

  @Bean
  @Primary
  public CacheManager cacheManager() {
    CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
    caffeineCacheManager.setCaffeine(caffeineConfig());
    return caffeineCacheManager;
  }
}
