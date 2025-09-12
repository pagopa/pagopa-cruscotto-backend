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
import java.time.Month;
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

    private final KpiB2DetailResultService kpiB2DetailResultService;

    private final KpiB2ResultService kpiB2ResultService;

    private final Scheduler scheduler;
    
    private List<KpiB2AnalyticDataDTO> aggregateKpiB2AnalyticData(InstanceDTO instanceDTO, InstanceModuleDTO instanceModuleDTO, Map<String, List<String>> stations, double averageTimeLimit, List<AnagPlannedShutdownDTO> maintenance, AtomicReference<KpiB2DetailResultDTO> kpiB2DetailResultRef) {
        List<KpiB2AnalyticDataDTO> analyticDataList = new ArrayList<>();
        
        for (String station : stations.keySet()) {
            for (String method : stations.get(station)) {
                long idStation = anagStationService.findIdByNameOrCreate(station, instanceDTO.getPartnerId());
                LocalDate start = instanceDTO.getAnalysisPeriodStartDate();
                LocalDate end = instanceDTO.getAnalysisPeriodEndDate();
                for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
                    List<PagoPaRecordedTimeoutDTO> dailyRecords = pagoPaRecordedTimeoutService.findAllRecordIntoDayForPartnerStationAndMethod(
                        instanceDTO.getPartnerFiscalCode(), station, method, date);
                    long sumTotReqDaily = 0;
                    long sumReqOkDaily = 0;
                    long sumReqTimeoutDaily = 0;
                    double sumWeightsDaily = 0.0;
                    for (PagoPaRecordedTimeoutDTO record : dailyRecords) {
                        sumTotReqDaily += record.getTotReq();
                        sumReqOkDaily += record.getReqOk();
                        sumReqTimeoutDaily += record.getReqTimeout();
                        double avgTime = Double.isNaN(record.getAvgTime()) ? 0.0 : record.getAvgTime();
                        sumWeightsDaily += (record.getTotReq() * avgTime);
                    }
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

    private List<KpiB2DetailResultDTO> aggregateKpiB2DetailResult(InstanceDTO instanceDTO, InstanceModuleDTO instanceModuleDTO, Map<String, List<String>> stations, double averageTimeLimit, double eligibilityThreshold, double tolerance, AtomicReference<KpiB2ResultDTO> kpiB2ResultRef) {
        List<KpiB2DetailResultDTO> detailResults = new ArrayList<>();
        LocalDate analysisStart = instanceDTO.getAnalysisPeriodStartDate();
        LocalDate analysisEnd = instanceDTO.getAnalysisPeriodEndDate();
        LocalDate current = analysisStart.withDayOfMonth(1);
        AtomicReference<OutcomeStatus> kpiB2ResultFinalOutcome = new AtomicReference<>(OutcomeStatus.OK);

        long sumTotReqTotal = 0;
        double sumWeightsTotal = 0.0;
        double sumTotalOverTimeLimit = 0.0;
        while (!current.isAfter(analysisEnd)) {

            LocalDate firstDayOfMonth = current.withDayOfMonth(1);
            LocalDate lastDayOfMonth = current.with(TemporalAdjusters.lastDayOfMonth());
            if (lastDayOfMonth.isAfter(analysisEnd)) {
                lastDayOfMonth = analysisEnd;
            }

            

            long sumTotReqMontly = 0;
            double sumWeightsMontly = 0.0;
            double sumMonthOverTimeLimit = 0.0;
            
            List<PagoPaRecordedTimeoutDTO> periodRecords = pagoPaRecordedTimeoutService.findAllRecordIntoPeriodForPartner(
                    instanceDTO.getPartnerFiscalCode(), firstDayOfMonth, lastDayOfMonth);
            for (PagoPaRecordedTimeoutDTO record : periodRecords) {
                double avgTime = Double.isNaN(record.getAvgTime()) ? 0.0 : record.getAvgTime();
                sumTotReqMontly += record.getTotReq();
                sumWeightsMontly += (record.getTotReq() * avgTime);
                if (avgTime > averageTimeLimit) {
                    sumMonthOverTimeLimit += record.getTotReq();
                }
            }
            sumTotReqTotal += sumTotReqMontly;
            sumWeightsTotal += sumWeightsMontly;
            sumTotalOverTimeLimit += sumMonthOverTimeLimit;
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

    private KpiB2ResultDTO aggregateKpiB2Result(InstanceDTO instanceDTO, InstanceModuleDTO instanceModuleDTO, KpiConfigurationDTO kpiConfigurationDTO, Map<String, List<String>> stations) {
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
        kpiB2ResultDTO.setOutcome(!stations.isEmpty() ? OutcomeStatus.STANDBY : OutcomeStatus.OK);
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
                Map<String, List<String>> stations = pagoPaRecordedTimeoutService.findAllStationAndMethodIntoPeriodForPartner(
                    instanceDTO.getPartnerFiscalCode(),
                    instanceDTO.getAnalysisPeriodStartDate(),
                    instanceDTO.getAnalysisPeriodEndDate()
                );
                // --- Aggregation: KpiB2Result ---
                KpiB2ResultDTO kpiB2ResultDTO = aggregateKpiB2Result(instanceDTO, instanceModuleDTO, kpiConfigurationDTO, stations);
                AtomicReference<KpiB2ResultDTO> kpiB2ResultRef = new AtomicReference<>(kpiB2ResultService.save(kpiB2ResultDTO));
                // --- Aggregation: KpiB2DetailResult ---
                List<KpiB2DetailResultDTO> detailResults = aggregateKpiB2DetailResult(
                    instanceDTO,
                    instanceModuleDTO,
                    stations,
                    kpiB2ResultDTO.getAverageTimeLimit(),
                    kpiB2ResultDTO.getEligibilityThreshold(),
                    kpiB2ResultDTO.getTolerance(),
                    kpiB2ResultRef
                );
                for (KpiB2DetailResultDTO detailResult : detailResults) {
                    AtomicReference<KpiB2DetailResultDTO> kpiB2DetailResultRef = new AtomicReference<>(kpiB2DetailResultService.save(detailResult));

                    // --- Aggregation: KpiB2AnalyticData ---
                    List<AnagPlannedShutdownDTO> maintenance = new ArrayList<>();
                    List<KpiB2AnalyticDataDTO> analyticDataList = aggregateKpiB2AnalyticData(
                            instanceDTO,
                            instanceModuleDTO,
                            stations,
                            kpiB2ResultDTO.getAverageTimeLimit(),
                            maintenance,
                            kpiB2DetailResultRef);
                    kpiB2AnalyticDataService.saveAll(analyticDataList);
                }
                
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

    
}

