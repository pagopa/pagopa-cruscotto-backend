package com.nexigroup.pagopa.cruscotto.service.bean;

import java.math.BigDecimal;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KpiConfigurationRequestBean {

    private Long id;

    @NotBlank
    private String moduleCode;

    private Boolean excludePlannedShutdown;

    private Boolean excludeUnplannedShutdown;

    private Double eligibilityThreshold;

    private Double tolerance;

    private Double averageTimeLimit;

    private EvaluationType evaluationType;

    private Integer institutionCount;

    private Integer transactionCount;

    private BigDecimal institutionTolerance;

    private BigDecimal transactionTolerance;

    private BigDecimal notificationTolerance;

}
