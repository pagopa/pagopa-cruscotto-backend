package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.service.dto.KpiA1AnalyticDrillDownDTO;
import java.util.List;

public interface KpiA1AnalyticDrillDownService {
    List<KpiA1AnalyticDrillDownDTO> findByKpiA1AnalyticDataId(Long analyticDataId);
    void saveAll(List<KpiA1AnalyticDrillDownDTO> drillDowns);
}
