package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A DTO for the generic KpiAnalyticData entity.
 * Uses JSON data field for KPI-specific information.
 */
@Data
@EqualsAndHashCode
public class KpiAnalyticDataDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @NotNull
    private ModuleCode moduleCode;

    private Long instanceId;

    private Long instanceModuleId;

    private Long kpiDetailResultId;

    private LocalDate analysisDate;

    private LocalDate dataDate;

    private String stationCode;

    /**
     * JSON field containing KPI-specific data
     */
    private String data;

    private String createdBy;

    private java.time.Instant createdDate;

    private String lastModifiedBy;

    private java.time.Instant lastModifiedDate;
}