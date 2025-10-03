package com.nexigroup.pagopa.cruscotto.job.standin;

import com.nexigroup.pagopa.cruscotto.config.ApplicationProperties;
import com.nexigroup.pagopa.cruscotto.domain.PagopaNumeroStandin;
import com.nexigroup.pagopa.cruscotto.job.client.PagoPaStandInClient;
import com.nexigroup.pagopa.cruscotto.repository.PagopaNumeroStandinRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.lang.NonNull;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

/**
 * Job to load and aggregate Stand-In events data from PagoPA API.
 * Runs daily at midnight to collect data from the previous day,
 * aggregating events by 15-minute intervals and by station.
 */
@Component
@DisallowConcurrentExecution
public class LoadStandInDataJob extends QuartzJobBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadStandInDataJob.class);
    
    private static final DateTimeFormatter API_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final ApplicationProperties applicationProperties;
    private final PagoPaStandInClient pagoPaStandInClient;
    private final PagopaNumeroStandinRepository pagopaNumeroStandinRepository;

    public LoadStandInDataJob(
        ApplicationProperties applicationProperties,
        PagoPaStandInClient pagoPaStandInClient,
        PagopaNumeroStandinRepository pagopaNumeroStandinRepository) {
        this.applicationProperties = applicationProperties;
        this.pagoPaStandInClient = pagoPaStandInClient;
        this.pagopaNumeroStandinRepository = pagopaNumeroStandinRepository;
    }

    @Override
    protected void executeInternal(@NonNull JobExecutionContext context) {
        try {
            if (!applicationProperties.getJob().getLoadStandInDataJob().isEnabled()) {
                LOGGER.info("LoadStandInDataJob is disabled, skipping execution");
                return;
            }

            // Determine execution mode: single date or date range
            DateRange dateRange = getDateRange(context);
            if (dateRange.isRange()) {
                LOGGER.info("Starting Stand-In data load for range: {} to {}", dateRange.getFromDate(), dateRange.getToDate());
                loadStandInDataForDateRange(dateRange.getFromDate(), dateRange.getToDate());
            } else {
                LOGGER.info("Starting Stand-In data load for date: {}", dateRange.getFromDate());
                loadStandInDataForDate(dateRange.getFromDate());
            }

            LOGGER.info("LoadStandInDataJob completed successfully");

        } catch (Exception e) {
            LOGGER.error("Error in LoadStandInDataJob: {}", e.getMessage(), e);
            throw new RuntimeException("LoadStandInDataJob failed", e);
        }
    }

    /**
     * Gets the date range from job context
     * Priority: fromDate + toDate -> targetDate -> default (previous day)
     */
    private DateRange getDateRange(JobExecutionContext context) {
        String fromDateStr = context.getMergedJobDataMap().getString("fromDate");
        String toDateStr = context.getMergedJobDataMap().getString("toDate");
        String targetDateStr = context.getMergedJobDataMap().getString("targetDate");
        
        LOGGER.debug("Job parameters - fromDate: '{}', toDate: '{}', targetDate: '{}'", 
                    fromDateStr, toDateStr, targetDateStr);
        
        // Case 1: Date range specified (fromDate and toDate)
        if (fromDateStr != null && !fromDateStr.trim().isEmpty() && 
            toDateStr != null && !toDateStr.trim().isEmpty()) {
            try {
                LocalDate fromDate = LocalDate.parse(fromDateStr.trim(), API_DATE_FORMATTER);
                LocalDate toDate = LocalDate.parse(toDateStr.trim(), API_DATE_FORMATTER);
                
                if (fromDate.isAfter(toDate)) {
                    throw new IllegalArgumentException("fromDate must be before or equal to toDate");
                }
                
                LOGGER.debug("Parsed date range: {} to {}", fromDate, toDate);
                return new DateRange(fromDate, toDate, true);
                
            } catch (Exception e) {
                LOGGER.warn("Invalid date range (fromDate: '{}', toDate: '{}'), using fallback", fromDateStr, toDateStr);
            }
        }
        
        // Case 2: Single target date specified
        if (targetDateStr != null && !targetDateStr.trim().isEmpty()) {
            try {
                LocalDate targetDate = LocalDate.parse(targetDateStr.trim(), API_DATE_FORMATTER);
                LOGGER.debug("Parsed target date: {}", targetDate);
                return new DateRange(targetDate, targetDate, false);
                
            } catch (Exception e) {
                LOGGER.warn("Invalid target date '{}', using default", targetDateStr);
            }
        }
        
        // Case 3: Default to previous day
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LOGGER.debug("Using default date: {}", yesterday);
        return new DateRange(yesterday, yesterday, false);
    }

    /**
     * Helper class to represent date range configuration
     */
    private static class DateRange {
        private final LocalDate fromDate;
        private final LocalDate toDate;
        private final boolean isRange;
        
        public DateRange(LocalDate fromDate, LocalDate toDate, boolean isRange) {
            this.fromDate = fromDate;
            this.toDate = toDate;
            this.isRange = isRange;
        }
        
        public LocalDate getFromDate() { return fromDate; }
        public LocalDate getToDate() { return toDate; }
        public boolean isRange() { return isRange; }
    }

    /**
     * Loads and aggregates Stand-In data for a date range
     */
    private void loadStandInDataForDateRange(LocalDate fromDate, LocalDate toDate) {
        String fromDateStr = fromDate.format(API_DATE_FORMATTER);
        String toDateStr = toDate.format(API_DATE_FORMATTER);

        // Check if data for this date range already exists (for informational purposes only)
        LocalDateTime startOfRange = fromDate.atStartOfDay();
        LocalDateTime endOfRange = toDate.atTime(23, 59, 59);
        
        List<PagopaNumeroStandin> existingData = pagopaNumeroStandinRepository.findByDateRange(startOfRange, endOfRange);
        if (!existingData.isEmpty()) {
            LOGGER.debug("Data already exists for range {} to {} ({} records), will check for updates", 
                       fromDate, toDate, existingData.size());
        }

        try {
            LOGGER.debug("Calling Stand-In API for range {} to {} with parameters: from={}, to={}", fromDate, toDate, fromDateStr, toDateStr);
            
            StandInEventsResponse response;
            try {
                response = pagoPaStandInClient.getStandInEvents(fromDateStr, toDateStr, null);
            } catch (Exception apiException) {
                LOGGER.error("API call failed for range {} to {} with parameters from={}, to={}: {}", 
                           fromDate, toDate, fromDateStr, toDateStr, apiException.getMessage(), apiException);
                throw new RuntimeException("Stand-In API call failed for range " + fromDate + " to " + toDate, apiException);
            }
            
            // API response validation
            if (response == null) {
                LOGGER.warn("API returned null response for range {} to {}", fromDate, toDate);
                return;
            }
            
            LOGGER.debug("API response for range {} to {}: status={}, totalCount={}, events={}",
                       fromDate, toDate, response.getStatus(), response.getTotalCount(), 
                       response.getEvents() != null ? response.getEvents().size() : "null");
            
            if (response.getEvents() == null) {
                LOGGER.warn("API returned null events list for range {} to {} (status: {}, totalCount: {})", 
                           fromDate, toDate, response.getStatus(), response.getTotalCount());
                return;
            }

            List<StandInEvent> events = response.getEvents();
            if (events.isEmpty()) {
                LOGGER.debug("No events found for range {} to {} (status: {}, totalCount: {})", 
                           fromDate, toDate, response.getStatus(), response.getTotalCount());
                return;
            }
            
            LOGGER.debug("Retrieved {} events for range {} to {}", events.size(), fromDate, toDate);

            // Log event types summary
            events.stream()
                .collect(Collectors.groupingBy(StandInEvent::getEventType, Collectors.counting()))
                .forEach((eventType, count) -> 
                    LOGGER.debug("Found {} events of type: {}", count, eventType));

            // Filter only ADD_TO_STANDIN events as per KPI B.3 specification
            List<StandInEvent> addToStandInEvents = events.stream()
                .filter(event -> "ADD_TO_STANDIN".equals(event.getEventType()))
                .collect(Collectors.toList());

            LOGGER.debug("Filtered {} ADD_TO_STANDIN events from {} total events", 
                        addToStandInEvents.size(), events.size());

            if (addToStandInEvents.isEmpty()) {
                LOGGER.info("No ADD_TO_STANDIN events found for range {} to {}", fromDate, toDate);
                return;
            }

            // Group events by date and aggregate each day separately
            Map<LocalDate, List<StandInEvent>> eventsByDate = addToStandInEvents.stream()
                .collect(Collectors.groupingBy(event -> event.getTimestamp().toLocalDate()));

            LOGGER.debug("Events grouped into {} distinct dates", eventsByDate.size());

            List<PagopaNumeroStandin> allAggregatedData = new ArrayList<>();
            
            for (Map.Entry<LocalDate, List<StandInEvent>> dateEntry : eventsByDate.entrySet()) {
                LocalDate eventDate = dateEntry.getKey();
                List<StandInEvent> dayEvents = dateEntry.getValue();
                
                LOGGER.debug("Processing {} events for {}", dayEvents.size(), eventDate);
                
                // Aggregate by quarter hours and by station for this specific day
                List<PagopaNumeroStandin> dayAggregatedData = aggregateEventsByQuarterHourAndStation(
                    dayEvents, eventDate.atStartOfDay());
                
                allAggregatedData.addAll(dayAggregatedData);
            }

            // Save all aggregated data without duplicates
            if (!allAggregatedData.isEmpty()) {
                int savedCount = saveWithoutDuplicates(allAggregatedData);
                LOGGER.info("Saved {} new records for range {} to {} (skipped {} duplicates)", 
                           savedCount, fromDate, toDate, allAggregatedData.size() - savedCount);
            }

        } catch (Exception e) {
            LOGGER.error("Failed to load Stand-In data for range {} to {}: {}", 
                        fromDate, toDate, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Schedules a single job execution for a specific date
     * 
     * @param targetDate the date to load Stand-In data for (format: yyyy-MM-dd)
     */
    public void scheduleJobForSpecificDate(String targetDate) {
        LOGGER.info("Scheduling LoadStandInDataJob for specific date: {}", targetDate);
        // This method can be used by controllers or other services to trigger
        // data loading for a specific date by creating a job with JobDataMap
        // containing the targetDate parameter
    }

    /**
     * Loads and aggregates Stand-In data for a specific date
     */
    private void loadStandInDataForDate(LocalDate targetDate) {
        LocalDateTime startOfDay = targetDate.atStartOfDay();
        LocalDateTime endOfDay = targetDate.atTime(23, 59, 59);

        // Check if data for this date already exists (for informational purposes only)
        List<PagopaNumeroStandin> existingData = pagopaNumeroStandinRepository.findByDateRange(startOfDay, endOfDay);
        if (!existingData.isEmpty()) {
            LOGGER.debug("Data already exists for {} ({} records), will check for updates", targetDate, existingData.size());
        }

        String fromDate = targetDate.format(API_DATE_FORMATTER);
        String toDate = targetDate.format(API_DATE_FORMATTER);

        try {
            LOGGER.debug("Calling Stand-In API for date {} with parameters: from={}, to={}", targetDate, fromDate, toDate);
            
            StandInEventsResponse response;
            try {
                response = pagoPaStandInClient.getStandInEvents(fromDate, toDate, null);
            } catch (Exception apiException) {
                LOGGER.error("API call failed for date {} with parameters from={}, to={}: {}", 
                           targetDate, fromDate, toDate, apiException.getMessage(), apiException);
                throw new RuntimeException("Stand-In API call failed for date " + targetDate, apiException);
            }
            
            // API response validation
            if (response == null) {
                LOGGER.warn("API returned null response for date {}", targetDate);
                return;
            }
            
            LOGGER.debug("API response for date {}: status={}, totalCount={}, events={}",
                       targetDate, response.getStatus(), response.getTotalCount(), 
                       response.getEvents() != null ? response.getEvents().size() : "null");
            
            if (response.getEvents() == null) {
                LOGGER.warn("API returned null events list for date {} (status: {}, totalCount: {})", 
                           targetDate, response.getStatus(), response.getTotalCount());
                return;
            }

            List<StandInEvent> events = response.getEvents();
            if (events.isEmpty()) {
                LOGGER.debug("No events found for date {} (status: {}, totalCount: {})", 
                           targetDate, response.getStatus(), response.getTotalCount());
                return;
            }
            
            LOGGER.debug("Retrieved {} events for date {}", events.size(), targetDate);

            // Log event types summary
            events.stream()
                .collect(Collectors.groupingBy(StandInEvent::getEventType, Collectors.counting()))
                .forEach((eventType, count) -> 
                    LOGGER.debug("Found {} events of type: {}", count, eventType));

            // Filter only ADD_TO_STANDIN events as per KPI B.3 specification
            List<StandInEvent> addToStandInEvents = events.stream()
                .filter(event -> "ADD_TO_STANDIN".equals(event.getEventType()))
                .collect(Collectors.toList());

            if (addToStandInEvents.isEmpty()) {
                LOGGER.info("No ADD_TO_STANDIN events found for date {}", targetDate);
                return;
            }

            LOGGER.debug("Filtered {} ADD_TO_STANDIN events from {} total events", 
                        addToStandInEvents.size(), events.size());

            // Aggregate by quarter hours and by station
            List<PagopaNumeroStandin> aggregatedData = aggregateEventsByQuarterHourAndStation(
                addToStandInEvents, startOfDay);

            // Save aggregated data without duplicates
            if (!aggregatedData.isEmpty()) {
                int savedCount = saveWithoutDuplicates(aggregatedData);
                LOGGER.info("Saved {} new records for date {} (skipped {} duplicates)", 
                           savedCount, targetDate, aggregatedData.size() - savedCount);
            }

        } catch (Exception e) {
            LOGGER.error("Failed to load Stand-In data for date {}: {}", targetDate, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Aggregates events by quarter hours and by station
     */
    private List<PagopaNumeroStandin> aggregateEventsByQuarterHourAndStation(
            List<StandInEvent> events, LocalDateTime dataDate) {

        Map<String, List<StandInEvent>> eventsByStation = events.stream()
            .collect(Collectors.groupingBy(StandInEvent::getStationCode));

        List<PagopaNumeroStandin> aggregatedRecords = new ArrayList<>();

        for (Map.Entry<String, List<StandInEvent>> stationEntry : eventsByStation.entrySet()) {
            String stationCode = stationEntry.getKey();
            List<StandInEvent> stationEvents = stationEntry.getValue();

            // Aggregate by quarter hours
            Map<LocalDateTime, List<StandInEvent>> eventsByQuarter = stationEvents.stream()
                .collect(Collectors.groupingBy(event -> getQuarterHourInterval(event.getTimestamp())));

            for (Map.Entry<LocalDateTime, List<StandInEvent>> quarterEntry : eventsByQuarter.entrySet()) {
                LocalDateTime quarterStart = quarterEntry.getKey();
                LocalDateTime quarterEnd = quarterStart.plusMinutes(15).minusSeconds(1);
                List<StandInEvent> quarterEvents = quarterEntry.getValue();

                // Sum stand-in counts for this quarter hour
                int totalStandInCount = quarterEvents.stream()
                    .mapToInt(StandInEvent::getStandInCount)
                    .sum();

                if (totalStandInCount > 0) {
                    PagopaNumeroStandin record = new PagopaNumeroStandin();
                    record.setStationCode(stationCode);
                    record.setIntervalStart(quarterStart);
                    record.setIntervalEnd(quarterEnd);
                    record.setStandInCount(totalStandInCount);
                    record.setEventType("ADD_TO_STANDIN");
                    record.setDataDate(dataDate);
                    record.setLoadTimestamp(LocalDateTime.now());

                    aggregatedRecords.add(record);
                }
            }
        }

        LOGGER.debug("Aggregated {} events into {} quarter-hour records", 
                    events.size(), aggregatedRecords.size());

        return aggregatedRecords;
    }

    /**
     * Calculates the beginning of the quarter hour for a given timestamp
     */
    private LocalDateTime getQuarterHourInterval(LocalDateTime timestamp) {
        int minutes = timestamp.getMinute();
        int quarterMinutes = (minutes / 15) * 15; // 0, 15, 30, 45
        
        return timestamp.withMinute(quarterMinutes).withSecond(0).withNano(0);
    }

    /**
     * Saves aggregated data while avoiding duplicates
     * @param aggregatedData List of records to save
     * @return Number of new records actually saved
     */
    private int saveWithoutDuplicates(List<PagopaNumeroStandin> aggregatedData) {
        int savedCount = 0;
        int skippedCount = 0;
        
        for (PagopaNumeroStandin record : aggregatedData) {
            // Check if record already exists
            List<PagopaNumeroStandin> existing = pagopaNumeroStandinRepository.findByBusinessKeys(
                record.getStationCode(), 
                record.getIntervalStart(), 
                record.getDataDate(), 
                record.getEventType());
                
            if (existing.isEmpty()) {
                // No duplicate found, save the record
                pagopaNumeroStandinRepository.save(record);
                savedCount++;
            } else {
                // Duplicate found, log and skip
                LOGGER.debug("Skipping duplicate record for station {} at interval {} on {}", 
                           record.getStationCode(), record.getIntervalStart(), record.getDataDate());
                skippedCount++;
            }
        }
        
        if (skippedCount > 0) {
            LOGGER.debug("Skipped {} duplicate records during save operation", skippedCount);
        }
        
        return savedCount;
    }


}