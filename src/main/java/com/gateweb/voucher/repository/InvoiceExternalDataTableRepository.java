package com.gateweb.voucher.repository;

import com.gateweb.voucher.model.entity.InvoiceExternalEntity;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceExternalDataTableRepository
    extends DataTablesRepository<InvoiceExternalEntity, Long> {}
