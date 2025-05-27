package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.KpiConfiguration;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import java.io.Serial;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * A DTO for the {@link KpiConfiguration} entity.
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class KpiConfigurationDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 4329953951455491176L;

    private Long id;

    private Long moduleId;

    private String moduleCode;

    private Boolean excludePlannedShutdown;

    private Boolean excludeUnplannedShutdown;

    private Double eligibilityThreshold;

    private Double tolerance;

    private Double averageTimeLimit;

    private EvaluationType evaluationType;
}
