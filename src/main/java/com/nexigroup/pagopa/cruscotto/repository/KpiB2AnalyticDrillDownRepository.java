package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.KpiB2AnalyticDrillDown;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface KpiB2AnalyticDrillDownRepository extends JpaRepository<KpiB2AnalyticDrillDown, Long> {
    List<KpiB2AnalyticDrillDown> findByKpiB2AnalyticDataIdOrderByFromHourAsc(Long kpiB2AnalyticDataId);

    void deleteByKpiB2AnalyticDataIdIn(List<Long> analyticDataIds);

    List<KpiB2AnalyticDrillDown>findByKpiB2AnalyticDataIdIn(List<Long> analyticDataIds);


}
