package com.nexigroup.pagopa.cruscotto.kpi.b6.aggregation;

import com.nexigroup.pagopa.cruscotto.kpi.framework.aggregation.DataAggregator;
import com.nexigroup.pagopa.cruscotto.service.dto.StationDataDTO;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Data aggregator for KPI B.6 - Payment Options
 * Aggregates station data to calculate compliance metrics
 */
@Component
public class StationPaymentOptionsAggregator implements DataAggregator<StationDataDTO, StationPaymentOptionsAggregator.AggregationResult> {
    
    @Data
    public static class AggregationResult {
        private int totalActiveStations;
        private int stationsWithPaymentOptions;
        private double compliancePercentage;
        
        public AggregationResult(int totalActiveStations, int stationsWithPaymentOptions) {
            this.totalActiveStations = totalActiveStations;
            this.stationsWithPaymentOptions = stationsWithPaymentOptions;
            this.compliancePercentage = totalActiveStations > 0 
                ? (double) stationsWithPaymentOptions / totalActiveStations * 100.0 
                : 0.0;
        }
    }
    
    @Override
    public AggregationResult aggregate(List<StationDataDTO> data, LocalDate startDate, LocalDate endDate) {
        // Filter only ACTIVE stations
        List<StationDataDTO> activeStations = data.stream()
                .filter(station -> "ACTIVE".equalsIgnoreCase(station.getStatus()))
                .collect(Collectors.toList());
        
        int totalActive = activeStations.size();
        
        int withPaymentOptions = (int) activeStations.stream()
                .filter(station -> Boolean.TRUE.equals(station.getPaymentOptionsEnabled()))
                .count();
        
        return new AggregationResult(totalActive, withPaymentOptions);
    }
    
    @Override
    public <K> Map<K, AggregationResult> aggregateByGroup(List<StationDataDTO> data, Function<StationDataDTO, K> groupingFunction) {
        return data.stream()
                .collect(Collectors.groupingBy(groupingFunction))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> aggregate(entry.getValue(), null, null)
                ));
    }
    
    @Override
    public String getAggregatorName() {
        return "StationPaymentOptionsAggregator";
    }
}