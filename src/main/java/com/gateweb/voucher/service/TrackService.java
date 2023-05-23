package com.gateweb.voucher.service;

import com.gateweb.voucher.component.TrackCacheProvider;
import com.gateweb.voucher.model.dto.ErrorInfo;
import com.gateweb.voucher.model.entity.InvoiceExternalTrackEntity;
import com.gateweb.voucher.utils.ValidatorUtils;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class TrackService {
  private final TrackCacheProvider trackCacheProvider;
  private final Validator validator;

  public TrackService(TrackCacheProvider trackCacheProvider, Validator validator) {
    this.trackCacheProvider = trackCacheProvider;
    this.validator = validator;
  }

  public void save(List<InvoiceExternalTrackEntity> tracks) {
    final Map<String, Boolean> trackMap = trackCacheProvider.getTrackMap();
    final List<InvoiceExternalTrackEntity> noRepeatList = tracks.stream().filter(t -> !trackMap.getOrDefault(t.key(), false)).collect(Collectors.toList());
    if (!noRepeatList.isEmpty()) {
      final List<InvoiceExternalTrackEntity> newTracks = trackCacheProvider.saveTracks(noRepeatList);
      trackCacheProvider.updateTrackMap(newTracks);
    }
  }

  public List<InvoiceExternalTrackEntity> read(Resource resource) throws Exception {
    final List<String> lines;
    try (Reader ir = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(ir)) {
      lines = br.lines().collect(Collectors.toList());
    }
    if (lines.isEmpty()) throw new Exception("read failed, csv is empty");
    return convert(lines);
  }

  List<InvoiceExternalTrackEntity> convert(List<String> lines) throws Exception {
    final List<InvoiceExternalTrackEntity> trackEntities = new ArrayList<>();
    try {
      lines.stream()
          .filter(line -> StringUtils.isNotBlank(line) && !StringUtils.startsWith(line, ",,,"))
          .forEach(
              line -> {
                final String[] columns = StringUtils.split(line, ",");
                final String[] typeCodes = StringUtils.split(columns[0], "@");
                for (String typeCode : typeCodes) {
                  for (int i = 2; i < columns.length; i++) {
                    trackEntities.add(
                        InvoiceExternalTrackEntity.builder()
                            .typeCode(typeCode)
                            .yearMonth(columns[1])
                            .track(columns[i])
                            .build());
                  }
                }
              });
    } catch (Exception e) {
      throw new Exception("convert failed", e);
    }
    return trackEntities;
  }

  public List<ErrorInfo> validate(List<InvoiceExternalTrackEntity> tracks) {
    final List<ErrorInfo> errors = new ArrayList<>();
    for (InvoiceExternalTrackEntity track : tracks) {
      final Set<ConstraintViolation<Object>> violations = validator.validate(track);
      final List<ErrorInfo> errorInfos =
          violations.isEmpty()
              ? new ArrayList<>()
              : ValidatorUtils.toError(track.key(), violations);
      errors.addAll(errorInfos);
    }
    return errors;
  }

  public String genTrackCsv(String year) {
    final List<InvoiceExternalTrackEntity> tracks = trackCacheProvider.findTracks();
    final List<InvoiceExternalTrackEntity> filterTracks =
        StringUtils.isBlank(year)
            ? tracks
            : tracks.stream()
                .filter(t -> StringUtils.startsWith(t.getYearMonth(), year))
                .collect(Collectors.toList());
    final StringBuilder sb = new StringBuilder();
    sb.append("yearMonth,track,type").append(System.lineSeparator());
    for (InvoiceExternalTrackEntity t : filterTracks) {
      sb.append(t.getYearMonth())
          .append(",")
          .append(t.getTrack())
          .append(",")
          .append(t.getTypeCode())
          .append(System.lineSeparator());
    }
    return sb.toString();
  }
}
