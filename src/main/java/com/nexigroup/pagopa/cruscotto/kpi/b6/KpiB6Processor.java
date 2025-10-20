package com.nexigroup.pagopa.cruscotto.kpi.b6;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.kpi.b6.aggregation.StationPaymentOptionsAggregator;
import com.nexigroup.pagopa.cruscotto.kpi.framework.AbstractKpiProcessor;
import com.nexigroup.pagopa.cruscotto.kpi.framework.KpiExecutionContext;
import com.nexigroup.pagopa.cruscotto.kpi.framework.evaluation.ToleranceBasedEvaluationStrategy;
import com.nexigroup.pagopa.cruscotto.service.dto.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

/**
 * KPI B.6 Processor - Payment Options
 * Implements the new reusable KPI framework with composition over inheritance
 */
@Component
public class KpiB6Processor extends AbstractKpiProcessor<KpiB6ResultDTO, KpiB6DetailResultDTO, KpiB6AnalyticDataDTO> {
    
    private final StationPaymentOptionsAggregator aggregator;
    private final ToleranceBasedEvaluationStrategy toleranceEvaluationStrategy;
    
    public KpiB6Processor(StationPaymentOptionsAggregator aggregator, ToleranceBasedEvaluationStrategy toleranceEvaluationStrategy) {
        super(toleranceEvaluationStrategy);
        this.aggregator = aggregator;
        this.toleranceEvaluationStrategy = toleranceEvaluationStrategy;
    }
    
    @Override
    public KpiB6ResultDTO processKpiResult(KpiExecutionContext context) {
        logger.info("Processing KPI B.6 result for partner: {}", context.getPartnerFiscalCode());
        
        // Get station data from context (should be loaded by the job)
        @SuppressWarnings("unchecked")
        List<StationDataDTO> stationData = (List<StationDataDTO>) context.getAdditionalParameters()
                .getOrDefault("stationData", new ArrayList<>());
        
        // Aggregate overall statistics
        StationPaymentOptionsAggregator.AggregationResult overallResult = 
                aggregator.aggregate(stationData, context.getAnalysisStart(), context.getAnalysisEnd());
        
        // Get tolerance from configuration
        BigDecimal tolerance = context.getConfiguration().getTolerance() != null ? 
                BigDecimal.valueOf(context.getConfiguration().getTolerance()) : BigDecimal.ZERO;
        
        KpiB6ResultDTO result = new KpiB6ResultDTO();
        result.setInstanceId(context.getInstance().getId());
        result.setInstanceModuleId(context.getInstanceModule().getId());
        result.setAnalysisDate(LocalDate.now());
        result.setEvaluationType(context.getConfiguration().getEvaluationType());
        result.setTotalActiveStations(overallResult.getTotalActiveStations());
        result.setStationsWithPaymentOptions(overallResult.getStationsWithPaymentOptions());
        result.setPaymentOptionsPercentage(BigDecimal.valueOf(overallResult.getCompliancePercentage()).setScale(2, RoundingMode.HALF_UP));
        result.setToleranceThreshold(tolerance);
        result.setOutcome(OutcomeStatus.STANDBY); // Will be updated after detail processing
        
        return result;
    }
    
    @Override
    public List<KpiB6DetailResultDTO> processDetailResults(KpiExecutionContext context, KpiB6ResultDTO kpiResult) {
        logger.info("Processing KPI B.6 detail results");
        
        @SuppressWarnings("unchecked")
        List<StationDataDTO> stationData = (List<StationDataDTO>) context.getAdditionalParameters()
                .getOrDefault("stationData", new ArrayList<>());
        
        List<KpiB6DetailResultDTO> detailResults = new ArrayList<>();
        
        // Process monthly results if evaluation type is MESE
        if (context.getConfiguration().getEvaluationType() == EvaluationType.MESE) {
            detailResults.addAll(processMonthlyResults(context, kpiResult, stationData));
        }
        
        // Always add total result
        detailResults.add(processTotalResult(context, kpiResult, stationData));
        
        return detailResults;
    }
    
    private List<KpiB6DetailResultDTO> processMonthlyResults(KpiExecutionContext context, KpiB6ResultDTO kpiResult, List<StationDataDTO> stationData) {
        List<KpiB6DetailResultDTO> monthlyResults = new ArrayList<>();
        
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
            
            KpiB6DetailResultDTO detailResult = createDetailResult(
                    context, kpiResult, EvaluationType.MESE, firstDayOfMonth, lastDayOfMonth, monthResult);
            
            monthlyResults.add(detailResult);
            current = current.plusMonths(1);
        }
        
        return monthlyResults;
    }
    
    private KpiB6DetailResultDTO processTotalResult(KpiExecutionContext context, KpiB6ResultDTO kpiResult, List<StationDataDTO> stationData) {
        StationPaymentOptionsAggregator.AggregationResult totalResult = 
                aggregator.aggregate(stationData, context.getAnalysisStart(), context.getAnalysisEnd());
        
        return createDetailResult(
                context, kpiResult, EvaluationType.TOTALE, 
                context.getAnalysisStart(), context.getAnalysisEnd(), totalResult);
    }
    
    private KpiB6DetailResultDTO createDetailResult(KpiExecutionContext context, KpiB6ResultDTO kpiResult,
                                                   EvaluationType evaluationType, LocalDate startDate, LocalDate endDate,
                                                   StationPaymentOptionsAggregator.AggregationResult aggregationResult) {
        
        KpiB6DetailResultDTO detailResult = new KpiB6DetailResultDTO();
        detailResult.setInstanceId(context.getInstance().getId());
        detailResult.setInstanceModuleId(context.getInstanceModule().getId());
        detailResult.setKpiB6ResultId(kpiResult.getId());
        detailResult.setAnalysisDate(LocalDate.now());
        detailResult.setEvaluationType(evaluationType);
        detailResult.setEvaluationStartDate(startDate);
        detailResult.setEvaluationEndDate(endDate);
        detailResult.setActiveStations(aggregationResult.getTotalActiveStations());
        detailResult.setStationsWithPaymentOptions(aggregationResult.getStationsWithPaymentOptions());
        detailResult.setCompliancePercentage(BigDecimal.valueOf(aggregationResult.getCompliancePercentage()).setScale(2, RoundingMode.HALF_UP));
        
        // Calculate differences from expected (total result)
        int expectedStations = kpiResult.getTotalActiveStations();
        int actualStations = aggregationResult.getStationsWithPaymentOptions();
        detailResult.setStationDifference(actualStations - kpiResult.getStationsWithPaymentOptions());
        detailResult.setStationDifferencePercentage(calculateDifferencePercentage(actualStations, expectedStations));
        
        // Evaluate outcome using tolerance-based strategy
        BigDecimal targetPercentage = BigDecimal.valueOf(100.0); // 100% compliance is the target
        BigDecimal tolerance = kpiResult.getToleranceThreshold();
        BigDecimal actualPercentage = detailResult.getCompliancePercentage();
        
        OutcomeStatus outcome = toleranceEvaluationStrategy.evaluate(
                actualPercentage, targetPercentage, tolerance, null);
        detailResult.setOutcome(outcome);
        
        return detailResult;
    }
    
    @Override
    public List<KpiB6AnalyticDataDTO> processAnalyticData(KpiExecutionContext context, KpiB6DetailResultDTO detailResult) {
        logger.info("Processing KPI B.6 analytic data for period: {} to {}", 
                detailResult.getEvaluationStartDate(), detailResult.getEvaluationEndDate());
        
        @SuppressWarnings("unchecked")
        List<StationDataDTO> stationData = (List<StationDataDTO>) context.getAdditionalParameters()
                .getOrDefault("stationData", new ArrayList<>());
        
        List<KpiB6AnalyticDataDTO> analyticDataList = new ArrayList<>();
        
        for (StationDataDTO station : stationData) {
            if (!"ACTIVE".equalsIgnoreCase(station.getStatus())) {
                continue; // Skip inactive stations
            }
            
            KpiB6AnalyticDataDTO analyticData = new KpiB6AnalyticDataDTO();
            analyticData.setInstanceId(context.getInstance().getId());
            analyticData.setInstanceModuleId(context.getInstanceModule().getId());
            analyticData.setKpiB6DetailResultId(detailResult.getId());
            analyticData.setAnalysisDate(LocalDate.now());
            analyticData.setDataDate(detailResult.getEvaluationStartDate()); // For B.6, use evaluation start date
            analyticData.setStationCode(station.getStationCode());
            analyticData.setStationStatus(station.getStatus());
            analyticData.setPaymentOptionsEnabled(station.getPaymentOptionsEnabled());
            analyticData.setInstitutionFiscalCode(station.getInstitutionFiscalCode());
            analyticData.setPartnerFiscalCode(station.getPartnerFiscalCode());
            
            analyticDataList.add(analyticData);
        }
        
        return analyticDataList;
    }
    
    @Override
    protected OutcomeStatus getDetailOutcome(KpiB6DetailResultDTO detailResult) {
        return detailResult.getOutcome();
    }
    
    @Override
    protected boolean isDetailResultForTotalPeriod(KpiB6DetailResultDTO detailResult) {
        return detailResult.getEvaluationType() == EvaluationType.TOTALE;
    }
    
    @Override
    public String getModuleCode() {
        return ModuleCode.B6.code;
    }
}