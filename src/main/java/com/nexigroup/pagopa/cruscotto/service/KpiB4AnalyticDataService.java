package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.service.dto.KpiB4AnalyticDataDTO;
import java.util.List;

/**
 * Service Interface for managing KpiB4AnalyticData.
 */
public interface KpiB4AnalyticDataService {
    
    KpiB4AnalyticDataDTO save(KpiB4AnalyticDataDTO kpiB4AnalyticDataDTO);

    int deleteAllByInstanceModule(long instanceModuleId);

    List<KpiB4AnalyticDataDTO> findByDetailResultId(long detailResultId);
}