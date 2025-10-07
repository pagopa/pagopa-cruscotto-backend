package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.KpiConfiguration;
import com.nexigroup.pagopa.cruscotto.domain.KpiB4Result;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.repository.InstanceRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiB4ResultRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiConfigurationRepository;
import com.nexigroup.pagopa.cruscotto.service.KpiB4Service;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB4AnalyticDataDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB4DetailResultDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB4ResultDTO;
import com.nexigroup.pagopa.cruscotto.service.mapper.KpiB4ResultMapper;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation for managing KPI B4 calculations and results.
 */
@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class KpiB4ServiceImpl implements KpiB4Service {

    private final KpiB4ResultRepository kpiB4ResultRepository;
    private final KpiConfigurationRepository kpiConfigurationRepository;
    private final InstanceRepository instanceRepository;
    private final KpiB4ResultMapper kpiB4ResultMapper;

    @Override
    public KpiB4ResultDTO executeKpiB4Calculation(Instance instance) {
        log.info("Starting KPI B4 calculation for instance: {}", instance.getId());

        try {
            // Get KPI configuration for threshold and tolerance
            KpiConfiguration configuration = kpiConfigurationRepository
                .findByModuleCode(ModuleCode.B4)
                .orElseThrow(() -> new RuntimeException("KPI B4 configuration not found"));

            // Get analysis period from instance
            LocalDate analysisStartDate = instance.getAnalysisPeriodStartDate();
            LocalDate analysisEndDate = instance.getAnalysisPeriodEndDate();

            // For now, use placeholder values since PagopaApiLog entity is not fully implemented yet
            // In a real implementation, these would query the API log data based on analysisStartDate/analysisEndDate
            long totalPaCreateCalls = 1000L; // Placeholder
            long successfulPaCreateCalls = 990L; // Placeholder - 99% success rate
            // Additional metrics for comprehensive analysis
            @SuppressWarnings("unused") long gpdCalls = 50L; // Placeholder - GPD API calls
            @SuppressWarnings("unused") long acaCalls = 25L; // Placeholder - ACA API calls
            
            log.info("Analysis period: {} to {} for partner: {}", 
                analysisStartDate, analysisEndDate, instance.getPartner().getFiscalCode());

            // Calculate success percentage
            BigDecimal successPercentage = totalPaCreateCalls > 0 ? 
                BigDecimal.valueOf(successfulPaCreateCalls)
                    .divide(BigDecimal.valueOf(totalPaCreateCalls), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100)) :
                BigDecimal.ZERO;

            // Get threshold from configuration (expected success percentage)
            Double thresholdValue = configuration.getEligibilityThreshold();
            BigDecimal threshold = thresholdValue != null ? BigDecimal.valueOf(thresholdValue) : BigDecimal.valueOf(95.0);
            
            // Calculate compliance: success rate meets or exceeds threshold
            boolean isCompliant = successPercentage.compareTo(threshold) >= 0;
            OutcomeStatus outcomeStatus = isCompliant ? OutcomeStatus.OK : OutcomeStatus.KO;

            // Create KPI B4 result using existing entity structure
            KpiB4Result result = new KpiB4Result();
            result.setInstance(instance);
            result.setAnalysisDate(LocalDate.now());
            result.setExcludePlannedShutdown(configuration.getExcludePlannedShutdown() != null ? configuration.getExcludePlannedShutdown() : false);
            result.setExcludeUnplannedShutdown(configuration.getExcludeUnplannedShutdown() != null ? configuration.getExcludeUnplannedShutdown() : false);
            result.setEligibilityThreshold(configuration.getEligibilityThreshold() != null ? configuration.getEligibilityThreshold() : 95.0);
            result.setTolerance(configuration.getTolerance() != null ? configuration.getTolerance() : 5.0);
            result.setEvaluationType(configuration.getEvaluationType() != null ? configuration.getEvaluationType() : EvaluationType.MESE);
            result.setOutcome(outcomeStatus);

            KpiB4Result savedResult = kpiB4ResultRepository.save(result);

            log.info("KPI B.4 calculated for instance {}: {}% success rate (threshold: {}%), status: {}", 
                instance.getId(), successPercentage, threshold, outcomeStatus);

            return kpiB4ResultMapper.toDto(savedResult);

        } catch (Exception e) {
            log.error("Error calculating KPI B.4 for instance {}: {}", instance.getId(), e.getMessage(), e);
            throw new RuntimeException("Failed to calculate KPI B.4", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<KpiB4ResultDTO> getKpiB4Results(String instanceId) {
        log.debug("Finding KPI B4 results for instance: {}", instanceId);
        Long instanceIdLong = Long.valueOf(instanceId);
        Instance instance = instanceRepository.findById(instanceIdLong)
            .orElseThrow(() -> new RuntimeException("Instance not found: " + instanceId));
        
        return kpiB4ResultRepository.findByInstanceOrderByAnalysisDateDesc(instance)
            .stream()
            .map(kpiB4ResultMapper::toDto)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<KpiB4ResultDTO> getKpiB4Results(String instanceId, Pageable pageable) {
        log.debug("Finding KPI B4 results for instance {} with pagination", instanceId);
        Long instanceIdLong = Long.valueOf(instanceId);
        Instance instance = instanceRepository.findById(instanceIdLong)
            .orElseThrow(() -> new RuntimeException("Instance not found: " + instanceId));
        
        return kpiB4ResultRepository.findByInstanceOrderByAnalysisDateDesc(instance, pageable)
            .map(kpiB4ResultMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public KpiB4ResultDTO getKpiB4Result(String instanceId, LocalDateTime analysisDate) {
        log.debug("Finding KPI B4 result for instance {} and date {}", instanceId, analysisDate);
        Long instanceIdLong = Long.valueOf(instanceId);
        Instance instance = instanceRepository.findById(instanceIdLong)
            .orElseThrow(() -> new RuntimeException("Instance not found: " + instanceId));
        
        LocalDate localDate = analysisDate.toLocalDate();
        KpiB4Result result = kpiB4ResultRepository.findByInstanceAndAnalysisDate(instance, localDate);
        return result != null ? kpiB4ResultMapper.toDto(result) : null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<KpiB4DetailResultDTO> getKpiB4DetailResults(String instanceId, LocalDateTime analysisDate) {
        log.debug("Finding KPI B4 detail results for instance {} and date {}", instanceId, analysisDate);
        // For KPI B.4, details are typically aggregated by API type or time period
        // This would be implemented based on specific requirements for detailed breakdown
        return List.of();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<KpiB4DetailResultDTO> getKpiB4DetailResults(String instanceId, LocalDateTime analysisDate, Pageable pageable) {
        log.debug("Finding KPI B4 detail results for instance {} and date {} with pagination", instanceId, analysisDate);
        // Implementation would depend on specific detail requirements
        return Page.empty(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<KpiB4AnalyticDataDTO> getKpiB4AnalyticData(String instanceId, LocalDateTime analysisDate) {
        log.debug("Finding KPI B4 analytic data for instance {} and date {}", instanceId, analysisDate);
        // Analytics might include hourly breakdowns, error categories, API response times, etc.
        // Implementation would depend on specific analytic requirements
        return List.of();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<KpiB4AnalyticDataDTO> getKpiB4AnalyticData(String instanceId, LocalDateTime analysisDate, Pageable pageable) {
        log.debug("Finding KPI B4 analytic data for instance {} and date {} with pagination", instanceId, analysisDate);
        // Implementation would depend on specific analytic requirements
        return Page.empty(pageable);
    }

    @Override
    public void deleteKpiB4Data(String instanceId) {
        log.info("Deleting KPI B4 data for instance: {}", instanceId);
        Long instanceIdLong = Long.valueOf(instanceId);
        Instance instance = instanceRepository.findById(instanceIdLong)
            .orElseThrow(() -> new RuntimeException("Instance not found: " + instanceId));
        
        kpiB4ResultRepository.deleteByInstance(instance);
        log.info("Deleted KPI B4 results for instance {}", instanceId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsKpiB4Calculation(String instanceId, LocalDateTime analysisDate) {
        log.debug("Checking if KPI B4 calculation exists for instance {} and date {}", instanceId, analysisDate);
        Long instanceIdLong = Long.valueOf(instanceId);
        Instance instance = instanceRepository.findById(instanceIdLong)
            .orElseThrow(() -> new RuntimeException("Instance not found: " + instanceId));
        
        LocalDate localDate = analysisDate.toLocalDate();
        return kpiB4ResultRepository.existsByInstanceAndAnalysisDate(instance, localDate);
    }

    @Override
    public KpiB4ResultDTO recalculateKpiB4(String instanceId) {
        log.info("Recalculating KPI B4 for instance: {}", instanceId);
        Long instanceIdLong = Long.valueOf(instanceId);
        Instance instance = instanceRepository.findById(instanceIdLong)
            .orElseThrow(() -> new RuntimeException("Instance not found: " + instanceId));
        
        // Delete existing results first
        kpiB4ResultRepository.deleteByInstance(instance);
        
        // Recalculate with current date
        return executeKpiB4Calculation(instance);
    }
}