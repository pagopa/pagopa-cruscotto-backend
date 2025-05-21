package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.KpiA2AnalyticData;
import com.nexigroup.pagopa.cruscotto.service.validation.ValidationGroups;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

/**
 * A DTO for the {@link KpiA2AnalyticData} entity.
 */
@Getter
@Setter
public class KpiA2AnalyticDataDTO implements Serializable {

    private static final long serialVersionUID = 5141899331669875400L;

    private Long id;

    @NotNull(groups = { ValidationGroups.KpiA2Job.class })
    private Long instanceId;

    @NotNull(groups = { ValidationGroups.KpiA2Job.class })
    private Long instanceModuleId;

    @NotNull(groups = { ValidationGroups.KpiA2Job.class })
    private LocalDate analysisDate;

    @NotNull(groups = { ValidationGroups.KpiA2Job.class })
    private LocalDate evaluationDate;

    @NotNull(groups = { ValidationGroups.KpiA2Job.class })
    private Long totPayments;

    @NotNull(groups = { ValidationGroups.KpiA2Job.class })
    private Long totIncorrectPayments;

    private Long kpiA2DetailResultId;

    @Override
    public String toString() {
        return (
            "KpiA2AnalyticDataDTO{" +
            "id=" +
            id +
            ", instanceId=" +
            instanceId +
            ", instanceModuleId=" +
            instanceModuleId +
            ", analysisDate=" +
            analysisDate +
            ", evaluationDate=" +
            evaluationDate +
            ", totPayments=" +
            totPayments +
            ", totIncorrectPayments=" +
            totIncorrectPayments +
            ", kpiA2DetailResultId=" +
            kpiA2DetailResultId +
            '}'
        );
    }
}
