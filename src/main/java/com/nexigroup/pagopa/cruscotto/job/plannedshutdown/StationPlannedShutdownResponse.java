package com.nexigroup.pagopa.cruscotto.job.plannedshutdown;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class StationPlannedShutdownResponse {
    private List<StationMaintenance> stationMaintenanceList;



    @Getter
    @Setter
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    protected static class StationMaintenance {

        @NotNull
        private Long maintenanceId;

        @NotNull
        private Date startDateTime;

        @NotNull
        private Date endDateTime;

        @NotNull
        private boolean standIn;

        @NotNull
        private String stationCode;

        @NotNull
        private String brokerCode;

        @Override
        public String toString() {
            return "StationMaintenance{" +
                "maintenanceId=" + maintenanceId +
                ", startDateTime=" + startDateTime +
                ", endDateTime=" + endDateTime +
                ", standIn=" + standIn +
                ", stationCode='" + stationCode + '\'' +
                ", brokerCode='" + brokerCode + '\'' +
                '}';
        }
    }

    @Override
    public String toString() {
        return "StationMaintenanceResponse{" +
            "stationMaintenanceList=" + stationMaintenanceList +
            '}';
    }
}

