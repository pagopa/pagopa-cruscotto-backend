package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.KpiA1AnalyticData;
import com.nexigroup.pagopa.cruscotto.service.validation.ValidationGroups;

import java.io.Serializable;
import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * A DTO for the {@link KpiA1AnalyticData} entity.
 */
@Getter
@Setter
public class KpiA1AnalyticDataDTO implements Serializable {

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
    private LocalDate evaluationDate;

    @NotNull(groups = { ValidationGroups.KpiA1Job.class })
    private Long totReq;

    @NotNull(groups = { ValidationGroups.KpiA1Job.class })
    private Long reqOk;

    @NotNull(groups = { ValidationGroups.KpiA1Job.class })
    private Long reqTimeoutReal;
    
    @NotNull(groups = { ValidationGroups.KpiA1Job.class })
    private Long reqTimeoutValid;

    private Long kpiA1DetailResultId;
    

    @Override
	public String toString() {
		return "KpiA1AnalyticDataDTO [id=" + id + ", instanceId=" + instanceId + ", instanceModuleId="
				+ instanceModuleId + ", analysisDate=" + analysisDate + ", stationId=" + stationId + ", method="
				+ method + ", evaluationDate=" + evaluationDate + ", totReq=" + totReq + ", reqOk=" + reqOk
				+ ", reqTimeoutReal=" + reqTimeoutReal + ", reqTimeoutValid=" + reqTimeoutValid
				+ ", kpiA1DetailResultId=" + kpiA1DetailResultId + "]";
	}
}
