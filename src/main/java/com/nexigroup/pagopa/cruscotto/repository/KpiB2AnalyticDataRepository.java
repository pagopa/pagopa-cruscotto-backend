package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.KpiB2AnalyticData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository for the KpiB2AnalyticData entity.
 */
@Repository
public interface KpiB2AnalyticDataRepository extends JpaRepository<KpiB2AnalyticData, Long>, JpaSpecificationExecutor<KpiB2AnalyticData> {}
