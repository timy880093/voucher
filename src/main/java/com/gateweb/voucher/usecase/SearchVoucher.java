package com.gateweb.voucher.usecase;

import com.gateweb.voucher.model.entity.VoucherEntity;
import com.gateweb.voucher.service.VoucherService;
import java.util.Set;
import javax.validation.Valid;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Service;

@Service
public class SearchVoucher {

  private final VoucherService voucherService;

  public SearchVoucher(VoucherService voucherService) {
    this.voucherService = voucherService;
  }

  public DataTablesOutput<VoucherEntity> execute(
      @Valid DataTablesInput input, Set<String> yearMonths, Set<String> businessNos) {
    return voucherService.search(input, yearMonths, businessNos);
  }
}
