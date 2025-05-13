package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.KpiConfiguration;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * A DTO for the {@link KpiConfiguration} entity.
 */
@Getter
@Setter
@EqualsAndHashCode
public class KpiConfigurationDTO implements Serializable {

    private static final long serialVersionUID = 4329953951455491176L;

    private Long id;

    private Long moduleId;

    private String moduleCode;

    private Boolean excludePlannedShutdown;

    private Boolean excludeUnplannedShutdown;

    private Double eligibilityThreshold;

    private Double tollerance;

    private Double averageTimeLimit;

    private EvaluationType evaluationType;

    @Override
    public String toString() {
        return (
            "KpiConfigurationDTO{" +
            "id=" +
            id +
            ", moduleId=" +
            moduleId +
            ", moduleCode='" +
            moduleCode +
            '\'' +
            ", excludePlannedShutdown=" +
            excludePlannedShutdown +
            ", excludeUnplannedShutdown=" +
            excludeUnplannedShutdown +
            ", eligibilityThreshold=" +
            eligibilityThreshold +
            ", tollerance=" +
            tollerance +
            ", averageTimeLimit=" +
            averageTimeLimit +
            ", evaluationType=" +
            evaluationType +
            '}'
        );
    }
}
