package com.nexigroup.pagopa.cruscotto.service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * A DTO for the {@link com.nexigroup.pagopa.cruscotto.domain.KpiC2AnalyticData} entity.
 * Represents daily API usage data for KPI C.2 "API Integration" analysis.
 */
@Getter
@Setter
public class KpiC2AnalyticDataDTO implements Serializable {

    private Long id;

    @NotNull
    private Long instanceId;

    private Long kpiC2DetailResultId;

    @NotNull
    private LocalDate analysisDate;

    @NotNull
    @com.fasterxml.jackson.annotation.JsonProperty("dataDate")
    private LocalDate evaluationDate;

    @com.fasterxml.jackson.annotation.JsonProperty("totalGPD")
    private Long numRequestGpd;

    @com.fasterxml.jackson.annotation.JsonProperty("totalCP")
    private Long numRequestCp;

    // Additional fields for API output
    private String analysisDatePeriod;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KpiC2AnalyticDataDTO)) {
            return false;
        }

        KpiC2AnalyticDataDTO kpiC2AnalyticDataDTO = (KpiC2AnalyticDataDTO) o;
        if (this.id == null) {
            return false;
        }
        return this.id.equals(kpiC2AnalyticDataDTO.id);
    }

    @Override
    public int hashCode() {
        return this.id != null ? this.id.hashCode() : 0;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "KpiC2AnalyticDataDTO{" +
            "id=" + id +
            ", instanceId=" + instanceId +
            ", kpiC2DetailResultId=" + kpiC2DetailResultId +
            ", analysisDate='" + analysisDate + "'" +
            ", evaluationDate='" + evaluationDate + "'" +
            ", numRequestGpd=" + numRequestGpd +
            ", numRequestCp=" + numRequestCp +
            ", analysisDatePeriod='" + analysisDatePeriod + "'" +
            "}";
    }
}
