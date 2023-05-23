package com.gateweb.voucher.model.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.gateweb.voucher.endpoint.rest.v1.request.VoucherExtra;
import com.gateweb.voucher.model.dto.LogStatus;
import com.vladmihalcea.hibernate.type.json.JsonNodeBinaryType;
import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "voucher_log")
@TypeDef(name = "jsonb-node", typeClass = JsonNodeBinaryType.class)
public class VoucherLogEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Integer status;
  private String yearMonth;
  private String owner;
  private String action;
  private String filename;

  @Type(type = "jsonb-node")
  private JsonNode data;

  @Type(type = "jsonb-node")
  private JsonNode error;

  private Integer createId;
  private LocalDateTime createDate;

  public static VoucherLogEntity create(VoucherExtra ve, LogStatus status) {
    return create(null, null, ve, status);
  }

  public static VoucherLogEntity create(JsonNode request, VoucherExtra ve, LogStatus status) {
    return create(request, null, ve, status);
  }

  public static VoucherLogEntity create(
      JsonNode request, JsonNode error, VoucherExtra ve, LogStatus status) {
    return VoucherLogEntity.builder()
        .status(status.status)
        .yearMonth(ve.getYearMonth())
        .owner(ve.getOwner())
        .action(ve.getAction())
        .filename(ve.getFilename())
        .createId(ve.getUserId())
        .createDate(LocalDateTime.now())
        .data(request)
        .error(error)
        .build();
  }
}
