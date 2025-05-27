package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.KpiB2AnalyticData;
import com.nexigroup.pagopa.cruscotto.service.validation.ValidationGroups;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

/**
 * A DTO for the {@link KpiB2AnalyticData} entity.
 */
@Getter
@Setter
public class KpiB2AnalyticDataDTO implements Serializable {

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
    private Long stationId;

    @NotNull(groups = { ValidationGroups.KpiB2Job.class })
    private String method;

    @NotNull(groups = { ValidationGroups.KpiB2Job.class })
    private LocalDate evaluationDate;

    @NotNull(groups = { ValidationGroups.KpiB2Job.class })
    private Long totReq;

    @NotNull(groups = { ValidationGroups.KpiB2Job.class })
    private Long reqOk;

    @NotNull(groups = { ValidationGroups.KpiB2Job.class })
    private Long reqTimeout;

    @NotNull(groups = { ValidationGroups.KpiB2Job.class })
    private Double avgTime;

    private Long kpiB2DetailResultId;

    private String stationName;

    @Override
    public String toString() {
        return (
            "KpiB2AnalyticDataDTO{" +
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
            ", evaluationDate=" +
            evaluationDate +
            ", totReq=" +
            totReq +
            ", reqOk=" +
            reqOk +
            ", reqTimeout=" +
            reqTimeout +
            ", avgTime=" +
            avgTime +
            ", kpiB2DetailResultId=" +
            kpiB2DetailResultId +
            '}'
        );
    }
}
