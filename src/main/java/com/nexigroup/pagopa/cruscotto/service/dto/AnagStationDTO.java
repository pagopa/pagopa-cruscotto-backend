package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.AnagStation;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.StationStatus;
import com.nexigroup.pagopa.cruscotto.service.validation.ValidationGroups;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * A DTO for the {@link AnagStation} entity.
 */
@Getter
@Setter
@EqualsAndHashCode
public class AnagStationDTO implements Serializable {

    private static final long serialVersionUID = -4635523946173239779L;

    private Long id;

    @NotNull(groups = { ValidationGroups.StationJob.class })
    private String name;

    private LocalDate activationDate;

    private Long partnerId;

    @NotNull(groups = { ValidationGroups.StationJob.class })
    private String partnerFiscalCode;

    private String partnerName;

    private String typeConnection;

    @NotNull(groups = { ValidationGroups.StationJob.class })
    private Integer primitiveVersion;

    @NotNull(groups = { ValidationGroups.StationJob.class })
    private Boolean paymentOption = Boolean.FALSE;

    private Integer associatedInstitutes;

    @NotNull(groups = { ValidationGroups.StationJob.class })
    private StationStatus status;

    private LocalDate deactivationDate;

    private String createdBy;

    private Instant createdDate;

    private String lastModifiedBy;

    private Instant lastModifiedDate;
}
