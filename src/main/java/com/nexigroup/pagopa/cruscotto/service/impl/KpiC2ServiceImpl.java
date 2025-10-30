package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.*;
import com.nexigroup.pagopa.cruscotto.domain.Module;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.repository.*;
import com.nexigroup.pagopa.cruscotto.service.InstanceModuleService;
import com.nexigroup.pagopa.cruscotto.service.KpiC2Service;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceModuleDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiC2AnalyticDataDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiC2DetailResultDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiC2ResultDTO;
import com.nexigroup.pagopa.cruscotto.service.mapper.KpiC2ResultMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service implementation for managing KPI C2 calculations and results.
 */
@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class KpiC2ServiceImpl implements KpiC2Service {

    private final KpiC2ResultRepository kpiC2ResultRepository;
    private final KpiC2DetailResultRepository kpiC2DetailResultRepository;
    private final KpiC2AnalyticDataRepository kpiC2AnalyticDataRepository;
    private final KpiConfigurationRepository kpiConfigurationRepository;
    private final InstanceRepository instanceRepository;
    private final ModuleRepository moduleRepository;
    private final PagopaApiLogRepository pagopaApiLogRepository;
    private final AnagStationRepository anagStationRepository;
    private final PagopaApiLogDrilldownRepository pagopaApiLogDrilldownRepository;
    private final InstanceModuleService instanceModuleService;
    private final KpiC2ResultMapper kpiC2ResultMapper;

    @Override
    public KpiC2ResultDTO executeKpiC2Calculation(Instance instance) {
        log.info("Starting KPI C2 calculation for instance: {}", instance.getId());

        try {
            // Get KPI configuration for threshold and tolerance
            KpiConfiguration configuration = kpiConfigurationRepository
                .findByModuleCode(ModuleCode.C2.code)
                .orElseThrow(() -> new RuntimeException("KPI C2 configuration not found"));

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

            // Find the C2 module and corresponding InstanceModule
            Module moduleC2 = moduleRepository.findByCode(ModuleCode.C2.code)
                .orElseThrow(() -> new RuntimeException("Module C2 not found"));

            InstanceModuleDTO instanceModuleDTO = instanceModuleService.findOne(instance.getId(), moduleC2.getId())
                .orElseThrow(() -> new RuntimeException("InstanceModule not found for instance " + instance.getId() + " and module C2"));

            // Create KPI C2 result using existing entity structure
            KpiC2Result result = new KpiC2Result();
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

            KpiC2Result savedResult = kpiC2ResultRepository.save(result);

            // NUOVO: Popola anche le tabelle di dettaglio KpiC2DetailResult e KpiC2AnalyticData
            createAndSaveDetailResults(savedResult, instance);
            createAndSaveAnalyticData(savedResult, instance);

            log.info("KPI B.8 calculated for instance {}: {}% success rate (threshold: {}%), status: {} - Detail and analytic data saved",
                instance.getId(), successPercentage, threshold, outcomeStatus);

            return kpiC2ResultMapper.toDto(savedResult);

        } catch (Exception e) {
            log.error("Error calculating KPI B.8 for instance {}: {}", instance.getId(), e.getMessage(), e);
            throw new RuntimeException("Failed to calculate KPI B.8", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<KpiC2ResultDTO> getKpiC2Results(String instanceId) {
        log.debug("Finding KPI C2 results for instance: {}", instanceId);
        Long instanceIdLong = Long.valueOf(instanceId);
        Instance instance = instanceRepository.findById(instanceIdLong)
            .orElseThrow(() -> new RuntimeException("Instance not found: " + instanceId));

        return kpiC2ResultRepository.findByInstanceOrderByAnalysisDateDesc(instance)
            .stream()
            .map(kpiC2ResultMapper::toDto)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<KpiC2ResultDTO> getKpiC2Results(String instanceId, Pageable pageable) {
        log.debug("Finding KPI C2 results for instance {} with pagination", instanceId);
        Long instanceIdLong = Long.valueOf(instanceId);
        Instance instance = instanceRepository.findById(instanceIdLong)
            .orElseThrow(() -> new RuntimeException("Instance not found: " + instanceId));

        return kpiC2ResultRepository.findByInstanceOrderByAnalysisDateDesc(instance, pageable)
            .map(kpiC2ResultMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public KpiC2ResultDTO getKpiC2Result(String instanceId, LocalDateTime analysisDate) {
        log.debug("Finding KPI C2 result for instance {} and date {}", instanceId, analysisDate);
        Long instanceIdLong = Long.valueOf(instanceId);
        Instance instance = instanceRepository.findById(instanceIdLong)
            .orElseThrow(() -> new RuntimeException("Instance not found: " + instanceId));

        LocalDate localDate = analysisDate.toLocalDate();
        KpiC2Result result = kpiC2ResultRepository.findByInstanceAndAnalysisDate(instance, localDate);
        return result != null ? kpiC2ResultMapper.toDto(result) : null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<KpiC2DetailResultDTO> getKpiC2DetailResults(String instanceId, LocalDateTime analysisDate) {
        log.debug("Finding KPI C2 detail results for instance {} and date {}", instanceId, analysisDate);
        // For KPI B.8, details are typically aggregated by API type or time period
        // This would be implemented based on specific requirements for detailed breakdown
        return List.of();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<KpiC2DetailResultDTO> getKpiC2DetailResults(String instanceId, LocalDateTime analysisDate, Pageable pageable) {
        log.debug("Finding KPI C2 detail results for instance {} and date {} with pagination", instanceId, analysisDate);
        // Implementation would depend on specific detail requirements
        return Page.empty(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<KpiC2AnalyticDataDTO> getKpiC2AnalyticData(String instanceId, LocalDateTime analysisDate) {
        log.debug("Finding KPI C2 analytic data for instance {} and date {}", instanceId, analysisDate);
        // Analytics might include hourly breakdowns, error categories, API response times, etc.
        // Implementation would depend on specific analytic requirements
        return List.of();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<KpiC2AnalyticDataDTO> getKpiC2AnalyticData(String instanceId, LocalDateTime analysisDate, Pageable pageable) {
        log.debug("Finding KPI C2 analytic data for instance {} and date {} with pagination", instanceId, analysisDate);
        // Implementation would depend on specific analytic requirements
        return Page.empty(pageable);
    }

    @Override
    public void deleteKpiC2Data(String instanceId) {
        log.info("Deleting KPI C2 data for instance: {}", instanceId);
        Long instanceIdLong = Long.valueOf(instanceId);
        Instance instance = instanceRepository.findById(instanceIdLong)
            .orElseThrow(() -> new RuntimeException("Instance not found: " + instanceId));

        kpiC2ResultRepository.deleteByInstance(instance);
        log.info("Deleted KPI C2 results for instance {}", instanceId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsKpiC2Calculation(String instanceId, LocalDateTime analysisDate) {
        log.debug("Checking if KPI C2 calculation exists for instance {} and date {}", instanceId, analysisDate);
        Long instanceIdLong = Long.valueOf(instanceId);
        Instance instance = instanceRepository.findById(instanceIdLong)
            .orElseThrow(() -> new RuntimeException("Instance not found: " + instanceId));

        LocalDate localDate = analysisDate.toLocalDate();
        return kpiC2ResultRepository.existsByInstanceAndAnalysisDate(instance, localDate);
    }

    @Override
    public KpiC2ResultDTO recalculateKpiC2(String instanceId) {
        log.info("Recalculating KPI C2 for instance: {}", instanceId);
        Long instanceIdLong = Long.valueOf(instanceId);
        Instance instance = instanceRepository.findById(instanceIdLong)
            .orElseThrow(() -> new RuntimeException("Instance not found: " + instanceId));

        // Delete existing results first
        kpiC2ResultRepository.deleteByInstance(instance);

        // Recalculate with current date
        return executeKpiC2Calculation(instance);
    }

    /**
     * Crea e salva i record KpiC2DetailResult con dati dettagliati per stazione.
     * Questa tabella contiene il dettaglio dei risultati KPI B.8 per ogni stazione dell'ente.
     */
    private void createAndSaveDetailResults(KpiC2Result kpiC2Result, Instance instance) {
        log.info("Creating KPI B.8 detail results for instance {} (partner-level aggregated)", instance.getId());

        try {
            // Verifica che il partner abbia stazioni (necessario per calcolare il KPI B.8)
            List<AnagStation> stations = anagStationRepository.findByAnagPartnerFiscalCode(instance.getPartner().getFiscalCode());

            if (stations.isEmpty()) {
                log.warn("SKIPPING KPI B.8 detail results for partner {} - No stations found. Cannot calculate KPI B.8 without stations.",
                    instance.getPartner().getFiscalCode());
                return; // Salta il partner se non ha stazioni associate
            }

            LocalDate analysisDate = kpiC2Result.getAnalysisDate();
            LocalDate periodStart = instance.getAnalysisPeriodStartDate();
            LocalDate periodEnd = instance.getAnalysisPeriodEndDate();
            String partnerFiscalCode = instance.getPartner().getFiscalCode();

            // Ottieni la prima stazione per il campo obbligatorio (il KPI B.8 è a livello partner, non per singola stazione)
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
                KpiC2DetailResult monthlyDetailResult = new KpiC2DetailResult();
                monthlyDetailResult.setInstanceId(instance.getId());
                monthlyDetailResult.setInstanceModuleId(kpiC2Result.getInstanceModule().getId());
                monthlyDetailResult.setAnagStationId(primaryStation.getId()); // Campo obbligatorio, usa la prima stazione
                monthlyDetailResult.setInstance(instance);
                monthlyDetailResult.setInstanceModule(kpiC2Result.getInstanceModule());
                monthlyDetailResult.setAnagStation(primaryStation);
                monthlyDetailResult.setKpiC2Result(kpiC2Result);
                monthlyDetailResult.setAnalysisDate(analysisDate);
                monthlyDetailResult.setEvaluationType(EvaluationType.MESE);
                monthlyDetailResult.setEvaluationStartDate(monthStart);
                monthlyDetailResult.setEvaluationEndDate(monthEnd);
                monthlyDetailResult.setReqKO(monthlyGpdCalls); // Totale GPD+ACA del mese
                monthlyDetailResult.setTotReq(monthlyGpdKOCalls); // Totale CP del mese
                monthlyDetailResult.setPerKO(monthlyPercentageCp); // % CP del mese
                monthlyDetailResult.setOutcome(calculateDetailResultOutcome(monthlyPercentageCp, kpiC2Result)); // Calcola outcome specifico per questo detail result

                kpiC2DetailResultRepository.save(monthlyDetailResult);
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
            KpiC2DetailResult totalDetailResult = new KpiC2DetailResult();
            totalDetailResult.setInstanceId(instance.getId());
            totalDetailResult.setInstanceModuleId(kpiC2Result.getInstanceModule().getId());
            totalDetailResult.setAnagStationId(primaryStation.getId()); // Campo obbligatorio, usa la prima stazione
            totalDetailResult.setInstance(instance);
            totalDetailResult.setInstanceModule(kpiC2Result.getInstanceModule());
            totalDetailResult.setAnagStation(primaryStation);
            totalDetailResult.setKpiC2Result(kpiC2Result);
            totalDetailResult.setAnalysisDate(analysisDate);
            totalDetailResult.setEvaluationType(EvaluationType.TOTALE);
            totalDetailResult.setEvaluationStartDate(periodStart);
            totalDetailResult.setEvaluationEndDate(periodEnd);
            totalDetailResult.setTotReq(totalGpdCalls); // Totale GPD+ACA dell'intero periodo
            totalDetailResult.setReqKO(totalGpdKOCalls); // Totale CP dell'intero periodo
            totalDetailResult.setPerKO(totalPercentageCp); // % CP dell'intero periodo
            totalDetailResult.setOutcome(calculateDetailResultOutcome(totalPercentageCp, kpiC2Result)); // Calcola outcome specifico per questo detail result

            kpiC2DetailResultRepository.save(totalDetailResult);

            log.info("Created {} monthly + 1 total KPI B.8 detail results for partner {} with {} total API calls",
                monthsInPeriod.size(), partnerFiscalCode, totalApiCallsAllMonths);

        } catch (Exception e) {
            log.error("Error creating KPI B.8 detail results for instance {}: {}", instance.getId(), e.getMessage(), e);
            throw new RuntimeException("Failed to create KPI B.8 detail results", e);
        }
    }

    /**
     * Crea e salva i record KpiC2AnalyticData con dati analitici giornalieri delle API.
     * Questa tabella contiene l'andamento giornaliero delle chiamate API per l'analisi di drill-down.
     */
    private void createAndSaveAnalyticData(KpiC2Result kpiC2Result, Instance instance) {
        log.info("Creating KPI B.8 analytic data for instance {}", instance.getId());

        try {
            LocalDate periodStart = instance.getAnalysisPeriodStartDate();
            LocalDate periodEnd = instance.getAnalysisPeriodEndDate();
            String partnerFiscalCode = instance.getPartner().getFiscalCode();

            // Ottieni i KpiC2DetailResult salvati per questo risultato
            List<KpiC2DetailResult> detailResults = kpiC2DetailResultRepository.findByKpiC2Result(kpiC2Result);

            if (detailResults.isEmpty()) {
                log.warn("No detail results found for KPI B.8 result {}, cannot create analytic data", kpiC2Result.getId());
                return;
            }

            // Crea una mappa dei detail result per periodo di valutazione per associare correttamente i dati analitici
            Map<LocalDate, KpiC2DetailResult> detailResultsByDate = new HashMap<>();
            KpiC2DetailResult totalDetailResult = null;

            for (KpiC2DetailResult detailResult : detailResults) {
                if (detailResult.getEvaluationType() == EvaluationType.TOTALE) {
                    totalDetailResult = detailResult;
                } else {
                    // Per i record mensili, usa la data di inizio del periodo come chiave
                    detailResultsByDate.put(detailResult.getEvaluationStartDate(), detailResult);
                }
            }

            // Usa il record TOTALE come fallback se non troviamo il detail result specifico per una data
            KpiC2DetailResult fallbackDetailResult = totalDetailResult != null ? totalDetailResult : detailResults.get(0);

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
                KpiC2DetailResult appropriateDetailResult = findDetailResultForDate(evaluationDate, detailResultsByDate, fallbackDetailResult);

                // Crea UN SOLO record per giornata con entrambi i valori GPD e CP
                // (secondo l'analisi: "Numero Request GPD" e "Numero Request CP" per ogni giornata)
                KpiC2AnalyticData dailyAnalyticData = new KpiC2AnalyticData();
                dailyAnalyticData.setInstanceId(instance.getId());
                dailyAnalyticData.setInstance(instance);
                dailyAnalyticData.setAnalysisDate(kpiC2Result.getAnalysisDate());
                dailyAnalyticData.setEvaluationDate(evaluationDate);
                dailyAnalyticData.setTotReq(gpdAcaTotal); // Numero Request GPD del giorno
                dailyAnalyticData.setReqKO(gpdAcaTotalKO); // Numero Request CP del giorno
                dailyAnalyticData.setKpiC2DetailResult(appropriateDetailResult);

                KpiC2AnalyticData savedAnalyticData = kpiC2AnalyticDataRepository.save(dailyAnalyticData);

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
    private KpiC2DetailResult findDetailResultForDate(LocalDate evaluationDate,
                                                      Map<LocalDate, KpiC2DetailResult> detailResultsByDate,
                                                      KpiC2DetailResult fallbackDetailResult) {
        // Trova il detail result per il mese che contiene la data di valutazione
        for (KpiC2DetailResult detailResult : detailResultsByDate.values()) {
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
     * @param analyticData il record KpiC2AnalyticData salvato
     * @param instance l'istanza analizzata
     * @param evaluationDate la data di valutazione
     */
    private void populateDrilldownData(KpiC2AnalyticData analyticData, Instance instance, LocalDate evaluationDate) {
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
                drilldownEntity.setInstanceModule(analyticData.getKpiC2DetailResult().getInstanceModule());
                drilldownEntity.setStation(station);
                drilldownEntity.setKpiC2AnalyticData(analyticData);

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
     * Calcola l'outcome specifico per un KpiC2DetailResult basato sulla percentuale CP.
     * La logica è: se la percentuale CP è <= (soglia + tolleranza) allora OK, altrimenti KO.
     *
     * @param percentageCp la percentuale CP del detail result
     * @param kpiC2Result il result principale per ottenere soglia e tolleranza
     * @return l'outcome specifico per questo detail result
     */
    private OutcomeStatus calculateDetailResultOutcome(BigDecimal percentageCp, KpiC2Result kpiC2Result) {
        if (percentageCp == null) {
            return OutcomeStatus.OK; // Se non ci sono dati, consideriamo OK
        }

        // Ottieni soglia e tolleranza dal result principale
        Double thresholdValue = kpiC2Result.getEligibilityThreshold(); // Soglia (es. 0%)
        Double toleranceValue = kpiC2Result.getTolerance(); // Tolleranza (es. 1%)

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
    public void updateKpiC2ResultOutcome(Long resultId, OutcomeStatus outcome) {
        log.debug("Updating KPI C2 result {} with outcome: {}", resultId, outcome);

        KpiC2Result result = kpiC2ResultRepository.findById(resultId)
            .orElseThrow(() -> new RuntimeException("KPI C2 result not found: " + resultId));

        result.setOutcome(outcome);
        kpiC2ResultRepository.save(result);

        log.info("Updated KPI C2 result {} outcome to: {}", resultId, outcome);
    }
}
