package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.KpiB8DetailResult;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB8DetailResultDTO;

import java.util.List;

/**
 * Service Interface for managing {@link KpiB8DetailResult}.
 */
public interface KpiB8DetailResultService {

    KpiB8DetailResultDTO save(KpiB8DetailResultDTO kpiB8DetailResultDTO);

    int deleteAllByInstanceModule(long instanceModuleId);

    void updateKpiB8DetailResultOutcome(long id, OutcomeStatus outcomeStatus);

    List<KpiB8DetailResultDTO> findByResultId(long resultId);
}
