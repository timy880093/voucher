package com.gateweb.voucher.usecase;

import com.gateweb.voucher.model.entity.VoucherEntity;
import com.gateweb.voucher.service.VoucherService;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class DownloadVoucher {

  private final VoucherService voucherService;

  public DownloadVoucher(VoucherService voucherService) {
    this.voucherService = voucherService;
  }

  public String execute(String yearMonths, String businessNos) throws IOException {
    // TODO
    final Set<String> yearMonthSet =
            Arrays.stream(StringUtils.split(yearMonths, ",")).collect(Collectors.toSet());
    final Set<String> businessNoSet =
        Arrays.stream(StringUtils.split(businessNos, ",")).collect(Collectors.toSet());
    final List<VoucherEntity> voucherEntities = voucherService.findDataBySeek(yearMonthSet, businessNoSet);
    return voucherService.writeCsvString(voucherEntities);
  }
  
  
}
