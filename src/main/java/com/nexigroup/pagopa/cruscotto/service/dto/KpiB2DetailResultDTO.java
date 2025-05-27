package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.KpiB2DetailResult;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.service.validation.ValidationGroups;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

/**
 * A DTO for the {@link KpiB2DetailResult} entity.
 */

@Getter
@Setter
public class KpiB2DetailResultDTO implements Serializable {

    private static final long serialVersionUID = 5141899331669875400L;

    private Long id;

    @NotNull(groups = { ValidationGroups.KpiB2Job.class })
    private Long instanceId;

    @NotNull(groups = { ValidationGroups.KpiB2Job.class })
    private Long instanceModuleId;

    @NotNull(groups = { ValidationGroups.KpiB2Job.class })
    private LocalDate analysisDate;

    @NotNull(groups = { ValidationGroups.KpiB2Job.class })
    private Long stationId;

    @NotNull(groups = { ValidationGroups.KpiB2Job.class })
    private String method;

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

    @Override
    public String toString() {
        return (
            "KpiB2DetailResultDTO{" +
            "id=" +
            id +
            ", instanceId=" +
            instanceId +
            ", instanceModuleId=" +
            instanceModuleId +
            ", analysisDate=" +
            analysisDate +
            ", stationId=" +
            stationId +
            ", method='" +
            method +
            '\'' +
            ", evaluationType=" +
            evaluationType +
            ", evaluationStartDate=" +
            evaluationStartDate +
            ", evaluationEndDate=" +
            evaluationEndDate +
            ", totReq=" +
            totReq +
            ", avgTime=" +
            avgTime +
            ", overTimeLimit=" +
            overTimeLimit +
            ", outcome=" +
            outcome +
            ", kpiB2ResultId=" +
            kpiB2ResultId +
            '}'
        );
    }
}
