//package com.gateweb.voucher.repository;
//
//import com.gateweb.voucher.model.entity.VoucherEntity;
//import java.util.Collection;
//import java.util.List;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//@Repository
//public interface VoucherRepository extends JpaRepository<VoucherEntity, Long> {
//
//  @Query(
//      value =
//          "SELECT *\n"
//              + "FROM voucher i\n"
//              + "WHERE filing_year_month IN (:yearMonths)\n"
//              + "  AND owner in (:businessNos)\n"
//              + "  AND id > :id\n"
//              + "ORDER BY id\n"
//              + "limit :limit",
//      nativeQuery = true)
//  List<VoucherEntity> findDataBySeek(
//      @Param("id") Long id,
//      @Param("yearMonths") Collection<String> yearMonths,
//      @Param("businessNos") Collection<String> businessNos,
//      @Param("limit") Integer limit);
//}
