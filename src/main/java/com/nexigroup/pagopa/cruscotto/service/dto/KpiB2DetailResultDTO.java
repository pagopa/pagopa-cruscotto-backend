package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.KpiB2DetailResult;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.service.validation.ValidationGroups;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * A DTO for the {@link KpiB2DetailResult} entity.
 */

@Getter
@Setter
@ToString
public class KpiB2DetailResultDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 5141899331669875400L;

    private Long id;

    @NotNull(groups = { ValidationGroups.KpiB2Job.class })
    private Long instanceId;

    @NotNull(groups = { ValidationGroups.KpiB2Job.class })
    private Long instanceModuleId;

    @NotNull(groups = { ValidationGroups.KpiB2Job.class })
    private LocalDate analysisDate;

    @NotNull(groups = { ValidationGroups.KpiB2Job.class })
    private EvaluationType evaluationType;

    @NotNull(groups = { ValidationGroups.KpiB2Job.class })
    private LocalDate evaluationStartDate;

    @NotNull(groups = { ValidationGroups.KpiB2Job.class })
    private LocalDate evaluationEndDate;

    @NotNull(groups = { ValidationGroups.KpiB2Job.class })
    private Long totReq;

    @NotNull(groups = { ValidationGroups.KpiB2Job.class })
    private Double avgTime;

    @NotNull(groups = { ValidationGroups.KpiB2Job.class })
    private Double overTimeLimit;

    @NotNull(groups = { ValidationGroups.KpiB2Job.class })
    private OutcomeStatus outcome;

    private Long kpiB2ResultId;
}
