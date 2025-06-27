package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.AnalysisOutcome;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.InstanceStatus;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

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

    private String partnerFiscalCode;

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

    private AnalysisOutcome lastAnalysisOutcome;

    private Set<InstanceModuleDTO> instanceModules;

    @Override
    public String toString() {
        return (
            "InstanceDTO{" +
            "id=" +
            id +
            ", instanceIdentification='" +
            instanceIdentification +
            '\'' +
            ", partnerId=" +
            partnerId +
            ", partnerFiscalCode='" +
            partnerFiscalCode +
            '\'' +
            ", partnerName='" +
            partnerName +
            '\'' +
            ", predictedDateAnalysis=" +
            predictedDateAnalysis +
            ", applicationDate=" +
            applicationDate +
            ", assignedUserId=" +
            assignedUserId +
            ", assignedFirstName='" +
            assignedFirstName +
            '\'' +
            ", assignedLastName='" +
            assignedLastName +
            '\'' +
            ", analysisPeriodStartDate=" +
            analysisPeriodStartDate +
            ", analysisPeriodEndDate=" +
            analysisPeriodEndDate +
            ", status=" +
            status +
            ", lastAnalysisDate=" +
            lastAnalysisDate +
            ", lastAnalysisOutcome=" +
            lastAnalysisOutcome +
            ", instanceModules=" +
            instanceModules +
            '}'
        );
    }
}
