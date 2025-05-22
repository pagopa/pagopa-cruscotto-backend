package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.KpiA1AnalyticData;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiA1AnalyticDataDTO;
import java.util.List;

/**
 * Service Interface for managing {@link KpiA1AnalyticData}.
 */

public interface KpiA1AnalyticDataService {
    void saveAll(List<KpiA1AnalyticDataDTO> kpiA1AnalyticDataDTOS);

    int deleteAllByInstanceModule(long instanceModuleId);

    List<KpiA1AnalyticDataDTO> findByInstanceModuleId(long instanceModuleId);
}
