package com.gateweb.voucher.usecase;

import com.gateweb.voucher.model.entity.InvoiceExternalEntity;
import com.gateweb.voucher.service.VoucherService;
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

  public String run(String yearMonths, String businessNos) throws Exception {
    // TODO
    final Set<String> yearMonthSet =
            Arrays.stream(StringUtils.split(yearMonths, ",")).collect(Collectors.toSet());
    final Set<String> businessNoSet =
        Arrays.stream(StringUtils.split(businessNos, ",")).collect(Collectors.toSet());
    final List<InvoiceExternalEntity> voucherEntities = voucherService.findDataBySeek(yearMonthSet, businessNoSet);
    if(voucherEntities.isEmpty()) throw new Exception("查無資料");
    return voucherService.writeCsvString(voucherEntities);
  }
  
  
}
