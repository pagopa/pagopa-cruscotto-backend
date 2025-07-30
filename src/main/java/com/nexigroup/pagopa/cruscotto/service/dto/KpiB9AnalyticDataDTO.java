package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.KpiB9AnalyticData;
import com.nexigroup.pagopa.cruscotto.service.validation.ValidationGroups;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

/**
 * A DTO for the {@link KpiB9AnalyticData} entity.
 */

@Getter
@Setter
public class KpiB9AnalyticDataDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @NotNull(groups = { ValidationGroups.KpiB9Job.class })
    private Long instanceId;

    @NotNull(groups = { ValidationGroups.KpiB9Job.class })
    private Long instanceModuleId;

    @NotNull(groups = { ValidationGroups.KpiB9Job.class })
    private LocalDate analysisDate;

    @NotNull(groups = { ValidationGroups.KpiB9Job.class })
    private Long stationId;

    @NotNull(groups = { ValidationGroups.KpiB9Job.class })
    private LocalDate evaluationDate;

    @NotNull(groups = { ValidationGroups.KpiB9Job.class })
    private Long totRes;

    @NotNull(groups = { ValidationGroups.KpiB9Job.class })
    private Long resOk;

    @NotNull(groups = { ValidationGroups.KpiB9Job.class })
    private Long resKoReal;

    @NotNull(groups = { ValidationGroups.KpiB9Job.class })
    private Long resKoValid;

    private Long kpiB9DetailResultId;
    
    private String stationName;

    @Override
    public String toString() {
        return (
            "KpiB9AnalyticDataDTO [id=" +
            id +
            ", instanceId=" +
            instanceId +
            ", instanceModuleId=" +
            instanceModuleId +
            ", analysisDate=" +
            analysisDate +
            ", stationId=" +
            stationId +
            ", evaluationDate=" +
            evaluationDate +
            ", totRes=" +
            totRes +
            ", resOk=" +
            resOk +
            ", resKoReal=" +
            resKoReal +
            ", resKoValid=" +
            resKoValid +
            ", kpiB9DetailResultId=" +
            kpiB9DetailResultId +
            "]"
        );
    }
}
