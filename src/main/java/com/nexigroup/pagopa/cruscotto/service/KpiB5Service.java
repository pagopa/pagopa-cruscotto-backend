package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.service.dto.KpiB5AnalyticDataDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB5DetailResultDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB5ResultDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.PagopaSpontaneiDTO;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for managing KPI B5 calculations and results.
 * KPI B5: Utilizzo funzionalità pagamenti spontanei
 */
public interface KpiB5Service {

    /**
     * Save a kpiB5Result.
     *
     * @param kpiB5ResultDTO the entity to save.
     * @return the persisted entity.
     */
    KpiB5ResultDTO save(KpiB5ResultDTO kpiB5ResultDTO);

    /**
     * Updates a kpiB5Result.
     *
     * @param kpiB5ResultDTO the entity to update.
     * @return the persisted entity.
     */
    KpiB5ResultDTO update(KpiB5ResultDTO kpiB5ResultDTO);

    /**
     * Partially updates a kpiB5Result.
     *
     * @param kpiB5ResultDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<KpiB5ResultDTO> partialUpdate(KpiB5ResultDTO kpiB5ResultDTO);

    /**
     * Get all the kpiB5Results.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<KpiB5ResultDTO> findAll(Pageable pageable);

    /**
     * Get the "id" kpiB5Result.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<KpiB5ResultDTO> findOne(Long id);

    /**
     * Delete the "id" kpiB5Result.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Delete all KpiB5Result by instanceModuleId.
     *
     * @param instanceModuleId the instance module ID
     */
    void deleteAllByInstanceModuleId(Long instanceModuleId);

    /**
     * Get KPI B5 detail results by result ID.
     *
     * @param kpiB5ResultId the KPI B5 result ID
     * @return the list of detail results
     */
    List<KpiB5DetailResultDTO> findDetailsByKpiB5ResultId(Long kpiB5ResultId);

    /**
     * Get KPI B5 analytic data by detail result ID.
     *
     * @param detailResultId the detail result ID
     * @return the list of analytic data
     */
    List<KpiB5AnalyticDataDTO> findAnalyticsByDetailResultId(Long detailResultId);

    /**
     * Get PagoPA spontaneous data by analytic data ID.
     * This is the final drilldown showing the snapshot of pagopa_spontaneous data.
     *
     * @param analyticDataId the analytic data ID
     * @return the list of spontaneous data
     */
    List<PagopaSpontaneiDTO> findDrillDownByAnalyticDataId(Long analyticDataId);

    /**
     * Execute KPI B5 calculation for a specific instance.
     * Implements the core business logic for spontaneous payments configuration analysis.
     *
     * @param instanceId the instance ID
     * @param instanceModuleId the instance module ID
     * @param analysisDate the date of analysis
     */
    void calculateKpiB5(Long instanceId, Long instanceModuleId, LocalDate analysisDate);
}