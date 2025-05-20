package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.KpiA1Result;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.service.validation.ValidationGroups;

import java.io.Serializable;
import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * A DTO for the {@link KpiA1Result} entity.
 */
@Getter
@Setter
public class KpiA1ResultDTO implements Serializable {

	private static final long serialVersionUID = -3635788827709487094L;

	private Long id;

    @NotNull(groups = { ValidationGroups.KpiA1Job.class })
    private Long instanceId;

    @NotNull(groups = { ValidationGroups.KpiA1Job.class })
    private Long instanceModuleId;

    @NotNull(groups = { ValidationGroups.KpiA1Job.class })
    private LocalDate analysisDate;

    @NotNull(groups = { ValidationGroups.KpiA1Job.class })
    private Boolean excludePlannedShutdown;
    
    @NotNull(groups = { ValidationGroups.KpiA1Job.class })
    private Boolean excludeUnplannedShutdown;
    
    @NotNull(groups = { ValidationGroups.KpiA1Job.class })
    private Double eligibilityThreshold;
    
    @NotNull(groups = { ValidationGroups.KpiA1Job.class })
    private Double tollerance;

    @NotNull(groups = { ValidationGroups.KpiA1Job.class })
    private EvaluationType evaluationType;

    @NotNull(groups = { ValidationGroups.KpiA1Job.class })
    private OutcomeStatus outcome;

    
	@Override
	public String toString() {
		return "KpiA1ResultDTO [id=" + id + ", instanceId=" + instanceId + ", instanceModuleId=" + instanceModuleId
				+ ", analysisDate=" + analysisDate + ", excludePlannedShutdown=" + excludePlannedShutdown
				+ ", excludeUnplannedShutdown=" + excludeUnplannedShutdown + ", eligibilityThreshold="
				+ eligibilityThreshold + ", tollerance=" + tollerance + ", evaluationType=" + evaluationType
				+ ", outcome=" + outcome + "]";
	}
}
