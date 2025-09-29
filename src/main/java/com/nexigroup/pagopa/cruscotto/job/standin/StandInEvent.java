package com.nexigroup.pagopa.cruscotto.job.standin;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class StandInEvent {
    private String eventId;
    private String stationCode;
    private LocalDateTime timestamp;
    private String eventType;
    private String fiscalCode;
    private String description;
    private int standInCount;
}