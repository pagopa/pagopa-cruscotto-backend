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
 * Implements the new reusable KPI framework using generic entities
 */
@Component
public class KpiB6Processor extends AbstractKpiProcessor<KpiResultDTO, KpiDetailResultDTO, KpiAnalyticDataDTO> {
    
    private final StationPaymentOptionsAggregator aggregator;
    
    public KpiB6Processor(StationPaymentOptionsAggregator aggregator, 
                         ToleranceBasedEvaluationStrategy toleranceEvaluationStrategy) {
        super(toleranceEvaluationStrategy);
        this.aggregator = aggregator;
    }
    
    @Override
    public KpiResultDTO processKpiResult(KpiExecutionContext context) {
        logger.info("Processing KPI B.6 result for partner: {}", context.getPartnerFiscalCode());
        
        // Get station data from context (should be loaded by the job)
        @SuppressWarnings("unchecked")
        List<AnagStationDTO> stationData = (List<AnagStationDTO>) context.getAdditionalParameters()
                .getOrDefault("stationData", new ArrayList<>());
        
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
        
        @SuppressWarnings("unchecked")
        List<AnagStationDTO> stationData = (List<AnagStationDTO>) context.getAdditionalParameters()
                .getOrDefault("stationData", new ArrayList<>());
        
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
        // Parse tolerance from kpiResult data JSON (simplified for now)
        BigDecimal tolerance = BigDecimal.ZERO;
        BigDecimal actualPercentage = BigDecimal.valueOf(aggregationResult.getCompliancePercentage()).setScale(2, RoundingMode.HALF_UP);
        
        OutcomeStatus outcome = evaluationStrategy.evaluate(
                actualPercentage, targetPercentage, tolerance, null);
        detailResult.setOutcome(outcome);
        
        return detailResult;
    }
    
    @Override
    public List<KpiAnalyticDataDTO> processAnalyticData(KpiExecutionContext context, KpiDetailResultDTO detailResult) {
        logger.info("Processing KPI B.6 analytic data for detail result ID: {}", detailResult.getId());
        
        @SuppressWarnings("unchecked")
        List<AnagStationDTO> stationData = (List<AnagStationDTO>) context.getAdditionalParameters()
                .getOrDefault("stationData", new ArrayList<>());
        
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
}