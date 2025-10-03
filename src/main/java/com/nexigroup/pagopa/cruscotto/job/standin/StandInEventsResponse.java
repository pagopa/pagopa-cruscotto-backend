package com.nexigroup.pagopa.cruscotto.job.standin;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class StandInEventsResponse {
    @JsonProperty("dateFrom")
    private String dateFrom;
    
    @JsonProperty("dateTo")
    private String dateTo;
    
    @JsonProperty("count")
    private Integer totalCount;
    
    @JsonProperty("data")
    private List<StandInEvent> events;
    
    // Computed properties for backward compatibility
    public String getStatus() {
        return events != null && !events.isEmpty() ? "SUCCESS" : "NO_DATA";
    }
    
    // Getter for totalCount that handles null values
    public int getTotalCount() {
        return totalCount != null ? totalCount : (events != null ? events.size() : 0);
    }
}