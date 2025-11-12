package com.nexigroup.pagopa.cruscotto.service.dto;

import java.io.Serializable;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for IoDrilldown negative evidences.
 */
@Getter
@Setter
public class IoDrilldownDTO implements Serializable {
    private Long id;
    private Long analyticDataId;
    private Long instanceId;
    private Long instanceModuleId;
    private LocalDate referenceDate;
    private LocalDate dataDate;
    private String cfInstitution;
    private String cfPartner;
    private Long positionsCount;
    private Long messagesCount;
    private Double percentage;
    private Boolean meetsTolerance;
}
