package com.nexigroup.pagopa.cruscotto.service.dto;

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

    private Long anagStationId;

    private Long kpiB8DetailResultId;

    private String eventId;

    private String eventType;

    private LocalDateTime eventTimestamp;

    private Integer standInCount;

    // Additional fields required for API output
    private LocalDate analysisDate;

    private String analysisPeriod;

    private String stationFiscalCode;

}
