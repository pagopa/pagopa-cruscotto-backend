package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.KpiB6AnalyticData;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB6AnalyticDataDTO;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link KpiB6AnalyticData}.
 */
public interface KpiB6AnalyticDataService {

    /**
     * Save kpiB6AnalyticData.
     *
     * @param kpiB6AnalyticDataDTO the entity to save.
     * @return the persisted entity.
     */
    KpiB6AnalyticDataDTO save(KpiB6AnalyticDataDTO kpiB6AnalyticDataDTO);

    /**
     * Save all kpiB6AnalyticData.
     *
     * @param kpiB6AnalyticDataDTOS the entities to save.
     * @return the list of persisted entities.
     */
    List<KpiB6AnalyticDataDTO> saveAll(List<KpiB6AnalyticDataDTO> kpiB6AnalyticDataDTOS);

    /**
     * Update kpiB6AnalyticData.
     *
     * @param kpiB6AnalyticDataDTO the entity to update.
     * @return the persisted entity.
     */
    KpiB6AnalyticDataDTO update(KpiB6AnalyticDataDTO kpiB6AnalyticDataDTO);

    /**
     * Partially update kpiB6AnalyticData.
     *
     * @param kpiB6AnalyticDataDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<KpiB6AnalyticDataDTO> partialUpdate(KpiB6AnalyticDataDTO kpiB6AnalyticDataDTO);

    /**
     * Get all the kpiB6AnalyticData.
     *
     * @return the list of entities.
     */
    List<KpiB6AnalyticDataDTO> findAll();

    /**
     * Get all the kpiB6AnalyticData by detail result id.
     *
     * @param detailResultId the detail result id.
     * @return the list of entities.
     */
    List<KpiB6AnalyticDataDTO> findByDetailResultId(Long detailResultId);

    /**
     * Get the "id" kpiB6AnalyticData.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<KpiB6AnalyticDataDTO> findOne(Long id);

    /**
     * Delete the "id" kpiB6AnalyticData.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Delete all KpiB6AnalyticData by instanceModule.
     *
     * @param instanceModuleId the instanceModule id
     * @return the number of deleted entities
     */
    int deleteAllByInstanceModuleId(Long instanceModuleId);

    /**
     * Find all KpiB6AnalyticData by KpiB6DetailResult id.
     *
     * @param kpiB6DetailResultId the kpiB6DetailResult id
     * @return the list of entities
     */
    List<KpiB6AnalyticDataDTO> findAllByKpiB6DetailResultId(Long kpiB6DetailResultId);
}