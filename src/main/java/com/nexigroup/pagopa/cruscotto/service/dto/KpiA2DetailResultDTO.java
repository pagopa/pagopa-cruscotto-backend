package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.KpiB2DetailResult;
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
public class KpiA2DetailResultDTO implements Serializable {

    private static final long serialVersionUID = 5141899331669875400L;

    private Long id;

    @NotNull(groups = { ValidationGroups.KpiA2Job.class })
    private Long instanceId;

    @NotNull(groups = { ValidationGroups.KpiA2Job.class })
    private Long instanceModuleId;

    @NotNull(groups = { ValidationGroups.KpiA2Job.class })
    private LocalDate analysisDate;

    @NotNull(groups = { ValidationGroups.KpiA2Job.class })
    private LocalDate evaluationStartDate;

    @NotNull(groups = { ValidationGroups.KpiA2Job.class })
    private LocalDate evaluationEndDate;

    @NotNull(groups = { ValidationGroups.KpiA2Job.class })
    private Integer totPayments;

    @NotNull(groups = { ValidationGroups.KpiA2Job.class })
    private Integer totIncorrectPayments;

    @NotNull(groups = { ValidationGroups.KpiA2Job.class })
    private Double errorPercentage;

    @NotNull(groups = { ValidationGroups.KpiA2Job.class })
    private OutcomeStatus outcome;

    private Long kpiA2ResultId;

    @Override
    public String toString() {
        return "KpiA2DetailResultDTO{" +
            "id=" + id +
            ", instanceId=" + instanceId +
            ", instanceModuleId=" + instanceModuleId +
            ", analysisDate=" + analysisDate +
            ", evaluationStartDate=" + evaluationStartDate +
            ", evaluationEndDate=" + evaluationEndDate +
            ", totPayments=" + totPayments +
            ", totIncorrectPayments=" + totIncorrectPayments +
            ", errorPercentage=" + errorPercentage +
            ", outcome=" + outcome +
            ", kpiA2ResultId=" + kpiA2ResultId +
            '}';
    }
}
