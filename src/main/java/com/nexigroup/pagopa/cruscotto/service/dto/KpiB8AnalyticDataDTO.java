package com.nexigroup.pagopa.cruscotto.service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * A DTO for the {@link com.nexigroup.pagopa.cruscotto.domain.KpiB3AnalyticData} entity.
 */
@Data
public class KpiB8AnalyticDataDTO implements Serializable {

    private Long id;

    @NotNull
    private Long instanceId;

    @NotNull
    private Long instanceModuleId;

    @NotNull
    private Long anagStationId;

    @NotNull
    private LocalDate analysisDate;

    @NotNull
    private LocalDate evaluationDate;

    @NotNull
    private Long totReq;

    @NotNull
    private Long reqKO;

    private Long kpiB8DetailResultId;

    // Additional fields for API output
    private String analysisDatePeriod;
}
