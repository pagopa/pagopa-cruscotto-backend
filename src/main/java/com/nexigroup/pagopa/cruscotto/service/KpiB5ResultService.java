package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.KpiB5Result;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB5ResultDTO;
import java.util.List;

/**
 * Service Interface for managing {@link KpiB5Result}.
 */
public interface KpiB5ResultService {
    
    KpiB5ResultDTO save(KpiB5ResultDTO kpiB5ResultDTO);

    int deleteAllByInstanceModule(long instanceModuleId);

    void updateKpiB5ResultOutcome(long id, OutcomeStatus outcomeStatus);

    List<KpiB5ResultDTO> findByInstanceModuleId(long instanceModuleId);
}