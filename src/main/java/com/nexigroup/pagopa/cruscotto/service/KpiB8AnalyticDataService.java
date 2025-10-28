package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.service.dto.KpiB8AnalyticDataDTO;

import java.util.List;

/**
 * Service Interface for managing KpiB8AnalyticData.
 */
public interface KpiB8AnalyticDataService {

    KpiB8AnalyticDataDTO save(KpiB8AnalyticDataDTO kpiB8AnalyticDataDTO);

    int deleteAllByInstanceModule(long instanceModuleId);

    List<KpiB8AnalyticDataDTO> findByDetailResultId(long detailResultId);
}
