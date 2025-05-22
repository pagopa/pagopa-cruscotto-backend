package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.KpiA2DetailResult;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.service.validation.ValidationGroups;

import java.io.Serializable;
import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * A DTO for the {@link KpiA2DetailResult} entity.
 */

@Getter
@Setter
public class KpiA2DetailResultDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	

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
    private Long totPayments;
    
    @NotNull(groups = { ValidationGroups.KpiA2Job.class })
    private Long totIncorrectPayments;    

    @NotNull(groups = { ValidationGroups.KpiA2Job.class })
    private Double errorPercentage;

    @NotNull(groups = { ValidationGroups.KpiA2Job.class })
    private OutcomeStatus outcome;

    private Long kpiA2ResultId;

    
    @Override
	public String toString() {
		return "KpiA2DetailResultDTO [id=" + id + ", instanceId=" + instanceId + ", instanceModuleId="
				+ instanceModuleId + ", analysisDate=" + analysisDate + ", evaluationStartDate=" + evaluationStartDate
				+ ", evaluationEndDate=" + evaluationEndDate + ", totPayments=" + totPayments
				+ ", totIncorrectPayments=" + totIncorrectPayments + ", errorPercentage=" + errorPercentage
				+ ", outcome=" + outcome + ", kpiA2ResultId=" + kpiA2ResultId + "]";
	}
}
