package com.gateweb.voucher.component;

import com.gateweb.voucher.model.entity.InvoiceExternalTrackEntity;
import com.gateweb.voucher.repository.InvoiceExternalTrackRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
// @CacheConfig(cacheManager = "cacheManager")
public class TrackCacheProvider {

  private final InvoiceExternalTrackRepository invoiceExternalTrackRepository;

  public TrackCacheProvider(InvoiceExternalTrackRepository invoiceExternalTrackRepository) {
    this.invoiceExternalTrackRepository = invoiceExternalTrackRepository;
  }

    @Cacheable(value = "tracks", key = "#root.caches[0].name")
  //  @Cacheable(value = "tracks", key = "tracks")
  public List<InvoiceExternalTrackEntity> findTracks() {
    final List<InvoiceExternalTrackEntity> list = invoiceExternalTrackRepository.findAll();
    //    log.debug("findTracks size : {}", list.size());
    return list;
  }

    @CachePut(value = "tracks", key = "#root.caches[0].name")
  //  @CachePut(value = "tracks", key = "tracks")
  public List<InvoiceExternalTrackEntity> saveTracks(
      List<InvoiceExternalTrackEntity> trackEntities) {
    invoiceExternalTrackRepository.saveAll(trackEntities);
    //    log.debug("saveTracks");
    return findTracks();
  }

  @Cacheable(value = "trackMap", key = "#root.caches[0].name")
  public Map<String, Boolean> getTrackMap() {
    final Map<String, Boolean> map = parseTrackMap(findTracks());
    log.debug("getTrackMap size : {}", map.size());
    return map;
  }

  @CachePut(value = "trackMap", key = "#root.caches[0].name")
  public Map<String, Boolean> updateTrackMap(List<InvoiceExternalTrackEntity> tracks) {
    final Map<String, Boolean> map = parseTrackMap(tracks);
    log.debug("updateTrackMap size : {}", map.size());
    return map;
  }

  Map<String, Boolean> parseTrackMap(List<InvoiceExternalTrackEntity> tracks) {
    return tracks.stream().collect(Collectors.toMap(InvoiceExternalTrackEntity::key, it -> true));
  }
}
