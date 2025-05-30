package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.KpiB9Result;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.service.validation.ValidationGroups;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

/**
 * A DTO for the {@link KpiB9Result} entity.
 */

@Getter
@Setter
public class KpiB9ResultDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @NotNull(groups = { ValidationGroups.KpiB9Job.class })
    private Long instanceId;

    @NotNull(groups = { ValidationGroups.KpiB9Job.class })
    private Long instanceModuleId;

    @NotNull(groups = { ValidationGroups.KpiB9Job.class })
    private LocalDate analysisDate;

    @NotNull(groups = { ValidationGroups.KpiB9Job.class })
    private Boolean excludePlannedShutdown;

    @NotNull(groups = { ValidationGroups.KpiB9Job.class })
    private Boolean excludeUnplannedShutdown;

    @NotNull(groups = { ValidationGroups.KpiB9Job.class })
    private Double eligibilityThreshold;

    @NotNull(groups = { ValidationGroups.KpiB9Job.class })
    private Double tolerance;

    @NotNull(groups = { ValidationGroups.KpiB9Job.class })
    private EvaluationType evaluationType;

    @NotNull(groups = { ValidationGroups.KpiB9Job.class })
    private OutcomeStatus outcome;

    @Override
    public String toString() {
        return (
            "KpiB9ResultDTO [id=" +
            id +
            ", instanceId=" +
            instanceId +
            ", instanceModuleId=" +
            instanceModuleId +
            ", analysisDate=" +
            analysisDate +
            ", excludePlannedShutdown=" +
            excludePlannedShutdown +
            ", excludeUnplannedShutdown=" +
            excludeUnplannedShutdown +
            ", eligibilityThreshold=" +
            eligibilityThreshold +
            ", tolerance=" +
            tolerance +
            ", evaluationType=" +
            evaluationType +
            ", outcome=" +
            outcome +
            "]"
        );
    }
}
