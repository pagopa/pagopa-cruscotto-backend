package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.service.dto.KpiC2AnalyticDrillDownDTO;

import java.time.LocalDate;
import java.util.List;

/**
 * Service Interface for managing PagopaApiLogDrilldown.
 */
public interface KpiC2AnalyticDrillDownService {

    /**
     * Save a PagopaApiLogDrilldown.
     * @param KpiC2AnalyticDrillDownDTO the entity to save.
     * @return the persisted entity.
     */
    KpiC2AnalyticDrillDownDTO save(KpiC2AnalyticDrillDownDTO KpiC2AnalyticDrillDownDTO);

    /**
     * Find all drilldown records for a specific KPI B.4 analytic data ID.
     * @param analyticDataId the KPI B.4 analytic data ID
     * @return the list of entities.
     */
    List<KpiC2AnalyticDrillDownDTO> findByKpiC2AnalyticDataId(Long analyticDataId);


    /**
     * Find all drilldown records for a specific instance and analysis date.
     * @param instanceId the instance ID
     * @param analysisDate the analysis date
     * @return the list of entities.
     */
    List<KpiC2AnalyticDrillDownDTO> findByInstanceIdAndAnalysisDate(Long instanceId, LocalDate analysisDate);

    /**
     * Delete all drilldown records for a specific instance module.
     * @param instanceModuleId the instance module ID
     * @return the number of deleted entities.
     */
    int deleteAllByInstanceModuleId(Long instanceModuleId);

    /**
     * Delete drilldown records for a specific instance module and analysis date.
     * @param instanceModuleId the instance module ID
     * @param analysisDate the analysis date
     * @return the number of deleted entities.
     */
    int deleteByInstanceModuleIdAndAnalysisDate(Long instanceModuleId, LocalDate analysisDate);
}
