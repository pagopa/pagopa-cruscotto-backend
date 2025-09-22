package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.service.dto.KpiB2AnalyticDrillDownDTO;

import java.util.List;

public interface KpiB2AnalyticDrillDownService {
    void saveAll(List<KpiB2AnalyticDrillDownDTO> drillDowns);
    List<KpiB2AnalyticDrillDownDTO> findByKpiB2AnalyticDataId(Long kpiB2AnalyticDataId);
}
