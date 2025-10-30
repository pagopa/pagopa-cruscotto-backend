package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.service.dto.KpiC2AnalyticDataDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiC2AnalyticDataDTO;

import java.util.List;

/**
 * Service Interface for managing KpiC2AnalyticData.
 */
public interface KpiC2AnalyticDataService {

    KpiC2AnalyticDataDTO save(KpiC2AnalyticDataDTO kpiC2AnalyticDataDTO);

    int deleteAllByInstanceModule(long instanceModuleId);

    List<KpiC2AnalyticDataDTO> findByDetailResultId(long detailResultId);
}
