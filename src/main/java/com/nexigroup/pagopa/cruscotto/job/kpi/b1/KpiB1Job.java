package com.nexigroup.pagopa.cruscotto.job.kpi.b1;

import com.nexigroup.pagopa.cruscotto.config.ApplicationProperties;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.service.*;
import com.nexigroup.pagopa.cruscotto.service.dto.*;

import lombok.AllArgsConstructor;

import com.nexigroup.pagopa.cruscotto.job.config.JobConstant;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

/**
 * Job for calculating KPI B.1: "Numero enti intermediati o transazioni gestite"
 * 
 * Business Rule: È richiesta l'intermediazione di oltre 5 enti o almeno 250.000 transazioni nel periodo di riferimento
 * (More than 5 Institutions OR at least 250,000 transactions in the reference period)
 */
@Component
@AllArgsConstructor
@DisallowConcurrentExecution
public class KpiB1Job extends QuartzJobBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(KpiB1Job.class);

    // Business thresholds for B.1 KPI are now retrieved from KpiConfigurationDTO

    private final ApplicationProperties applicationProperties;
    private final InstanceService instanceService;
    private final InstanceModuleService instanceModuleService;
    private final KpiConfigurationService kpiConfigurationService;
    private final PagopaTransazioniService pagopaTransazioniService;
    private final KpiB1ResultService kpiB1ResultService;
    private final KpiB1DetailResultService kpiB1DetailResultService;
    private final KpiB1AnalyticDataService kpiB1AnalyticDataService;
    private final KpiB1AnalyticDrillDownService kpiB1AnalyticDrillDownService;
    private final Scheduler scheduler;

    /**
     * Aggregates drill-down data for KpiB1AnalyticData
     */
    private List<KpiB1AnalyticDrillDownDTO> aggregateKpiB1AnalyticDataDrillDown(
            AtomicReference<KpiB1AnalyticDataDTO> kpiB1AnalyticDataRef,
            List<PagopaTransactionDTO> filteredPeriodRecords,
            LocalDate detailResultEvaluationStartDate,
            LocalDate detailResultEvaluationEndDate) {

        List<KpiB1AnalyticDrillDownDTO> drillDownList = new ArrayList<>();

        for (PagopaTransactionDTO record : filteredPeriodRecords) {
            KpiB1AnalyticDrillDownDTO drillDown = new KpiB1AnalyticDrillDownDTO();
            drillDown.setKpiB1AnalyticDataId(kpiB1AnalyticDataRef.get().getId());
            drillDown.setInstitutionFiscalCode(record.getCfInstitution());
            drillDown.setStationCode(record.getStation());
            drillDown.setDataDate(record.getDate());
            drillDown.setTransactionCount(record.getTransactionTotal());
            drillDown.setPartnerFiscalCode(record.getCfPartner());
            drillDownList.add(drillDown);
        }

        return drillDownList;
    }

    /**
     * Aggregates KpiB1AnalyticData from transaction records for a specific month
     */
    private List<KpiB1AnalyticDataDTO> aggregateKpiB1AnalyticData(
            InstanceDTO instanceDTO,
            InstanceModuleDTO instanceModuleDTO,
            AtomicReference<KpiB1DetailResultDTO> kpiB1DetailResultRef,
            List<PagopaTransactionDTO> filteredMonthRecords,
            LocalDate detailResultEvaluationStartDate,
            LocalDate detailResultEvaluationEndDate) {

        List<KpiB1AnalyticDataDTO> analyticDataList = new ArrayList<>();

        // Group by date for the month
        Map<LocalDate, List<PagopaTransactionDTO>> groupedByDate = filteredMonthRecords.stream()
                .collect(Collectors.groupingBy(PagopaTransactionDTO::getDate));

        for (Map.Entry<LocalDate, List<PagopaTransactionDTO>> dateEntry : groupedByDate.entrySet()) {
            List<PagopaTransactionDTO> dateRecords = dateEntry.getValue();
            LocalDate recordDate = dateEntry.getKey();

            // Calculate totals for this date in the month
            int totalTransactions = (int) dateRecords.stream()
                    .mapToLong(PagopaTransactionDTO::getTransactionTotal)
                    .sum();

            int institutionCount = (int) dateRecords.stream()
                    .map(PagopaTransactionDTO::getCfInstitution)
                    .distinct()
                    .count();

            KpiB1AnalyticDataDTO analyticData = new KpiB1AnalyticDataDTO();
            analyticData.setInstanceId(instanceDTO.getId());
            analyticData.setInstanceModuleId(instanceModuleDTO.getId());
            analyticData.setAnalysisDate(LocalDate.now());
            analyticData.setDataDate(recordDate);
            analyticData.setInstitutionCount(institutionCount);
            analyticData.setTransactionCount(totalTransactions);
            analyticData.setKpiB1DetailResultId(kpiB1DetailResultRef.get().getId());

            analyticDataList.add(analyticData);
        }

        return analyticDataList;
    }

    /**
     * Aggregates KpiB1DetailResult for monthly and total evaluations
     */
    private List<KpiB1DetailResultDTO> aggregateKpiB1DetailResult(
            InstanceDTO instanceDTO,
            InstanceModuleDTO instanceModuleDTO,
            AtomicReference<KpiB1ResultDTO> kpiB1ResultRef,
            List<PagopaTransactionDTO> filteredPeriodRecords,
            AtomicReference<OutcomeStatus> kpiB1ResultFinalOutcome,
            Integer institutionThreshold,
            Long transactionThreshold,
            BigDecimal institutionTolerance,
            BigDecimal transactionTolerance) {

        List<KpiB1DetailResultDTO> detailResults = new ArrayList<>();
        LocalDate analysisStart = instanceDTO.getAnalysisPeriodStartDate();
        LocalDate analysisEnd = instanceDTO.getAnalysisPeriodEndDate();
        LocalDate current = analysisStart.withDayOfMonth(1);

        long totalTransactionsForPeriod = 0;
        int totalInstitutionsForPeriod = 0;

        // Process month by month
        while (!current.isAfter(analysisEnd)) {
            LocalDate firstDayOfMonth = current.withDayOfMonth(1).isBefore(analysisStart) ? analysisStart
                    : current.withDayOfMonth(1);
            LocalDate lastDayOfMonth = current.with(TemporalAdjusters.lastDayOfMonth()).isAfter(analysisEnd)
                    ? analysisEnd
                    : current.with(TemporalAdjusters.lastDayOfMonth());

            List<PagopaTransactionDTO> monthPeriodRecords = filteredPeriodRecords.stream()
                    .filter(record -> {
                        LocalDate recordDate = record.getDate();
                        return !recordDate.isBefore(firstDayOfMonth) && !recordDate.isAfter(lastDayOfMonth);
                    })
                    .collect(Collectors.toList());

            // Calculate monthly totals
            long monthlyTotalTransactions = monthPeriodRecords.stream()
                    .mapToLong(PagopaTransactionDTO::getTransactionTotal)
                    .sum();

            int monthlyUniqueInstitutions = (int) monthPeriodRecords.stream()
                    .map(PagopaTransactionDTO::getCfInstitution)
                    .distinct()
                    .count();

            totalTransactionsForPeriod += monthlyTotalTransactions;
            if (monthlyUniqueInstitutions > totalInstitutionsForPeriod) {
                totalInstitutionsForPeriod = monthlyUniqueInstitutions;
            }

            // Determine monthly outcome: OK if institutions > threshold OR transactions >= threshold
            OutcomeStatus monthlyOutcome = (monthlyUniqueInstitutions > institutionThreshold || 
                                         monthlyTotalTransactions >= transactionThreshold) 
                                         ? OutcomeStatus.OK : OutcomeStatus.KO;

            KpiB1DetailResultDTO detailResult = new KpiB1DetailResultDTO();
            detailResult.setInstanceId(instanceDTO.getId());
            detailResult.setInstanceModuleId(instanceModuleDTO.getId());
            detailResult.setKpiB1ResultId(kpiB1ResultRef.get().getId());
            detailResult.setAnalysisDate(LocalDate.now());
            detailResult.setEvaluationType(EvaluationType.MESE);
            detailResult.setEvaluationStartDate(firstDayOfMonth);
            detailResult.setEvaluationEndDate(lastDayOfMonth);
            detailResult.setTotalInstitutions(monthlyUniqueInstitutions);
            detailResult.setTotalTransactions((int) monthlyTotalTransactions);
            
            // Calculate differences: monthly actual - overall result values
            int institutionDifference = monthlyUniqueInstitutions - kpiB1ResultRef.get().getInstitutionCount();
            int transactionDifference = (int) (monthlyTotalTransactions - kpiB1ResultRef.get().getTransactionCount());
            
            // Calculate percentages: (monthly - overall) / overall * 100
            BigDecimal institutionDifferencePercentage = kpiB1ResultRef.get().getInstitutionCount() != 0 ? 
                BigDecimal.valueOf((double) institutionDifference / kpiB1ResultRef.get().getInstitutionCount() * 100) : BigDecimal.ZERO;
            BigDecimal transactionDifferencePercentage = kpiB1ResultRef.get().getTransactionCount() != 0 ? 
                BigDecimal.valueOf((double) transactionDifference / kpiB1ResultRef.get().getTransactionCount() * 100) : BigDecimal.ZERO;
            
            // Determine separate outcomes for institutions and transactions using tolerance against difference percentages
            OutcomeStatus institutionOutcome = monthlyUniqueInstitutions > institutionThreshold ? OutcomeStatus.OK : 
                (institutionDifferencePercentage.abs().compareTo(institutionTolerance) <= 0 ? OutcomeStatus.OK : OutcomeStatus.KO);
            OutcomeStatus transactionOutcome = monthlyTotalTransactions >= transactionThreshold ? OutcomeStatus.OK :
                (transactionDifferencePercentage.abs().compareTo(transactionTolerance) <= 0 ? OutcomeStatus.OK : OutcomeStatus.KO);
            
            detailResult.setInstitutionDifference(institutionDifference);
            detailResult.setInstitutionDifferencePercentage(institutionDifferencePercentage);
            detailResult.setTransactionDifference(transactionDifference);
            detailResult.setTransactionDifferencePercentage(transactionDifferencePercentage);
            detailResult.setInstitutionOutcome(institutionOutcome);
            detailResult.setTransactionOutcome(transactionOutcome);

            detailResults.add(detailResult);
            if (kpiB1ResultRef.get().getEvaluationType().compareTo(EvaluationType.MESE) == 0 &&
                    monthlyOutcome.compareTo(OutcomeStatus.KO) == 0) {
                kpiB1ResultFinalOutcome.set(OutcomeStatus.KO);
            }
            current = current.plusMonths(1);
        }

        // Add TOTALE detail result for the whole analysis period
        // For total period, we need to count unique Institutions across the entire period
        int totalUniqueInstitutionsAcrossPeriod = (int) filteredPeriodRecords.stream()
                .map(PagopaTransactionDTO::getCfInstitution)
                .distinct()
                .count();

        OutcomeStatus totalOutcomeStatus = (totalUniqueInstitutionsAcrossPeriod > institutionThreshold || 
                                          totalTransactionsForPeriod >= transactionThreshold) 
                                          ? OutcomeStatus.OK : OutcomeStatus.KO;

        if (kpiB1ResultRef.get().getEvaluationType() == EvaluationType.TOTALE &&
                totalOutcomeStatus == OutcomeStatus.KO) {
            kpiB1ResultFinalOutcome.set(OutcomeStatus.KO);
        }

        KpiB1DetailResultDTO totalDetailResult = new KpiB1DetailResultDTO();
        totalDetailResult.setInstanceId(instanceDTO.getId());
        totalDetailResult.setInstanceModuleId(instanceModuleDTO.getId());
        totalDetailResult.setKpiB1ResultId(kpiB1ResultRef.get().getId());
        totalDetailResult.setAnalysisDate(LocalDate.now());
        totalDetailResult.setEvaluationType(EvaluationType.TOTALE);
        totalDetailResult.setEvaluationStartDate(analysisStart);
        totalDetailResult.setEvaluationEndDate(analysisEnd);
        totalDetailResult.setTotalInstitutions(totalUniqueInstitutionsAcrossPeriod);
        totalDetailResult.setTotalTransactions((int) totalTransactionsForPeriod);
        
        // Calculate differences for total period: total actual - overall result values (should be 0)
        int totalInstitutionDifference = totalUniqueInstitutionsAcrossPeriod - kpiB1ResultRef.get().getInstitutionCount();
        int totalTransactionDifference = (int) (totalTransactionsForPeriod - kpiB1ResultRef.get().getTransactionCount());
        
        // Calculate percentages for total period: (total - overall) / overall * 100
        BigDecimal totalInstitutionDifferencePercentage = kpiB1ResultRef.get().getInstitutionCount() != 0 ? 
            BigDecimal.valueOf((double) totalInstitutionDifference / kpiB1ResultRef.get().getInstitutionCount() * 100) : BigDecimal.ZERO;
        BigDecimal totalTransactionDifferencePercentage = kpiB1ResultRef.get().getTransactionCount() != 0 ? 
            BigDecimal.valueOf((double) totalTransactionDifference / kpiB1ResultRef.get().getTransactionCount() * 100) : BigDecimal.ZERO;
        
        // Determine separate outcomes for institutions and transactions for total period using tolerance against difference percentages
        OutcomeStatus totalInstitutionOutcome = totalUniqueInstitutionsAcrossPeriod > institutionThreshold ? OutcomeStatus.OK : 
            (totalInstitutionDifferencePercentage.abs().compareTo(institutionTolerance) <= 0 ? OutcomeStatus.OK : OutcomeStatus.KO);
        OutcomeStatus totalTransactionOutcome = totalTransactionsForPeriod >= transactionThreshold ? OutcomeStatus.OK :
            (totalTransactionDifferencePercentage.abs().compareTo(transactionTolerance) <= 0 ? OutcomeStatus.OK : OutcomeStatus.KO);
        
        totalDetailResult.setInstitutionDifference(totalInstitutionDifference);
        totalDetailResult.setInstitutionDifferencePercentage(totalInstitutionDifferencePercentage);
        totalDetailResult.setTransactionDifference(totalTransactionDifference);
        totalDetailResult.setTransactionDifferencePercentage(totalTransactionDifferencePercentage);
        totalDetailResult.setInstitutionOutcome(totalInstitutionOutcome);
        totalDetailResult.setTransactionOutcome(totalTransactionOutcome);

        detailResults.add(totalDetailResult);

        return detailResults;
    }

    /**
     * Aggregates KpiB1Result from transaction records
     */
    private KpiB1ResultDTO aggregateKpiB1Result(
            InstanceDTO instanceDTO, 
            InstanceModuleDTO instanceModuleDTO, 
            KpiConfigurationDTO kpiConfigurationDTO, 
            List<PagopaTransactionDTO> records) {

        // Use counts from kpiConfigurationDTO if available, otherwise set to 0
        int institutionCount = kpiConfigurationDTO.getInstitutionCount() != null ? 
            kpiConfigurationDTO.getInstitutionCount() : 0;
        
        int transactionCount = kpiConfigurationDTO.getTransactionCount() != null ? 
            kpiConfigurationDTO.getTransactionCount() : 0;

        KpiB1ResultDTO kpiB1ResultDTO = new KpiB1ResultDTO();
        kpiB1ResultDTO.setInstanceId(instanceDTO.getId());
        kpiB1ResultDTO.setInstanceModuleId(instanceModuleDTO.getId());
        kpiB1ResultDTO.setAnalysisDate(LocalDate.now());
        kpiB1ResultDTO.setEvaluationType(kpiConfigurationDTO.getEvaluationType());
        kpiB1ResultDTO.setInstitutionCount(institutionCount);
        kpiB1ResultDTO.setTransactionCount(transactionCount);
        kpiB1ResultDTO.setInstitutionTolerance(kpiConfigurationDTO.getInstitutionTolerance() != null ? 
            kpiConfigurationDTO.getInstitutionTolerance() : BigDecimal.ZERO);
        kpiB1ResultDTO.setTransactionTolerance(kpiConfigurationDTO.getTransactionTolerance() != null ? 
            kpiConfigurationDTO.getTransactionTolerance() : BigDecimal.ZERO);
        kpiB1ResultDTO.setOutcome(!records.isEmpty() ? OutcomeStatus.STANDBY : OutcomeStatus.OK);

        return kpiB1ResultDTO;
    }

    @Override
    public void executeInternal(@NonNull JobExecutionContext context) {
        AtomicReference<OutcomeStatus> kpiB1ResultFinalOutcome = new AtomicReference<>(OutcomeStatus.OK);
        LOGGER.info("Start calculate kpi B.1");

        if (!applicationProperties.getJob().getKpiB1Job().isEnabled()) {
            LOGGER.info("Job calculate kpi B.1 disabled. Exit...");
            return;
        }

        List<InstanceDTO> instanceDTOS = instanceService.findInstanceToCalculate(
                ModuleCode.B1,
                applicationProperties.getJob().getKpiB1Job().getLimit()
        );

        if (instanceDTOS.isEmpty()) {
            LOGGER.info("No instance to calculate B.1. Exit....");
            return;
        }

        KpiConfigurationDTO kpiConfigurationDTO = kpiConfigurationService
                .findKpiConfigurationByCode(ModuleCode.B1.code)
                .orElseThrow(() -> new NullPointerException("KPI B.1 Configuration not found"));

        LOGGER.info("Kpi configuration {}", kpiConfigurationDTO);
        List<String> errors = new ArrayList<>();

        for (InstanceDTO instanceDTO : instanceDTOS) {
            try {
                LOGGER.info(
                        "Start elaboration instance {} for partner {} - {} with period {} - {}",
                        instanceDTO.getInstanceIdentification(),
                        instanceDTO.getPartnerFiscalCode(),
                        instanceDTO.getPartnerName(),
                        instanceDTO.getAnalysisPeriodStartDate(),
                        instanceDTO.getAnalysisPeriodEndDate()
                );

                instanceService.updateInstanceStatusInProgress(instanceDTO.getId());

                InstanceModuleDTO instanceModuleDTO = instanceModuleService
                        .findOne(instanceDTO.getId(), kpiConfigurationDTO.getModuleId())
                        .orElseThrow(() -> new NullPointerException("KPI B.1 InstanceModule not found"));

                LOGGER.info("Deletion phase for any previous processing in error");

                // Delete previous data for this instanceModule
                List<KpiB1AnalyticDataDTO> analyticDataListToDelete = kpiB1AnalyticDataService
                        .findByInstanceModuleId(instanceModuleDTO.getId());
                List<Long> analyticDataIds = analyticDataListToDelete.stream()
                        .map(KpiB1AnalyticDataDTO::getId)
                        .collect(Collectors.toList());

                kpiB1AnalyticDrillDownService.deleteByKpiB1AnalyticDataIds(analyticDataIds);
                LOGGER.info("Deleted kpiB1AnalyticDrillDown records for analyticDataIds: {}", analyticDataIds);

                kpiB1AnalyticDataService.deleteAllByInstanceModule(instanceModuleDTO.getId());
                kpiB1DetailResultService.deleteAllByInstanceModule(instanceModuleDTO.getId());
                kpiB1ResultService.deleteAllByInstanceModule(instanceModuleDTO.getId());

                // Fetch all transaction records for the analysis period from PAGOPA_TRANSAZIONI
                List<PagopaTransactionDTO> periodRecords = pagopaTransazioniService
                        .findAllRecordIntoPeriodForPartner(
                                instanceDTO.getPartnerFiscalCode(),
                                instanceDTO.getAnalysisPeriodStartDate(),
                                instanceDTO.getAnalysisPeriodEndDate());

                LOGGER.info("Found {} transaction records for analysis period", periodRecords.size());

                // --- Aggregation: KpiB1Result ---
                KpiB1ResultDTO kpiB1ResultDTO = aggregateKpiB1Result(instanceDTO, instanceModuleDTO, 
                        kpiConfigurationDTO, periodRecords);
                AtomicReference<KpiB1ResultDTO> kpiB1ResultRef = new AtomicReference<>(
                        kpiB1ResultService.save(kpiB1ResultDTO));

                // For B.1, we don't need maintenance filtering like B.2, but we use all records
                List<PagopaTransactionDTO> filteredPeriodRecords = new ArrayList<>(periodRecords);

                // Extract thresholds from the saved result
                Integer institutionThreshold = kpiB1ResultRef.get().getInstitutionCount();
                Long transactionThreshold = kpiB1ResultRef.get().getTransactionCount().longValue();
                BigDecimal institutionTolerance = kpiB1ResultRef.get().getInstitutionTolerance();
                BigDecimal transactionTolerance = kpiB1ResultRef.get().getTransactionTolerance();

                // --- Aggregation: KpiB1DetailResult ---
                List<KpiB1DetailResultDTO> detailResults = aggregateKpiB1DetailResult(
                        instanceDTO,
                        instanceModuleDTO,
                        kpiB1ResultRef,
                        filteredPeriodRecords,
                        kpiB1ResultFinalOutcome,
                        institutionThreshold,
                        transactionThreshold,
                        institutionTolerance,
                        transactionTolerance);

                for (KpiB1DetailResultDTO detailResult : detailResults) {
                    AtomicReference<KpiB1DetailResultDTO> kpiB1DetailResultRef = new AtomicReference<>(
                            kpiB1DetailResultService.save(detailResult));

                    if (detailResult.getEvaluationType() == EvaluationType.MESE) {
                        // --- Aggregation: KpiB1AnalyticData ---
                        List<KpiB1AnalyticDataDTO> analyticDataList = aggregateKpiB1AnalyticData(
                                instanceDTO,
                                instanceModuleDTO,
                                kpiB1DetailResultRef,
                                filteredPeriodRecords.stream()
                                        .filter(record -> {
                                            LocalDate recordDate = record.getDate();
                                            return !recordDate.isBefore(detailResult.getEvaluationStartDate())
                                                    && !recordDate.isAfter(detailResult.getEvaluationEndDate());
                                        })
                                        .collect(Collectors.toList()),
                                detailResult.getEvaluationStartDate(),
                                detailResult.getEvaluationEndDate());

                        for (KpiB1AnalyticDataDTO analyticData : analyticDataList) {
                            AtomicReference<KpiB1AnalyticDataDTO> kpiB1AnalyticDataRef = new AtomicReference<>(
                                    kpiB1AnalyticDataService.save(analyticData));

                            // --- Aggregation: DrillDown ---
                            List<KpiB1AnalyticDrillDownDTO> drillDowns = aggregateKpiB1AnalyticDataDrillDown(
                                    kpiB1AnalyticDataRef,
                                    filteredPeriodRecords.stream()
                                            .filter(record -> {
                                                LocalDate recordDate = record.getDate();
                                                return !recordDate.isBefore(detailResult.getEvaluationStartDate()) &&
                                                       !recordDate.isAfter(detailResult.getEvaluationEndDate());
                                            })
                                            .collect(Collectors.toList()),
                                    detailResult.getEvaluationStartDate(),
                                    detailResult.getEvaluationEndDate()
                            );
                            kpiB1AnalyticDrillDownService.saveAll(drillDowns);
                        }
                    }
                }

                // Final outcome update
                LOGGER.info("Final outcome {}", kpiB1ResultFinalOutcome.get());
                kpiB1ResultService.updateKpiB1ResultOutcome(kpiB1ResultRef.get().getId(), 
                        kpiB1ResultFinalOutcome.get());
                instanceModuleService.updateAutomaticOutcome(instanceModuleDTO.getId(), 
                        kpiB1ResultFinalOutcome.get());

                // Trigger state calculation job
                JobDetail job = scheduler.getJobDetail(JobKey.jobKey(JobConstant.CALCULATE_STATE_INSTANCE_JOB, "DEFAULT"));
                Trigger trigger = TriggerBuilder.newTrigger()
                        .usingJobData("instanceId", instanceDTO.getId())
                        .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                                .withMisfireHandlingInstructionFireNow()
                                .withRepeatCount(0))
                        .forJob(job)
                        .build();
                scheduler.scheduleJob(trigger);

            } catch (Exception e) {
                errors.add(String.format(
                        "Error in elaboration instance %s for partner %s - %s with period %s - %s. Exception: %s",
                        instanceDTO.getInstanceIdentification(),
                        instanceDTO.getPartnerFiscalCode(),
                        instanceDTO.getPartnerName(),
                        instanceDTO.getAnalysisPeriodStartDate(),
                        instanceDTO.getAnalysisPeriodEndDate(),
                        e.getMessage()
                ));
                LOGGER.error(
                        "Error in elaboration instance {} for partner {} - {} with period {} - {}",
                        instanceDTO.getInstanceIdentification(),
                        instanceDTO.getPartnerFiscalCode(),
                        instanceDTO.getPartnerName(),
                        instanceDTO.getAnalysisPeriodStartDate(),
                        instanceDTO.getAnalysisPeriodEndDate(),
                        e
                );
            }
        }

        if (!errors.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            errors.forEach(error -> sb.append(error).append("\n"));
            throw new RuntimeException(sb.toString());
        }

        LOGGER.info("End");
    }
}