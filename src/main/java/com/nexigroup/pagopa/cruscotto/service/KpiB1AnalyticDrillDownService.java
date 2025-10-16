package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.KpiB1AnalyticDrillDown;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB1AnalyticDrillDownDTO;
import java.util.List;

/**
 * Service Interface for managing {@link KpiB1AnalyticDrillDown}.
 */
public interface KpiB1AnalyticDrillDownService {

    /**
     * Save all kpiB1AnalyticDrillDown.
     *
     * @param kpiB1AnalyticDrillDownDTOS the entities to save.
     */
    void saveAll(List<KpiB1AnalyticDrillDownDTO> kpiB1AnalyticDrillDownDTOS);

    /**
     * Delete all KpiB1AnalyticDrillDown by KpiB1AnalyticData ids.
     *
     * @param analyticDataIds the list of analytic data ids
     * @return the number of deleted entities
     */
    int deleteByKpiB1AnalyticDataIds(List<Long> analyticDataIds);

    /**
     * Find all KpiB1AnalyticDrillDown by analyticDataId.
     *
     * @param analyticDataId the analytic data id
     * @return list of KpiB1AnalyticDrillDownDTO
     */
    List<KpiB1AnalyticDrillDownDTO> findByAnalyticDataId(Long analyticDataId);
}