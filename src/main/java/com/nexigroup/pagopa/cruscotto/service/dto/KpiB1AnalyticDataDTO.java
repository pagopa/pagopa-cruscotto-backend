package com.nexigroup.pagopa.cruscotto.service.dto;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * A DTO for the {@link com.nexigroup.pagopa.cruscotto.domain.KpiB1AnalyticData} entity.
 */
@Data
public class KpiB1AnalyticDataDTO implements Serializable {

    private Long id;

    private Long instanceId;

    private Long instanceModuleId;

    private LocalDate analysisDate;

    private LocalDate dataDate;

    private Integer institutionCount;

    private Integer transactionCount;

    private Long kpiB1DetailResultId;
}