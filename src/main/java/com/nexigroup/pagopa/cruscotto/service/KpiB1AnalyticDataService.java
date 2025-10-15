package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.KpiB1AnalyticData;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB1AnalyticDataDTO;
import java.util.List;

/**
 * Service Interface for managing {@link KpiB1AnalyticData}.
 */
public interface KpiB1AnalyticDataService {

    /**
     * Save kpiB1AnalyticData.
     *
     * @param kpiB1AnalyticDataDTO the entity to save.
     * @return the persisted entity.
     */
    KpiB1AnalyticDataDTO save(KpiB1AnalyticDataDTO kpiB1AnalyticDataDTO);

    /**
     * Save all kpiB1AnalyticData.
     *
     * @param kpiB1AnalyticDataDTOS the entities to save.
     */
    void saveAll(List<KpiB1AnalyticDataDTO> kpiB1AnalyticDataDTOS);

    /**
     * Delete all KpiB1AnalyticData by instanceModule.
     *
     * @param instanceModuleId the instanceModule id
     * @return the number of deleted entities
     */
    int deleteAllByInstanceModule(long instanceModuleId);

    /**
     * Find all KpiB1AnalyticData by detailResultId.
     *
     * @param detailResultId the detail result id
     * @return list of KpiB1AnalyticDataDTO
     */
    List<KpiB1AnalyticDataDTO> findByDetailResultId(long detailResultId);

    /**
     * Find all KpiB1AnalyticDataDTO by instanceModuleId.
     *
     * @param instanceModuleId the instance module id
     * @return list of KpiB1AnalyticDataDTO
     */
    List<KpiB1AnalyticDataDTO> findByInstanceModuleId(Long instanceModuleId);
}