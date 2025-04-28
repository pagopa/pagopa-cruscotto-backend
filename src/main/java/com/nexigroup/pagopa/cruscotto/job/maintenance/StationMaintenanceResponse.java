package com.nexigroup.pagopa.cruscotto.job.maintenance;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class StationMaintenanceResponse {
    private List<StationMaintenance> stationMaintenanceList;



    @Getter
    @Setter
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    private static class StationMaintenance {
        private Long maintenanceId;

        private Date startDateTime;

        private Date endDateTime;

        private boolean standIn;

        private String stationCode;

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

