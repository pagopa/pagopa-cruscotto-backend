package com.nexigroup.pagopa.cruscotto.service.dto;


import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.InstanceStatus;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;

/**
 * A DTO for the {@link Instance} entity.
 */
@Getter
@Setter
@EqualsAndHashCode
public class InstanceDTO implements Serializable {

    private static final long serialVersionUID = 5276880971060143924L;

    private Long id;

    private String instanceIdentification;

    private Long partnerId;

    private String partnerName;

    private LocalDate predictedDateAnalysis;

    private Instant applicationDate;

    private Long assignedUserId;

    private String assignedFirstName;

    private String assignedLastName;

    private LocalDate analysisPeriodStartDate;

    private LocalDate analysisPeriodEndDate;

    private InstanceStatus status;

    private Instant lastAnalysisDate;

}
