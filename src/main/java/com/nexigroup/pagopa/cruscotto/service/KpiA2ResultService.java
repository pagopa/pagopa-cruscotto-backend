package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.KpiA2Result;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiA2ResultDTO;

/**
 * Service Interface for managing {@link KpiA2Result}.
 */

public interface KpiA2ResultService {
	
	KpiA2ResultDTO save(KpiA2ResultDTO kpiA2ResultDTO);
	
    int deleteAllByInstanceModule(long instanceModuleId);
    
    void updateKpiA2ResultOutcome(long id, OutcomeStatus outcomeStatus);
}
