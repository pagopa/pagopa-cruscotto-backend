package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.KpiB2DetailResult;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.service.validation.ValidationGroups;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * A DTO for the {@link KpiB2DetailResult} entity.
 */
@Getter
@Setter
public class KpiA1DetailResultDTO implements Serializable {

    private static final long serialVersionUID = 5141899331669875400L;

    private Long id;

    @NotNull(groups = { ValidationGroups.KpiA1Job.class })
    private Long instanceId;

    @NotNull(groups = { ValidationGroups.KpiA1Job.class })
    private Long instanceModuleId;

    @NotNull(groups = { ValidationGroups.KpiA1Job.class })
    private LocalDate analysisDate;

    @NotNull(groups = { ValidationGroups.KpiA1Job.class })
    private Long stationId;

    @NotNull(groups = { ValidationGroups.KpiA1Job.class })
    private String method;

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

    @Override
    public String toString() {
        return "KpiA1DetailResultDTO{" +
            "id=" + id +
            ", instanceId=" + instanceId +
            ", instanceModuleId=" + instanceModuleId +
            ", analysisDate=" + analysisDate +
            ", stationId=" + stationId +
            ", method='" + method + '\'' +
            ", evaluationType=" + evaluationType +
            ", evaluationStartDate=" + evaluationStartDate +
            ", evaluationEndDate=" + evaluationEndDate +
            ", totReq=" + totReq +
            ", reqTimeout=" + reqTimeout +
            ", timeoutPercentage=" + timeoutPercentage +
            ", outcome=" + outcome +
            ", kpiA1ResultId=" + kpiA1ResultId +
            '}';
    }
}
