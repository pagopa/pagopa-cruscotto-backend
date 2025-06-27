package com.nexigroup.pagopa.cruscotto.job.plannedshutdown;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class StationPlannedShutdown {

    private List<StationMaintenance> stationMaintenanceList;

    @Getter
    @Setter
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    protected static class StationMaintenance {

        private Long maintenanceId;

        private Date startDateTime;

        private Date endDateTime;

        private Boolean standIn;

        private String stationCode;

        private String brokerCode;

        @Override
        public String toString() {
            return (
                "StationMaintenance{" +
                "maintenanceId=" +
                maintenanceId +
                ", startDateTime=" +
                startDateTime +
                ", endDateTime=" +
                endDateTime +
                ", standIn=" +
                standIn +
                ", stationCode='" +
                stationCode +
                '\'' +
                ", brokerCode='" +
                brokerCode +
                '\'' +
                '}'
            );
        }
    }

    @Override
    public String toString() {
        return "StationMaintenanceResponse{" + "stationMaintenanceList=" + stationMaintenanceList + '}';
    }
}
