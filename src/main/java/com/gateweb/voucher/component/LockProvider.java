package com.gateweb.voucher.component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LockProvider {

  private final Map<String, ReentrantLock> map;
  private static final int TIME_OUT_MIN = 10;

  public LockProvider() {
    this.map = new ConcurrentHashMap<>();
  }

  public LockProvider(Map<String, ReentrantLock> map) {
    this.map = map;
  }

  public void lock(String key) throws Exception {
    final ReentrantLock lock = map.getOrDefault(key, new ReentrantLock());
    if (lock.isLocked()) {
      throw new Exception("key(" + key + ") is locked , please try later in 10 minutes");
    }
    lock.tryLock(TIME_OUT_MIN, TimeUnit.MINUTES);
    log.debug("lock key({}) OK", key);
  }

  public void unlock(String key) {
    final ReentrantLock lock = map.getOrDefault(key, new ReentrantLock());
    try {
      if (lock.isLocked()) {
        lock.unlock();
        log.debug("unlock key({}) OK", key);
      }
    } catch (Exception e) {
      log.error("unlock key({}) error : {}", key, e.getMessage());
    }
  }
}
