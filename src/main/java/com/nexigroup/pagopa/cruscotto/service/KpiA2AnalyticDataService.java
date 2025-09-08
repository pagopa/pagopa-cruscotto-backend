package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.KpiA2AnalyticData;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiA2AnalyticDataDTO;
import java.util.List;

/**
 * Service Interface for managing {@link KpiA2AnalyticData}.
 */

public interface KpiA2AnalyticDataService {
    

    int deleteAllByInstanceModule(long instanceModuleId);

    List<KpiA2AnalyticDataDTO> findByDetailResultId(long detailResultId);

    /**
     * Save kpiA2AnalyticData and return saved entity as DTO.
     *
     * @param kpiA2AnalyticDataDTO the entity to save.
     * @return saved KpiA2AnalyticDataDTO
     */
    KpiA2AnalyticDataDTO save(KpiA2AnalyticDataDTO kpiA2AnalyticDataDTO);
}
