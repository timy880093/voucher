package com.gateweb.voucher.usecase;

import com.gateweb.voucher.service.LogService;
import com.gateweb.voucher.service.VoucherService;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class DeleteVoucher {

  private final LogService logService;
  private final VoucherService voucherService;

  public DeleteVoucher(LogService logService, VoucherService voucherService) {
    this.logService = logService;
    this.voucherService = voucherService;
  }

  public List<String> run(String ids, int userId) {
    final List<String> keys = deleteReturnKeys(ids);
    if (!keys.isEmpty()) logService.saveForDelete(keys, userId);
    return keys;
  }

  List<String> deleteReturnKeys(String ids) {
    final List<Long> idList =
        Arrays.stream(StringUtils.split(ids, ","))
            .map(Long::parseLong)
            .collect(Collectors.toList());
    return voucherService.deleteReturnKeys(idList);
  }
}
