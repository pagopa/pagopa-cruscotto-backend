package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB1ResultDTO;
import java.util.List;

/**
 * Service Interface for managing {@link KpiB1Result}.
 */
public interface KpiB1ResultService {

    /**
     * Save kpiB1Result.
     *
     * @param kpiB1ResultDTO the entity to save.
     * @return the persisted entity.
     */
    KpiB1ResultDTO save(KpiB1ResultDTO kpiB1ResultDTO);

    /**
     * Delete all KpiB1Result by instanceModule.
     *
     * @param instanceModuleId the instanceModule id
     * @return the number of deleted entities
     */
    int deleteAllByInstanceModule(long instanceModuleId);

    /**
     * Update the outcome of a KpiB1Result.
     *
     * @param resultId the id of the result to update
     * @param outcome the new outcome status
     */
    void updateKpiB1ResultOutcome(Long resultId, OutcomeStatus outcome);

    /**
     * Find all KpiB1ResultDTO by instanceModuleId.
     *
     * @param instanceModuleId the instance module id
     * @return list of KpiB1ResultDTO
     */
    List<KpiB1ResultDTO> findByInstanceModuleId(Long instanceModuleId);
}