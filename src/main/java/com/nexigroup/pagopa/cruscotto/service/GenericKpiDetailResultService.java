package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiDetailResultDTO;
import java.util.List;
import java.util.Optional;

/**
 * Generic Service Interface for managing KPI Detail Results.
 * This service handles all KPI types using ModuleCode discrimination.
 */
public interface GenericKpiDetailResultService {

    /**
     * Save kpiDetailResult.
     *
     * @param kpiDetailResultDTO the entity to save.
     * @return the persisted entity.
     */
    KpiDetailResultDTO save(KpiDetailResultDTO kpiDetailResultDTO);

    /**
     * Save all kpiDetailResults.
     *
     * @param kpiDetailResultDTOs the entities to save.
     * @return the persisted entities.
     */
    List<KpiDetailResultDTO> saveAll(List<KpiDetailResultDTO> kpiDetailResultDTOs);

    /**
     * Update kpiDetailResult.
     *
     * @param kpiDetailResultDTO the entity to update.
     * @return the persisted entity.
     */
    KpiDetailResultDTO update(KpiDetailResultDTO kpiDetailResultDTO);

    /**
     * Partially update kpiDetailResult.
     *
     * @param kpiDetailResultDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<KpiDetailResultDTO> partialUpdate(KpiDetailResultDTO kpiDetailResultDTO);

    /**
     * Get all kpiDetailResults.
     *
     * @return the list of entities.
     */
    List<KpiDetailResultDTO> findAll();

    /**
     * Get all the kpiDetailResults for a specific KPI type.
     *
     * @param moduleCode the KPI module code.
     * @return the list of entities.
     */
    List<KpiDetailResultDTO> findAll(ModuleCode moduleCode);

    /**
     * Get the "id" kpiDetailResult.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<KpiDetailResultDTO> findOne(Long id);

    /**
     * Delete the "id" kpiDetailResult.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Find detail results by module code and KPI result id.
     *
     * @param moduleCode the KPI module code.
     * @param kpiResultId the KPI result id.
     * @return the list of entities.
     */
    List<KpiDetailResultDTO> findByKpiResultId(ModuleCode moduleCode, Long kpiResultId);

    /**
     * Find detail results by module code and instance module id.
     *
     * @param moduleCode the KPI module code.
     * @param instanceModuleId the instance module id.
     * @return the list of entities.
     */
    List<KpiDetailResultDTO> findByInstanceModuleId(ModuleCode moduleCode, Long instanceModuleId);

    /**
     * Delete all detail results by module code and instance module id.
     *
     * @param moduleCode the KPI module code.
     * @param instanceModuleId the instance module id.
     */
    void deleteAllByInstanceModuleId(ModuleCode moduleCode, Long instanceModuleId);
}