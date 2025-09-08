package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.service.dto.InstanceDTO;

import com.nexigroup.pagopa.cruscotto.domain.AuthUser;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceModuleDTO;

public interface CalculateStateInstanceService {
    void calculateStateInstance(InstanceDTO instanceDTO);

    InstanceModuleDTO updateModuleAndInstanceState(InstanceModuleDTO moduleDTO, AuthUser user);
}
