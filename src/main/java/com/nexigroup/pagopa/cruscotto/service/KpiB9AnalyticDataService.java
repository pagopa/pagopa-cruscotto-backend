package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.KpiB9AnalyticData;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB9AnalyticDataDTO;

import java.util.List;

/**
 * Service Interface for managing {@link KpiB9AnalyticData}.
 */

public interface KpiB9AnalyticDataService {
	
    void saveAll(List<KpiB9AnalyticDataDTO> kpiB9AnalyticDataDTOS);

    int deleteAllByInstanceModule(long instanceModuleId);

    List<KpiB9AnalyticDataDTO> findByInstanceModuleId(long instanceModuleId);
}
