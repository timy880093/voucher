package com.gateweb.voucher.model.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "invoice_external_track")
public class InvoiceExternalTrackEntity implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", unique = true, nullable = false)
  private Integer id;

  @Column(name = "year_month")
  @Pattern(message = "IET0101", regexp = "^1[0-9]{2}(0[1-9]|1[0-2])$")
  private String yearMonth;

  @Column(name = "type_code")
  @Pattern(message = "IET0201", regexp = "^(2[1-9]|3[1-8])$")
  private String typeCode;

  @Column(name = "track")
  @Pattern(message = "IET0301", regexp = "^[A-Z]{2}$")
  private String track;

  public Integer getId() {
    return id;
  }

  public InvoiceExternalTrackEntity setId(Integer id) {
    this.id = id;
    return this;
  }

  public String getYearMonth() {
    return yearMonth;
  }

  public InvoiceExternalTrackEntity setYearMonth(String yearMonth) {
    this.yearMonth = yearMonth;
    return this;
  }

  public String getTypeCode() {
    return typeCode;
  }

  public InvoiceExternalTrackEntity setTypeCode(String typeCode) {
    this.typeCode = typeCode;
    return this;
  }

  public String getTrack() {
    return track;
  }

  public InvoiceExternalTrackEntity setTrack(String track) {
    this.track = track;
    return this;
  }

  public String key() {
    return StringUtils.joinWith("_", yearMonth, track, typeCode);
  }
}
