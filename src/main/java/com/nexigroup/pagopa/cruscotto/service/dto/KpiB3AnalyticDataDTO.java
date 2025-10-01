package com.nexigroup.pagopa.cruscotto.service.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * A DTO for the {@link com.nexigroup.pagopa.cruscotto.domain.KpiB3AnalyticData} entity.
 */
@Data
public class KpiB3AnalyticDataDTO implements Serializable {

    private Long id;

    private Long instanceId;

    private Long instanceModuleId;

    private Long anagStationId;

    private Long kpiB3DetailResultId;

    private String eventId;

    private String eventType;

    private LocalDateTime eventTimestamp;

    private Integer standInCount;

}