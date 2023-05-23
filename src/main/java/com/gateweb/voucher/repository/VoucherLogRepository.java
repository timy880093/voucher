package com.gateweb.voucher.repository;

import com.gateweb.voucher.model.entity.VoucherLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoucherLogRepository extends JpaRepository<VoucherLogEntity, Long> {}
