package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.KpiA1DetailResult;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.service.validation.ValidationGroups;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * A DTO for the {@link KpiA1DetailResult} entity.
 */

@Getter
@Setter
@ToString
public class KpiA1DetailResultDTO implements Serializable {

    private static final long serialVersionUID = 1477746486886196475L;

    private Long id;

    @NotNull(groups = { ValidationGroups.KpiA1Job.class })
    private Long instanceId;

    @NotNull(groups = { ValidationGroups.KpiA1Job.class })
    private Long instanceModuleId;

    @NotNull(groups = { ValidationGroups.KpiA1Job.class })
    private LocalDate analysisDate;

    @NotNull(groups = { ValidationGroups.KpiA1Job.class })
    private EvaluationType evaluationType;

    @NotNull(groups = { ValidationGroups.KpiA1Job.class })
    private LocalDate evaluationStartDate;

    @NotNull(groups = { ValidationGroups.KpiA1Job.class })
    private LocalDate evaluationEndDate;

    @NotNull(groups = { ValidationGroups.KpiA1Job.class })
    private Long totReq;

    @NotNull(groups = { ValidationGroups.KpiA1Job.class })
    private Long reqTimeout;

    @NotNull(groups = { ValidationGroups.KpiA1Job.class })
    private Double timeoutPercentage;

    @NotNull(groups = { ValidationGroups.KpiA1Job.class })
    private OutcomeStatus outcome;

    private Long kpiA1ResultId;
}
