package com.nexigroup.pagopa.cruscotto.service.dto;

import java.io.Serializable;
import java.time.LocalDate;

import com.nexigroup.pagopa.cruscotto.domain.AnagPartner;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.PartnerStatus;
import com.nexigroup.pagopa.cruscotto.service.validation.ValidationGroups;

import jakarta.validation.constraints.NotNull;
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

    private PartnerIdentificationDTO partnerIdentification;

    @NotNull(groups = { ValidationGroups.RegistryJob.class })
    private PartnerStatus status;

    private Boolean qualified = Boolean.FALSE;

    private LocalDate deactivationDate;
    
    private LocalDate lastAnalysisDate;

    private LocalDate analysisPeriodStartDate;

    private LocalDate analysisPeriodEndDate;
    
    private Long stationsCount;

//    private Integer associatedInstitutes;
//
//    

}
