package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.KpiConfiguration;
import com.nexigroup.pagopa.cruscotto.domain.KpiB4Result;
import com.nexigroup.pagopa.cruscotto.domain.KpiB4DetailResult;
import com.nexigroup.pagopa.cruscotto.domain.KpiB4AnalyticData;
import com.nexigroup.pagopa.cruscotto.domain.Module;
import com.nexigroup.pagopa.cruscotto.domain.AnagStation;
import com.nexigroup.pagopa.cruscotto.domain.PagopaApiLogDrilldown;
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
import com.nexigroup.pagopa.cruscotto.repository.PagopaApiLogDrilldownRepository;
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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
    private final PagopaApiLogDrilldownRepository pagopaApiLogDrilldownRepository;
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

            // CORREZIONE BUG: Dopo aver creato i detail results, ricalcola l'outcome del result principale
            // basandosi sui detail results effettivi invece che sui dati placeholder
            log.info("BEFORE correction - KPI B.4 result {} has outcome: {}, evaluationType: {}", 
                savedResult.getId(), savedResult.getOutcome(), savedResult.getEvaluationType());
            
            OutcomeStatus correctedOutcome = calculateOutcomeFromDetailResults(savedResult);
            
            log.info("AFTER calculation - correctedOutcome: {}, original outcome: {}", 
                correctedOutcome, savedResult.getOutcome());
            
            if (correctedOutcome != savedResult.getOutcome()) {
                log.warn("CORRECTING KPI B.4 outcome for instance {} (result id: {}) from {} to {} based on detail results",
                    instance.getId(), savedResult.getId(), savedResult.getOutcome(), correctedOutcome);
                savedResult.setOutcome(correctedOutcome);
                savedResult = kpiB4ResultRepository.save(savedResult);
                log.info("SAVED corrected outcome to database for result id: {}", savedResult.getId());
            } else {
                log.info("Outcome already correct, no update needed for result id: {}", savedResult.getId());
            }

            log.info("KPI B.4 calculated for instance {}: final status: {} - Detail and analytic data saved",
                instance.getId(), correctedOutcome);

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
        log.info("Creating KPI B.4 detail results for instance {} (partner-level aggregated)", instance.getId());

        try {
            // Verifica che il partner abbia stazioni (necessario per calcolare il KPI B.4)
            List<AnagStation> stations = anagStationRepository.findByAnagPartnerFiscalCode(instance.getPartner().getFiscalCode());

            if (stations.isEmpty()) {
                log.warn("SKIPPING KPI B.4 detail results for partner {} - No stations found. Cannot calculate KPI B.4 without stations.",
                        instance.getPartner().getFiscalCode());
                return; // Salta il partner se non ha stazioni associate
            }

            LocalDate analysisDate = kpiB4Result.getAnalysisDate();
            LocalDate periodStart = instance.getAnalysisPeriodStartDate();
            LocalDate periodEnd = instance.getAnalysisPeriodEndDate();
            String partnerFiscalCode = instance.getPartner().getFiscalCode();

            // Ottieni la prima stazione per il campo obbligatorio (il KPI B.4 è a livello partner, non per singola stazione)
            AnagStation primaryStation = stations.get(0);

            // Calcola tutti i mesi nel periodo di analisi
            List<YearMonth> monthsInPeriod = getMonthsInPeriod(periodStart, periodEnd);

            int totalApiCallsAllMonths = 0;

            // Calcola i dati per ogni mese
            for (YearMonth yearMonth : monthsInPeriod) {
                LocalDate monthStart = yearMonth.atDay(1);
                LocalDate monthEnd = yearMonth.atEndOfMonth();

                // Assicurati che le date siano all'interno del periodo di analisi
                if (monthStart.isBefore(periodStart)) {
                    monthStart = periodStart;
                }
                if (monthEnd.isAfter(periodEnd)) {
                    monthEnd = periodEnd;
                }


                // Calcola GPD e CP separatamente per questo mese
                Long monthlyGpdCalls = pagopaApiLogRepository.calculateTotalGpdAcaRequests(partnerFiscalCode, monthStart, monthEnd);
                Long monthlyCpCalls = pagopaApiLogRepository.calculateTotalPaCreateRequests(partnerFiscalCode, monthStart, monthEnd);

                if (monthlyGpdCalls == null) monthlyGpdCalls = 0L;
                if (monthlyCpCalls == null) monthlyCpCalls = 0L;

                totalApiCallsAllMonths += (monthlyGpdCalls + monthlyCpCalls);

                // Calcola percentuale CP per questo mese
                BigDecimal monthlyPercentageCp = BigDecimal.ZERO;
                if (monthlyGpdCalls > 0) {
                    monthlyPercentageCp = new BigDecimal(monthlyCpCalls)
                        .multiply(new BigDecimal("100"))
                        .divide(new BigDecimal(monthlyGpdCalls), 2, RoundingMode.HALF_UP);
                }

                // Crea record mensile (PARTNER LEVEL - aggregato per tutto il partner)
                KpiB4DetailResult monthlyDetailResult = new KpiB4DetailResult();
                monthlyDetailResult.setInstanceId(instance.getId());
                monthlyDetailResult.setInstanceModuleId(kpiB4Result.getInstanceModule().getId());
                monthlyDetailResult.setAnagStationId(primaryStation.getId()); // Campo obbligatorio, usa la prima stazione
                monthlyDetailResult.setInstance(instance);
                monthlyDetailResult.setInstanceModule(kpiB4Result.getInstanceModule());
                monthlyDetailResult.setAnagStation(primaryStation);
                monthlyDetailResult.setKpiB4Result(kpiB4Result);
                monthlyDetailResult.setAnalysisDate(analysisDate);
                monthlyDetailResult.setEvaluationType(com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType.MESE);
                monthlyDetailResult.setEvaluationStartDate(monthStart);
                monthlyDetailResult.setEvaluationEndDate(monthEnd);
                monthlyDetailResult.setSumTotGpd(monthlyGpdCalls); // Totale GPD+ACA del mese
                monthlyDetailResult.setSumTotCp(monthlyCpCalls); // Totale CP del mese
                monthlyDetailResult.setPerApiCp(monthlyPercentageCp); // % CP del mese
                monthlyDetailResult.setOutcome(calculateDetailResultOutcome(monthlyPercentageCp, kpiB4Result)); // Calcola outcome specifico per questo detail result

                kpiB4DetailResultRepository.save(monthlyDetailResult);
                log.debug("Saved monthly KPI B.4 detail result for partner {} in {}: {} GPD, {} CP",
                         partnerFiscalCode, yearMonth, monthlyGpdCalls, monthlyCpCalls);
            }

            // Calcola totali per l'intero periodo
            Long totalGpdCalls = pagopaApiLogRepository.calculateTotalGpdAcaRequests(partnerFiscalCode, periodStart, periodEnd);
            Long totalCpCalls = pagopaApiLogRepository.calculateTotalPaCreateRequests(partnerFiscalCode, periodStart, periodEnd);

            if (totalGpdCalls == null) totalGpdCalls = 0L;
            if (totalCpCalls == null) totalCpCalls = 0L;

            // Calcola percentuale CP per l'intero periodo
            BigDecimal totalPercentageCp = BigDecimal.ZERO;
            if (totalGpdCalls > 0) {
                totalPercentageCp = new BigDecimal(totalCpCalls)
                    .multiply(new BigDecimal("100"))
                    .divide(new BigDecimal(totalGpdCalls), 2, RoundingMode.HALF_UP);
            }

            // Crea record totale (intero periodo di analisi, livello partner)
            KpiB4DetailResult totalDetailResult = new KpiB4DetailResult();
            totalDetailResult.setInstanceId(instance.getId());
            totalDetailResult.setInstanceModuleId(kpiB4Result.getInstanceModule().getId());
            totalDetailResult.setAnagStationId(primaryStation.getId()); // Campo obbligatorio, usa la prima stazione
            totalDetailResult.setInstance(instance);
            totalDetailResult.setInstanceModule(kpiB4Result.getInstanceModule());
            totalDetailResult.setAnagStation(primaryStation);
            totalDetailResult.setKpiB4Result(kpiB4Result);
            totalDetailResult.setAnalysisDate(analysisDate);
            totalDetailResult.setEvaluationType(com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType.TOTALE);
            totalDetailResult.setEvaluationStartDate(periodStart);
            totalDetailResult.setEvaluationEndDate(periodEnd);
            totalDetailResult.setSumTotGpd(totalGpdCalls); // Totale GPD+ACA dell'intero periodo
            totalDetailResult.setSumTotCp(totalCpCalls); // Totale CP dell'intero periodo
            totalDetailResult.setPerApiCp(totalPercentageCp); // % CP dell'intero periodo
            totalDetailResult.setOutcome(calculateDetailResultOutcome(totalPercentageCp, kpiB4Result)); // Calcola outcome specifico per questo detail result

            kpiB4DetailResultRepository.save(totalDetailResult);

            log.info("Created {} monthly + 1 total KPI B.4 detail results for partner {} with {} total API calls",
                    monthsInPeriod.size(), partnerFiscalCode, totalApiCallsAllMonths);

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

            // Ottieni i KpiB4DetailResult salvati per questo risultato
            List<KpiB4DetailResult> detailResults = kpiB4DetailResultRepository.findByKpiB4Result(kpiB4Result);

            if (detailResults.isEmpty()) {
                log.warn("No detail results found for KPI B.4 result {}, cannot create analytic data", kpiB4Result.getId());
                return;
            }

            // Crea una mappa dei detail result per periodo di valutazione per associare correttamente i dati analitici
            Map<LocalDate, KpiB4DetailResult> detailResultsByDate = new HashMap<>();
            KpiB4DetailResult totalDetailResult = null;

            for (KpiB4DetailResult detailResult : detailResults) {
                if (detailResult.getEvaluationType() == com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType.TOTALE) {
                    totalDetailResult = detailResult;
                } else {
                    // Per i record mensili, usa la data di inizio del periodo come chiave
                    detailResultsByDate.put(detailResult.getEvaluationStartDate(), detailResult);
                }
            }

            // Usa il record TOTALE come fallback se non troviamo il detail result specifico per una data
            KpiB4DetailResult fallbackDetailResult = totalDetailResult != null ? totalDetailResult : detailResults.get(0);

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

                // Gestione valori null
                if (gpdAcaTotal == null) gpdAcaTotal = 0L;
                if (paCreateTotal == null) paCreateTotal = 0L;

                // Trova il detail result corretto per questa data
                KpiB4DetailResult appropriateDetailResult = findDetailResultForDate(evaluationDate, detailResultsByDate, fallbackDetailResult);

                // Crea UN SOLO record per giornata con entrambi i valori GPD e CP
                // (secondo l'analisi: "Numero Request GPD" e "Numero Request CP" per ogni giornata)
                KpiB4AnalyticData dailyAnalyticData = new KpiB4AnalyticData();
                dailyAnalyticData.setInstanceId(instance.getId());
                dailyAnalyticData.setInstance(instance);
                dailyAnalyticData.setAnalysisDate(kpiB4Result.getAnalysisDate());
                dailyAnalyticData.setEvaluationDate(evaluationDate);
                dailyAnalyticData.setNumRequestGpd(gpdAcaTotal); // Numero Request GPD del giorno
                dailyAnalyticData.setNumRequestCp(paCreateTotal); // Numero Request CP del giorno
                dailyAnalyticData.setKpiB4DetailResult(appropriateDetailResult);

                KpiB4AnalyticData savedAnalyticData = kpiB4AnalyticDataRepository.save(dailyAnalyticData);

                log.debug("Saved KPI B.4 analytic data for date {} - GPD: {}, CP: {} (linked to detail result {})",
                         evaluationDate, gpdAcaTotal, paCreateTotal, appropriateDetailResult.getId());

                // Popola la tabella drilldown con i dati API log dettagliati per questa data
                populateDrilldownData(savedAnalyticData, instance, evaluationDate);
            }

            log.info("Created {} days of KPI B.4 analytic data for instance {} with proper detail result associations",
                    dailyAggregatedData.size(), instance.getId());

        } catch (Exception e) {
            log.error("Error creating KPI B.4 analytic data for instance {}: {}", instance.getId(), e.getMessage(), e);
            throw new RuntimeException("Failed to create KPI B.4 analytic data", e);
        }
    }

    /**
     * Helper method to get all months within the analysis period.
     */
    private List<YearMonth> getMonthsInPeriod(LocalDate startDate, LocalDate endDate) {
        List<YearMonth> months = new ArrayList<>();
        YearMonth start = YearMonth.from(startDate);
        YearMonth end = YearMonth.from(endDate);

        YearMonth current = start;
        while (!current.isAfter(end)) {
            months.add(current);
            current = current.plusMonths(1);
        }

        return months;
    }



    /**
     * Trova il detail result appropriato per una specifica data di valutazione.
     * Associa ogni giorno al detail result del mese corrispondente, se esiste.
     */
    private KpiB4DetailResult findDetailResultForDate(LocalDate evaluationDate,
                                                     Map<LocalDate, KpiB4DetailResult> detailResultsByDate,
                                                     KpiB4DetailResult fallbackDetailResult) {
        // Trova il detail result per il mese che contiene la data di valutazione
        for (KpiB4DetailResult detailResult : detailResultsByDate.values()) {
            // Verifica se la data di valutazione è nel range del detail result
            if (!evaluationDate.isBefore(detailResult.getEvaluationStartDate()) &&
                !evaluationDate.isAfter(detailResult.getEvaluationEndDate())) {
                return detailResult;
            }
        }

        // Se non trovato, usa il fallback (normalmente il record TOTALE)
        return fallbackDetailResult;
    }

    /**
     * Popola la tabella drilldown API_LOG_DRILLDOWN con i dati dettagliati delle API
     * per preservare uno snapshot storico al momento dell'analisi KPI B.4.
     *
     * @param analyticData il record KpiB4AnalyticData salvato
     * @param instance l'istanza analizzata
     * @param evaluationDate la data di valutazione
     */
    private void populateDrilldownData(KpiB4AnalyticData analyticData, Instance instance, LocalDate evaluationDate) {
        log.debug("Populating drilldown data for KPI B.4 analytic data {} on date {}",
                 analyticData.getId(), evaluationDate);

        try {
            String partnerFiscalCode = instance.getPartner().getFiscalCode();

            // Query dettagliata per ottenere i singoli record API log per la data specifica
            // Questo crea uno snapshot storico dei dati al momento dell'analisi
            List<Object[]> detailedApiLogData = pagopaApiLogRepository.findDetailedApiLogByPartnerAndDate(
                partnerFiscalCode, evaluationDate);

            if (detailedApiLogData.isEmpty()) {
                log.debug("No detailed API log data found for partner {} on date {}", partnerFiscalCode, evaluationDate);
                return;
            }

            for (Object[] logData : detailedApiLogData) {
                // Estrazione dati dal result set basandosi sulla query del repository
                // Format atteso: [CF_PARTNER, DATE, STATION, CF_ENTE, API, TOT_REQ, REQ_OK, REQ_KO]
                String cfPartner = (String) logData[0];
                LocalDate dataDate = (LocalDate) logData[1];
                String stationCode = (String) logData[2];
                String fiscalCode = (String) logData[3];
                String api = (String) logData[4];
                Integer totalRequests = ((Number) logData[5]).intValue();
                Integer okRequests = ((Number) logData[6]).intValue();
                Integer koRequests = ((Number) logData[7]).intValue();

                // Trova la stazione corrispondente al station code
                AnagStation station = anagStationRepository.findOneByName(stationCode)
                    .orElse(null);

                if (station == null) {
                    log.warn("Station not found for code: {}. Skipping drilldown record.", stationCode);
                    continue;
                }

                // Crea l'entità drilldown direttamente per avere controllo completo sui riferimenti
                PagopaApiLogDrilldown drilldownEntity = new PagopaApiLogDrilldown();

                // Setta i riferimenti alle entità
                drilldownEntity.setInstance(instance);
                drilldownEntity.setInstanceModule(analyticData.getKpiB4DetailResult().getInstanceModule());
                drilldownEntity.setStation(station);
                drilldownEntity.setKpiB4AnalyticData(analyticData);

                // Setta la data di analisi
                drilldownEntity.setAnalysisDate(analyticData.getAnalysisDate());

                // Setta i dati API log
                drilldownEntity.setPartnerFiscalCode(cfPartner);
                drilldownEntity.setDataDate(dataDate);
                drilldownEntity.setStationCode(stationCode);
                drilldownEntity.setFiscalCode(fiscalCode);
                drilldownEntity.setApi(api);
                drilldownEntity.setTotalRequests(totalRequests);
                drilldownEntity.setOkRequests(okRequests);
                drilldownEntity.setKoRequests(koRequests);

                // Salva direttamente l'entità nel repository
                pagopaApiLogDrilldownRepository.save(drilldownEntity);

                log.trace("Saved drilldown record: {} {} {} {} {} (Total: {}, OK: {}, KO: {})",
                         cfPartner, dataDate, stationCode, fiscalCode, api,
                         totalRequests, okRequests, koRequests);
            }

            log.debug("Populated {} drilldown records for KPI B.4 analytic data {} on date {}",
                     detailedApiLogData.size(), analyticData.getId(), evaluationDate);

        } catch (Exception e) {
            log.error("Error populating drilldown data for KPI B.4 analytic data {} on date {}: {}",
                     analyticData.getId(), evaluationDate, e.getMessage(), e);
            // Non interrompere il flusso principale per errori nel drilldown
        }
    }

    /**
     * Calcola l'outcome specifico per un KpiB4DetailResult basato sulla percentuale CP.
     * La logica è: se la percentuale CP è <= (soglia + tolleranza) allora OK, altrimenti KO.
     *
     * @param percentageCp la percentuale CP del detail result
     * @param kpiB4Result il result principale per ottenere soglia e tolleranza
     * @return l'outcome specifico per questo detail result
     */
    private OutcomeStatus calculateDetailResultOutcome(BigDecimal percentageCp, KpiB4Result kpiB4Result) {
        if (percentageCp == null) {
            return OutcomeStatus.OK; // Se non ci sono dati, consideriamo OK
        }

        // Ottieni soglia e tolleranza dal result principale
        Double thresholdValue = kpiB4Result.getEligibilityThreshold(); // Soglia (es. 0%)
        Double toleranceValue = kpiB4Result.getTolerance(); // Tolleranza (es. 1%)

        // Valori di default se non configurati
        if (thresholdValue == null) thresholdValue = 0.0; // Default soglia 0%
        if (toleranceValue == null) toleranceValue = 1.0; // Default tolleranza 1%

        // Calcola il limite massimo consentito
        BigDecimal threshold = BigDecimal.valueOf(thresholdValue);
        BigDecimal tolerance = BigDecimal.valueOf(toleranceValue);
        BigDecimal maxAllowed = threshold.add(tolerance);

        // Se la percentuale CP è <= (soglia + tolleranza) → OK, altrimenti → KO
        boolean isCompliant = percentageCp.compareTo(maxAllowed) <= 0;
        OutcomeStatus outcome = isCompliant ? OutcomeStatus.OK : OutcomeStatus.KO;

        log.debug("Detail result outcome calculation: CP={}%, threshold={}%, tolerance={}%, maxAllowed={}%, outcome={}",
                 percentageCp, thresholdValue, toleranceValue, maxAllowed, outcome);

        return outcome;
    }

    /**
     * Calcola l'outcome corretto del KPI B.4 result basandosi sui detail results effettivi.
     * Per valutazione TOTALE: usa l'outcome del detail result TOTALE
     * Per valutazione MESE: se almeno un detail result mensile è KO, l'outcome è KO, altrimenti OK
     *
     * @param kpiB4Result il result principale
     * @return l'outcome corretto basato sui detail results
     */
    private OutcomeStatus calculateOutcomeFromDetailResults(KpiB4Result kpiB4Result) {
        log.debug("Calculating outcome from detail results for KPI B.4 result id: {}, evaluationType: {}", 
            kpiB4Result.getId(), kpiB4Result.getEvaluationType());
        
        List<KpiB4DetailResult> detailResults = kpiB4DetailResultRepository.findByKpiB4Result(kpiB4Result);
        
        log.info("Found {} detail results for KPI B.4 result id: {}", detailResults.size(), kpiB4Result.getId());
        
        if (detailResults.isEmpty()) {
            log.warn("No detail results found for KPI B.4 result {}, keeping original outcome", kpiB4Result.getId());
            return kpiB4Result.getOutcome();
        }

        // Log dei detail results trovati
        for (KpiB4DetailResult dr : detailResults) {
            log.info("Detail result: id={}, evaluationType={}, outcome={}, startDate={}, endDate={}", 
                dr.getId(), dr.getEvaluationType(), dr.getOutcome(), 
                dr.getEvaluationStartDate(), dr.getEvaluationEndDate());
        }

        EvaluationType evaluationType = kpiB4Result.getEvaluationType();
        
        if (evaluationType == EvaluationType.TOTALE) {
            // Per valutazione TOTALE, trova il detail result di tipo TOTALE e usa il suo outcome
            log.info("Evaluation type is TOTALE, looking for TOTALE detail result");
            OutcomeStatus totalOutcome = detailResults.stream()
                .filter(dr -> dr.getEvaluationType() == com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType.TOTALE)
                .findFirst()
                .map(dr -> {
                    log.info("Found TOTALE detail result with outcome: {}", dr.getOutcome());
                    return dr.getOutcome();
                })
                .orElse(kpiB4Result.getOutcome());
            
            log.info("Calculated outcome from TOTALE detail result: {}", totalOutcome);
            return totalOutcome;
        } else {
            // Per valutazione MESE, se almeno un detail result mensile è KO, l'outcome è KO
            log.info("Evaluation type is MESE, checking monthly detail results for KO");
            
            long monthlyCount = detailResults.stream()
                .filter(dr -> dr.getEvaluationType() == com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType.MESE)
                .count();
            
            long koCount = detailResults.stream()
                .filter(dr -> dr.getEvaluationType() == com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType.MESE)
                .filter(dr -> dr.getOutcome() == OutcomeStatus.KO)
                .count();
            
            log.info("Monthly detail results: total={}, KO count={}", monthlyCount, koCount);
            
            boolean hasKoInMonthlyResults = koCount > 0;
            OutcomeStatus finalOutcome = hasKoInMonthlyResults ? OutcomeStatus.KO : OutcomeStatus.OK;
            
            log.info("Calculated outcome from monthly detail results: {} (hasKO: {})", finalOutcome, hasKoInMonthlyResults);
            return finalOutcome;
        }
    }

    @Override
    @Transactional
    public void updateKpiB4ResultOutcome(Long resultId, OutcomeStatus outcome) {
        log.debug("Updating KPI B4 result {} with outcome: {}", resultId, outcome);

        KpiB4Result result = kpiB4ResultRepository.findById(resultId)
            .orElseThrow(() -> new RuntimeException("KPI B4 result not found: " + resultId));

        result.setOutcome(outcome);
        kpiB4ResultRepository.save(result);

        log.info("Updated KPI B4 result {} outcome to: {}", resultId, outcome);
    }
}
