package com.nexigroup.pagopa.cruscotto.job.standin;

import lombok.Data;
import java.util.List;

@Data
public class StandInEventsResponse {
    private List<StandInEvent> events;
    private int totalCount;
    private String status;
}