package com.gateweb.voucher.repository;

import com.gateweb.voucher.model.entity.InvoiceExternalTrackEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceExternalTrackRepository
    extends JpaRepository<InvoiceExternalTrackEntity, Long> {}
