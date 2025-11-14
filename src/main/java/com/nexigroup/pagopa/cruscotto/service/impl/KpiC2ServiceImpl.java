package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.*;
import com.nexigroup.pagopa.cruscotto.domain.Module;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.repository.*;
import com.nexigroup.pagopa.cruscotto.service.AnagInstitutionService;
import com.nexigroup.pagopa.cruscotto.service.InstanceModuleService;
import com.nexigroup.pagopa.cruscotto.service.KpiC2Service;
import com.nexigroup.pagopa.cruscotto.service.dto.*;
import com.nexigroup.pagopa.cruscotto.service.filter.AnagInstitutionFilter;
import com.nexigroup.pagopa.cruscotto.service.mapper.KpiC2ResultMapper;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.time.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final PagopaSendRepository pagopaSendRepository;
    private final AnagStationRepository anagStationRepository;
    private final InstanceModuleService instanceModuleService;
    private final KpiC2ResultMapper kpiC2ResultMapper;
    private final KpiC2AnalyticDrillDownRepository kpiC2AnalyticDrillDownRepository;
    private final AnagInstitutionService anagInstitutionService;


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
            result.setInstitutionTolerance(configuration.getInstitutionTolerance() != null ? configuration.getInstitutionTolerance().doubleValue() : 0.0);
            result.setNotificationTolerance(configuration.getNotificationTolerance() != null ? configuration.getNotificationTolerance().doubleValue() : 0.0);
            result.setEvaluationType(configuration.getEvaluationType() != null ? configuration.getEvaluationType() : EvaluationType.MESE);
            result.setOutcome(outcomeStatus);

            KpiC2Result savedResult = kpiC2ResultRepository.save(result);

            // NUOVO: Popola anche le tabelle di dettaglio KpiC2DetailResult e KpiC2AnalyticData
            AnagInstitutionFilter filter = new AnagInstitutionFilter();
            filter.setPartnerId(instance.getPartner().getId());
            List<AnagInstitutionDTO> inistutionListPartner = anagInstitutionService.findAllNoPaging(filter);
            List<String> listInstitutionFiscalCode = inistutionListPartner.stream()
                .map(anagInstitutionDTO -> anagInstitutionDTO.getInstitutionIdentification().getFiscalCode())
                .distinct() // <-- rimuove i duplicati
                .toList();

            createAndSaveDetailResults(savedResult, instance, listInstitutionFiscalCode);
            createAndSaveAnalyticData(savedResult, instance, listInstitutionFiscalCode);

            log.info("KPI C.2 calculated for instance {}: {}% success rate (threshold: {}%), status: {} - Detail and analytic data saved",
                instance.getId(), successPercentage, threshold, outcomeStatus);

            return kpiC2ResultMapper.toDto(savedResult);

        } catch (Exception e) {
            log.error("Error calculating KPI C.2 for instance {}: {}", instance.getId(), e.getMessage(), e);
            throw new RuntimeException("Failed to calculate KPI C.2", e);
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
        // For KPI C.2, details are typically aggregated by API type or time period
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
     * Questa tabella contiene il dettaglio dei risultati KPI C.2 per ogni stazione dell'ente.
     */
    private void createAndSaveDetailResults(KpiC2Result kpiC2Result, Instance instance, List<String> listInstitutionFiscalCode) {
        log.info("Creating KPI C.2 detail results for instance {} (partner-level aggregated)", instance.getId());

        try {
            // Verifica che il partner abbia stazioni (necessario per calcolare il KPI C.2)
            List<AnagStation> stations = anagStationRepository.findByAnagPartnerFiscalCode(instance.getPartner().getFiscalCode());

            if (stations.isEmpty()) {
                log.warn("SKIPPING KPI C.2 detail results for partner {} - No stations found. Cannot calculate KPI C.2 without stations.",
                    instance.getPartner().getFiscalCode());
                return; // Salta il partner se non ha stazioni associate
            }

            LocalDate analysisDate = kpiC2Result.getAnalysisDate();
            LocalDate periodStart = instance.getAnalysisPeriodStartDate();
            LocalDate periodEnd = instance.getAnalysisPeriodEndDate();
            String partnerFiscalCode = instance.getPartner().getFiscalCode();

            // Ottieni la prima stazione per il campo obbligatorio (il KPI C.2 è a livello partner, non per singola stazione)
            AnagStation primaryStation = stations.get(0);

            // Calcola tutti i mesi nel periodo di analisi
            List<YearMonth> monthsInPeriod = getMonthsInPeriod(periodStart, periodEnd);

            int totalApiCallsAllMonths = 0;



            Long numberTotalIntitution = listInstitutionFiscalCode!=null ? listInstitutionFiscalCode.size():0l;
            //pagopaSendRepository.calculateTotalNumberInsitution(partnerFiscalCode, listInstitutionFiscalCode);


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
                Long numberTotalInstitutionSend = pagopaSendRepository.calculateTotalNumberInstitutionSend(partnerFiscalCode,listInstitutionFiscalCode, monthStart.atStartOfDay(), endOfDay(monthEnd));
                Long totalNumberPayment = pagopaSendRepository.calculateTotalNumberPayment(partnerFiscalCode, listInstitutionFiscalCode,monthStart.atStartOfDay(), endOfDay(monthEnd));
                Long totalNumberNotification = pagopaSendRepository.calculateTotalNumberNotification(partnerFiscalCode, listInstitutionFiscalCode,monthStart.atStartOfDay(), endOfDay(monthEnd));

                if (numberTotalIntitution == null) numberTotalIntitution = 0L;
                if (numberTotalInstitutionSend == null) numberTotalInstitutionSend = 0L;


                // Calcola percentuale Institution per questo mese
                BigDecimal monthlyPercentageInstitution = getPercentagePeriodInstitution(numberTotalIntitution, numberTotalInstitutionSend);

                if (totalNumberPayment == null) totalNumberPayment = 0L;
                if (totalNumberNotification == null) totalNumberNotification = 0L;
                // Calcola percentuale notification Payment per questo mese
                BigDecimal monthlyPercentageNotification = getPercentagePeriodNotification(totalNumberPayment, totalNumberNotification);


                // Crea record mensile (PARTNER LEVEL - aggregato per tutto il partner)
                KpiC2DetailResult monthlyDetailResult = new KpiC2DetailResult();
                monthlyDetailResult.setInstanceId(instance.getId());
                monthlyDetailResult.setInstanceModuleId(kpiC2Result.getInstanceModule().getId());
                monthlyDetailResult.setInstance(instance);
                monthlyDetailResult.setInstanceModule(kpiC2Result.getInstanceModule());
                monthlyDetailResult.setKpiC2Result(kpiC2Result);
                monthlyDetailResult.setAnalysisDate(analysisDate);
                monthlyDetailResult.setEvaluationType(EvaluationType.MESE);
                monthlyDetailResult.setEvaluationStartDate(monthStart);
                monthlyDetailResult.setEvaluationEndDate(monthEnd);

                monthlyDetailResult.setTotalInstitution(numberTotalIntitution);
                monthlyDetailResult.setTotalInstitutionSend(numberTotalInstitutionSend);
                monthlyDetailResult.setPercentInstitutionSend(monthlyPercentageInstitution);


                monthlyDetailResult.setTotalPayment(totalNumberPayment);
                monthlyDetailResult.setTotalNotification(totalNumberNotification);
                monthlyDetailResult.setPercentEntiOk(monthlyPercentageNotification);


                monthlyDetailResult.setOutcome(calculateDetailResultOutcome(monthlyPercentageInstitution,monthlyPercentageNotification, kpiC2Result)); // Calcola outcome specifico per questo detail result

                kpiC2DetailResultRepository.save(monthlyDetailResult);
                log.info("Created {} monthly + 1 total KPI C.2 detail results for partner {} " +
                        "with numberTotalIntitution {} " +
                        "with numberTotalInstitutionSend {} " +
                        "with totalNumberPayment {} " +
                        "with totalNumberNotification {} "
                    ,
                    monthsInPeriod.size(), partnerFiscalCode, numberTotalIntitution,numberTotalInstitutionSend, totalNumberPayment,totalNumberNotification  );


                log.debug("Saved monthly KPI C.2 detail result for partner {} in {}: {} TOT INST, {} TOT INST SEND",
                    partnerFiscalCode, yearMonth, numberTotalIntitution, numberTotalInstitutionSend);
            }



            Long numberTotalInstitutionSend = pagopaSendRepository.calculateTotalNumberInstitutionSend(partnerFiscalCode,listInstitutionFiscalCode, periodStart.atStartOfDay(), endOfDay(periodEnd));
            Long totalNumberPayment = pagopaSendRepository.calculateTotalNumberPayment(partnerFiscalCode, listInstitutionFiscalCode,periodStart.atStartOfDay(), endOfDay(periodEnd));
            Long totalNumberNotification = pagopaSendRepository.calculateTotalNumberNotification(partnerFiscalCode, listInstitutionFiscalCode,periodStart.atStartOfDay(), endOfDay(periodEnd));

            if (numberTotalIntitution == null) numberTotalIntitution = 0L;
            if (numberTotalInstitutionSend == null) numberTotalInstitutionSend = 0L;


            // Calcola percentuale Institution per questo mese
            BigDecimal percentageInstitutionPeriod = getPercentagePeriodInstitution(numberTotalIntitution, numberTotalInstitutionSend);

            if (totalNumberPayment == null) totalNumberPayment = 0L;
            if (totalNumberNotification == null) totalNumberNotification = 0L;
            // Calcola percentuale notification Payment per questo mese
            BigDecimal percentageNotificationPeriod = getPercentagePeriodNotification(totalNumberPayment, totalNumberNotification);

            // Crea record totale (intero periodo di analisi, livello partner)
            KpiC2DetailResult totalDetailResult = new KpiC2DetailResult();
            totalDetailResult.setInstanceId(instance.getId());
            totalDetailResult.setInstanceModuleId(kpiC2Result.getInstanceModule().getId());
            totalDetailResult.setInstance(instance);
            totalDetailResult.setInstanceModule(kpiC2Result.getInstanceModule());
            totalDetailResult.setKpiC2Result(kpiC2Result);
            totalDetailResult.setAnalysisDate(analysisDate);
            totalDetailResult.setEvaluationType(com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType.TOTALE);
            totalDetailResult.setEvaluationStartDate(periodStart);
            totalDetailResult.setEvaluationEndDate(periodEnd);

            totalDetailResult.setTotalInstitution(numberTotalIntitution);
            totalDetailResult.setTotalInstitutionSend(numberTotalInstitutionSend);
            totalDetailResult.setPercentInstitutionSend(percentageInstitutionPeriod);


            totalDetailResult.setTotalPayment(totalNumberPayment);
            totalDetailResult.setTotalNotification(totalNumberNotification);
            totalDetailResult.setPercentEntiOk(percentageNotificationPeriod);
            totalDetailResult.setOutcome(calculateDetailResultOutcome(percentageInstitutionPeriod, percentageNotificationPeriod, kpiC2Result)); // Calcola outcome specifico per questo detail result

            kpiC2DetailResultRepository.save(totalDetailResult);

            log.info("Created {} monthly + 1 total KPI C.2 detail results for partner {} " +
                    "with numberTotalIntitution {} " +
                    "with numberTotalInstitutionSend {} " +
                    "with totalNumberPayment {} " +
                    "with totalNumberNotification {} "
                ,
                monthsInPeriod.size(), partnerFiscalCode, numberTotalIntitution,numberTotalInstitutionSend, totalNumberPayment,totalNumberNotification  );

        } catch (Exception e) {
            log.error("Error creating KPI C.2 detail results for instance {}: {}", instance.getId(), e.getMessage(), e);
            throw new RuntimeException("Failed to create KPI C.2 detail results", e);
        }
    }

    private LocalDateTime endOfDay(LocalDate date){
        return LocalDateTime.of(date, LocalTime.MAX);
    }



    public static @NotNull BigDecimal getPercentagePeriodInstitution(Long totalNumberIntitution, Long totalNumberInstitutionSend) {
        BigDecimal percentageInstitutionPeriod = BigDecimal.ZERO;
        BigDecimal percentageInstitutionPeriodMin = new BigDecimal("0.01").setScale(2, RoundingMode.UNNECESSARY);

        if (totalNumberIntitution > 0) {
            percentageInstitutionPeriod = new BigDecimal(totalNumberInstitutionSend)
                .multiply(new BigDecimal("100"))
                .divide(new BigDecimal(totalNumberIntitution), 2, RoundingMode.HALF_UP);
            if (totalNumberInstitutionSend>0){
                percentageInstitutionPeriod=percentageInstitutionPeriod.compareTo(percentageInstitutionPeriodMin)>0?
                    percentageInstitutionPeriod:
                    percentageInstitutionPeriodMin ;
            }
        }


        return percentageInstitutionPeriod;
    }

    public static @NotNull BigDecimal getPercentagePeriodNotification(Long totalNumberPayment, Long totalNumberNotification) {
        BigDecimal percentageNotificationPeriod = BigDecimal.ZERO;
        if (totalNumberPayment > 0) {
            percentageNotificationPeriod = new BigDecimal(totalNumberNotification)
                .multiply(new BigDecimal("100"))
                .divide(new BigDecimal(totalNumberPayment), 2, RoundingMode.HALF_UP);
        }
        BigDecimal maxPercentage = new BigDecimal("100.00").setScale(2, RoundingMode.UNNECESSARY);

        return percentageNotificationPeriod.compareTo(maxPercentage)>0?maxPercentage: percentageNotificationPeriod;
    }



    /**
     * Crea e salva i record KpiC2AnalyticData con dati analitici giornalieri delle API.
     * Questa tabella contiene l'andamento giornaliero delle chiamate API per l'analisi di drill-down.
     */
    private void createAndSaveAnalyticData(KpiC2Result kpiC2Result, Instance instance, List<String> listInstitutionFiscalCode) {
        log.info("Creating KPI C.2 analytic data for instance {}", instance.getId());

        try {
            LocalDate periodStart = instance.getAnalysisPeriodStartDate();
            LocalDate periodEnd = instance.getAnalysisPeriodEndDate();
            String partnerFiscalCode = instance.getPartner().getFiscalCode();

            // Ottieni i KpiC2DetailResult salvati per questo risultato
            List<KpiC2DetailResult> detailResults = kpiC2DetailResultRepository.findByKpiC2Result(kpiC2Result);

            if (detailResults.isEmpty()) {
                log.warn("No detail results found for KPI C.2 result {}, cannot create analytic data", kpiC2Result.getId());
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
            List<Object[]> dailyAggregatedData = pagopaSendRepository.calculateDailyAggregatedDataAndInstitutionAndNotification(
                partnerFiscalCode,listInstitutionFiscalCode, periodStart.atStartOfDay(), endOfDay(periodEnd));

            if (dailyAggregatedData.isEmpty()) {
                log.warn("No daily aggregated data found for partner {} in period {} to {}",
                    partnerFiscalCode, periodStart, periodEnd);
                return;
            }

            for (Object[] dayData : dailyAggregatedData) {
                Date date = (Date) dayData[0];
                LocalDate evaluationDate = date.toLocalDate();
                Long totalInstitutions = (Long) dayData[1];
                Long institutionsWithPayments = (Long) dayData[2];
                Long totalPayments = (Long) dayData[3];
                Long totalNotifications = (Long) dayData[4];

                // Gestione valori null
                if (totalInstitutions == null) totalInstitutions = 0L;
                if (institutionsWithPayments == null) institutionsWithPayments = 0L;
                if (totalPayments == null) totalPayments = 0L;
                if (totalNotifications == null) totalNotifications = 0L;


                // Trova il detail result corretto per questa data
                KpiC2DetailResult appropriateDetailResult = findDetailResultForDate(evaluationDate, detailResultsByDate, fallbackDetailResult);

                // Crea UN SOLO record per giornata con entrambi i valori GPD e CP
                // (secondo l'analisi: "Numero Request GPD" e "Numero Request CP" per ogni giornata)
                KpiC2AnalyticData dailyAnalyticData = new KpiC2AnalyticData();
                dailyAnalyticData.setInstance(instance);
                dailyAnalyticData.setInstanceModule(kpiC2Result.getInstanceModule());
                dailyAnalyticData.setAnalysisDate(kpiC2Result.getAnalysisDate());
                dailyAnalyticData.setEvaluationDate(evaluationDate);
                dailyAnalyticData.setInstanceModule(kpiC2Result.getInstanceModule());

                dailyAnalyticData.setNumInstitution(totalInstitutions); // Numero Request GPD del giorno
                dailyAnalyticData.setNumInstitutionSend(institutionsWithPayments);
                dailyAnalyticData.setPerInstitutionSend(getPercentagePeriodInstitution(totalInstitutions, institutionsWithPayments));

                dailyAnalyticData.setNumPayment(totalPayments);
                dailyAnalyticData.setNumNotification(totalNotifications);
                dailyAnalyticData.setPerNotification(getPercentagePeriodNotification(totalPayments, totalNotifications));

                dailyAnalyticData.setKpiC2DetailResult(appropriateDetailResult);

                KpiC2AnalyticData savedAnalyticData = kpiC2AnalyticDataRepository.save(dailyAnalyticData);

                log.debug("Saved KPI C.2 analytic data for date {} - totalInstitutions: {}, institutionsWithPayments: {} , totalPayments {} , totalNotifications {} -  (linked to detail result {})",
                    evaluationDate, totalInstitutions, institutionsWithPayments,totalPayments,totalNotifications,  appropriateDetailResult.getId());

                // Popola la tabella drilldown con i dati API log dettagliati per questa data
                populateDrilldownData(savedAnalyticData,listInstitutionFiscalCode, instance, evaluationDate);
            }

            log.info("Created {} days of KPI C.2 analytic data for instance {} with proper detail result associations",
                dailyAggregatedData.size(), instance.getId());

        } catch (Exception e) {
            log.error("Error creating KPI C.2 analytic data for instance {}: {}", instance.getId(), e.getMessage(), e);
            throw new RuntimeException("Failed to create KPI C.2 analytic data", e);
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
     * per preservare uno snapshot storico al momento dell'analisi KPI C.2.
     *
     * @param analyticData il record KpiC2AnalyticData salvato
     * @param instance l'istanza analizzata
     * @param evaluationDate la data di valutazione
     */
    private void populateDrilldownData(KpiC2AnalyticData analyticData,List<String> listInstitutionFiscalCode, Instance instance, LocalDate evaluationDate) {
        log.debug("Populating drilldown data for KPI C.2 analytic data {} on date {}",
            analyticData.getId(), evaluationDate);

        try {
            String partnerFiscalCode = instance.getPartner().getFiscalCode();

            // Query dettagliata per ottenere i singoli record API log per la data specifica
            // Questo crea uno snapshot storico dei dati al momento dell'analisi
            List<Object[]> detailedApiLogData = pagopaSendRepository.findDetailedPagopaSendByPartnerAndDate(
                partnerFiscalCode,listInstitutionFiscalCode, evaluationDate.atStartOfDay());

            if (detailedApiLogData.isEmpty()) {
                log.debug("No detailed API log data found for partner {} on date {}", partnerFiscalCode, evaluationDate);
                return;
            }

            for (Object[] logData : detailedApiLogData) {
                // Estrazione dati dal result set basandosi sulla query del repository
                // Format atteso: [CF_PARTNER, DATE, STATION, CF_ENTE, API, TOT_REQ, REQ_OK, REQ_KO]
                String cfPartner = (String) logData[0];
                String cfInstitution = (String) logData[1];
                LocalDate date = ((LocalDateTime) logData[2]).toLocalDate();

                Long paymentsNumber =  ((Long) logData[3]).longValue();
                Long notificationNumber = ((Long) logData[4]).longValue();

                // Trova la stazione corrispondente al station code

// Crea l'entità drilldown direttamente per avere controllo completo sui riferimenti
                KpiC2AnalyticDrillDown drilldownEntity = new KpiC2AnalyticDrillDown();

                // Setta i riferimenti alle entità
                drilldownEntity.setInstance(instance);
                drilldownEntity.setInstanceModule(analyticData.getKpiC2DetailResult().getInstanceModule());
                drilldownEntity.setKpiC2AnalyticData(analyticData);

                // Setta la data di analisi
                drilldownEntity.setAnalysisDate(analyticData.getAnalysisDate());

                // Setta i dati API log
                drilldownEntity.setInstitutionCf(cfInstitution);
                drilldownEntity.setNumPayment(paymentsNumber);
                drilldownEntity.setNumNotification(notificationNumber);
                drilldownEntity.setPercentNotification(getPercentagePeriodNotification(paymentsNumber, notificationNumber));


                // Salva direttamente l'entità nel repository
                kpiC2AnalyticDrillDownRepository.save(drilldownEntity);

                log.trace("Saved drilldown record: {} {} {} {} {} ",
                    cfPartner, date, cfInstitution, paymentsNumber, notificationNumber);
            }

            log.debug("Populated {} drilldown records for KPI C.2 analytic data {} on date {}",
                detailedApiLogData.size(), analyticData.getId(), evaluationDate);

        } catch (Exception e) {
            log.error("Error populating drilldown data for KPI C.2 analytic data {} on date {}: {}",
                analyticData.getId(), evaluationDate, e.getMessage(), e);
            // Non interrompere il flusso principale per errori nel drilldown
        }
    }

    /**
     * Calcola l'outcome specifico per un KpiC2DetailResult basato sulla percentuale CP.
     * La logica è: se la percentuale CP è <= (soglia + tolleranza) allora OK, altrimenti KO.
     *
     * @param percentageInstitution  la percentuale CP del detail result
     * @param percentageNotification
     * @param kpiC2Result            il result principale per ottenere soglia e tolleranza
     * @return l'outcome specifico per questo detail result
     */
    private OutcomeStatus calculateDetailResultOutcome(BigDecimal percentageInstitution, BigDecimal percentageNotification, KpiC2Result kpiC2Result) {
        if (percentageInstitution == null || percentageNotification ==null) {
            return OutcomeStatus.OK; // Se non ci sono dati, consideriamo OK
        }

        // Ottieni soglia e tolleranza dal result principale
        Double toleranceInstitution = kpiC2Result.getInstitutionTolerance(); // Soglia (es. 0%)
        Double toleranceNotification = kpiC2Result.getNotificationTolerance(); // Tolleranza (es. 1%)

        // Valori di default se non configurati
        if (toleranceInstitution == null) toleranceInstitution = 0.0; // Default soglia 0%
        if (toleranceNotification == null) toleranceNotification = 0.0; // Default tolleranza 1%

        // Calcola il limite massimo consentito
        BigDecimal toleranceInstitutionBD = BigDecimal.valueOf(toleranceInstitution);
        BigDecimal toleranceNotificationBD = BigDecimal.valueOf(toleranceNotification);

        // Se la percentuale CP è <= (soglia + tolleranza) → OK, altrimenti → KO
        boolean isCompliantInstitution = percentageInstitution.compareTo(toleranceInstitutionBD) >= 0;
        boolean isCompliantNotification = percentageNotification.compareTo(toleranceNotificationBD) >= 0;

        OutcomeStatus outcome = isCompliantInstitution && isCompliantNotification ? OutcomeStatus.OK : OutcomeStatus.KO;

        log.debug("Detail result outcome calculation:  " +
                "percentageInstitution={}%, percentageNotification={}%" +
                ", toleranceInstitution={}%, toleranceNotification={}%, " +
                "outcome={}",
            percentageInstitution, percentageNotification, toleranceInstitution, toleranceNotification, outcome);

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
