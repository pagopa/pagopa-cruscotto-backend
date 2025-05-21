package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.KpiB2Result;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB2ResultDTO;
import java.util.List;

/**
 * Service Interface for managing {@link KpiB2Result}.
 */
public interface KpiB2ResultService {
    KpiB2ResultDTO save(KpiB2ResultDTO kpiB2ResultDTO);

    int deleteAllByInstanceModule(long instanceModuleId);

    void updateKpiB2ResultOutcome(long id, OutcomeStatus outcomeStatus);

    List<KpiB2ResultDTO> findByInstanceModuleId(long instanceModuleId);
}
