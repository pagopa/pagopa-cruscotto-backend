package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.KpiA1AnalyticDrillDown;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KpiA1AnalyticDrillDownRepository extends JpaRepository<KpiA1AnalyticDrillDown, Long> {
    List<KpiA1AnalyticDrillDown> findByKpiA1AnalyticDataId(Long analyticDataId);
}
