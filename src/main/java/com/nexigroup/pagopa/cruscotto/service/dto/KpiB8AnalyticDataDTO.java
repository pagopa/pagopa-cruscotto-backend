package com.nexigroup.pagopa.cruscotto.service.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * A DTO for the {@link com.nexigroup.pagopa.cruscotto.domain.KpiB3AnalyticData} entity.
 */
@Data
public class KpiB8AnalyticDataDTO implements Serializable {

    private Long id;

    private Long instanceId;

    private Long instanceModuleId;

    private LocalDate analysisDate;

    private LocalDate dataDate;

    private Long totReq;

    private Long reqKO;

    private Long kpiB8DetailResultId;


}
