package com.gateweb.voucher.service;

import static com.gateweb.voucher.model.dto.LogStatus.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gateweb.voucher.endpoint.rest.v1.request.VoucherExtra;
import com.gateweb.voucher.model.entity.VoucherLogEntity;
import com.gateweb.voucher.repository.VoucherLogRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LogService {

  private final VoucherLogRepository voucherLogRepository;
  private final ObjectMapper objectMapper;

  public LogService(VoucherLogRepository voucherLogRepository, ObjectMapper objectMapper) {
    this.voucherLogRepository = voucherLogRepository;
    this.objectMapper = objectMapper;
  }

  @Transactional(rollbackFor = Exception.class)
  public VoucherLogEntity saveValidateError(
      List<?> requestData, List<?> errorData, VoucherExtra voucherExtra) {
    final VoucherLogEntity voucherLogEntity =
        VoucherLogEntity.create(
            toNode(requestData), toNode(errorData), voucherExtra, VALIDATE_ERROR);
    return voucherLogRepository.save(voucherLogEntity);
  }

  @Transactional(rollbackFor = Exception.class)
  public VoucherLogEntity saveException(
      List<?> requestData, List<?> errorData, VoucherExtra voucherExtra) {
    final VoucherLogEntity voucherLogEntity =
        VoucherLogEntity.create(toNode(requestData), toNode(errorData), voucherExtra, EXCEPTION);
    return voucherLogRepository.save(voucherLogEntity);
  }

  @Transactional(rollbackFor = Exception.class)
  public VoucherLogEntity saveProcessing(List<?> requestData, VoucherExtra voucherExtra) {
    final VoucherLogEntity voucherLogEntity =
        VoucherLogEntity.create(toNode(requestData), voucherExtra, PROCESSING);
    return voucherLogRepository.save(voucherLogEntity);
  }

  @Transactional(rollbackFor = Exception.class)
  public VoucherLogEntity saveForDelete(List<String> keys, int userId) {
    final VoucherLogEntity voucherLogEntity =
        VoucherLogEntity.builder()
            .status(SUCCESS.status)
            //        .owner(ve.getOwner())
            .action("delete")
            .createId(userId)
            .createDate(LocalDateTime.now())
            .data(objectMapper.valueToTree(keys))
            .build();
    return voucherLogRepository.save(voucherLogEntity);
  }

  @Transactional(rollbackFor = Exception.class)
  public VoucherLogEntity saveReadError(VoucherExtra voucherExtra) {
    final VoucherLogEntity voucherLogEntity =
        VoucherLogEntity.create(null, voucherExtra, READ_ERROR);
    return voucherLogRepository.save(voucherLogEntity);
  }

  @Transactional(rollbackFor = Exception.class)
  public void updateSuccess(VoucherLogEntity voucherLogEntity) {
    voucherLogEntity.setStatus(SUCCESS.status);
    voucherLogRepository.save(voucherLogEntity);
  }

  @Transactional(rollbackFor = Exception.class)
  public void updateValidateError(VoucherLogEntity voucherLogEntity, JsonNode errorNode) {
    updateError(voucherLogEntity, errorNode, VALIDATE_ERROR.status);
  }

  @Transactional(rollbackFor = Exception.class)
  public void updateException(VoucherLogEntity voucherLogEntity, String message) {
    updateError(voucherLogEntity, message, EXCEPTION.status);
  }

  void updateError(VoucherLogEntity voucherLogEntity, String message, int status) {
    final ObjectNode error = objectMapper.createObjectNode().put("error", message);
    updateError(voucherLogEntity, error, status);
  }

  void updateError(VoucherLogEntity voucherLogEntity, JsonNode error, int status) {
    voucherLogEntity.setStatus(status);
    voucherLogEntity.setError(error);
    voucherLogRepository.save(voucherLogEntity);
  }

  JsonNode toNode(List<?> list) {
    return objectMapper.convertValue(list, ArrayNode.class);
  }
}
