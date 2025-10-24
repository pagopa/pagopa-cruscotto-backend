package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiAnalyticDataDTO;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Generic Service Interface for managing KPI Analytic Data.
 * This service handles all KPI types using ModuleCode discrimination.
 */
public interface GenericKpiAnalyticDataService {

    /**
     * Save kpiAnalyticData.
     *
     * @param kpiAnalyticDataDTO the entity to save.
     * @return the persisted entity.
     */
    KpiAnalyticDataDTO save(KpiAnalyticDataDTO kpiAnalyticDataDTO);

    /**
     * Save all kpiAnalyticData.
     *
     * @param kpiAnalyticDataDTOs the entities to save.
     * @return the persisted entities.
     */
    List<KpiAnalyticDataDTO> saveAll(List<KpiAnalyticDataDTO> kpiAnalyticDataDTOs);

    /**
     * Update kpiAnalyticData.
     *
     * @param kpiAnalyticDataDTO the entity to update.
     * @return the persisted entity.
     */
    KpiAnalyticDataDTO update(KpiAnalyticDataDTO kpiAnalyticDataDTO);

    /**
     * Partially update kpiAnalyticData.
     *
     * @param kpiAnalyticDataDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<KpiAnalyticDataDTO> partialUpdate(KpiAnalyticDataDTO kpiAnalyticDataDTO);

    /**
     * Get all the kpiAnalyticData for a specific KPI type.
     *
     * @param moduleCode the KPI module code.
     * @return the list of entities.
     */
    List<KpiAnalyticDataDTO> findAll(ModuleCode moduleCode);

    /**
     * Get all the kpiAnalyticData.
     *
     * @return the list of entities.
     */
    List<KpiAnalyticDataDTO> findAll();

    /**
     * Get the "id" kpiAnalyticData.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<KpiAnalyticDataDTO> findOne(Long id);

    /**
     * Delete the "id" kpiAnalyticData.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Find analytic data by module code and detail result id.
     *
     * @param moduleCode the KPI module code.
     * @param kpiDetailResultId the detail result id.
     * @return the list of entities.
     */
    List<KpiAnalyticDataDTO> findByDetailResultId(ModuleCode moduleCode, Long kpiDetailResultId);

    /**
     * Find analytic data by module code and instance module id.
     *
     * @param moduleCode the KPI module code.
     * @param instanceModuleId the instance module id.
     * @return the list of entities.
     */
    List<KpiAnalyticDataDTO> findByInstanceModuleId(ModuleCode moduleCode, Long instanceModuleId);

    /**
     * Delete all analytic data by module code and instance module id.
     *
     * @param moduleCode the KPI module code.
     * @param instanceModuleId the instance module id.
     */
    void deleteAllByInstanceModuleId(ModuleCode moduleCode, Long instanceModuleId);

    /**
     * Find analytic data by module code and date range.
     *
     * @param moduleCode the KPI module code.
     * @param startDate the start date.
     * @param endDate the end date.
     * @return the list of entities.
     */
    List<KpiAnalyticDataDTO> findByDateRange(ModuleCode moduleCode, LocalDate startDate, LocalDate endDate);
}