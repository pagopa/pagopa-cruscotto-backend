package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.KpiB4DetailResult;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB4DetailResultDTO;
import java.util.List;

/**
 * Service Interface for managing {@link KpiB4DetailResult}.
 */
public interface KpiB4DetailResultService {
    
    KpiB4DetailResultDTO save(KpiB4DetailResultDTO kpiB4DetailResultDTO);

    int deleteAllByInstanceModule(long instanceModuleId);

    void updateKpiB4DetailResultOutcome(long id, OutcomeStatus outcomeStatus);

    List<KpiB4DetailResultDTO> findByResultId(long resultId);
}