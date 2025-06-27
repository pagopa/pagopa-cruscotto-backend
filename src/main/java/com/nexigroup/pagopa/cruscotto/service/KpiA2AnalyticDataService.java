package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.KpiA2AnalyticData;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiA2AnalyticDataDTO;
import java.util.List;

/**
 * Service Interface for managing {@link KpiA2AnalyticData}.
 */

public interface KpiA2AnalyticDataService {
    void saveAll(List<KpiA2AnalyticDataDTO> kpiA2AnalyticDataDTOS);

    int deleteAllByInstanceModule(long instanceModuleId);

    List<KpiA2AnalyticDataDTO> findByDetailResultId(long detailResultId);
}
