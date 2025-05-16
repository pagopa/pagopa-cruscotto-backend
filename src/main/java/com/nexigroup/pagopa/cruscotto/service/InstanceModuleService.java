package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.InstanceModule;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceModuleDTO;

import java.util.Optional;

/**
 * Service Interface for managing {@link InstanceModule}.
 */
public interface InstanceModuleService {
	
    Optional<InstanceModuleDTO> findOne(Long instanceId, Long moduleId);
    
    void updateAutomaticOutcome(Long instanceModuleId, OutcomeStatus automaticOutcome);
}
