package com.nexigroup.pagopa.cruscotto.service.dto;

import java.io.Serializable;

import com.nexigroup.pagopa.cruscotto.service.validation.ValidationGroups;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartnerIdentificationDTO implements Serializable {

	private static final long serialVersionUID = -7310297221281618446L;

    private Long id;

    @NotNull(groups = { ValidationGroups.RegistryJob.class })
    private String fiscalCode;

    @NotNull(groups = { ValidationGroups.RegistryJob.class })
    private String name;
}
