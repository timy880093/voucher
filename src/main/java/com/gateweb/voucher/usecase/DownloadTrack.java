package com.gateweb.voucher.usecase;

import com.gateweb.voucher.service.TrackService;
import org.springframework.stereotype.Service;

@Service
public class DownloadTrack {
  private final TrackService trackService;

  public DownloadTrack(TrackService trackService) {
    this.trackService = trackService;
  }

  public String execute(String year) {
    return trackService.genTrackCsv(year);
  }
}
