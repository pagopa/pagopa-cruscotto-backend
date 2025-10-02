package com.nexigroup.pagopa.cruscotto.job.kpi.b9;

import com.nexigroup.pagopa.cruscotto.config.ApplicationProperties;
import com.nexigroup.pagopa.cruscotto.domain.AnagStation;
import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.InstanceModule;
import com.nexigroup.pagopa.cruscotto.domain.PagoPaPaymentReceiptDrilldown;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.*;
import com.nexigroup.pagopa.cruscotto.job.config.JobConstant;
import com.nexigroup.pagopa.cruscotto.service.*;
import com.nexigroup.pagopa.cruscotto.service.dto.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.jetbrains.annotations.NotNull;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@DisallowConcurrentExecution
public class KpiB9Job extends QuartzJobBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(KpiB9Job.class);

    private final InstanceService instanceService;

    private final InstanceModuleService instanceModuleService;

    private final ApplicationProperties applicationProperties;

    private final PagoPaPaymentReceiptService pagoPaPaymentReceiptService;

    private final AnagStationService anagStationService;

    private final KpiConfigurationService kpiConfigurationService;

    private final AnagPlannedShutdownService anagPlannedShutdownService;

    private final KpiB9AnalyticDataService kpiB9AnalyticDataService;

    private final KpiB9DetailResultService kpiB9DetailResultService;

    private final KpiB9ResultService kpiB9ResultService;

    private final PagoPaPaymentReceiptDrilldownService drilldownService;

    private final Scheduler scheduler;

    @Override
    protected void executeInternal(@NotNull JobExecutionContext context) {
        LOGGER.info("Starting KPI B.9 calculation job");

        try {
            if (!applicationProperties.getJob().getKpiB9Job().isEnabled()) {
                LOGGER.info("KPI B.9 calculation job is disabled. Exiting...");
                return;
            }

            List<InstanceDTO> instanceDTOS = instanceService.findInstanceToCalculate(
                ModuleCode.B9,
                applicationProperties.getJob().getKpiB9Job().getLimit()
            );

            if (instanceDTOS.isEmpty()) {
                LOGGER.info("No instances found for KPI B.9 calculation. Exiting...");
            } else {
                KpiConfigurationDTO kpiConfigurationDTO = kpiConfigurationService
                    .findKpiConfigurationByCode(ModuleCode.B9.code)
                    .orElseThrow(() -> new NullPointerException("KPI B.9 Configuration not found"));

                LOGGER.debug("Using KPI configuration: {}", kpiConfigurationDTO);

                Double eligibilityThreshold = kpiConfigurationDTO.getEligibilityThreshold() != null
                    ? kpiConfigurationDTO.getEligibilityThreshold()
                    : 0.0;
                Double tolerance = kpiConfigurationDTO.getTolerance() != null ? kpiConfigurationDTO.getTolerance() : 0.0;

                instanceDTOS.forEach(instanceDTO -> {
                    try {
                        LOGGER.info(
                            "Starting elaboration of instance {} for partner {} - {} with analysis period {} - {}",
                            instanceDTO.getInstanceIdentification(),
                            instanceDTO.getPartnerFiscalCode(),
                            instanceDTO.getPartnerName(),
                            instanceDTO.getAnalysisPeriodStartDate(),
                            instanceDTO.getAnalysisPeriodEndDate()
                        );

                        instanceService.updateInstanceStatusInProgress(instanceDTO.getId());

                        InstanceModuleDTO instanceModuleDTO = instanceModuleService
                            .findOne(instanceDTO.getId(), kpiConfigurationDTO.getModuleId())
                            .orElseThrow(() -> new NullPointerException("KPI B9 InstanceModule not found"));

                        LOGGER.debug("Starting deletion phase for any previous processing in error state");

                        // Clean only current analysis drilldown data, preserving historical snapshots
                        int drilldownDataDeleted = drilldownService.deleteByInstanceModuleIdAndAnalysisDate(
                            instanceModuleDTO.getId(), LocalDate.now());
                        LOGGER.info("{} drilldown records deleted for current analysis date", drilldownDataDeleted);

                        int kpiB9AnalyticRecordsDataDeleted = kpiB9AnalyticDataService.deleteAllByInstanceModule(instanceModuleDTO.getId());
                        LOGGER.info("{} kpiB9AnalyticData records deleted", kpiB9AnalyticRecordsDataDeleted);

                        int kpiB9DetailResultDeleted = kpiB9DetailResultService.deleteAllByInstanceModule(instanceModuleDTO.getId());
                        LOGGER.info("{} kpiB9DetailResult records deleted", kpiB9DetailResultDeleted);

                        int kpiB9ResultDeleted = kpiB9ResultService.deleteAllByInstanceModule(instanceModuleDTO.getId());
                        LOGGER.info("{} kpiB9ResultDeleted records deleted", kpiB9ResultDeleted);

                        List<String> stations = pagoPaPaymentReceiptService.findAllStationIntoPeriodForPartner(
                            instanceDTO.getPartnerFiscalCode(),
                            instanceDTO.getAnalysisPeriodStartDate(),
                            instanceDTO.getAnalysisPeriodEndDate()
                        );

                        AtomicReference<KpiB9ResultDTO> kpiB9ResultRef = new AtomicReference<>();

                        KpiB9ResultDTO kpiB9ResultDTO = new KpiB9ResultDTO();
                        kpiB9ResultDTO.setInstanceId(instanceDTO.getId());
                        kpiB9ResultDTO.setInstanceModuleId(instanceModuleDTO.getId());
                        kpiB9ResultDTO.setAnalysisDate(LocalDate.now());
                        kpiB9ResultDTO.setExcludePlannedShutdown(
                            BooleanUtils.toBooleanDefaultIfNull(kpiConfigurationDTO.getExcludePlannedShutdown(), false)
                        );
                        kpiB9ResultDTO.setExcludeUnplannedShutdown(
                            BooleanUtils.toBooleanDefaultIfNull(kpiConfigurationDTO.getExcludeUnplannedShutdown(), false)
                        );
                        kpiB9ResultDTO.setEligibilityThreshold(eligibilityThreshold);
                        kpiB9ResultDTO.setTolerance(tolerance);
                        kpiB9ResultDTO.setEvaluationType(kpiConfigurationDTO.getEvaluationType());
                        kpiB9ResultDTO.setOutcome(!stations.isEmpty() ? OutcomeStatus.STANDBY : OutcomeStatus.OK);

                        kpiB9ResultRef.set(kpiB9ResultService.save(kpiB9ResultDTO));

                        AtomicReference<OutcomeStatus> kpiB9ResultFinalOutcome = new AtomicReference<>(OutcomeStatus.OK);

                        if (stations.isEmpty()) {
                            LOGGER.info("No stations found for analysis");
                        } else {
                            // Batch collection for optimized drilldown processing
                            final List<PagoPaPaymentReceiptDrilldown> drilldownBatch = new ArrayList<>();
                            final LocalDate analysisDate = LocalDate.now();
                            
                            // Map to aggregate data by month and year: Map<YearMonth, [totRes, totResKo]>
                            Map<YearMonth, long[]> monthlyAggregatedData = new HashMap<>();
                            Map<YearMonth, List<KpiB9AnalyticDataDTO>> monthlyAnalyticData = new HashMap<>();
                            
                            // Aggregate all data from all stations by month
                            stations.forEach(station -> {
                                LOGGER.debug("Processing station: {}", station);

                                long idStation = anagStationService.findIdByNameOrCreate(station, instanceDTO.getPartnerId());

                                // Collect all maintenance periods for the station
                                List<AnagPlannedShutdownDTO> maintenance = new ArrayList<>();
                                if (BooleanUtils.toBooleanDefaultIfNull(kpiConfigurationDTO.getExcludePlannedShutdown(), false)) {
                                    maintenance.addAll(
                                        anagPlannedShutdownService.findAllByTypePlannedIntoPeriod(
                                            instanceDTO.getPartnerId(),
                                            idStation,
                                            TypePlanned.PROGRAMMATO,
                                            instanceDTO.getAnalysisPeriodStartDate(),
                                            instanceDTO.getAnalysisPeriodEndDate()
                                        )
                                    );
                                }

                                if (BooleanUtils.toBooleanDefaultIfNull(kpiConfigurationDTO.getExcludeUnplannedShutdown(), false)) {
                                    maintenance.addAll(
                                        anagPlannedShutdownService.findAllByTypePlannedIntoPeriod(
                                            instanceDTO.getPartnerId(),
                                            idStation,
                                            TypePlanned.NON_PROGRAMMATO,
                                            instanceDTO.getAnalysisPeriodStartDate(),
                                            instanceDTO.getAnalysisPeriodEndDate()
                                        )
                                    );
                                }

                                // Process each day for this station
                                instanceDTO
                                    .getAnalysisPeriodStartDate()
                                    .datesUntil(instanceDTO.getAnalysisPeriodEndDate().plusDays(1))
                                    .forEach(date -> {
                                        YearMonth yearMonth = YearMonth.from(date);

                                        List<PagoPaPaymentReceiptDTO> pagoPaPaymentReceiptDTOS =
                                            pagoPaPaymentReceiptService.findAllRecordIntoDayForPartnerAndStation(
                                                instanceDTO.getPartnerFiscalCode(),
                                                station,
                                                date
                                            );

                                        long sumTotResDaily = 0;
                                        long sumResOkDaily = 0;
                                        long sumRealResKoDaily = 0;
                                        long sumValidResKoDaily = 0;
                                        
                                        // Track non-excluded records for drilldown (following KpiA1Job logic)
                                        List<PagoPaPaymentReceiptDTO> validPagoPaPaymentReceiptDTOS = new ArrayList<>();

                                        for (PagoPaPaymentReceiptDTO pagoPaPaymentReceiptDTO : pagoPaPaymentReceiptDTOS) {
                                            // Always count all values for statistical purposes (following KpiA1Job logic)
                                            sumTotResDaily += pagoPaPaymentReceiptDTO.getTotRes();
                                            sumResOkDaily += pagoPaPaymentReceiptDTO.getResOk();
                                            sumRealResKoDaily += pagoPaPaymentReceiptDTO.getResKo();

                                            boolean exclude = maintenance
                                                .stream()
                                                .map(anagPlannedShutdownDTO -> {
                                                    Boolean excludePlanned =
                                                        isInstantInRangeInclusive(
                                                            pagoPaPaymentReceiptDTO.getStartDate(),
                                                            anagPlannedShutdownDTO.getShutdownStartDate(),
                                                            anagPlannedShutdownDTO.getShutdownEndDate()
                                                        ) &&
                                                        isInstantInRangeInclusive(
                                                            pagoPaPaymentReceiptDTO.getEndDate(),
                                                            anagPlannedShutdownDTO.getShutdownStartDate(),
                                                            anagPlannedShutdownDTO.getShutdownEndDate()
                                                        );
                                                    return excludePlanned;
                                                })
                                                .anyMatch(Boolean::booleanValue);
                                            
                                            // Only count ResKo for KPI calculation if not in maintenance period
                                            if (!exclude) {
                                                validPagoPaPaymentReceiptDTOS.add(pagoPaPaymentReceiptDTO);
                                                sumValidResKoDaily += pagoPaPaymentReceiptDTO.getResKo();
                                            }
                                        }

                                        // Aggregate data for the corresponding month
                                        monthlyAggregatedData.computeIfAbsent(yearMonth, k -> new long[]{0L, 0L});
                                        monthlyAggregatedData.get(yearMonth)[0] += sumTotResDaily; // totRes
                                        monthlyAggregatedData.get(yearMonth)[1] += sumValidResKoDaily; // totResKo

                                        // Create analytic data for this station/day
                                        KpiB9AnalyticDataDTO kpiB9AnalyticDataDTO = new KpiB9AnalyticDataDTO();
                                        kpiB9AnalyticDataDTO.setInstanceId(instanceDTO.getId());
                                        kpiB9AnalyticDataDTO.setInstanceModuleId(instanceModuleDTO.getId());
                                        kpiB9AnalyticDataDTO.setAnalysisDate(LocalDate.now());
                                        kpiB9AnalyticDataDTO.setStationId(idStation);
                                        kpiB9AnalyticDataDTO.setEvaluationDate(date);
                                        kpiB9AnalyticDataDTO.setTotRes(sumTotResDaily);
                                        kpiB9AnalyticDataDTO.setResOk(sumResOkDaily);
                                        kpiB9AnalyticDataDTO.setResKoReal(sumRealResKoDaily);
                                        kpiB9AnalyticDataDTO.setResKoValid(sumValidResKoDaily);

                                        // Group analytic data by month
                                        monthlyAnalyticData.computeIfAbsent(yearMonth, k -> new ArrayList<>());
                                        monthlyAnalyticData.get(yearMonth).add(kpiB9AnalyticDataDTO);

                                        // Add quarter-hour drilldown data to batch if there are valid (non-excluded) transactions
                                        if (!validPagoPaPaymentReceiptDTOS.isEmpty()) {
                                            try {
                                                // Use optimized method that doesn't reload entities
                                                Instance instanceEntity = instanceModuleService.findById(instanceModuleDTO.getId()).orElseThrow().getInstance();
                                                InstanceModule instanceModuleEntity = instanceModuleService.findById(instanceModuleDTO.getId()).orElseThrow();
                                                AnagStation stationEntity = anagStationService.findOneByName(station).orElse(null);
                                                
                                                if (stationEntity != null) {
                                                    drilldownService.addToBatch(
                                                        drilldownBatch,
                                                        instanceEntity,
                                                        instanceModuleEntity,
                                                        stationEntity,
                                                        date,
                                                        analysisDate,
                                                        validPagoPaPaymentReceiptDTOS
                                                    );
                                                    LOGGER.debug("Added drilldown data to batch for station {} on date {}", station, date);
                                                }
                                            } catch (Exception e) {
                                                LOGGER.error("Error adding drilldown data to batch for station {} on date {}: {}", 
                                                           station, date, e.getMessage());
                                            }
                                        }
                                    });
                            });

                            LOGGER.debug("Processing {} months with aggregated data", monthlyAggregatedData.size());

                            // Create monthly aggregated results
                            List<KpiB9DetailResultDTO> monthlyResults = new ArrayList<>();
                            long totalResPeriod = 0;
                            long totalResKoPeriod = 0;

                            for (Map.Entry<YearMonth, long[]> entry : monthlyAggregatedData.entrySet()) {
                                YearMonth yearMonth = entry.getKey();
                                long[] data = entry.getValue();
                                long totResMonth = data[0];
                                long totResKoMonth = data[1];

                                totalResPeriod += totResMonth;
                                totalResKoPeriod += totResKoMonth;

                                LocalDate firstDayOfMonth = yearMonth.atDay(1);
                                LocalDate lastDayOfMonth = yearMonth.atEndOfMonth();
                                
                                // Ensure dates are within the analysis period range
                                if (firstDayOfMonth.isBefore(instanceDTO.getAnalysisPeriodStartDate())) {
                                    firstDayOfMonth = instanceDTO.getAnalysisPeriodStartDate();
                                }
                                if (lastDayOfMonth.isAfter(instanceDTO.getAnalysisPeriodEndDate())) {
                                    lastDayOfMonth = instanceDTO.getAnalysisPeriodEndDate();
                                }

                                double percResKoMonth = totResMonth > 0 
                                    ? (double) (totResKoMonth * 100) / totResMonth
                                    : 0.0;

                                KpiB9DetailResultDTO kpiB9DetailResultDTO = new KpiB9DetailResultDTO();
                                kpiB9DetailResultDTO.setInstanceId(instanceDTO.getId());
                                kpiB9DetailResultDTO.setInstanceModuleId(instanceModuleDTO.getId());
                                kpiB9DetailResultDTO.setAnalysisDate(LocalDate.now());
                                kpiB9DetailResultDTO.setEvaluationType(EvaluationType.MESE);
                                kpiB9DetailResultDTO.setEvaluationStartDate(firstDayOfMonth);
                                kpiB9DetailResultDTO.setEvaluationEndDate(lastDayOfMonth);
                                kpiB9DetailResultDTO.setTotRes(totResMonth);
                                kpiB9DetailResultDTO.setResKo(totResKoMonth);
                                kpiB9DetailResultDTO.setResKoPercentage(roundToNDecimalPlaces(percResKoMonth));
                                kpiB9DetailResultDTO.setKpiB9ResultId(kpiB9ResultRef.get().getId());

                                OutcomeStatus outcomeStatus = OutcomeStatus.OK;
                                if (percResKoMonth > (eligibilityThreshold + tolerance)) {
                                    outcomeStatus = OutcomeStatus.KO;
                                }

                                if (kpiConfigurationDTO.getEvaluationType().compareTo(EvaluationType.MESE) == 0 &&
                                    outcomeStatus.compareTo(OutcomeStatus.KO) == 0) {
                                    kpiB9ResultFinalOutcome.set(OutcomeStatus.KO);
                                }

                                kpiB9DetailResultDTO.setOutcome(outcomeStatus);
                                KpiB9DetailResultDTO savedMonthlyResult = kpiB9DetailResultService.save(kpiB9DetailResultDTO);
                                monthlyResults.add(savedMonthlyResult);

                                // Associate this month's analytic data with the monthly result
                                List<KpiB9AnalyticDataDTO> monthAnalyticData = monthlyAnalyticData.get(yearMonth);
                                if (monthAnalyticData != null) {
                                    monthAnalyticData.forEach(analyticData -> {
                                        analyticData.setKpiB9DetailResultId(savedMonthlyResult.getId());
                                    });
                                    kpiB9AnalyticDataService.saveAll(monthAnalyticData);
                                    LOGGER.debug("Saved {} analytic data records for month {}", monthAnalyticData.size(), yearMonth);
                                }
                            }

                            // Create aggregated result for the entire period
                            double percResKoPeriod = totalResPeriod > 0 
                                ? (double) (totalResKoPeriod * 100) / totalResPeriod
                                : 0.0;

                            KpiB9DetailResultDTO kpiB9DetailResultDTO = new KpiB9DetailResultDTO();
                            kpiB9DetailResultDTO.setInstanceId(instanceDTO.getId());
                            kpiB9DetailResultDTO.setInstanceModuleId(instanceModuleDTO.getId());
                            kpiB9DetailResultDTO.setAnalysisDate(LocalDate.now());
                            kpiB9DetailResultDTO.setEvaluationType(EvaluationType.TOTALE);
                            kpiB9DetailResultDTO.setEvaluationStartDate(instanceDTO.getAnalysisPeriodStartDate());
                            kpiB9DetailResultDTO.setEvaluationEndDate(instanceDTO.getAnalysisPeriodEndDate());
                            kpiB9DetailResultDTO.setTotRes(totalResPeriod);
                            kpiB9DetailResultDTO.setResKo(totalResKoPeriod);
                            kpiB9DetailResultDTO.setResKoPercentage(roundToNDecimalPlaces(percResKoPeriod));
                            kpiB9DetailResultDTO.setKpiB9ResultId(kpiB9ResultRef.get().getId());

                            OutcomeStatus outcomeStatus = OutcomeStatus.OK;
                            if (percResKoPeriod > (eligibilityThreshold + tolerance)) {
                                outcomeStatus = OutcomeStatus.KO;
                            }

                            if (kpiConfigurationDTO.getEvaluationType().compareTo(EvaluationType.TOTALE) == 0 &&
                                outcomeStatus.compareTo(OutcomeStatus.KO) == 0) {
                                kpiB9ResultFinalOutcome.set(OutcomeStatus.KO);
                            }

                            kpiB9DetailResultDTO.setOutcome(outcomeStatus);
                            kpiB9DetailResultService.save(kpiB9DetailResultDTO);

                            LOGGER.info("Final outcome for analysis: {}", kpiB9ResultFinalOutcome.get());
                            kpiB9ResultService.updateKpiB9ResultOutcome(kpiB9ResultRef.get().getId(), kpiB9ResultFinalOutcome.get());
                            
                            // Save all drilldown data in a single batch for optimal performance
                            if (!drilldownBatch.isEmpty()) {
                                try {
                                    int savedCount = drilldownService.saveBatch(drilldownBatch);
                                    LOGGER.info("Successfully saved {} drilldown records in batch", savedCount);
                                } catch (Exception e) {
                                    LOGGER.error("Error saving drilldown batch: {}", e.getMessage(), e);
                                }
                            }
                        }
                        
                        instanceModuleService.updateAutomaticOutcome(instanceModuleDTO.getId(), kpiB9ResultFinalOutcome.get());

                        // Trigger next job in the workflow
                        JobDetail job = scheduler.getJobDetail(JobKey.jobKey(JobConstant.CALCULATE_STATE_INSTANCE_JOB, "DEFAULT"));

                        Trigger trigger = TriggerBuilder.newTrigger()
                            .usingJobData("instanceId", instanceDTO.getId())
                            .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow().withRepeatCount(0))
                            .forJob(job)
                            .build();

                        scheduler.scheduleJob(trigger);
                    } catch (Exception e) {
                        LOGGER.error(
                            "Error during elaboration of instance {} for partner {} - {} with analysis period {} - {}",
                            instanceDTO.getInstanceIdentification(),
                            instanceDTO.getPartnerFiscalCode(),
                            instanceDTO.getPartnerName(),
                            instanceDTO.getAnalysisPeriodStartDate(),
                            instanceDTO.getAnalysisPeriodEndDate(),
                            e
                        );
                    }
                });
            }
        } catch (Exception exception) {
            LOGGER.error("Problem during KPI B.9 calculation", exception);
        }

        LOGGER.info("KPI B.9 calculation job completed");
    }

    private static double roundToNDecimalPlaces(double value) {
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(5, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private boolean isInstantInRangeInclusive(Instant instantToCheck, Instant startInstant, Instant endInstant) {
        return (
            (instantToCheck.atZone(ZoneOffset.systemDefault()).isEqual(startInstant.atZone(ZoneOffset.systemDefault())) ||
                instantToCheck.atZone(ZoneOffset.systemDefault()).isAfter(startInstant.atZone(ZoneOffset.systemDefault()))) &&
            (instantToCheck.atZone(ZoneOffset.systemDefault()).isEqual(endInstant.atZone(ZoneOffset.systemDefault())) ||
                instantToCheck.atZone(ZoneOffset.systemDefault()).isBefore(endInstant.atZone(ZoneOffset.systemDefault())))
        );
    }
}
