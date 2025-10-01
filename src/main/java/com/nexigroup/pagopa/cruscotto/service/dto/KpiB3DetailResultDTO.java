package com.nexigroup.pagopa.cruscotto.service.dto;

import java.io.Serializable;
import java.time.LocalDate;
import lombok.Data;

/**
 * A DTO for the {@link com.nexigroup.pagopa.cruscotto.domain.KpiB3DetailResult} entity.
 */
@Data
public class KpiB3DetailResultDTO implements Serializable {

    private Long id;

    private Long instanceId;

    private Long instanceModuleId;

    private Long anagStationId;

    private Long kpiB3ResultId;

    private LocalDate analysisDate;

    private Integer totalIncidents;

    private Integer totalEvents;

    private Boolean outcome;

}