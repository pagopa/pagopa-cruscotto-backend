package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.KpiConfiguration;
import com.nexigroup.pagopa.cruscotto.domain.KpiB4Result;
import com.nexigroup.pagopa.cruscotto.domain.KpiB4DetailResult;
import com.nexigroup.pagopa.cruscotto.domain.KpiB4AnalyticData;
import com.nexigroup.pagopa.cruscotto.domain.Module;
import com.nexigroup.pagopa.cruscotto.domain.AnagStation;
import com.nexigroup.pagopa.cruscotto.domain.InstanceModule;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.repository.InstanceRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiB4ResultRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiB4DetailResultRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiB4AnalyticDataRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiConfigurationRepository;
import com.nexigroup.pagopa.cruscotto.repository.ModuleRepository;
import com.nexigroup.pagopa.cruscotto.repository.PagopaApiLogRepository;
import com.nexigroup.pagopa.cruscotto.repository.AnagStationRepository;
import com.nexigroup.pagopa.cruscotto.service.InstanceModuleService;
import com.nexigroup.pagopa.cruscotto.service.KpiB4Service;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceModuleDTO;
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
    private final KpiB4DetailResultRepository kpiB4DetailResultRepository;
    private final KpiB4AnalyticDataRepository kpiB4AnalyticDataRepository;
    private final KpiConfigurationRepository kpiConfigurationRepository;
    private final InstanceRepository instanceRepository;
    private final ModuleRepository moduleRepository;
    private final PagopaApiLogRepository pagopaApiLogRepository;
    private final AnagStationRepository anagStationRepository;
    private final InstanceModuleService instanceModuleService;
    private final KpiB4ResultMapper kpiB4ResultMapper;

    @Override
    public KpiB4ResultDTO executeKpiB4Calculation(Instance instance) {
        log.info("Starting KPI B4 calculation for instance: {}", instance.getId());

        try {
            // Get KPI configuration for threshold and tolerance
            KpiConfiguration configuration = kpiConfigurationRepository
                .findByModuleCode(ModuleCode.B4.code)
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

            // Find the B4 module and corresponding InstanceModule
            Module moduleB4 = moduleRepository.findByCode(ModuleCode.B4.code)
                .orElseThrow(() -> new RuntimeException("Module B4 not found"));
            
            InstanceModuleDTO instanceModuleDTO = instanceModuleService.findOne(instance.getId(), moduleB4.getId())
                .orElseThrow(() -> new RuntimeException("InstanceModule not found for instance " + instance.getId() + " and module B4"));

            // Create KPI B4 result using existing entity structure
            KpiB4Result result = new KpiB4Result();
            result.setInstance(instance);
            // Set the required instanceModule field to avoid constraint violation
            result.setInstanceModule(instanceModuleService.findById(instanceModuleDTO.getId())
                .orElseThrow(() -> new RuntimeException("InstanceModule entity not found by ID")));
            result.setAnalysisDate(LocalDate.now());
            result.setExcludePlannedShutdown(configuration.getExcludePlannedShutdown() != null ? configuration.getExcludePlannedShutdown() : false);
            result.setExcludeUnplannedShutdown(configuration.getExcludeUnplannedShutdown() != null ? configuration.getExcludeUnplannedShutdown() : false);
            result.setEligibilityThreshold(configuration.getEligibilityThreshold() != null ? configuration.getEligibilityThreshold() : 95.0);
            result.setTolerance(configuration.getTolerance() != null ? configuration.getTolerance() : 5.0);
            result.setEvaluationType(configuration.getEvaluationType() != null ? configuration.getEvaluationType() : EvaluationType.MESE);
            result.setOutcome(outcomeStatus);

            KpiB4Result savedResult = kpiB4ResultRepository.save(result);

            // NUOVO: Popola anche le tabelle di dettaglio KpiB4DetailResult e KpiB4AnalyticData
            createAndSaveDetailResults(savedResult, instance);
            createAndSaveAnalyticData(savedResult, instance);

            log.info("KPI B.4 calculated for instance {}: {}% success rate (threshold: {}%), status: {} - Detail and analytic data saved", 
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

    /**
     * Crea e salva i record KpiB4DetailResult con dati dettagliati per stazione.
     * Questa tabella contiene il dettaglio dei risultati KPI B.4 per ogni stazione dell'ente.
     */
    private void createAndSaveDetailResults(KpiB4Result kpiB4Result, Instance instance) {
        log.info("Creating KPI B.4 detail results for instance {}", instance.getId());

        try {
            // Ottieni tutte le stazioni associate all'ente per creare record dettagliati
            List<AnagStation> stations = anagStationRepository.findByAnagPartnerFiscalCode(instance.getPartner().getFiscalCode());
            
            if (stations.isEmpty()) {
                log.warn("No stations found for partner {}, creating single detail record", 
                        instance.getPartner().getFiscalCode());
                // Se non ci sono stazioni, creiamo un record generico
                stations = List.of(createDefaultStation(instance));
            }

            LocalDate analysisDate = kpiB4Result.getAnalysisDate();
            LocalDate periodStart = instance.getAnalysisPeriodStartDate();
            LocalDate periodEnd = instance.getAnalysisPeriodEndDate();

            for (AnagStation station : stations) {
                KpiB4DetailResult detailResult = new KpiB4DetailResult();
                // Impostare gli ID esplicitamente (necessario per i campi @NotNull)
                detailResult.setInstanceId(instance.getId());
                detailResult.setInstanceModuleId(kpiB4Result.getInstanceModule().getId());
                detailResult.setAnagStationId(station.getId());
                // Impostare anche le relazioni per JPA
                detailResult.setInstance(instance);
                detailResult.setInstanceModule(kpiB4Result.getInstanceModule());
                detailResult.setAnagStation(station);
                detailResult.setAnalysisDate(analysisDate);
                detailResult.setEvaluationType(kpiB4Result.getEvaluationType());
                detailResult.setEvaluationStartDate(periodStart);
                detailResult.setEvaluationEndDate(periodEnd);
                detailResult.setKpiB4Result(kpiB4Result);
                detailResult.setOutcome(kpiB4Result.getOutcome());

                // Calcola il totale stand-in per questa stazione (placeholder per ora)
                // In un'implementazione reale, questo dovrebbe essere calcolato dai dati effettivi
                Integer totalStandIn = calculateStandInForStation(instance, station, periodStart, periodEnd);
                detailResult.setTotalStandIn(totalStandIn);

                kpiB4DetailResultRepository.save(detailResult);
                log.debug("Saved KPI B.4 detail result for station {} (Partner: {})", 
                         station.getId(), instance.getPartner().getFiscalCode());
            }

            log.info("Created {} KPI B.4 detail results for instance {}", stations.size(), instance.getId());

        } catch (Exception e) {
            log.error("Error creating KPI B.4 detail results for instance {}: {}", instance.getId(), e.getMessage(), e);
            throw new RuntimeException("Failed to create KPI B.4 detail results", e);
        }
    }

    /**
     * Crea e salva i record KpiB4AnalyticData con dati analitici giornalieri delle API.
     * Questa tabella contiene l'andamento giornaliero delle chiamate API per l'analisi di drill-down.
     */
    private void createAndSaveAnalyticData(KpiB4Result kpiB4Result, Instance instance) {
        log.info("Creating KPI B.4 analytic data for instance {}", instance.getId());

        try {
            LocalDate periodStart = instance.getAnalysisPeriodStartDate();
            LocalDate periodEnd = instance.getAnalysisPeriodEndDate();
            String partnerFiscalCode = instance.getPartner().getFiscalCode();

            // Ottieni i dati aggregati giornalieri dalla tabella pagopa_apilog
            List<Object[]> dailyAggregatedData = pagopaApiLogRepository.calculateDailyAggregatedData(
                partnerFiscalCode, periodStart, periodEnd);

            if (dailyAggregatedData.isEmpty()) {
                log.warn("No daily aggregated data found for partner {} in period {} to {}", 
                        partnerFiscalCode, periodStart, periodEnd);
                return;
            }

            for (Object[] dayData : dailyAggregatedData) {
                LocalDate evaluationDate = (LocalDate) dayData[0];
                Long gpdAcaTotal = (Long) dayData[1];
                Long paCreateTotal = (Long) dayData[2];

                // Crea record per GPD/ACA se ci sono dati
                if (gpdAcaTotal != null && gpdAcaTotal > 0) {
                    KpiB4AnalyticData gpdAcaAnalyticData = new KpiB4AnalyticData();
                    // Impostare l'ID esplicitamente (necessario per il campo @NotNull)
                    gpdAcaAnalyticData.setInstanceId(instance.getId());
                    // Impostare anche le relazioni per JPA
                    gpdAcaAnalyticData.setInstance(instance);
                    gpdAcaAnalyticData.setAnalysisDate(kpiB4Result.getAnalysisDate());
                    gpdAcaAnalyticData.setEvaluationDate(evaluationDate);
                    gpdAcaAnalyticData.setApiType("GPD/ACA");
                    gpdAcaAnalyticData.setRequestCount(gpdAcaTotal);
                    // gpdAcaAnalyticData.setKpiB4DetailResult(null); // Potresti collegarlo a un detail result specifico

                    kpiB4AnalyticDataRepository.save(gpdAcaAnalyticData);
                }

                // Crea record per paCreate se ci sono dati
                if (paCreateTotal != null && paCreateTotal > 0) {
                    KpiB4AnalyticData paCreateAnalyticData = new KpiB4AnalyticData();
                    // Impostare l'ID esplicitamente (necessario per il campo @NotNull)
                    paCreateAnalyticData.setInstanceId(instance.getId());
                    // Impostare anche le relazioni per JPA
                    paCreateAnalyticData.setInstance(instance);
                    paCreateAnalyticData.setAnalysisDate(kpiB4Result.getAnalysisDate());
                    paCreateAnalyticData.setEvaluationDate(evaluationDate);
                    paCreateAnalyticData.setApiType("paCreate");
                    paCreateAnalyticData.setRequestCount(paCreateTotal);
                    // paCreateAnalyticData.setKpiB4DetailResult(null); // Potresti collegarlo a un detail result specifico

                    kpiB4AnalyticDataRepository.save(paCreateAnalyticData);
                }

                log.debug("Saved KPI B.4 analytic data for date {} - GPD/ACA: {}, paCreate: {}", 
                         evaluationDate, gpdAcaTotal, paCreateTotal);
            }

            log.info("Created {} days of KPI B.4 analytic data for instance {}", 
                    dailyAggregatedData.size(), instance.getId());

        } catch (Exception e) {
            log.error("Error creating KPI B.4 analytic data for instance {}: {}", instance.getId(), e.getMessage(), e);
            throw new RuntimeException("Failed to create KPI B.4 analytic data", e);
        }
    }

    /**
     * Calcola il totale stand-in per una specifica stazione nel periodo di analisi.
     * Questo è un placeholder - nell'implementazione reale dovrebbe interrogare i dati effettivi.
     */
    private Integer calculateStandInForStation(Instance instance, AnagStation station, 
                                              LocalDate periodStart, LocalDate periodEnd) {
        // Placeholder: in una implementazione reale, questo dovrebbe:
        // 1. Interrogare la tabella dei dati stand-in per la stazione specifica
        // 2. Filtrare per il periodo di analisi
        // 3. Sommare gli eventi stand-in
        
        // Per ora, restituiamo un valore fittizio basato sull'ID della stazione
        return (int) (station.getId() % 10); // 0-9 eventi stand-in
    }

    /**
     * Crea una stazione di default quando non ne esistono per il partner.
     */
    private AnagStation createDefaultStation(Instance instance) {
        // Questo è un placeholder - in una implementazione reale potresti voler:
        // 1. Creare una stazione generica nel database
        // 2. Oppure gestire diversamente il caso di partner senza stazioni
        
        AnagStation defaultStation = new AnagStation();
        defaultStation.setId(0L); // ID fittizio
        defaultStation.setAnagPartner(instance.getPartner());
        // Altri campi necessari...
        
        return defaultStation;
    }
}