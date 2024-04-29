package com.gateweb.voucher.usecase;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gateweb.voucher.component.LockProvider;
import com.gateweb.voucher.endpoint.rest.v1.request.FileTypes;
import com.gateweb.voucher.endpoint.rest.v1.request.VoucherExtra;
import com.gateweb.voucher.endpoint.rest.v1.request.VoucherRequest;
import com.gateweb.voucher.model.VoucherCore;
import com.gateweb.voucher.model.dto.ErrorInfo;
import com.gateweb.voucher.model.entity.VoucherLogEntity;
import com.gateweb.voucher.model.exception.VoucherException;
import com.gateweb.voucher.service.LogService;
import com.gateweb.voucher.service.VoucherService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class ImportVoucher {
  private static final Logger log = LoggerFactory.getLogger(ImportVoucher.class);
  private final LockProvider lockProvider;
  private final VoucherService voucherService;
  private final LogService logService;
  private final ObjectMapper objectMapper;

  public ImportVoucher(
      LockProvider lockProvider,
      VoucherService voucherService,
      LogService logService,
      ObjectMapper objectMapper) {
    this.lockProvider = lockProvider;
    this.voucherService = voucherService;
    this.logService = logService;
    this.objectMapper = objectMapper;
  }

  public <V extends VoucherRequest> List<ErrorInfo> run(
      List<V> requestData, VoucherExtra extra, boolean validate) throws VoucherException {
    extra.setOwner(requestData.get(0).parseOwner());
    final VoucherLogEntity voucherLogEntity = logService.saveProcessing(requestData, extra);
    extra.setLogId(voucherLogEntity.getId());

    try {
      lockProvider.lock(extra.getOwner());
      final List<ErrorInfo> errorInfos = importData(requestData, extra, validate);
      if (errorInfos.isEmpty()) logService.updateSuccess(voucherLogEntity);
      else logService.updateValidateError(voucherLogEntity, objectMapper.valueToTree(errorInfos));

      return errorInfos;
    } catch (Exception e) {
      log.error("importVoucher error: {}", Arrays.toString(e.getStackTrace()));
      //      logService.updateValidateError(voucherLogEntity,
      // objectMapper.writeValueAsString(e.getStackTrace()));
      logService.updateValidateError(
          voucherLogEntity,
          objectMapper.valueToTree(
              ErrorInfo.create(null, Arrays.toString(e.getStackTrace()), new String[0])));
      throw new VoucherException(e);
    } finally {
      lockProvider.unlock(extra.getOwner());
    }
  }

  <V extends VoucherRequest> List<ErrorInfo> importData(
      List<V> requestData, VoucherExtra extra, boolean validate) {
    final List<VoucherCore> voucherCores = voucherService.convert(requestData, extra);
    final List<ErrorInfo> errorInfos =
        validate ? voucherService.validate(voucherCores) : new ArrayList<>();
    final List<ErrorInfo> errorInfos2 = voucherService.checkLogic(voucherCores, extra);
    errorInfos.addAll(errorInfos2);
    if (errorInfos.isEmpty()) voucherService.save(voucherCores);
    return errorInfos;
  }

  public List<VoucherRequest> readFile(Resource resource, FileTypes type, VoucherExtra extra)
      throws Exception {
    extra.setFilename(resource.getFilename());
    try {
      return voucherService.readFile(resource, type);
    } catch (Exception e) {
      logService.saveReadError(extra);
      throw new VoucherException("read error: " + Arrays.toString(e.getStackTrace()));
    }
  }

  <V extends VoucherRequest> List<VoucherCore> convert(List<V> requestData, VoucherExtra extra) {
    return requestData.stream().map(v -> v.toDomain().combine(extra)).collect(Collectors.toList());
  }
}
