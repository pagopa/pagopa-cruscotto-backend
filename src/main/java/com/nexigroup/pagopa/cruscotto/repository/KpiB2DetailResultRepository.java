package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.KpiB2DetailResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository for the KpiB2DetailResult entity.
 */
@Repository
public interface KpiB2DetailResultRepository extends JpaRepository<KpiB2DetailResult, Long>, JpaSpecificationExecutor<KpiB2DetailResult> {}
