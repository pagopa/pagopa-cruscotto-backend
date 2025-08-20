package com.nexigroup.pagopa.cruscotto.service;

import java.util.List;
import java.util.Optional;

import com.nexigroup.pagopa.cruscotto.domain.AuthUser;
import com.nexigroup.pagopa.cruscotto.domain.InstanceModule;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceModuleDTO;

/**
 * Service Interface for managing {@link InstanceModule}.
 */
public interface InstanceModuleService {
    Optional<InstanceModuleDTO> findOne(Long instanceId, Long moduleId);

    void updateAutomaticOutcome(Long instanceModuleId, OutcomeStatus automaticOutcome);

    Optional<InstanceModule> findById(Long id);

    Optional<InstanceModuleDTO> findInstanceModuleDTOById(Long id);

    List<InstanceModuleDTO> findAllByInstanceId(Long instanceId);
    
//    void updateStatusAndAllowManualOutcome(Long instanceModuleId, ModuleStatus status, Boolean allowManualOutcome);
        
    InstanceModuleDTO updateInstanceModule(InstanceModuleDTO instanceModuleDTO, AuthUser currentUser);
}
