package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.KpiB1DetailResult;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB1DetailResultDTO;
import java.util.List;

/**
 * Service Interface for managing {@link KpiB1DetailResult}.
 */
public interface KpiB1DetailResultService {

    /**
     * Save kpiB1DetailResult.
     *
     * @param kpiB1DetailResultDTO the entity to save.
     * @return the persisted entity.
     */
    KpiB1DetailResultDTO save(KpiB1DetailResultDTO kpiB1DetailResultDTO);

    /**
     * Delete all KpiB1DetailResult by instanceModule.
     *
     * @param instanceModuleId the instanceModule id
     * @return the number of deleted entities
     */
    int deleteAllByInstanceModule(long instanceModuleId);

    /**
     * Find all KpiB1DetailResult by resultId.
     *
     * @param resultId the result id
     * @return list of KpiB1DetailResultDTO
     */
    List<KpiB1DetailResultDTO> findByResultId(long resultId);
}