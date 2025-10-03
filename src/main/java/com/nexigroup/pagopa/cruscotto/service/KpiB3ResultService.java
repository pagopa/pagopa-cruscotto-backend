package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.KpiB3Result;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB3ResultDTO;
import java.util.List;

/**
 * Service Interface for managing {@link KpiB3Result}.
 */
public interface KpiB3ResultService {
    
    KpiB3ResultDTO save(KpiB3ResultDTO kpiB3ResultDTO);

    int deleteAllByInstanceModule(long instanceModuleId);

    void updateKpiB3ResultOutcome(long id, OutcomeStatus outcomeStatus);

    List<KpiB3ResultDTO> findByInstanceModuleId(long instanceModuleId);
}