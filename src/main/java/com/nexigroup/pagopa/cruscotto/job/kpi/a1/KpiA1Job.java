package com.nexigroup.pagopa.cruscotto.job.kpi.a1;

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
public class KpiA1Job extends QuartzJobBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(KpiA1Job.class);

    private final InstanceService instanceService;

    private final InstanceModuleService instanceModuleService;

    private final ApplicationProperties applicationProperties;

    private final PagoPaRecordedTimeoutService pagoPaRecordedTimeoutService;

    private final AnagStationService anagStationService;

    private final KpiConfigurationService kpiConfigurationService;

    private final AnagPlannedShutdownService anagPlannedShutdownService;

    private final KpiA1AnalyticDataService kpiA1AnalyticDataService;

    private final KpiA1DetailResultService kpiA1DetailResultService;

    private final KpiA1ResultService kpiA1ResultService;

    private final Scheduler scheduler;

    private final ModuleService moduleService;

    @Override
    protected void executeInternal(@NotNull JobExecutionContext context) {
        LOGGER.info("Start calculate kpi A.1");

        try {
            if (!applicationProperties.getJob().getKpiA1Job().isEnabled()) {
                LOGGER.info("Job calculate kpi A.1 disabled. Exit...");
                return;
            }

            List<InstanceDTO> instanceDTOS = instanceService.findInstanceToCalculate(
                ModuleCode.A1,
                applicationProperties.getJob().getKpiA1Job().getLimit()
            );

            if (instanceDTOS.isEmpty()) {
                LOGGER.info("No instance to calculate A.1. Exit....");
            } else {
                KpiConfigurationDTO kpiConfigurationDTO = kpiConfigurationService
                    .findKpiConfigurationByCode(ModuleCode.A1.code)
                    .orElseThrow(() -> new NullPointerException("KPI A.1 Configuration not found"));

                LOGGER.info("Kpi configuration {}", kpiConfigurationDTO);

                ModuleDTO moduleDTO = moduleService
                    .findOne(kpiConfigurationDTO.getModuleId())
                    .orElseThrow(() -> new NullPointerException("Module for KPI A.1 not found"));

                if (moduleDTO.getAnalysisType().compareTo(AnalysisType.MANUALE) == 0) {
                    throw new RuntimeException("Analysis type MANUALE not supported for KPI A.1");
                }

                Double eligibilityThreshold = kpiConfigurationDTO.getEligibilityThreshold() != null
                    ? kpiConfigurationDTO.getEligibilityThreshold()
                    : 0.0;
                Double tolerance = kpiConfigurationDTO.getTolerance() != null ? kpiConfigurationDTO.getTolerance() : 0.0;

                instanceDTOS.forEach(instanceDTO -> {
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
                            .orElseThrow(() -> new NullPointerException("KPI A1 InstanceModule not found"));

                        LOGGER.info("Deletion phase for any previous processing in error");

                        int kpiA1AnalyticRecordsDataDeleted = kpiA1AnalyticDataService.deleteAllByInstanceModule(instanceModuleDTO.getId());
                        LOGGER.info("{} kpiA1AnalyticData records deleted", kpiA1AnalyticRecordsDataDeleted);

                        int kpiA1DetailResultDeleted = kpiA1DetailResultService.deleteAllByInstanceModule(instanceModuleDTO.getId());
                        LOGGER.info("{} kpiA1DetailResult records deleted", kpiA1DetailResultDeleted);

                        int kpiA1ResultDeleted = kpiA1ResultService.deleteAllByInstanceModule(instanceModuleDTO.getId());
                        LOGGER.info("{} kpiA1ResultDeleted records deleted", kpiA1ResultDeleted);

                        Map<String, List<String>> stations = pagoPaRecordedTimeoutService.findAllStationAndMethodIntoPeriodForPartner(
                            instanceDTO.getPartnerFiscalCode(),
                            instanceDTO.getAnalysisPeriodStartDate(),
                            instanceDTO.getAnalysisPeriodEndDate()
                        );

                        AtomicReference<KpiA1ResultDTO> kpiA1ResultRef = new AtomicReference<>();

                        KpiA1ResultDTO kpiA1ResultDTO = new KpiA1ResultDTO();
                        kpiA1ResultDTO.setInstanceId(instanceDTO.getId());
                        kpiA1ResultDTO.setInstanceModuleId(instanceModuleDTO.getId());
                        kpiA1ResultDTO.setAnalysisDate(LocalDate.now());
                        kpiA1ResultDTO.setExcludePlannedShutdown(
                            BooleanUtils.toBooleanDefaultIfNull(kpiConfigurationDTO.getExcludePlannedShutdown(), false)
                        );
                        kpiA1ResultDTO.setExcludeUnplannedShutdown(
                            BooleanUtils.toBooleanDefaultIfNull(kpiConfigurationDTO.getExcludeUnplannedShutdown(), false)
                        );
                        kpiA1ResultDTO.setEligibilityThreshold(eligibilityThreshold);
                        kpiA1ResultDTO.setTolerance(tolerance);
                        kpiA1ResultDTO.setEvaluationType(kpiConfigurationDTO.getEvaluationType());
                        kpiA1ResultDTO.setOutcome(!stations.isEmpty() ? OutcomeStatus.STANDBY : OutcomeStatus.OK);

                        kpiA1ResultRef.set(kpiA1ResultService.save(kpiA1ResultDTO));

                        AtomicReference<OutcomeStatus> kpiA1ResultFinalOutcome = new AtomicReference<>(OutcomeStatus.OK);

                        if (stations.isEmpty()) {
                            LOGGER.info("No stations found");
                        } else {
                            stations.forEach((station, methods) -> {
                                LOGGER.info("Station {}", station);

                                long idStation = anagStationService.findIdByNameOrCreate(station, instanceDTO.getPartnerId());

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

                                LOGGER.info("Found {} rows of maintenance", maintenance.size());

                                methods.forEach(method -> {
                                    LOGGER.info("Method {}", method);

                                    AtomicReference<Month> prevMonth = new AtomicReference<>();
                                    AtomicReference<Long> totReqMonth = new AtomicReference<>(0L);
                                    AtomicReference<Long> totTimeoutReqMonth = new AtomicReference<>(0L);
                                    AtomicReference<Long> totReqPeriod = new AtomicReference<>(0L);
                                    AtomicReference<Long> totTimeoutReqPeriod = new AtomicReference<>(0L);
                                    List<KpiA1AnalyticDataDTO> kpiA1AnalyticDataDTOS = new ArrayList<>();
                                    List<KpiA1DetailResultDTO> kpiA1DetailResultDTOS = new ArrayList<>();
                                    AtomicReference<LocalDate> firstDayOfMonth = new AtomicReference<>();
                                    AtomicReference<LocalDate> lastDayOfMonth = new AtomicReference<>();

                                    instanceDTO
                                        .getAnalysisPeriodStartDate()
                                        .datesUntil(instanceDTO.getAnalysisPeriodEndDate().plusDays(1))
                                        .forEach(date -> {
                                            LOGGER.info("Date {}", date);

                                            Month currentMonth = date.getMonth();

                                            if (prevMonth.get() == null || prevMonth.get().compareTo(currentMonth) != 0) {
                                                if (prevMonth.get() == null) {
                                                    firstDayOfMonth.set(instanceDTO.getAnalysisPeriodStartDate());
                                                } else {
                                                    firstDayOfMonth.set(date.with(TemporalAdjusters.firstDayOfMonth()));
                                                    totReqMonth.set(0L);
                                                    totTimeoutReqMonth.set(0L);
                                                    kpiA1AnalyticDataDTOS.clear();
                                                }

                                                if (currentMonth.compareTo(instanceDTO.getAnalysisPeriodEndDate().getMonth()) == 0) {
                                                    lastDayOfMonth.set(instanceDTO.getAnalysisPeriodEndDate());
                                                } else {
                                                    lastDayOfMonth.set(date.with(TemporalAdjusters.lastDayOfMonth()));
                                                }
                                            }

                                            List<PagoPaRecordedTimeoutDTO> pagoPaRecordedTimeoutDTOS =
                                                pagoPaRecordedTimeoutService.findAllRecordIntoDayForPartnerStationAndMethod(
                                                    instanceDTO.getPartnerFiscalCode(),
                                                    station,
                                                    method,
                                                    date
                                                );

                                            long sumTotReqDaily = 0;
                                            long sumOkReqDaily = 0;
                                            long sumRealTimeoutReqDaily = 0;
                                            long sumValidTimeouReqtDaily = 0;

                                            for (PagoPaRecordedTimeoutDTO pagoPaRecordedTimeoutDTO : pagoPaRecordedTimeoutDTOS) {
                                                LOGGER.debug("PagoPaRecordedTimeoutDTO: {}", pagoPaRecordedTimeoutDTO);

                                                sumTotReqDaily = sumTotReqDaily + pagoPaRecordedTimeoutDTO.getTotReq();
                                                sumOkReqDaily = sumOkReqDaily + pagoPaRecordedTimeoutDTO.getReqOk();
                                                sumRealTimeoutReqDaily = sumRealTimeoutReqDaily + pagoPaRecordedTimeoutDTO.getReqTimeout();

                                                LOGGER.debug("sumTotReqDaily: {}", sumTotReqDaily);

                                                boolean exclude = maintenance
                                                    .stream()
                                                    .map(anagPlannedShutdownDTO -> {
                                                        Boolean excludePlanned =
                                                            isInstantInRangeInclusive(
                                                                pagoPaRecordedTimeoutDTO.getStartDate(),
                                                                anagPlannedShutdownDTO.getShutdownStartDate(),
                                                                anagPlannedShutdownDTO.getShutdownEndDate()
                                                            ) &&
                                                            isInstantInRangeInclusive(
                                                                pagoPaRecordedTimeoutDTO.getEndDate(),
                                                                anagPlannedShutdownDTO.getShutdownStartDate(),
                                                                anagPlannedShutdownDTO.getShutdownEndDate()
                                                            );
                                                        return excludePlanned;
                                                    })
                                                    .anyMatch(Boolean::booleanValue);
                                                if (!exclude) {
                                                    sumValidTimeouReqtDaily =
                                                        sumValidTimeouReqtDaily + pagoPaRecordedTimeoutDTO.getReqTimeout();
                                                }
                                            }

                                            totReqMonth.set(totReqMonth.get() + sumTotReqDaily);
                                            totTimeoutReqMonth.set(totTimeoutReqMonth.get() + sumValidTimeouReqtDaily);

                                            KpiA1AnalyticDataDTO kpiA1AnalyticDataDTO = new KpiA1AnalyticDataDTO();
                                            kpiA1AnalyticDataDTO.setInstanceId(instanceDTO.getId());
                                            kpiA1AnalyticDataDTO.setInstanceModuleId(instanceModuleDTO.getId());
                                            kpiA1AnalyticDataDTO.setAnalysisDate(LocalDate.now());
                                            kpiA1AnalyticDataDTO.setStationId(idStation);
                                            kpiA1AnalyticDataDTO.setMethod(method);
                                            kpiA1AnalyticDataDTO.setEvaluationDate(date);
                                            kpiA1AnalyticDataDTO.setTotReq(sumTotReqDaily);
                                            kpiA1AnalyticDataDTO.setReqOk(sumOkReqDaily);
                                            kpiA1AnalyticDataDTO.setReqTimeoutReal(sumRealTimeoutReqDaily);
                                            kpiA1AnalyticDataDTO.setReqTimeoutValid(sumValidTimeouReqtDaily);

                                            kpiA1AnalyticDataDTOS.add(kpiA1AnalyticDataDTO);

                                            if (date.isEqual(lastDayOfMonth.get())) {
                                                totReqPeriod.set(totReqPeriod.get() + totReqMonth.get());
                                                totTimeoutReqPeriod.set(totTimeoutReqPeriod.get() + totTimeoutReqMonth.get());

                                                Long totReqMonthValue = totReqMonth.get();
                                                double percTimeoutReqMonth = totReqMonthValue.compareTo(0L) > 0
                                                    ? (double) (totTimeoutReqMonth.get() * 100) / totReqMonthValue
                                                    : 0.0;

                                                KpiA1DetailResultDTO kpiA1DetailResultDTO = new KpiA1DetailResultDTO();
                                                kpiA1DetailResultDTO.setInstanceId(instanceDTO.getId());
                                                kpiA1DetailResultDTO.setInstanceModuleId(instanceModuleDTO.getId());
                                                kpiA1DetailResultDTO.setAnalysisDate(LocalDate.now());
                                                kpiA1DetailResultDTO.setStationId(idStation);
                                                kpiA1DetailResultDTO.setMethod(method);
                                                kpiA1DetailResultDTO.setEvaluationType(EvaluationType.MESE);
                                                kpiA1DetailResultDTO.setEvaluationStartDate(firstDayOfMonth.get());
                                                kpiA1DetailResultDTO.setEvaluationEndDate(lastDayOfMonth.get());
                                                kpiA1DetailResultDTO.setTotReq(totReqMonth.get());
                                                kpiA1DetailResultDTO.setReqTimeout(totTimeoutReqMonth.get());
                                                kpiA1DetailResultDTO.setTimeoutPercentage(roundToNDecimalPlaces(percTimeoutReqMonth));
                                                kpiA1DetailResultDTO.setKpiA1ResultId(kpiA1ResultRef.get().getId());

                                                OutcomeStatus outcomeStatus = OutcomeStatus.OK;

                                                if (percTimeoutReqMonth > (eligibilityThreshold + tolerance)) {
                                                    outcomeStatus = OutcomeStatus.KO;
                                                }

                                                if (
                                                    kpiConfigurationDTO.getEvaluationType().compareTo(EvaluationType.MESE) == 0 &&
                                                    outcomeStatus.compareTo(OutcomeStatus.KO) == 0
                                                ) {
                                                    kpiA1ResultFinalOutcome.set(OutcomeStatus.KO);
                                                }

                                                kpiA1DetailResultDTO.setOutcome(outcomeStatus);

                                                kpiA1DetailResultDTO = kpiA1DetailResultService.save(kpiA1DetailResultDTO);

                                                kpiA1DetailResultDTOS.add(kpiA1DetailResultDTO);

                                                KpiA1DetailResultDTO finalKpiA1DetailResultDTO = kpiA1DetailResultDTO;

                                                kpiA1AnalyticDataDTOS.forEach(kpiA1AnalyticData -> {
                                                    kpiA1AnalyticData.setKpiA1DetailResultId(finalKpiA1DetailResultDTO.getId());
                                                });

                                                kpiA1AnalyticDataService.saveAll(kpiA1AnalyticDataDTOS);
                                            }

                                            prevMonth.set(currentMonth);
                                        });

                                    Long totReqPeriodValue = totReqPeriod.get();
                                    double percTimeoutReqPeriod = totReqPeriodValue.compareTo(0L) > 0
                                        ? (double) (totTimeoutReqPeriod.get() * 100) / totReqPeriodValue
                                        : 0.0;

                                    KpiA1DetailResultDTO kpiA1DetailResultDTO = new KpiA1DetailResultDTO();
                                    kpiA1DetailResultDTO.setInstanceId(instanceDTO.getId());
                                    kpiA1DetailResultDTO.setInstanceModuleId(instanceModuleDTO.getId());
                                    kpiA1DetailResultDTO.setAnalysisDate(LocalDate.now());
                                    kpiA1DetailResultDTO.setStationId(idStation);
                                    kpiA1DetailResultDTO.setMethod(method);
                                    kpiA1DetailResultDTO.setEvaluationType(EvaluationType.TOTALE);
                                    kpiA1DetailResultDTO.setEvaluationStartDate(instanceDTO.getAnalysisPeriodStartDate());
                                    kpiA1DetailResultDTO.setEvaluationEndDate(instanceDTO.getAnalysisPeriodEndDate());
                                    kpiA1DetailResultDTO.setTotReq(totReqPeriod.get());
                                    kpiA1DetailResultDTO.setReqTimeout(totTimeoutReqPeriod.get());
                                    kpiA1DetailResultDTO.setTimeoutPercentage(roundToNDecimalPlaces(percTimeoutReqPeriod));
                                    kpiA1DetailResultDTO.setKpiA1ResultId(kpiA1ResultRef.get().getId());

                                    OutcomeStatus outcomeStatus = OutcomeStatus.OK;

                                    if (percTimeoutReqPeriod > (eligibilityThreshold + tolerance)) {
                                        outcomeStatus = OutcomeStatus.KO;
                                    }

                                    if (
                                        kpiConfigurationDTO.getEvaluationType().compareTo(EvaluationType.TOTALE) == 0 &&
                                        outcomeStatus.compareTo(OutcomeStatus.KO) == 0
                                    ) {
                                        kpiA1ResultFinalOutcome.set(OutcomeStatus.KO);
                                    }

                                    kpiA1DetailResultDTO.setOutcome(outcomeStatus);

                                    kpiA1DetailResultService.save(kpiA1DetailResultDTO);
                                });
                            });

                            LOGGER.info("Final outcome {}", kpiA1ResultFinalOutcome.get());
                            kpiA1ResultService.updateKpiA1ResultOutcome(kpiA1ResultRef.get().getId(), kpiA1ResultFinalOutcome.get());
                        }
                        instanceModuleService.updateAutomaticOutcome(instanceModuleDTO.getId(), kpiA1ResultFinalOutcome.get());

                        // Trigger
                        JobDetail job = scheduler.getJobDetail(JobKey.jobKey(JobConstant.CALCULATE_STATE_INSTANCE_JOB, "DEFAULT"));

                        Trigger trigger = TriggerBuilder.newTrigger()
                            .usingJobData("instanceId", instanceDTO.getId())
                            .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow().withRepeatCount(0))
                            .forJob(job)
                            .build();

                        scheduler.scheduleJob(trigger);
                    } catch (Exception e) {
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
                });
            }
        } catch (Exception exception) {
            LOGGER.error("Problem during calculate kpi A.1", exception);
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
