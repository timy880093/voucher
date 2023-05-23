package com.gateweb.voucher.repository;

import static com.gateweb.voucher.repository.VoucherDao.VoucherSql.*;

import com.gateweb.voucher.model.entity.VoucherEntity;
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

  //  public List<Long> findInvoiceRepeatIds() {
  //    final MapSqlParameterSource source = new MapSqlParameterSource().addValue("", "");
  //    return jdbcOperations.queryForList(FIND_REPEAT, source, Long.class);
  //  }

  //  public List<Long> findByRepeatKey(List<VoucherEntity> vouchers) {
  //    final Set<String> yearMonths =
  //        vouchers.stream().map(VoucherEntity::getVoucherYearMonth).collect(Collectors.toSet());
  //    final Set<String> keys =
  // vouchers.stream().map(VoucherEntity::getKey).collect(Collectors.toSet());
  //    final MapSqlParameterSource source =
  //        new MapSqlParameterSource().addValue("yearMonth", yearMonths).addValue("key", keys);
  //    return jdbcOperations.queryForList(FIND_BY_REPEAT_KEY, source, Long.class);
  //  }

  //  public void deleteByRepeatKey(List<VoucherEntity> voucherEntities) {
  //    final Set<String> yearMonths =
  //        voucherEntities.stream()
  //            .map(VoucherEntity::getVoucherYearMonth)
  //            .collect(Collectors.toSet());
  //    final Set<String> keys =
  //        voucherEntities.stream().map(VoucherEntity::getKey).collect(Collectors.toSet());
  //    final MapSqlParameterSource source =
  //        new MapSqlParameterSource().addValue("yearMonth", yearMonths).addValue("key", keys);
  //    final List<Long> ids = jdbcOperations.queryForList(FIND_BY_REPEAT_KEY, source, Long.class);
  //    deleteByIds(ids);
  //    log.debug("deleteByRepeatKey id: {}", ids);
  //  }

  public List<VoucherEntity> findByRepeatKey(List<VoucherEntity> voucherEntities) {
    final Set<String> yearMonths =
        voucherEntities.stream()
            .map(VoucherEntity::getVoucherYearMonth)
            .collect(Collectors.toSet());
    final Set<String> keys =
        voucherEntities.stream().map(VoucherEntity::getKey).collect(Collectors.toSet());
    final MapSqlParameterSource source =
        new MapSqlParameterSource().addValue("yearMonth", yearMonths).addValue("keys", keys);
    return jdbcOperations.query(
        FIND_PAIR_BY_REPEAT_KEY,
        source,
        (rs, row) -> {
          final VoucherEntity voucherEntity = new VoucherEntity();
          voucherEntity.setId(rs.getLong("id"));
          voucherEntity.setKey(rs.getString("key"));
          voucherEntity.setLogIdHistory(rs.getString("log_id_history"));
          voucherEntity.setCreatorId(rs.getInt("creator_id"));
          voucherEntity.setCreateDate(rs.getTimestamp("create_date").toLocalDateTime());
          return voucherEntity;
        });
  }

  static final class VoucherSql {
    static final String FIND_REPEAT =
        "delete id\n"
            + "from voucher\n"
            + "where ((invoice_number is not null and invoice_number = :invoiceNumber)\n"
            + "    or (common_number is not null and common_number = :commonNumber))\n"
            + "  and type_code in (:typeCodes)\n"
            + "  and date between :start and :end";

    static final String FIND_BY_REPEAT_KEY =
        "select id\n"
            + "from voucher\n"
            + "where voucher_year_month in (:yearMonth)\n"
            + "  and key in (:keys);";

    static final String FIND_PAIR_BY_REPEAT_KEY =
        "select id, key, log_id_history, creator_id, create_date\n"
            + "from voucher\n"
            + "where voucher_year_month in (:yearMonth)\n"
            + "  and key in (:keys);";

    static final String FIND_BY_IDS = "select key from voucher where id in (:id)";
    static final String DELETE_BY_IDS = "delete from voucher where id in (:id);";
    static final String DELETE_REPEAT_KEY =
        "delete\n"
            + "from voucher\n"
            + "where voucher_year_month = :yearMonth\n"
            + "  and key = :key";

    static final String DELETE_ALL_REPEAT_KEY =
        "delete\n"
            + "from voucher\n"
            + "where voucher_year_month in (:yearMonth)\n"
            + "  and key in (:key)";
  }
}
