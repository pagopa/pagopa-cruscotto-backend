package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.AnagPartner;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.PartnerStatus;
import com.nexigroup.pagopa.cruscotto.service.validation.ValidationGroups;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * A DTO for the {@link AnagPartner} entity.
 */
@Getter
@Setter
@EqualsAndHashCode
public class AnagPartnerDTO implements Serializable {

    private static final long serialVersionUID = 3543581324420880777L;

    private Long id;

    @NotNull(groups = { ValidationGroups.RegistryJob.class })
    private String fiscalCode;

    @NotNull(groups = { ValidationGroups.RegistryJob.class })
    private String name;

    @NotNull(groups = { ValidationGroups.RegistryJob.class })
    private PartnerStatus status;

    private Boolean qualified = Boolean.FALSE;

    private LocalDate deactivationDate;

    private String createdBy;

    private Instant createdDate;

    private String lastModifiedBy;

    private Instant lastModifiedDate;
}
