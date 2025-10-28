package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.kpi.framework.KpiResult;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A DTO for the generic KpiResult entity.
 * Uses JSON data field for KPI-specific information.
 */
@Data
@EqualsAndHashCode
public class KpiResultDTO implements Serializable, KpiResult {

    private static final long serialVersionUID = 1L;

    private Long id;

    @NotNull
    private ModuleCode moduleCode;

    private Long instanceId;

    private Long instanceModuleId;

    private LocalDate analysisDate;

    private OutcomeStatus outcome;

    /**
     * JSON field containing KPI-specific data
     */
    private String additionalData;

    private String createdBy;

    private java.time.Instant createdDate;

    private String lastModifiedBy;

    private java.time.Instant lastModifiedDate;
}