package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.KpiConfiguration;
import com.nexigroup.pagopa.cruscotto.domain.KpiB8Result;
import com.nexigroup.pagopa.cruscotto.domain.KpiB8DetailResult;
import com.nexigroup.pagopa.cruscotto.domain.KpiB8AnalyticData;
import com.nexigroup.pagopa.cruscotto.domain.Module;
import com.nexigroup.pagopa.cruscotto.domain.AnagStation;
import com.nexigroup.pagopa.cruscotto.domain.PagopaApiLogDrilldown;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.repository.InstanceRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiB8ResultRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiB8DetailResultRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiB8AnalyticDataRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiConfigurationRepository;
import com.nexigroup.pagopa.cruscotto.repository.ModuleRepository;
import com.nexigroup.pagopa.cruscotto.repository.PagopaApiLogRepository;
import com.nexigroup.pagopa.cruscotto.repository.PagopaApiLogDrilldownRepository;
import com.nexigroup.pagopa.cruscotto.repository.AnagStationRepository;
import com.nexigroup.pagopa.cruscotto.service.InstanceModuleService;
import com.nexigroup.pagopa.cruscotto.service.KpiB8Service;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceModuleDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB8AnalyticDataDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB8DetailResultDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB8ResultDTO;
import com.nexigroup.pagopa.cruscotto.service.mapper.KpiB8ResultMapper;
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
 * Service implementation for managing KPI B8 calculations and results.
 */
@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class KpiB8ServiceImpl implements KpiB8Service {

    private static final String INSTANCE_NOT_FOUND = "Instance not found: ";

    private final KpiB8ResultRepository kpiB8ResultRepository;
    private final KpiB8DetailResultRepository kpiB8DetailResultRepository;
    private final KpiB8AnalyticDataRepository kpiB8AnalyticDataRepository;
    private final KpiConfigurationRepository kpiConfigurationRepository;
    private final InstanceRepository instanceRepository;
    private final ModuleRepository moduleRepository;
    private final PagopaApiLogRepository pagopaApiLogRepository;
    private final AnagStationRepository anagStationRepository;
    private final PagopaApiLogDrilldownRepository pagopaApiLogDrilldownRepository;
    private final InstanceModuleService instanceModuleService;
    private final KpiB8ResultMapper kpiB8ResultMapper;

    @Override
    public KpiB8ResultDTO executeKpiB8Calculation(Instance instance) {
        log.info("Starting KPI B8 calculation for instance: {}", instance.getId());

        try {
            // Get KPI configuration for threshold and tolerance
            KpiConfiguration configuration = kpiConfigurationRepository
                .findByModuleCode(ModuleCode.B8.code)
                .orElseThrow(() -> new RuntimeException("KPI B8 configuration not found"));

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

            // Find the B8 module and corresponding InstanceModule
            Module moduleB8 = moduleRepository.findByCode(ModuleCode.B8.code)
                .orElseThrow(() -> new RuntimeException("Module B8 not found"));

            InstanceModuleDTO instanceModuleDTO = instanceModuleService.findOne(instance.getId(), moduleB8.getId())
                .orElseThrow(() -> new RuntimeException("InstanceModule not found for instance " + instance.getId() + " and module B8"));

            // Create KPI B8 result using existing entity structure
            KpiB8Result result = new KpiB8Result();
            result.setInstance(instance);
            // Set the required instanceModule field to avoid constraint violation
            result.setInstanceModule(instanceModuleService.findById(instanceModuleDTO.getId())
                .orElseThrow(() -> new RuntimeException("InstanceModule entity not found by ID")));
            result.setAnalysisDate(LocalDate.now());
            //result.setExcludePlannedShutdown(configuration.getExcludePlannedShutdown() != null ? configuration.getExcludePlannedShutdown() : false);
            //result.setExcludeUnplannedShutdown(configuration.getExcludeUnplannedShutdown() != null ? configuration.getExcludeUnplannedShutdown() : false);
            result.setEligibilityThreshold(configuration.getEligibilityThreshold() != null ? configuration.getEligibilityThreshold() : 95.0);
            result.setTolerance(configuration.getTolerance() != null ? configuration.getTolerance() : 5.0);
            result.setEvaluationType(configuration.getEvaluationType() != null ? configuration.getEvaluationType() : EvaluationType.MESE);
            result.setOutcome(outcomeStatus);

            KpiB8Result savedResult = kpiB8ResultRepository.save(result);

            // NUOVO: Popola anche le tabelle di dettaglio KpiB8DetailResult e KpiB8AnalyticData
            createAndSaveDetailResults(savedResult, instance);
            createAndSaveAnalyticData(savedResult, instance);

            log.info("KPI B.8 calculated for instance {}: {}% success rate (threshold: {}%), status: {} - Detail and analytic data saved",
                instance.getId(), successPercentage, threshold, outcomeStatus);

            return kpiB8ResultMapper.toDto(savedResult);

        } catch (Exception e) {
            log.error("Error calculating KPI B.8 for instance {}: {}", instance.getId(), e.getMessage(), e);
            throw new RuntimeException("Failed to calculate KPI B.8", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<KpiB8ResultDTO> getKpiB8Results(String instanceId) {
        log.debug("Finding KPI B8 results for instance: {}", instanceId);
        Long instanceIdLong = Long.valueOf(instanceId);
        Instance instance = instanceRepository.findById(instanceIdLong)
            .orElseThrow(() -> new RuntimeException(INSTANCE_NOT_FOUND + instanceId));

        return kpiB8ResultRepository.findByInstanceOrderByAnalysisDateDesc(instance)
            .stream()
            .map(kpiB8ResultMapper::toDto)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<KpiB8ResultDTO> getKpiB8Results(String instanceId, Pageable pageable) {
        log.debug("Finding KPI B8 results for instance {} with pagination", instanceId);
        Long instanceIdLong = Long.valueOf(instanceId);
        Instance instance = instanceRepository.findById(instanceIdLong)
            .orElseThrow(() -> new RuntimeException(INSTANCE_NOT_FOUND + instanceId));

        return kpiB8ResultRepository.findByInstanceOrderByAnalysisDateDesc(instance, pageable)
            .map(kpiB8ResultMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public KpiB8ResultDTO getKpiB8Result(String instanceId, LocalDateTime analysisDate) {
        log.debug("Finding KPI B8 result for instance {} and date {}", instanceId, analysisDate);
        Long instanceIdLong = Long.valueOf(instanceId);
        Instance instance = instanceRepository.findById(instanceIdLong)
            .orElseThrow(() -> new RuntimeException(INSTANCE_NOT_FOUND + instanceId));

        LocalDate localDate = analysisDate.toLocalDate();
        KpiB8Result result = kpiB8ResultRepository.findByInstanceAndAnalysisDate(instance, localDate);
        return result != null ? kpiB8ResultMapper.toDto(result) : null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<KpiB8DetailResultDTO> getKpiB8DetailResults(String instanceId, LocalDateTime analysisDate) {
        log.debug("Finding KPI B8 detail results for instance {} and date {}", instanceId, analysisDate);
        // For KPI B.8, details are typically aggregated by API type or time period
        // This would be implemented based on specific requirements for detailed breakdown
        return List.of();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<KpiB8DetailResultDTO> getKpiB8DetailResults(String instanceId, LocalDateTime analysisDate, Pageable pageable) {
        log.debug("Finding KPI B8 detail results for instance {} and date {} with pagination", instanceId, analysisDate);
        // Implementation would depend on specific detail requirements
        return Page.empty(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<KpiB8AnalyticDataDTO> getKpiB8AnalyticData(String instanceId, LocalDateTime analysisDate) {
        log.debug("Finding KPI B8 analytic data for instance {} and date {}", instanceId, analysisDate);
        // Analytics might include hourly breakdowns, error categories, API response times, etc.
        // Implementation would depend on specific analytic requirements
        return List.of();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<KpiB8AnalyticDataDTO> getKpiB8AnalyticData(String instanceId, LocalDateTime analysisDate, Pageable pageable) {
        log.debug("Finding KPI B8 analytic data for instance {} and date {} with pagination", instanceId, analysisDate);
        // Implementation would depend on specific analytic requirements
        return Page.empty(pageable);
    }

    @Override
    public void deleteKpiB8Data(String instanceId) {
        log.info("Deleting KPI B8 data for instance: {}", instanceId);
        Long instanceIdLong = Long.valueOf(instanceId);
        Instance instance = instanceRepository.findById(instanceIdLong)
            .orElseThrow(() -> new RuntimeException(INSTANCE_NOT_FOUND + instanceId));

        kpiB8ResultRepository.deleteByInstance(instance);
        log.info("Deleted KPI B8 results for instance {}", instanceId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsKpiB8Calculation(String instanceId, LocalDateTime analysisDate) {
        log.debug("Checking if KPI B8 calculation exists for instance {} and date {}", instanceId, analysisDate);
        Long instanceIdLong = Long.valueOf(instanceId);
        Instance instance = instanceRepository.findById(instanceIdLong)
            .orElseThrow(() -> new RuntimeException(INSTANCE_NOT_FOUND + instanceId));

        LocalDate localDate = analysisDate.toLocalDate();
        return kpiB8ResultRepository.existsByInstanceAndAnalysisDate(instance, localDate);
    }

    @Override
    public KpiB8ResultDTO recalculateKpiB8(String instanceId) {
        log.info("Recalculating KPI B8 for instance: {}", instanceId);
        Long instanceIdLong = Long.valueOf(instanceId);
        Instance instance = instanceRepository.findById(instanceIdLong)
            .orElseThrow(() -> new RuntimeException(INSTANCE_NOT_FOUND + instanceId));

        // Delete existing results first
        kpiB8ResultRepository.deleteByInstance(instance);

        // Recalculate with current date
        return executeKpiB8Calculation(instance);
    }

    /**
     * Crea e salva i record KpiB8DetailResult con dati dettagliati per stazione.
     * Questa tabella contiene il dettaglio dei risultati KPI B.8 per ogni stazione dell'ente.
     */
    private void createAndSaveDetailResults(KpiB8Result kpiB8Result, Instance instance) {
        log.info("Creating KPI B.8 detail results for instance {} (partner-level aggregated)", instance.getId());

        try {

            LocalDate analysisDate = kpiB8Result.getAnalysisDate();
            LocalDate periodStart = instance.getAnalysisPeriodStartDate();
            LocalDate periodEnd = instance.getAnalysisPeriodEndDate();
            String partnerFiscalCode = instance.getPartner().getFiscalCode();


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
                Long monthlyGpdKOCalls = pagopaApiLogRepository.calculateTotalGpdAcaRequestsKO(partnerFiscalCode, monthStart, monthEnd);

                if (monthlyGpdCalls == null) monthlyGpdCalls = 0L;
                if (monthlyGpdKOCalls == null) monthlyGpdKOCalls = 0L;

                totalApiCallsAllMonths += (monthlyGpdCalls + monthlyGpdKOCalls);

                // Calcola percentuale CP per questo mese
                BigDecimal monthlyPercentageCp = BigDecimal.ZERO;
                if (monthlyGpdCalls > 0) {
                    monthlyPercentageCp = new BigDecimal(monthlyGpdKOCalls)
                        .multiply(new BigDecimal("100"))
                        .divide(new BigDecimal(monthlyGpdCalls), 2, RoundingMode.HALF_UP);
                }

                // Crea record mensile (PARTNER LEVEL - aggregato per tutto il partner)
                KpiB8DetailResult monthlyDetailResult = new KpiB8DetailResult();
                monthlyDetailResult.setInstanceId(instance.getId());
                monthlyDetailResult.setInstanceModuleId(kpiB8Result.getInstanceModule().getId());
                monthlyDetailResult.setInstance(instance);
                monthlyDetailResult.setInstanceModule(kpiB8Result.getInstanceModule());
                monthlyDetailResult.setKpiB8Result(kpiB8Result);
                monthlyDetailResult.setAnalysisDate(analysisDate);
                monthlyDetailResult.setEvaluationType(com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType.MESE);
                monthlyDetailResult.setEvaluationStartDate(monthStart);
                monthlyDetailResult.setEvaluationEndDate(monthEnd);
                monthlyDetailResult.setReqKO(monthlyGpdKOCalls); // Totale GPD+ACA del mese
                monthlyDetailResult.setTotReq(monthlyGpdCalls); // Totale CP del mese
                monthlyDetailResult.setPerKO(monthlyPercentageCp); // % CP del mese
                monthlyDetailResult.setOutcome(calculateDetailResultOutcome(monthlyPercentageCp, kpiB8Result)); // Calcola outcome specifico per questo detail result

                kpiB8DetailResultRepository.save(monthlyDetailResult);
                log.debug("Saved monthly KPI B.8 detail result for partner {} in {}: {} GPD, {} CP",
                    partnerFiscalCode, yearMonth, monthlyGpdCalls, monthlyGpdKOCalls);
            }

            // Calcola totali per l'intero periodo
            Long totalGpdCalls = pagopaApiLogRepository.calculateTotalGpdAcaRequests(partnerFiscalCode, periodStart, periodEnd);
            Long totalGpdKOCalls = pagopaApiLogRepository.calculateTotalGpdAcaRequestsKO(partnerFiscalCode, periodStart, periodEnd);

            if (totalGpdCalls == null) totalGpdCalls = 0L;
            if (totalGpdKOCalls == null) totalGpdKOCalls = 0L;

            // Calcola percentuale CP per l'intero periodo
            BigDecimal totalPercentageCp = BigDecimal.ZERO;
            if (totalGpdCalls > 0) {
                totalPercentageCp = new BigDecimal(totalGpdKOCalls)
                    .multiply(new BigDecimal("100"))
                    .divide(new BigDecimal(totalGpdCalls), 2, RoundingMode.HALF_UP);
            }

            // Crea record totale (intero periodo di analisi, livello partner)
            KpiB8DetailResult totalDetailResult = new KpiB8DetailResult();
            totalDetailResult.setInstanceId(instance.getId());
            totalDetailResult.setInstanceModuleId(kpiB8Result.getInstanceModule().getId());
            totalDetailResult.setInstance(instance);
            totalDetailResult.setInstanceModule(kpiB8Result.getInstanceModule());
            totalDetailResult.setKpiB8Result(kpiB8Result);
            totalDetailResult.setAnalysisDate(analysisDate);
            totalDetailResult.setEvaluationType(com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType.TOTALE);
            totalDetailResult.setEvaluationStartDate(periodStart);
            totalDetailResult.setEvaluationEndDate(periodEnd);
            totalDetailResult.setTotReq(totalGpdCalls); // Totale GPD+ACA dell'intero periodo
            totalDetailResult.setReqKO(totalGpdKOCalls); // Totale CP dell'intero periodo
            totalDetailResult.setPerKO(totalPercentageCp); // % CP dell'intero periodo
            totalDetailResult.setOutcome(calculateDetailResultOutcome(totalPercentageCp, kpiB8Result)); // Calcola outcome specifico per questo detail result

            kpiB8DetailResultRepository.save(totalDetailResult);

            log.info("Created {} monthly + 1 total KPI B.8 detail results for partner {} with {} total API calls",
                monthsInPeriod.size(), partnerFiscalCode, totalApiCallsAllMonths);

        } catch (Exception e) {
            log.error("Error creating KPI B.8 detail results for instance {}: {}", instance.getId(), e.getMessage(), e);
            throw new RuntimeException("Failed to create KPI B.8 detail results", e);
        }
    }

    /**
     * Crea e salva i record KpiB8AnalyticData con dati analitici giornalieri delle API.
     * Questa tabella contiene l'andamento giornaliero delle chiamate API per l'analisi di drill-down.
     */
    private void createAndSaveAnalyticData(KpiB8Result kpiB8Result, Instance instance) {
        log.info("Creating KPI B.8 analytic data for instance {}", instance.getId());

        try {
            LocalDate periodStart = instance.getAnalysisPeriodStartDate();
            LocalDate periodEnd = instance.getAnalysisPeriodEndDate();
            String partnerFiscalCode = instance.getPartner().getFiscalCode();

            // Ottieni i KpiB8DetailResult salvati per questo risultato
            List<KpiB8DetailResult> detailResults = kpiB8DetailResultRepository.findByKpiB8Result(kpiB8Result);

            if (detailResults.isEmpty()) {
                log.warn("No detail results found for KPI B.8 result {}, cannot create analytic data", kpiB8Result.getId());
                return;
            }

            // Crea una mappa dei detail result per periodo di valutazione per associare correttamente i dati analitici
            Map<LocalDate, KpiB8DetailResult> detailResultsByDate = new HashMap<>();
            KpiB8DetailResult totalDetailResult = null;

            for (KpiB8DetailResult detailResult : detailResults) {
                if (detailResult.getEvaluationType() == com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType.TOTALE) {
                    totalDetailResult = detailResult;
                } else {
                    // Per i record mensili, usa la data di inizio del periodo come chiave
                    detailResultsByDate.put(detailResult.getEvaluationStartDate(), detailResult);
                }
            }

            // Usa il record TOTALE come fallback se non troviamo il detail result specifico per una data
            KpiB8DetailResult fallbackDetailResult = totalDetailResult != null ? totalDetailResult : detailResults.get(0);

            // Ottieni i dati aggregati giornalieri dalla tabella pagopa_apilog
            List<Object[]> dailyAggregatedData = pagopaApiLogRepository.calculateDailyAggregatedDataAndApiGPDOrAca(
                partnerFiscalCode, periodStart, periodEnd);

            if (dailyAggregatedData.isEmpty()) {
                log.warn("No daily aggregated data found for partner {} in period {} to {}",
                    partnerFiscalCode, periodStart, periodEnd);
                return;
            }

            for (Object[] dayData : dailyAggregatedData) {
                LocalDate evaluationDate = (LocalDate) dayData[0];
                Long gpdAcaTotal = (Long) dayData[1];
                Long gpdAcaTotalKO = (Long) dayData[2];

                // Gestione valori null
                if (gpdAcaTotal == null) gpdAcaTotal = 0L;
                if (gpdAcaTotalKO == null) gpdAcaTotalKO = 0L;

                // Trova il detail result corretto per questa data
                KpiB8DetailResult appropriateDetailResult = findDetailResultForDate(evaluationDate, detailResultsByDate, fallbackDetailResult);

                // Crea UN SOLO record per giornata con entrambi i valori GPD e CP
                // (secondo l'analisi: "Numero Request GPD" e "Numero Request CP" per ogni giornata)
                KpiB8AnalyticData dailyAnalyticData = new KpiB8AnalyticData();
                dailyAnalyticData.setInstanceId(instance.getId());
                dailyAnalyticData.setInstance(instance);
                dailyAnalyticData.setAnalysisDate(kpiB8Result.getAnalysisDate());
                dailyAnalyticData.setEvaluationDate(evaluationDate);
                dailyAnalyticData.setTotReq(gpdAcaTotal); // Numero Request GPD del giorno
                dailyAnalyticData.setReqKO(gpdAcaTotalKO); // Numero Request CP del giorno
                dailyAnalyticData.setKpiB8DetailResult(appropriateDetailResult);

                KpiB8AnalyticData savedAnalyticData = kpiB8AnalyticDataRepository.save(dailyAnalyticData);

                log.debug("Saved KPI B.8 analytic data for date {} - GPD: {}, CP: {} (linked to detail result {})",
                    evaluationDate, gpdAcaTotal, gpdAcaTotalKO, appropriateDetailResult.getId());

                // Popola la tabella drilldown con i dati API log dettagliati per questa data
                populateDrilldownData(savedAnalyticData, instance, evaluationDate);
            }

            log.info("Created {} days of KPI B.8 analytic data for instance {} with proper detail result associations",
                dailyAggregatedData.size(), instance.getId());

        } catch (Exception e) {
            log.error("Error creating KPI B.8 analytic data for instance {}: {}", instance.getId(), e.getMessage(), e);
            throw new RuntimeException("Failed to create KPI B.8 analytic data", e);
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
    private KpiB8DetailResult findDetailResultForDate(LocalDate evaluationDate,
                                                      Map<LocalDate, KpiB8DetailResult> detailResultsByDate,
                                                      KpiB8DetailResult fallbackDetailResult) {
        // Trova il detail result per il mese che contiene la data di valutazione
        for (KpiB8DetailResult detailResult : detailResultsByDate.values()) {
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
     * Popola la tabella drilldown PAGOPA_API_LOG_DRILLDOWN con i dati dettagliati delle API
     * per preservare uno snapshot storico al momento dell'analisi KPI B.8.
     *
     * @param analyticData il record KpiB8AnalyticData salvato
     * @param instance l'istanza analizzata
     * @param evaluationDate la data di valutazione
     */
    private void populateDrilldownData(KpiB8AnalyticData analyticData, Instance instance, LocalDate evaluationDate) {
        log.debug("Populating drilldown data for KPI B.8 analytic data {} on date {}",
            analyticData.getId(), evaluationDate);

        try {
            String partnerFiscalCode = instance.getPartner().getFiscalCode();

            // Query dettagliata per ottenere i singoli record API log per la data specifica
            // Questo crea uno snapshot storico dei dati al momento dell'analisi
            List<Object[]> detailedApiLogData = pagopaApiLogRepository.findDetailedApiLogByPartnerAndDateGPDAndACA(
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
                drilldownEntity.setInstanceModule(analyticData.getKpiB8DetailResult().getInstanceModule());
                drilldownEntity.setStation(station);
                drilldownEntity.setKpiB8AnalyticData(analyticData);

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

            log.debug("Populated {} drilldown records for KPI B.8 analytic data {} on date {}",
                detailedApiLogData.size(), analyticData.getId(), evaluationDate);

        } catch (Exception e) {
            log.error("Error populating drilldown data for KPI B.8 analytic data {} on date {}: {}",
                analyticData.getId(), evaluationDate, e.getMessage(), e);
            // Non interrompere il flusso principale per errori nel drilldown
        }
    }

    /**
     * Calcola l'outcome specifico per un KpiB8DetailResult basato sulla percentuale CP.
     * La logica è: se la percentuale CP è <= (soglia + tolleranza) allora OK, altrimenti KO.
     *
     * @param percentageCp la percentuale CP del detail result
     * @param kpiB8Result il result principale per ottenere soglia e tolleranza
     * @return l'outcome specifico per questo detail result
     */
    private OutcomeStatus calculateDetailResultOutcome(BigDecimal percentageCp, KpiB8Result kpiB8Result) {
        if (percentageCp == null) {
            return OutcomeStatus.OK; // Se non ci sono dati, consideriamo OK
        }

        // Ottieni soglia e tolleranza dal result principale
        Double thresholdValue = kpiB8Result.getEligibilityThreshold(); // Soglia (es. 0%)
        Double toleranceValue = kpiB8Result.getTolerance(); // Tolleranza (es. 1%)

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

    @Override
    @Transactional
    public void updateKpiB8ResultOutcome(Long resultId, OutcomeStatus outcome) {
        log.debug("Updating KPI B8 result {} with outcome: {}", resultId, outcome);

        KpiB8Result result = kpiB8ResultRepository.findById(resultId)
            .orElseThrow(() -> new RuntimeException("KPI B8 result not found: " + resultId));

        result.setOutcome(outcome);
        kpiB8ResultRepository.save(result);

        log.info("Updated KPI B8 result {} outcome to: {}", resultId, outcome);
    }
}
