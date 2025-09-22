package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.KpiB9DetailResult;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.service.validation.ValidationGroups;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

/**
 * A DTO for the {@link KpiB9DetailResult} entity.
 */

@Getter
@Setter
public class KpiB9DetailResultDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @NotNull(groups = { ValidationGroups.KpiB9Job.class })
    private Long instanceId;

    @NotNull(groups = { ValidationGroups.KpiB9Job.class })
    private Long instanceModuleId;

    @NotNull(groups = { ValidationGroups.KpiB9Job.class })
    private LocalDate analysisDate;

    @NotNull(groups = { ValidationGroups.KpiB9Job.class })
    private EvaluationType evaluationType;

    @NotNull(groups = { ValidationGroups.KpiB9Job.class })
    private LocalDate evaluationStartDate;

    @NotNull(groups = { ValidationGroups.KpiB9Job.class })
    private LocalDate evaluationEndDate;

    @NotNull(groups = { ValidationGroups.KpiB9Job.class })
    private Long totRes;

    @NotNull(groups = { ValidationGroups.KpiB9Job.class })
    private Long resKo;

    @NotNull(groups = { ValidationGroups.KpiB9Job.class })
    private Double resKoPercentage;

    @NotNull(groups = { ValidationGroups.KpiB9Job.class })
    private OutcomeStatus outcome;

    private Long kpiB9ResultId;

    @Override
    public String toString() {
        return (
            "KpiB9DetailResultDTO [id=" +
            id +
            ", instanceId=" +
            instanceId +
            ", instanceModuleId=" +
            instanceModuleId +
            ", analysisDate=" +
            analysisDate +
            ", evaluationType=" +
            evaluationType +
            ", evaluationStartDate=" +
            evaluationStartDate +
            ", evaluationEndDate=" +
            evaluationEndDate +
            ", totRes=" +
            totRes +
            ", resKo=" +
            resKo +
            ", resKoPercentage=" +
            resKoPercentage +
            ", outcome=" +
            outcome +
            ", kpiB9ResultId=" +
            kpiB9ResultId +
            "]"
        );
    }
}
