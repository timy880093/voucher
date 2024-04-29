package com.gateweb.voucher.repository;

import static com.gateweb.voucher.repository.VoucherDao.VoucherSql.*;

import com.gateweb.voucher.model.entity.InvoiceExternalEntity;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

@Repository
public class VoucherDao {
  private static final Logger log = LoggerFactory.getLogger(VoucherDao.class);

  private final NamedParameterJdbcOperations jdbcOperations;

  public VoucherDao(NamedParameterJdbcOperations jdbcOperations) {
    this.jdbcOperations = jdbcOperations;
  }

  public List<String> findKeyByIds(Collection<Long> ids) {
    if (ids.isEmpty()) return new ArrayList<>();
    final MapSqlParameterSource source = new MapSqlParameterSource().addValue("id", ids);
    final List<String> keys = jdbcOperations.queryForList(FIND_BY_IDS, source, String.class);
    log.debug("find size: {}", keys.size());
    return keys;
  }

  public void deleteByIds(List<Long> ids) {
    if (ids.isEmpty()) return;
    final MapSqlParameterSource source = new MapSqlParameterSource().addValue("id", ids);
    jdbcOperations.update(DELETE_BY_IDS, source);
    log.debug("delete id: {}", ids);
  }

  public List<InvoiceExternalEntity> findByRepeatKey(List<InvoiceExternalEntity> voucherEntities) {
    final Set<String> yearMonths =
        voucherEntities.stream()
            .map(InvoiceExternalEntity::getOriginalYearMonth)
            .collect(Collectors.toSet());
    final Set<String> keys =
        voucherEntities.stream().map(InvoiceExternalEntity::getCheckRepeatKey).collect(Collectors.toSet());
    final MapSqlParameterSource source =
        new MapSqlParameterSource().addValue("yearMonth", yearMonths).addValue("keys", keys);
    return jdbcOperations.query(
        FIND_PAIR_BY_REPEAT_KEY,
        source,
        (rs, row) -> {
          final InvoiceExternalEntity entity = new InvoiceExternalEntity();
          entity.setExternalId(rs.getLong("external_id"));
          entity.setCheckRepeatKey(rs.getString("check_repeat_key"));
          entity.setLogIdHistory(rs.getString("log_id_history"));
          entity.setCreatorId(rs.getInt("creator_id"));
          entity.setCreateDate(rs.getTimestamp("create_date").toLocalDateTime());
          return entity;
        });
  }

  static final class VoucherSql {

    static final String FIND_PAIR_BY_REPEAT_KEY =
        "select external_id, check_repeat_key, log_id_history, creator_id, create_date\n"
            + "from invoice_external\n"
            + "where original_year_month in (:yearMonth)\n"
            + "  and check_repeat_key in (:keys);";

    static final String FIND_BY_IDS = "select check_repeat_key from invoice_external where external_id in (:id)";
    static final String DELETE_BY_IDS = "delete from invoice_external where external_id in (:id);";
  }
}
