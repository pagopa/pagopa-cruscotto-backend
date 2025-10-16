package com.nexigroup.pagopa.cruscotto.service.dto;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

/**
 * A DTO for the {@link com.nexigroup.pagopa.cruscotto.domain.KpiB4AnalyticData} entity.
 * Represents daily API usage data for KPI B.4 "API Integration" analysis.
 */
@Getter
@Setter
public class KpiB4AnalyticDataDTO implements Serializable {

    private Long id;

    @NotNull
    private Long instanceId;

    private Long kpiB4DetailResultId;

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
        if (!(o instanceof KpiB4AnalyticDataDTO)) {
            return false;
        }

        KpiB4AnalyticDataDTO kpiB4AnalyticDataDTO = (KpiB4AnalyticDataDTO) o;
        if (this.id == null) {
            return false;
        }
        return this.id.equals(kpiB4AnalyticDataDTO.id);
    }

    @Override
    public int hashCode() {
        return this.id != null ? this.id.hashCode() : 0;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "KpiB4AnalyticDataDTO{" +
            "id=" + id +
            ", instanceId=" + instanceId +
            ", kpiB4DetailResultId=" + kpiB4DetailResultId +
            ", analysisDate='" + analysisDate + "'" +
            ", evaluationDate='" + evaluationDate + "'" +
            ", numRequestGpd=" + numRequestGpd +
            ", numRequestCp=" + numRequestCp +
            ", analysisDatePeriod='" + analysisDatePeriod + "'" +
            "}";
    }
}