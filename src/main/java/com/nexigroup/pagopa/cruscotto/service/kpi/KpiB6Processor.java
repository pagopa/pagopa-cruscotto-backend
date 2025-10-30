package com.nexigroup.pagopa.cruscotto.service.kpi;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.StationStatus;
import com.nexigroup.pagopa.cruscotto.kpi.b6.aggregation.StationPaymentOptionsAggregator;
import com.nexigroup.pagopa.cruscotto.kpi.framework.AbstractKpiProcessor;
import com.nexigroup.pagopa.cruscotto.kpi.framework.KpiExecutionContext;
import com.nexigroup.pagopa.cruscotto.kpi.framework.evaluation.ToleranceBasedEvaluationStrategy;
import com.nexigroup.pagopa.cruscotto.service.AnagStationService;
import com.nexigroup.pagopa.cruscotto.service.dto.*;
import com.nexigroup.pagopa.cruscotto.service.filter.AnagStationFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

/**
 * KPI B.6 Processor - Payment Options
 * Implements the new reusable KPI framework using generic entities
 */
@Component
public class KpiB6Processor extends AbstractKpiProcessor<KpiResultDTO, KpiDetailResultDTO, KpiAnalyticDataDTO> {
    
    private final StationPaymentOptionsAggregator aggregator;
    private final AnagStationService anagStationService;
    
    public KpiB6Processor(StationPaymentOptionsAggregator aggregator, 
                         ToleranceBasedEvaluationStrategy toleranceEvaluationStrategy,
                         AnagStationService anagStationService) {
        super(toleranceEvaluationStrategy);
        this.aggregator = aggregator;
        this.anagStationService = anagStationService;
    }
    
    @Override
    public KpiResultDTO processKpiResult(KpiExecutionContext context) {
        logger.info("Processing KPI B.6 result for partner: {}", context.getPartnerFiscalCode());
        
        // Load station data once and cache it in context for reuse
        List<AnagStationDTO> stationData = getOrLoadStationData(context);
        
        // Aggregate overall statistics
        StationPaymentOptionsAggregator.AggregationResult overallResult = 
                aggregator.aggregate(stationData, context.getAnalysisStart(), context.getAnalysisEnd());
        
        // Get tolerance from configuration
        BigDecimal tolerance = context.getConfiguration().getTolerance() != null ? 
                BigDecimal.valueOf(context.getConfiguration().getTolerance()) : BigDecimal.ZERO;
        
        KpiResultDTO result = new KpiResultDTO();
        result.setModuleCode(ModuleCode.B6);
        result.setInstanceId(context.getInstance().getId());
        result.setInstanceModuleId(context.getInstanceModule().getId());
        result.setAnalysisDate(LocalDate.now());
        
        // Get evaluation type with default fallback
        EvaluationType evaluationType = null;
        if (context.getConfiguration() != null) {
            evaluationType = context.getConfiguration().getEvaluationType();
        }
        if (evaluationType == null) {
            evaluationType = EvaluationType.TOTALE; // Default for B.6
        }
        
        // Store KPI-specific data as JSON in the additionalData field
        result.setAdditionalData("{" +
            "\"totalActiveStations\": " + overallResult.getTotalActiveStations() + ", " +
            "\"stationsWithPaymentOptions\": " + overallResult.getStationsWithPaymentOptions() + ", " +
            "\"paymentOptionsPercentage\": " + BigDecimal.valueOf(overallResult.getCompliancePercentage()).setScale(2, RoundingMode.HALF_UP) + ", " +
            "\"toleranceThreshold\": " + tolerance + ", " +
            "\"evaluationType\": \"" + evaluationType.name() + "\"" +
            "}");
        
        result.setOutcome(OutcomeStatus.STANDBY); // Will be updated after detail processing
        
        return result;
    }
    
    @Override
    public List<KpiDetailResultDTO> processDetailResults(KpiExecutionContext context, KpiResultDTO kpiResult) {
        logger.info("Processing KPI B.6 detail results");
        
        // Reuse cached station data from context
        List<AnagStationDTO> stationData = getOrLoadStationData(context);
        
        List<KpiDetailResultDTO> detailResults = new ArrayList<>();
        
        // Get evaluation type with default fallback
        EvaluationType evaluationType = null;
        if (context.getConfiguration() != null) {
            evaluationType = context.getConfiguration().getEvaluationType();
        }
        if (evaluationType == null) {
            evaluationType = EvaluationType.TOTALE; // Default for B.6
        }
        
        // Process monthly results if evaluation type is MESE
        if (evaluationType == EvaluationType.MESE) {
            detailResults.addAll(processMonthlyResults(context, kpiResult, stationData));
        }
        
        // Always add total result
        detailResults.add(processTotalResult(context, kpiResult, stationData));
        
        return detailResults;
    }
    
    private List<KpiDetailResultDTO> processMonthlyResults(KpiExecutionContext context, KpiResultDTO kpiResult, List<AnagStationDTO> stationData) {
        List<KpiDetailResultDTO> monthlyResults = new ArrayList<>();
        
        LocalDate current = context.getAnalysisStart().withDayOfMonth(1);
        LocalDate analysisEnd = context.getAnalysisEnd();
        
        while (!current.isAfter(analysisEnd)) {
            LocalDate firstDayOfMonth = current.isBefore(context.getAnalysisStart()) ? 
                    context.getAnalysisStart() : current;
            LocalDate lastDayOfMonth = current.with(TemporalAdjusters.lastDayOfMonth()).isAfter(analysisEnd) ? 
                    analysisEnd : current.with(TemporalAdjusters.lastDayOfMonth());
            
            // For B.6, station data doesn't change by date, so we use the same dataset
            StationPaymentOptionsAggregator.AggregationResult monthResult = 
                    aggregator.aggregate(stationData, firstDayOfMonth, lastDayOfMonth);
            
            KpiDetailResultDTO detailResult = createDetailResult(
                    context, kpiResult, EvaluationType.MESE, firstDayOfMonth, lastDayOfMonth, monthResult);
            
            monthlyResults.add(detailResult);
            current = current.plusMonths(1);
        }
        
        return monthlyResults;
    }
    
    private KpiDetailResultDTO processTotalResult(KpiExecutionContext context, KpiResultDTO kpiResult, List<AnagStationDTO> stationData) {
        StationPaymentOptionsAggregator.AggregationResult totalResult = 
                aggregator.aggregate(stationData, context.getAnalysisStart(), context.getAnalysisEnd());
        
        return createDetailResult(
                context, kpiResult, EvaluationType.TOTALE, 
                context.getAnalysisStart(), context.getAnalysisEnd(), totalResult);
    }
    
    private KpiDetailResultDTO createDetailResult(KpiExecutionContext context, KpiResultDTO kpiResult,
                                                   EvaluationType evaluationType, LocalDate startDate, LocalDate endDate,
                                                   StationPaymentOptionsAggregator.AggregationResult aggregationResult) {
        
        KpiDetailResultDTO detailResult = new KpiDetailResultDTO();
        detailResult.setModuleCode(ModuleCode.B6);
        detailResult.setInstanceId(context.getInstance().getId());
        detailResult.setInstanceModuleId(context.getInstanceModule().getId());
        detailResult.setKpiResultId(kpiResult.getId());
        detailResult.setAnalysisDate(LocalDate.now());
        
        // Store KPI-specific data as JSON in the additionalData field
        detailResult.setAdditionalData("{" +
            "\"activeStations\": " + aggregationResult.getTotalActiveStations() + ", " +
            "\"stationsWithPaymentOptions\": " + aggregationResult.getStationsWithPaymentOptions() + ", " +
            "\"compliancePercentage\": " + BigDecimal.valueOf(aggregationResult.getCompliancePercentage()).setScale(2, RoundingMode.HALF_UP) + ", " +
            "\"evaluationType\": \"" + evaluationType.name() + "\", " +
            "\"evaluationStartDate\": \"" + startDate + "\", " +
            "\"evaluationEndDate\": \"" + endDate + "\"" +
            "}");
        
        // Evaluate outcome using tolerance-based strategy
        BigDecimal targetPercentage = BigDecimal.valueOf(100.0); // 100% compliance is the target
        // Get tolerance from configuration
        BigDecimal tolerance = context.getConfiguration().getTolerance() != null ? 
                BigDecimal.valueOf(context.getConfiguration().getTolerance()) : BigDecimal.ZERO;
        BigDecimal actualPercentage = BigDecimal.valueOf(aggregationResult.getCompliancePercentage()).setScale(2, RoundingMode.HALF_UP);
        
        OutcomeStatus outcome = evaluationStrategy.evaluate(
                actualPercentage, targetPercentage, tolerance, null);
        detailResult.setOutcome(outcome);
        
        return detailResult;
    }
    
    @Override
    public List<KpiAnalyticDataDTO> processAnalyticData(KpiExecutionContext context, KpiDetailResultDTO detailResult) {
        logger.info("Processing KPI B.6 analytic data for detail result ID: {}", detailResult.getId());
        
        // Reuse cached station data from context
        List<AnagStationDTO> stationData = getOrLoadStationData(context);
        
        List<KpiAnalyticDataDTO> analyticDataList = new ArrayList<>();
        
        for (AnagStationDTO station : stationData) {
            if (!com.nexigroup.pagopa.cruscotto.domain.enumeration.StationStatus.ATTIVA.equals(station.getStatus())) {
                continue; // Skip inactive stations
            }
            
            KpiAnalyticDataDTO analyticData = new KpiAnalyticDataDTO();
            analyticData.setModuleCode(ModuleCode.B6);
            analyticData.setInstanceId(context.getInstance().getId());
            analyticData.setInstanceModuleId(context.getInstanceModule().getId());
            analyticData.setKpiDetailResultId(detailResult.getId());
            analyticData.setAnalysisDate(LocalDate.now());
            analyticData.setDataDate(LocalDate.now()); // Use current date since no evaluation date in simplified DTO
            
            // Store KPI-specific data as JSON including station code and all relevant fields
            analyticData.setAnalyticData("{" +
                "\"stationCode\": \"" + station.getName() + "\", " +
                "\"stationStatus\": \"" + station.getStatus().name() + "\", " +
                "\"paymentOptionsEnabled\": " + station.getPaymentOption() + ", " +
                "\"institutionFiscalCode\": \"" + station.getPartnerFiscalCode() + "\", " +
                "\"partnerFiscalCode\": \"" + station.getPartnerFiscalCode() + "\"" +
                "}");
            
            
            analyticDataList.add(analyticData);
        }
        
        return analyticDataList;
    }
    
    @Override
    protected OutcomeStatus getDetailOutcome(KpiDetailResultDTO detailResult) {
        return detailResult.getOutcome();
    }
    
    @Override
    protected boolean isDetailResultForTotalPeriod(KpiDetailResultDTO detailResult) {
        // Since evaluation type is now stored in JSON, we need to parse it
        // For now, we can assume B.6 doesn't use total period evaluation
        return false;
    }
    
    @Override
    public String getModuleCode() {
        return ModuleCode.B6.code;
    }
    
    /**
     * Get station data from context cache or load it if not cached
     */
    @SuppressWarnings("unchecked")
    private List<AnagStationDTO> getOrLoadStationData(KpiExecutionContext context) {
        // Check if station data is already cached in context
        List<AnagStationDTO> cachedData = (List<AnagStationDTO>) context.getAdditionalParameters()
                .get("stationData");
        
        if (cachedData != null) {
            logger.debug("Using cached station data ({} stations)", cachedData.size());
            return cachedData;
        }
        
        // Load station data and cache it in context for future use
        List<AnagStationDTO> stationData = loadStationData(context.getPartnerFiscalCode());
        context.getAdditionalParameters().put("stationData", stationData);
        
        return stationData;
    }
    
    /**
     * Load station data for KPI B.6 processing with freshness validation
     */
    private List<AnagStationDTO> loadStationData(String partnerFiscalCode) {
        logger.info("Loading station data for KPI B.6, partner: {}", partnerFiscalCode);
        
        AnagStationFilter filter = new AnagStationFilter();
        filter.setShowNotActive(false); // Only active stations
        
        Page<AnagStationDTO> stationPage = anagStationService.findAll(filter, PageRequest.of(0, Integer.MAX_VALUE));
        
        // Filter by partner fiscal code and only active stations
        List<AnagStationDTO> stationData = stationPage.getContent().stream()
                .filter(station -> partnerFiscalCode.equals(station.getPartnerFiscalCode()))
                .filter(station -> StationStatus.ATTIVA.equals(station.getStatus()))
                .toList();

        logger.info("Found {} stations for analysis", stationData.size());
        
        // Validate data freshness
        validateDataFreshness(stationData);
        
        return stationData;
    }
    
    /**
     * Validate that station data is fresh (updated recently by LoadRegistryJob)
     */
    private void validateDataFreshness(List<AnagStationDTO> stationData) {
        if (stationData.isEmpty()) {
            logger.warn("No station data found - skipping freshness validation");
            return;
        }
        
        // Find the most recent lastModifiedDate in the station data
        java.time.Instant mostRecentUpdate = stationData.stream()
                .map(AnagStationDTO::getLastModifiedDate)
                .filter(java.util.Objects::nonNull)
                .max(java.time.Instant::compareTo)
                .orElse(null);
        
        if (mostRecentUpdate == null) {
            logger.warn("No lastModifiedDate found in station data - data freshness cannot be validated");
            return;
        }
        
        // Calculate how old the most recent data is
        java.time.Duration dataAge = java.time.Duration.between(mostRecentUpdate, java.time.Instant.now());
        
        // LoadRegistryJob runs weekdays only (cron: 0 0/30 07-8 ? * MON,TUE,WED,THU,FRI *)
        // It runs 4 times per weekday: 07:00, 07:30, 08:00, 08:30
        // We expect data to be updated at least within the last 72 hours to account for:
        // - Weekend gap (Friday 08:30 to Monday 07:00 = ~70.5 hours)
        // - Some buffer time for processing delays
        // - Time zone differences
        long maxDataAgeHours = 72;
        
        if (dataAge.toHours() > maxDataAgeHours) {
            String errorMessage = String.format(
                "Station data is stale! Most recent update was %d hours ago (threshold: %d hours). " +
                "Last update: %s, Current time: %s. LoadRegistryJob may not have run recently.",
                dataAge.toHours(), maxDataAgeHours, mostRecentUpdate, java.time.Instant.now());
            
            logger.error(errorMessage);
            throw new IllegalStateException(errorMessage);
        } else {
            logger.info("Station data is fresh - most recent update was {} hours ago (within {} hour threshold)", 
                    dataAge.toHours(), maxDataAgeHours);
        }
    }
}