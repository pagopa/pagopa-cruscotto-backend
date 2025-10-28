package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiResultDTO;
import java.util.List;
import java.util.Optional;

/**
 * Generic Service Interface for managing KPI Results.
 * This service handles all KPI types using ModuleCode discrimination.
 */
public interface GenericKpiResultService {

    /**
     * Save kpiResult.
     *
     * @param kpiResultDTO the entity to save.
     * @return the persisted entity.
     */
    KpiResultDTO save(KpiResultDTO kpiResultDTO);

    /**
     * Update kpiResult.
     *
     * @param kpiResultDTO the entity to update.
     * @return the persisted entity.
     */
    KpiResultDTO update(KpiResultDTO kpiResultDTO);

    /**
     * Partially update kpiResult.
     *
     * @param kpiResultDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<KpiResultDTO> partialUpdate(KpiResultDTO kpiResultDTO);

    /**
     * Get all the kpiResults for a specific KPI type.
     *
     * @param moduleCode the KPI module code.
     * @return the list of entities.
     */
    List<KpiResultDTO> findAll(ModuleCode moduleCode);

    /**
     * Get the "id" kpiResult.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<KpiResultDTO> findOne(Long id);

    /**
     * Delete the "id" kpiResult.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Find by instance module id for a specific KPI type.
     *
     * @param moduleCode the KPI module code.
     * @param instanceModuleId the instance module id.
     * @return the list of entities.
     */
    List<KpiResultDTO> findByInstanceModuleId(ModuleCode moduleCode, Long instanceModuleId);

    /**
     * Delete all by instance module id for a specific KPI type.
     *
     * @param moduleCode the KPI module code.
     * @param instanceModuleId the instance module id.
     */
    void deleteAllByInstanceModuleId(ModuleCode moduleCode, Long instanceModuleId);

    /**
     * Update outcome for a specific result.
     *
     * @param id the result id.
     * @param outcome the new outcome.
     */
    void updateOutcome(Long id, OutcomeStatus outcome);

    /**
     * Find results by instance module id and outcome for a specific KPI type.
     *
     * @param moduleCode the KPI module code.
     * @param instanceModuleId the instance module id.
     * @param outcome the outcome status.
     * @return the list of entities.
     */
    List<KpiResultDTO> findByInstanceModuleIdAndOutcome(ModuleCode moduleCode, Long instanceModuleId, OutcomeStatus outcome);
}