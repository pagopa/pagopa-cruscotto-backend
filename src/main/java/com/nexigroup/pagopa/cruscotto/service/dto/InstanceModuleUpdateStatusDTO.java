package com.nexigroup.pagopa.cruscotto.service.dto;

import java.io.Serial;
import java.io.Serializable;

import com.nexigroup.pagopa.cruscotto.domain.InstanceModule;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleStatus;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * A DTO for the {@link InstanceModule} entity.
 */

@Getter
@Setter
@EqualsAndHashCode
public class InstanceModuleUpdateStatusDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -1063577564042796502L;

    private Long id;

    private Boolean allowManualOutcome;

    private ModuleStatus status;


}
