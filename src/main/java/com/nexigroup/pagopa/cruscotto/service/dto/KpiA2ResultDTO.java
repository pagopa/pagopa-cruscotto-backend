package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.InstanceModule;
import com.nexigroup.pagopa.cruscotto.domain.KpiA2Result;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.service.validation.ValidationGroups;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

/**
 * A DTO for the {@link KpiA2Result} entity.
 */
@Getter
@Setter
public class KpiA2ResultDTO implements Serializable {

    private Long id;

    @NotNull(groups = { ValidationGroups.KpiA1Job.class })
    private Instance instance;

    @NotNull(groups = { ValidationGroups.KpiA1Job.class })
    private Long instanceId;

    @NotNull(groups = { ValidationGroups.KpiA1Job.class })
    private InstanceModule instanceModule;

    @NotNull(groups = { ValidationGroups.KpiA1Job.class })
    private Long instanceModuleId;

    @NotNull(groups = { ValidationGroups.KpiA1Job.class })
    private Instant analysisDate;

    @NotNull(groups = { ValidationGroups.KpiA1Job.class })
    private Double tollerance;

    @NotNull(groups = { ValidationGroups.KpiA1Job.class })
    private EvaluationType evaluationType;

    @NotNull(groups = { ValidationGroups.KpiA1Job.class })
    private OutcomeStatus outcome;

    @Override
    public String toString() {
        return "KpiA2ResultDTO{" +
            "id=" + id +
            ", instance=" + instance +
            ", instanceModule=" + instanceModule +
            ", analysisDate=" + analysisDate +
            ", tollerance=" + tollerance +
            ", evaluationType=" + evaluationType +
            ", outcome=" + outcome +
            '}';
    }
}
