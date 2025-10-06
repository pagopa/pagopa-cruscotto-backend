package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.KpiB3AnalyticData;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB3AnalyticDataDTO;
import java.util.List;

/**
 * Service Interface for managing {@link KpiB3AnalyticData}.
 */
public interface KpiB3AnalyticDataService {
    
    KpiB3AnalyticDataDTO save(KpiB3AnalyticDataDTO kpiB3AnalyticDataDTO);

    void saveAll(List<KpiB3AnalyticDataDTO> kpiB3AnalyticDataDTOList);

    int deleteAllByInstanceModule(long instanceModuleId);

    List<KpiB3AnalyticDataDTO> findByDetailResultId(long detailResultId);
}