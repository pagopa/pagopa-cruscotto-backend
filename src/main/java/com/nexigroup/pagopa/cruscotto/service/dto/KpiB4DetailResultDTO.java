package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

/**
 * A DTO for the {@link com.nexigroup.pagopa.cruscotto.domain.KpiB4DetailResult} entity.
 */
@Getter
@Setter
public class KpiB4DetailResultDTO implements Serializable {

    private Long id;

    @NotNull
    private Long instanceId;

    @NotNull
    private Long instanceModuleId;

    @NotNull
    private Long anagStationId;

    @NotNull
    private LocalDate analysisDate;

    @NotNull
    private EvaluationType evaluationType;

    @NotNull
    private LocalDate evaluationStartDate;

    @NotNull
    private LocalDate evaluationEndDate;

    private Integer totalStandIn;

    private OutcomeStatus outcome;

    private Long kpiB4ResultId;

    // Station details for display
    private String stationCode;
    private String stationName;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KpiB4DetailResultDTO)) {
            return false;
        }

        KpiB4DetailResultDTO kpiB4DetailResultDTO = (KpiB4DetailResultDTO) o;
        if (this.id == null) {
            return false;
        }
        return this.id.equals(kpiB4DetailResultDTO.id);
    }

    @Override
    public int hashCode() {
        return this.id != null ? this.id.hashCode() : 0;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "KpiB4DetailResultDTO{" +
            "id=" + id +
            ", instanceId=" + instanceId +
            ", instanceModuleId=" + instanceModuleId +
            ", anagStationId=" + anagStationId +
            ", analysisDate='" + analysisDate + "'" +
            ", evaluationType='" + evaluationType + "'" +
            ", evaluationStartDate='" + evaluationStartDate + "'" +
            ", evaluationEndDate='" + evaluationEndDate + "'" +
            ", totalStandIn=" + totalStandIn +
            ", outcome='" + outcome + "'" +
            ", kpiB4ResultId=" + kpiB4ResultId +
            ", stationCode='" + stationCode + "'" +
            ", stationName='" + stationName + "'" +
            "}";
    }
}