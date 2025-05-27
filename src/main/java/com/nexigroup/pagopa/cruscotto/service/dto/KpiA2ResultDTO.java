package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.KpiA2Result;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.service.validation.ValidationGroups;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

/**
 * A DTO for the {@link KpiA2Result} entity.
 */

@Getter
@Setter
public class KpiA2ResultDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @NotNull(groups = { ValidationGroups.KpiA2Job.class })
    private Long instanceId;

    @NotNull(groups = { ValidationGroups.KpiA2Job.class })
    private Long instanceModuleId;

    @NotNull(groups = { ValidationGroups.KpiA2Job.class })
    private LocalDate analysisDate;

    @NotNull(groups = { ValidationGroups.KpiA2Job.class })
    private Double tolerance;

    @NotNull(groups = { ValidationGroups.KpiA2Job.class })
    private OutcomeStatus outcome;

    @Override
    public String toString() {
        return (
            "KpiA2ResultDTO [id=" +
            id +
            ", instanceId=" +
            instanceId +
            ", instanceModuleId=" +
            instanceModuleId +
            ", analysisDate=" +
            analysisDate +
            ", tolerance=" +
            tolerance +
            ", outcome=" +
            outcome +
            "]"
        );
    }
}
