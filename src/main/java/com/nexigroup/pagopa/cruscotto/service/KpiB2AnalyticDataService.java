package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.KpiB2AnalyticData;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB2AnalyticDataDTO;
import java.util.List;

/**
 * Service Interface for managing {@link KpiB2AnalyticData}.
 */

public interface KpiB2AnalyticDataService {
    void saveAll(List<KpiB2AnalyticDataDTO> kpiB2AnalyticDataDTOS);

    int deleteAllByInstanceModule(long instanceModuleId);

    List<KpiB2AnalyticDataDTO> findByDetailResultId(long detailResultId);

    /**
     * Save kpiB2AnalyticData.
     *
     * @param kpiB2AnalyticDataDTO the entity to save.
     */
    KpiB2AnalyticDataDTO save(KpiB2AnalyticDataDTO kpiB2AnalyticDataDTO);
}
