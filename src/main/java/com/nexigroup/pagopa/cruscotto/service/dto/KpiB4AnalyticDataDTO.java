package com.nexigroup.pagopa.cruscotto.service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * A DTO for the {@link com.nexigroup.pagopa.cruscotto.domain.KpiB4AnalyticData} entity.
 */
@Getter
@Setter
public class KpiB4AnalyticDataDTO implements Serializable {

    private Long id;

    @NotNull
    private Long instanceId;

    @NotNull
    private Long instanceModuleId;

    @NotNull
    private Long anagStationId;

    private Long kpiB4DetailResultId;

    @Size(max = 255)
    private String eventId;

    @Size(max = 255)
    private String eventType;

    private LocalDateTime eventTimestamp;

    private Integer standInCount;

    // Station details for display
    private String stationCode;
    private String stationName;

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
            ", instanceModuleId=" + instanceModuleId +
            ", anagStationId=" + anagStationId +
            ", kpiB4DetailResultId=" + kpiB4DetailResultId +
            ", eventId='" + eventId + "'" +
            ", eventType='" + eventType + "'" +
            ", eventTimestamp='" + eventTimestamp + "'" +
            ", standInCount=" + standInCount +
            ", stationCode='" + stationCode + "'" +
            ", stationName='" + stationName + "'" +
            "}";
    }
}