package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.KpiB4DetailResult;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB4DetailResultDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiC2DetailResultDTO;

import java.util.List;

/**
 * Service Interface for managing {@link KpiB4DetailResult}.
 */
public interface KpiC2DetailResultService {

    KpiC2DetailResultDTO save(KpiC2DetailResultDTO kpiC2DetailResultDTO);

    int deleteAllByInstanceModule(long instanceModuleId);

    void updateKpiC2DetailResultOutcome(long id, OutcomeStatus outcomeStatus);

    List<KpiC2DetailResultDTO> findByResultId(long resultId);
}
