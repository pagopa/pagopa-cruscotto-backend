package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.KpiB6DetailResult;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB6DetailResultDTO;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link KpiB6DetailResult}.
 */
public interface KpiB6DetailResultService {

    /**
     * Save kpiB6DetailResult.
     *
     * @param kpiB6DetailResultDTO the entity to save.
     * @return the persisted entity.
     */
    KpiB6DetailResultDTO save(KpiB6DetailResultDTO kpiB6DetailResultDTO);

    /**
     * Save all kpiB6DetailResults.
     *
     * @param kpiB6DetailResultDTOs the entities to save.
     * @return the persisted entities.
     */
    List<KpiB6DetailResultDTO> saveAll(List<KpiB6DetailResultDTO> kpiB6DetailResultDTOs);

    /**
     * Update kpiB6DetailResult.
     *
     * @param kpiB6DetailResultDTO the entity to update.
     * @return the persisted entity.
     */
    KpiB6DetailResultDTO update(KpiB6DetailResultDTO kpiB6DetailResultDTO);

    /**
     * Partially update kpiB6DetailResult.
     *
     * @param kpiB6DetailResultDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<KpiB6DetailResultDTO> partialUpdate(KpiB6DetailResultDTO kpiB6DetailResultDTO);

    /**
     * Get all the kpiB6DetailResults.
     *
     * @return the list of entities.
     */
    List<KpiB6DetailResultDTO> findAll();

    /**
     * Get the "id" kpiB6DetailResult.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<KpiB6DetailResultDTO> findOne(Long id);

    /**
     * Delete the "id" kpiB6DetailResult.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Delete all KpiB6DetailResult by instanceModule.
     *
     * @param instanceModuleId the instanceModule id
     * @return the number of deleted entities
     */
    int deleteAllByInstanceModule(long instanceModuleId);

    /**
     * Find all KpiB6DetailResult by resultId.
     *
     * @param kpiB6ResultId the KpiB6Result id
     * @return the list of entities
     */
    List<KpiB6DetailResultDTO> findAllByKpiB6ResultId(Long kpiB6ResultId);
}