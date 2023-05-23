package com.gateweb.voucher.repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

@Repository
public class InvoiceMainDao {

  private final NamedParameterJdbcOperations jdbcOperations;

  public InvoiceMainDao(NamedParameterJdbcOperations jdbcOperations) {
    this.jdbcOperations = jdbcOperations;
  }

  private static final String EXIST_INVOICE_NUMBER =
      "select exists(select 1\n"
          + "              from invoice_main\n"
          + "              where c_year_month = :yearMonth\n"
          + "                and invoice_number = :invoiceNumber\n"
          + "                and upload_status = 'C')";

  private static final String FIND_EXIST_INVOICE_NUMBER =
      "select invoice_number\n"
          + "from invoice_main\n"
          + "where c_year_month = :yearMonth\n"
          + "  and invoice_number in (:invoiceNumbers)\n"
          + "  and upload_status = 'C'";

  public boolean existInvoiceNumber(String yearMonth, String invoiceNumber) {
    final MapSqlParameterSource source =
        new MapSqlParameterSource()
            .addValue("yearMonth", yearMonth)
            .addValue("invoiceNumber", invoiceNumber);
    final Boolean result =
        jdbcOperations.queryForObject(EXIST_INVOICE_NUMBER, source, Boolean.class);
    return Boolean.TRUE.equals(result);
  }

  public Set<String> findExistInvoiceNumbers(String yearMonth, Set<String> invoiceNumbers) {
    final MapSqlParameterSource source =
        new MapSqlParameterSource()
            .addValue("yearMonth", yearMonth)
            .addValue("invoiceNumbers", invoiceNumbers);
    final List<String> existNumbers =
        jdbcOperations.queryForList(FIND_EXIST_INVOICE_NUMBER, source, String.class);
    return new HashSet<>(existNumbers);
  }
}
