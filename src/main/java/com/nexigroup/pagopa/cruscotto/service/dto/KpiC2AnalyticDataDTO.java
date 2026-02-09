package com.nexigroup.pagopa.cruscotto.service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * A DTO for the {@link com.nexigroup.pagopa.cruscotto.domain.KpiC2AnalyticData} entity.
 * Represents daily API usage data for KPI C.2 "API Integration" analysis.
 */
@Getter
@Setter
public class KpiC2AnalyticDataDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    @NotNull
    private Long id;

    // === Relazioni e chiavi esterne ===
    @NotNull
    private Long instanceId;
    @NotNull
    private Long instanceModuleId;
    @NotNull
    private Long kpiC2DetailResultId;
    @NotNull
    private LocalDate analysisDate;
    @NotNull
    private LocalDate evaluationDate;

    // === Dati specifici KPI ===

    @NotNull
    private Long numInstitution;
    @NotNull
    private Long numInstitutionSend;
    @NotNull
    private BigDecimal perInstitutionSend;

    @NotNull
    private Long numPayment;
    @NotNull
    private long numNotification;
    @NotNull
    private BigDecimal perNotification;

    // Additional fields for API output
    @NotNull
    private String analysisDatePeriod;



    @Override
    public String toString() {
        return "KpiC2AnalyticDataDTO{" +
            "id=" + id +
            ", instanceId=" + instanceId +
            ", instanceModuleId=" + instanceModuleId +
            ", kpiC2DetailResultId=" + kpiC2DetailResultId +
            ", analysisDate=" + analysisDate +
            ", evaluationDate=" + evaluationDate +
            ", numInstitution=" + numInstitution +
            ", numInstitutionSend=" + numInstitutionSend +
            ", perInstitutionSend=" + perInstitutionSend +
            ", numPayment=" + numPayment +
            ", numNotification=" + numNotification +
            ", perNotification=" + perNotification +
            ", analysisDatePeriod='" + analysisDatePeriod + '\'' +
            '}';
    }


}
