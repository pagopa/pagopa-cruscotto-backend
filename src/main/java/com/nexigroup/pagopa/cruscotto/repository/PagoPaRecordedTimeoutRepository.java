package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.PagoPaRecordedTimeout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository for the PagoPaRecordedTimeout entity.
 */
@Repository
public interface PagoPaRecordedTimeoutRepository
    extends JpaRepository<PagoPaRecordedTimeout, Long>, JpaSpecificationExecutor<PagoPaRecordedTimeout> {}
