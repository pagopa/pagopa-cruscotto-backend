package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.KpiB4Result;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB4ResultDTO;
import java.util.List;

/**
 * Service Interface for managing {@link KpiB4Result}.
 */
public interface KpiB4ResultService {
    
    KpiB4ResultDTO save(KpiB4ResultDTO kpiB4ResultDTO);

    int deleteAllByInstanceModule(long instanceModuleId);

    void updateKpiB4ResultOutcome(long id, OutcomeStatus outcomeStatus);

    List<KpiB4ResultDTO> findByInstanceModuleId(long instanceModuleId);
}