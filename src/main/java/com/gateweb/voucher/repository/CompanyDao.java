package com.gateweb.voucher.repository;

import java.util.HashSet;
import java.util.Set;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

@Repository
public class CompanyDao {

  private final NamedParameterJdbcOperations jdbcOperations;

  public CompanyDao(NamedParameterJdbcOperations jdbcOperations) {
    this.jdbcOperations = jdbcOperations;
  }

  private static final String FIND_BUSINESS_NO =
      "select c.business_no\n"
          + "FROM company c\n"
          + "         JOIN public.user u ON (c.company_id = u.company_id or c.parent_id = u.company_id)\n"
          + "WHERE u.user_id = :userId";

  public Set<String> findBusinessNos(int userId) {
    final MapSqlParameterSource source = new MapSqlParameterSource().addValue("userId", userId);
    return new HashSet<>(jdbcOperations.queryForList(FIND_BUSINESS_NO, source, String.class));
  }
}
