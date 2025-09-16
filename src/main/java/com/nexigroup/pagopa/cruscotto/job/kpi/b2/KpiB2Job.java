package com.nexigroup.pagopa.cruscotto.job.kpi.b2;

import com.nexigroup.pagopa.cruscotto.config.ApplicationProperties;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.*;
import com.nexigroup.pagopa.cruscotto.job.config.JobConstant;
import com.nexigroup.pagopa.cruscotto.service.*;
import com.nexigroup.pagopa.cruscotto.service.dto.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
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
public class KpiB2Job extends QuartzJobBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(KpiB2Job.class);

    private final InstanceService instanceService;

    private final InstanceModuleService instanceModuleService;

    private final ApplicationProperties applicationProperties;

    private final PagoPaRecordedTimeoutService pagoPaRecordedTimeoutService;

    private final AnagStationService anagStationService;

    private final KpiConfigurationService kpiConfigurationService;

    private final AnagPlannedShutdownService anagPlannedShutdownService;

    private final KpiB2AnalyticDataService kpiB2AnalyticDataService;

    private final KpiB2AnalyticDrillDownService kpiB2AnalyticDrillDownService;

    private final KpiB2DetailResultService kpiB2DetailResultService;

    private final KpiB2ResultService kpiB2ResultService;

    private final Scheduler scheduler;

    private List<KpiB2AnalyticDrillDownDTO> aggregateKpiB2AnalyticDataDrillDown(
     AtomicReference<KpiB2AnalyticDataDTO> kpiB2AnalyticDataRef,
     List<PagoPaRecordedTimeoutDTO> filteredPeriodRecords,
     LocalDate detailResultEvaluationStartDate,
     LocalDate detailResultEvaluationEndDate) {
     
       return filteredPeriodRecords.stream().map(record -> {
                                        KpiB2AnalyticDrillDownDTO dto = new KpiB2AnalyticDrillDownDTO();
                                        dto.setKpiB2AnalyticDataId(kpiB2AnalyticDataRef.get().getId());
                                        dto.setAverageTimeMs(record.getAvgTime());
                                        dto.setTotalRequests(record.getTotReq());
                                        dto.setOkRequests(record.getReqOk());
                                        dto.setFromHour(record.getStartDate());
                                        dto.setEndHour(record.getEndDate());
                                        return dto;
                                    })
                                    .collect(java.util.stream.Collectors.toList());
    
    
    
    }
    
    private List<KpiB2AnalyticDataDTO> aggregateKpiB2AnalyticData(InstanceDTO instanceDTO,
     InstanceModuleDTO instanceModuleDTO,
     double averageTimeLimit,
     AtomicReference<KpiB2DetailResultDTO> kpiB2DetailResultRef,
     List<PagoPaRecordedTimeoutDTO> filteredPeriodRecords,
     LocalDate detailResultEvaluationStartDate,
     LocalDate detailResultEvaluationEndDate) {

        List<KpiB2AnalyticDataDTO> analyticDataList = new ArrayList<>();

        // Group by station and method
        Map<String, Map<String, List<PagoPaRecordedTimeoutDTO>>> grouped = filteredPeriodRecords.stream()
            .collect(java.util.stream.Collectors.groupingBy(
                PagoPaRecordedTimeoutDTO::getStation,
                java.util.stream.Collectors.groupingBy(PagoPaRecordedTimeoutDTO::getMethod)
            ));

        for (Map.Entry<String, Map<String, List<PagoPaRecordedTimeoutDTO>>> stationEntry : grouped.entrySet()) {
            String station = stationEntry.getKey();
            long idStation = anagStationService.findIdByNameOrCreate(station, instanceDTO.getPartnerId());
            Map<String, List<PagoPaRecordedTimeoutDTO>> methodMap = stationEntry.getValue();
            for (Map.Entry<String, List<PagoPaRecordedTimeoutDTO>> methodEntry : methodMap.entrySet()) {
                String method = methodEntry.getKey();
                List<PagoPaRecordedTimeoutDTO> records = methodEntry.getValue();
                // For each day in the period
                for (LocalDate date = detailResultEvaluationStartDate; !date.isAfter(detailResultEvaluationEndDate); date = date.plusDays(1)) {
                    final LocalDate currentDate = date;
                    List<PagoPaRecordedTimeoutDTO> dailyRecords = records.stream()
                        .filter(r -> r.getStartDate().atZone(ZoneOffset.systemDefault()).toLocalDate().isEqual(currentDate))
                        .collect(java.util.stream.Collectors.toList());
                    long sumTotReqDaily = dailyRecords.stream().mapToLong(PagoPaRecordedTimeoutDTO::getTotReq).sum();
                    long sumReqOkDaily = dailyRecords.stream().mapToLong(PagoPaRecordedTimeoutDTO::getReqOk).sum();
                    long sumReqTimeoutDaily = dailyRecords.stream().mapToLong(PagoPaRecordedTimeoutDTO::getReqTimeout).sum();
                    double sumWeightsDaily = dailyRecords.stream().mapToDouble(r -> r.getTotReq() * (Double.isNaN(r.getAvgTime()) ? 0.0 : r.getAvgTime())).sum();
                    double weightedAverageDaily = sumTotReqDaily > 0 ? sumWeightsDaily / sumTotReqDaily : 0.0;
                    KpiB2AnalyticDataDTO analyticData = new KpiB2AnalyticDataDTO();
                    analyticData.setInstanceId(instanceDTO.getId());
                    analyticData.setInstanceModuleId(instanceModuleDTO.getId());
                    analyticData.setAnalysisDate(LocalDate.now());
                    analyticData.setStationId(idStation);
                    analyticData.setMethod(method);
                    analyticData.setEvaluationDate(date);
                    analyticData.setTotReq(sumTotReqDaily);
                    analyticData.setReqOk(sumReqOkDaily);
                    analyticData.setReqTimeout(sumReqTimeoutDaily);
                    analyticData.setAvgTime(roundToNDecimalPlaces(weightedAverageDaily));
                    analyticData.setKpiB2DetailResultId(kpiB2DetailResultRef.get().getId());
                    analyticDataList.add(analyticData);
                }
            }
        }
        return analyticDataList;
    }

    private List<KpiB2DetailResultDTO> aggregateKpiB2DetailResult(InstanceDTO instanceDTO,
     InstanceModuleDTO instanceModuleDTO,
       double averageTimeLimit,
        double eligibilityThreshold,
         double tolerance,
          AtomicReference<KpiB2ResultDTO> kpiB2ResultRef,
        List<PagoPaRecordedTimeoutDTO> filteredPeriodRecords) {
        List<KpiB2DetailResultDTO> detailResults = new ArrayList<>();
        LocalDate analysisStart = instanceDTO.getAnalysisPeriodStartDate();
        LocalDate analysisEnd = instanceDTO.getAnalysisPeriodEndDate();
        LocalDate current = analysisStart.withDayOfMonth(1);
        AtomicReference<OutcomeStatus> kpiB2ResultFinalOutcome = new AtomicReference<>(OutcomeStatus.OK);
        AtomicReference<Long> totRecordMonth = new AtomicReference<>();

        long totRecordInstance = filteredPeriodRecords.stream()
                .mapToLong(PagoPaRecordedTimeoutDTO::getTotReq)
                .sum();

        long sumTotReqTotal = 0;
        double sumWeightsTotal = 0.0;
        double sumTotalOverTimeLimit = 0.0;
        
        while (!current.isAfter(analysisEnd)) {

            LocalDate firstDayOfMonth = current.withDayOfMonth(1).isBefore(analysisStart) ? analysisEnd :current.withDayOfMonth(1);
            LocalDate lastDayOfMonth = current.with(TemporalAdjusters.lastDayOfMonth()).isAfter(analysisEnd) ? analysisStart : current.with(TemporalAdjusters.lastDayOfMonth());


            long sumTotReqMontly = 0;
            double sumWeightsMontly = 0.0;
            double sumMonthOverTimeLimit = 0.0;
            
            List<PagoPaRecordedTimeoutDTO> monthPeriodRecords = filteredPeriodRecords.stream()
                .filter(record -> {
                    final LocalDate recordDate = record.getStartDate().atZone(ZoneOffset.systemDefault()).toLocalDate();
                    return !recordDate.isBefore(firstDayOfMonth) && !recordDate.isAfter(lastDayOfMonth);
                })
                .collect(java.util.stream.Collectors.toList());
            
            totRecordMonth.set(monthPeriodRecords.stream()
                .mapToLong(PagoPaRecordedTimeoutDTO::getTotReq)
                .sum());
            for (PagoPaRecordedTimeoutDTO record : monthPeriodRecords) {
                double avgTime = Double.isNaN(record.getAvgTime()) ? 0.0 : record.getAvgTime();
                sumTotReqMontly += record.getTotReq();
                sumWeightsMontly += (record.getTotReq() * avgTime);

                double monthWeight = (double) (record.getTotReq() * 100) / totRecordMonth.get();
                double totalWeight = (double) (record.getTotReq() * 100) / totRecordInstance;
                if (avgTime > averageTimeLimit) {
                    
                    sumMonthOverTimeLimit += monthWeight;
                    sumTotalOverTimeLimit += totalWeight;
                    
                }
            }
            sumTotReqTotal += sumTotReqMontly;
            sumWeightsTotal += sumWeightsMontly;
            
            double weightedAverageMontly = sumTotReqMontly > 0 ? sumWeightsMontly / sumTotReqMontly : 0.0;
            KpiB2DetailResultDTO detailResult = new KpiB2DetailResultDTO();
            detailResult.setInstanceId(instanceDTO.getId());
            detailResult.setInstanceModuleId(instanceModuleDTO.getId());
            detailResult.setAnalysisDate(LocalDate.now());
            detailResult.setEvaluationType(EvaluationType.MESE);
            detailResult.setEvaluationStartDate(firstDayOfMonth);
            detailResult.setEvaluationEndDate(lastDayOfMonth);
            detailResult.setTotReq(sumTotReqMontly);
            detailResult.setAvgTime(roundToNDecimalPlaces(weightedAverageMontly));
            detailResult.setOverTimeLimit(roundToNDecimalPlaces(sumMonthOverTimeLimit));
            detailResult.setKpiB2ResultId(kpiB2ResultRef.get().getId());
            OutcomeStatus outcomeStatus = OutcomeStatus.OK;
            if (sumMonthOverTimeLimit > (eligibilityThreshold + tolerance)) {
                outcomeStatus = OutcomeStatus.KO;
            }
            if (
                outcomeStatus.compareTo(OutcomeStatus.KO) == 0
            ) {
                kpiB2ResultFinalOutcome.set(OutcomeStatus.KO);
            }
            detailResult.setOutcome(outcomeStatus);
            detailResults.add(detailResult);

            
            current = current.plusMonths(1);
        }

        // Add TOTALE detail result for the whole analysis period
            double weightedAverageTotal = sumTotReqTotal > 0 ? sumWeightsTotal / sumTotReqTotal : 0.0;
            KpiB2DetailResultDTO totalDetailResult = new KpiB2DetailResultDTO();
            totalDetailResult.setInstanceId(instanceDTO.getId());
            totalDetailResult.setInstanceModuleId(instanceModuleDTO.getId());
            totalDetailResult.setAnalysisDate(LocalDate.now());
            totalDetailResult.setEvaluationType(EvaluationType.TOTALE);
            totalDetailResult.setEvaluationStartDate(analysisStart);
            totalDetailResult.setEvaluationEndDate(analysisEnd);
            totalDetailResult.setTotReq(sumTotReqTotal);
            totalDetailResult.setAvgTime(roundToNDecimalPlaces(weightedAverageTotal));
            totalDetailResult.setOverTimeLimit(roundToNDecimalPlaces(sumTotalOverTimeLimit));
            totalDetailResult.setKpiB2ResultId(kpiB2ResultRef.get().getId());
            OutcomeStatus totalOutcomeStatus = OutcomeStatus.OK;
            if (sumTotalOverTimeLimit > (eligibilityThreshold + tolerance)) {
                totalOutcomeStatus = OutcomeStatus.KO;
            }
            totalDetailResult.setOutcome(totalOutcomeStatus);
            detailResults.add(totalDetailResult);
            
        return detailResults;
    }

    private KpiB2ResultDTO aggregateKpiB2Result(InstanceDTO instanceDTO, InstanceModuleDTO instanceModuleDTO, KpiConfigurationDTO kpiConfigurationDTO, List<PagoPaRecordedTimeoutDTO> records) {
        KpiB2ResultDTO kpiB2ResultDTO = new KpiB2ResultDTO();
        kpiB2ResultDTO.setInstanceId(instanceDTO.getId());
        kpiB2ResultDTO.setInstanceModuleId(instanceModuleDTO.getId());
        kpiB2ResultDTO.setAnalysisDate(LocalDate.now());
        kpiB2ResultDTO.setExcludePlannedShutdown(BooleanUtils.toBooleanDefaultIfNull(kpiConfigurationDTO.getExcludePlannedShutdown(), false));
        kpiB2ResultDTO.setExcludeUnplannedShutdown(BooleanUtils.toBooleanDefaultIfNull(kpiConfigurationDTO.getExcludeUnplannedShutdown(), false));
        kpiB2ResultDTO.setEligibilityThreshold(kpiConfigurationDTO.getEligibilityThreshold() != null ? kpiConfigurationDTO.getEligibilityThreshold() : 0.0);
        kpiB2ResultDTO.setTolerance(kpiConfigurationDTO.getTolerance() != null ? kpiConfigurationDTO.getTolerance() : 0.0);
        kpiB2ResultDTO.setAverageTimeLimit(kpiConfigurationDTO.getAverageTimeLimit() != null ? kpiConfigurationDTO.getAverageTimeLimit() : 0.0);
        kpiB2ResultDTO.setEvaluationType(kpiConfigurationDTO.getEvaluationType());
        kpiB2ResultDTO.setOutcome(!records.isEmpty() ? OutcomeStatus.STANDBY : OutcomeStatus.OK);
        return kpiB2ResultDTO;
    }

    @Override
    protected void executeInternal(@NotNull JobExecutionContext context) {
        LOGGER.info("Start calculate kpi B.2");
        if (!applicationProperties.getJob().getKpiB2Job().isEnabled()) {
            LOGGER.info("Job calculate kpi B.2 disabled. Exit...");
            return;
        }
        List<InstanceDTO> instanceDTOS = instanceService.findInstanceToCalculate(
            ModuleCode.B2,
            applicationProperties.getJob().getKpiB2Job().getLimit()
        );
        if (instanceDTOS.isEmpty()) {
            LOGGER.info("No instance to calculate B.2. Exit....");
            return;
        }
        KpiConfigurationDTO kpiConfigurationDTO = kpiConfigurationService
            .findKpiConfigurationByCode(ModuleCode.B2.code)
            .orElseThrow(() -> new NullPointerException("KPI B.2 Configuration not found"));
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
                    .orElseThrow(() -> new NullPointerException("KPI B.2 InstanceModule not found"));
                // instanceModuleId is now handled locally in aggregation methods
                LOGGER.info("Deletion phase for any previous processing in error");
                kpiB2AnalyticDataService.deleteAllByInstanceModule(instanceModuleDTO.getId());
                kpiB2DetailResultService.deleteAllByInstanceModule(instanceModuleDTO.getId());
                kpiB2ResultService.deleteAllByInstanceModule(instanceModuleDTO.getId());
                
                List<AnagPlannedShutdownDTO> maintenance = new ArrayList<>();
                if (BooleanUtils.toBooleanDefaultIfNull(kpiConfigurationDTO.getExcludePlannedShutdown(), false)) {
                                maintenance.addAll(
                                    anagPlannedShutdownService.findAllByTypePlannedIntoPeriod(
                                        instanceDTO.getPartnerId(),
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
                                        TypePlanned.NON_PROGRAMMATO,
                                        instanceDTO.getAnalysisPeriodStartDate(),
                                        instanceDTO.getAnalysisPeriodEndDate()
                                    )
                                );
                            }

                LOGGER.info("Found {} rows of maintenance", maintenance.size());

                // Fetch all records for the analysis period
                List<PagoPaRecordedTimeoutDTO> periodRecords = pagoPaRecordedTimeoutService
                        .findAllRecordIntoPeriodForPartner(
                                instanceDTO.getPartnerFiscalCode(),
                                instanceDTO.getAnalysisPeriodStartDate(),
                                instanceDTO.getAnalysisPeriodEndDate());

                // --- Aggregation: KpiB2Result ---
                KpiB2ResultDTO kpiB2ResultDTO = aggregateKpiB2Result(instanceDTO, instanceModuleDTO, kpiConfigurationDTO, periodRecords);
                AtomicReference<KpiB2ResultDTO> kpiB2ResultRef = new AtomicReference<>(kpiB2ResultService.save(kpiB2ResultDTO));

                // Filter out records that should be excluded due to maintenance
                List<PagoPaRecordedTimeoutDTO> filteredPeriodRecords = new ArrayList<>();
                for (PagoPaRecordedTimeoutDTO record : periodRecords) {
                    boolean exclude = maintenance
                            .stream()
                            .filter(maintenanceRecord -> maintenanceRecord.getStationName().equals(record.getStation()))
                            .map(anagPlannedShutdownDTO -> isInstantInRangeInclusive(
                                    record.getStartDate(),
                                    anagPlannedShutdownDTO.getShutdownStartDate(),
                                    anagPlannedShutdownDTO.getShutdownEndDate()) &&
                                    isInstantInRangeInclusive(
                                            record.getEndDate(),
                                            anagPlannedShutdownDTO.getShutdownStartDate(),
                                            anagPlannedShutdownDTO.getShutdownEndDate())

                            )
                            .anyMatch(Boolean::booleanValue);
                    if (!exclude) {
                        filteredPeriodRecords.add(record);
                    }
                }

                // --- Aggregation: KpiB2DetailResult ---
                List<KpiB2DetailResultDTO> detailResults = aggregateKpiB2DetailResult(
                        instanceDTO,
                        instanceModuleDTO,
                        kpiB2ResultDTO.getAverageTimeLimit(),
                        kpiB2ResultDTO.getEligibilityThreshold(),
                        kpiB2ResultDTO.getTolerance(),
                        kpiB2ResultRef,
                        filteredPeriodRecords);
                for (KpiB2DetailResultDTO detailResult : detailResults) {
                    AtomicReference<KpiB2DetailResultDTO> kpiB2DetailResultRef = new AtomicReference<>(kpiB2DetailResultService.save(detailResult));

                    if (detailResult.getEvaluationType() == EvaluationType.MESE) {


                    // --- Aggregation: KpiB2AnalyticData ---
                    List<KpiB2AnalyticDataDTO> analyticDataList = aggregateKpiB2AnalyticData(
                            instanceDTO,
                            instanceModuleDTO,
                            kpiB2ResultDTO.getAverageTimeLimit(),
                            kpiB2DetailResultRef,
                            filteredPeriodRecords.stream()
                                    .filter(record -> {
                                        LocalDate recordDate = record.getStartDate().atZone(ZoneOffset.systemDefault())
                                                .toLocalDate();
                                        return !recordDate.isBefore(detailResult.getEvaluationStartDate())
                                                && !recordDate.isAfter(detailResult.getEvaluationEndDate());
                                    })
                                    .collect(java.util.stream.Collectors.toList()),
                            detailResult.getEvaluationStartDate(),
                            detailResult.getEvaluationEndDate());

                    
                    for (KpiB2AnalyticDataDTO analyticData : analyticDataList) {
                         AtomicReference<KpiB2AnalyticDataDTO> kpiB2AnalyticDataRef = new AtomicReference<>(kpiB2AnalyticDataService.save(analyticData));      
                         
                         // --- Aggregation: DrillDown ---
                        List<KpiB2AnalyticDrillDownDTO> drillDowns = aggregateKpiB2AnalyticDataDrillDown(
                            kpiB2AnalyticDataRef,
                            filteredPeriodRecords.stream()
                                    .filter(record -> record.getStartDate().atZone(ZoneOffset.systemDefault())
                                                .toLocalDate().isEqual(analyticData.getEvaluationDate())
                                    )
                                    .collect(java.util.stream.Collectors.toList()),
                            detailResult.getEvaluationStartDate(),
                            detailResult.getEvaluationEndDate());

                        kpiB2AnalyticDrillDownService.saveAll(drillDowns);    
                    }
                }}
                
                // Final outcome update
                kpiB2ResultService.updateKpiB2ResultOutcome(kpiB2ResultRef.get().getId(), OutcomeStatus.OK);
                instanceModuleService.updateAutomaticOutcome(instanceModuleDTO.getId(), OutcomeStatus.OK);
                // Trigger
                JobDetail job = scheduler.getJobDetail(JobKey.jobKey(JobConstant.CALCULATE_STATE_INSTANCE_JOB, "DEFAULT"));
                Trigger trigger = TriggerBuilder.newTrigger()
                    .usingJobData("instanceId", instanceDTO.getId())
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow().withRepeatCount(0))
                    .forJob(job)
                    .build();
                scheduler.scheduleJob(trigger);
            } catch (Exception e) {
                errors.add(
                    String.format(
                        "Error in elaboration instance %s for partner %s - %s with period %s - %s. Exception: %s",
                        instanceDTO.getInstanceIdentification(),
                        instanceDTO.getPartnerFiscalCode(),
                        instanceDTO.getPartnerName(),
                        instanceDTO.getAnalysisPeriodStartDate(),
                        instanceDTO.getAnalysisPeriodEndDate(),
                        e.getMessage()
                    )
                );
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

