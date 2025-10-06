package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.KpiB3DetailResult;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB3DetailResultDTO;
import java.util.List;

/**
 * Service Interface for managing {@link KpiB3DetailResult}.
 */
public interface KpiB3DetailResultService {
    
    KpiB3DetailResultDTO save(KpiB3DetailResultDTO kpiB3DetailResultDTO);

    int deleteAllByInstanceModule(long instanceModuleId);

    void updateKpiB3DetailResultOutcome(long id, OutcomeStatus outcomeStatus);

    List<KpiB3DetailResultDTO> findByResultId(long resultId);
}