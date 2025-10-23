package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB6ResultDTO;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.nexigroup.pagopa.cruscotto.domain.KpiB6Result}.
 */
public interface KpiB6ResultService {

    /**
     * Save kpiB6Result.
     *
     * @param kpiB6ResultDTO the entity to save.
     * @return the persisted entity.
     */
    KpiB6ResultDTO save(KpiB6ResultDTO kpiB6ResultDTO);

    /**
     * Update kpiB6Result.
     *
     * @param kpiB6ResultDTO the entity to update.
     * @return the persisted entity.
     */
    KpiB6ResultDTO update(KpiB6ResultDTO kpiB6ResultDTO);

    /**
     * Partially update kpiB6Result.
     *
     * @param kpiB6ResultDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<KpiB6ResultDTO> partialUpdate(KpiB6ResultDTO kpiB6ResultDTO);

    /**
     * Get all the kpiB6Results.
     *
     * @return the list of entities.
     */
    List<KpiB6ResultDTO> findAll();

    /**
     * Get the "id" kpiB6Result.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<KpiB6ResultDTO> findOne(Long id);

    /**
     * Delete the "id" kpiB6Result.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Delete all KpiB6Result by instanceModule.
     *
     * @param instanceModuleId the instanceModule id
     * @return the number of deleted entities
     */
    int deleteAllByInstanceModuleId(Long instanceModuleId);

    /**
     * Update outcome for KpiB6Result.
     *
     * @param kpiB6ResultId the kpiB6Result id
     * @param outcome the new outcome status
     */
    void updateOutcome(Long kpiB6ResultId, OutcomeStatus outcome);
}