package com.gateweb.voucher.usecase;

import com.gateweb.voucher.model.dto.ErrorInfo;
import com.gateweb.voucher.model.entity.InvoiceExternalTrackEntity;
import com.gateweb.voucher.service.TrackService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ImportTrack {
  private final TrackService trackService;

  public ImportTrack(TrackService trackService) {
    this.trackService = trackService;
  }

  public List<ErrorInfo> run(Resource resource) throws Exception {
    final List<InvoiceExternalTrackEntity> tracks = trackService.read(resource);
    final List<ErrorInfo> errors = trackService.validate(tracks);
    if (errors.isEmpty()) trackService.save(tracks);
    return errors;
  }
}
