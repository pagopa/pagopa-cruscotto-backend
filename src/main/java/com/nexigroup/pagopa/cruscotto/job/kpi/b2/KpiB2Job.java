package com.nexigroup.pagopa.cruscotto.job.kpi.b2;

import com.nexigroup.pagopa.cruscotto.config.ApplicationProperties;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.TypePlanned;
import com.nexigroup.pagopa.cruscotto.service.*;
import com.nexigroup.pagopa.cruscotto.service.dto.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.jetbrains.annotations.NotNull;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
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

    @Override
    protected void executeInternal(@NotNull JobExecutionContext context) {
        LOGGER.info("Start calculate kpi B.2");

        List<InstanceDTO> instanceDTOS = instanceService.findInstanceToCalculate(
            ModuleCode.B2,
            applicationProperties.getJob().getKpiB2Job().getLimit()
        );

        if (instanceDTOS.isEmpty()) {
            LOGGER.info("No instance to calculate B.2. Exit....");
        } else {
            instanceDTOS.forEach(instanceDTO -> {
                LOGGER.info(
                    "Start elaboration instance {} for pattern {} - {} with period {} - {}",
                    instanceDTO.getInstanceIdentification(),
                    instanceDTO.getPartnerFiscalCode(),
                    instanceDTO.getPartnerName(),
                    instanceDTO.getAnalysisPeriodStartDate(),
                    instanceDTO.getAnalysisPeriodEndDate()
                );

                KpiConfigurationDTO kpiConfigurationDTO = kpiConfigurationService
                    .findKpiConfigurationByCode(ModuleCode.B2)
                    .orElseThrow(() -> new NullPointerException("KPI B.2 not found"));

                InstanceModuleDTO instanceModuleDTO = instanceModuleService
                    .findOne(instanceDTO.getId(), kpiConfigurationDTO.getModuleId())
                    .orElseThrow(() -> new NullPointerException("InstanceModule not found"));

                LOGGER.info("Kpi configuration {}", kpiConfigurationDTO);

                Map<String, List<String>> stations = pagoPaRecordedTimeoutService.findAllStationAndMethodIntoPeriodForPartner(
                    instanceDTO.getPartnerFiscalCode(),
                    instanceDTO.getAnalysisPeriodStartDate(),
                    instanceDTO.getAnalysisPeriodEndDate()
                );

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
                            if (method.compareTo("activatePaymentNotice") == 0) {
                                LOGGER.info("Method {}", method);
                                long totRecordInstance = pagoPaRecordedTimeoutService.sumRecordIntoPeriodForPartnerStationAndMethod(
                                    instanceDTO.getPartnerFiscalCode(),
                                    station,
                                    method,
                                    instanceDTO.getAnalysisPeriodStartDate(),
                                    instanceDTO.getAnalysisPeriodEndDate()
                                );

                                LOGGER.info("Tot Record Instance {}", totRecordInstance);
                                AtomicReference<Month> prevMonth = new AtomicReference<>();
                                AtomicReference<Long> totRecordMonth = new AtomicReference<>();
                                AtomicReference<Double> totMonthWeight = new AtomicReference<>(0.0);
                                AtomicReference<Double> totPeriodWeight = new AtomicReference<>(0.0);
                                List<KpiB2AnalyticDataDTO> kpiB2AnalyticDataDTOS = new ArrayList<>();
                                List<KpiB2DetailResultDTO> kpiB2DetailResultDTOS = new ArrayList<>();
                                instanceDTO
                                    .getAnalysisPeriodStartDate()
                                    .datesUntil(instanceDTO.getAnalysisPeriodEndDate().plusDays(1))
                                    .forEach(date -> {
                                        LOGGER.info("Date {}", date);

                                        LocalDate firstDayOfMonth = date.with(TemporalAdjusters.firstDayOfMonth());
                                        LocalDate lastDayOfMonth = date.with(TemporalAdjusters.lastDayOfMonth());
                                        Month currentMonth = date.getMonth();

                                        if (date.isEqual(firstDayOfMonth)) {
                                            totMonthWeight.set(0.0);
                                            kpiB2AnalyticDataDTOS.clear();
                                        }

                                        if (prevMonth.get() == null || prevMonth.get().compareTo(currentMonth) != 0) {
                                            totRecordMonth.set(
                                                pagoPaRecordedTimeoutService.sumRecordIntoPeriodForPartnerStationAndMethod(
                                                    instanceDTO.getPartnerFiscalCode(),
                                                    station,
                                                    method,
                                                    firstDayOfMonth,
                                                    lastDayOfMonth
                                                )
                                            );

                                            LOGGER.info("Tot Record Month {}", totRecordMonth.get());
                                        }

                                        List<PagoPaRecordedTimeoutDTO> pagoPaRecordedTimeoutDTOS =
                                            pagoPaRecordedTimeoutService.findAllRecordIntoDayForPartnerStationAndMethod(
                                                instanceDTO.getPartnerFiscalCode(),
                                                station,
                                                method,
                                                date
                                            );

                                        long sumTotReqDaily = 0;
                                        long sumReqOkDaily = 0;
                                        long sumReqTimeoutDaily = 0;
                                        double sumWeightsDaily = 0L;

                                        for (PagoPaRecordedTimeoutDTO pagoPaRecordedTimeoutDTO : pagoPaRecordedTimeoutDTOS) {
                                            sumTotReqDaily = sumTotReqDaily + pagoPaRecordedTimeoutDTO.getTotReq();
                                            sumReqOkDaily = sumReqOkDaily + pagoPaRecordedTimeoutDTO.getReqOk();
                                            sumReqTimeoutDaily = sumReqTimeoutDaily + pagoPaRecordedTimeoutDTO.getReqTimeout();

                                            /* inizio fermo programmato **/
                                            for (AnagPlannedShutdownDTO anagPlannedShutdownDTO : maintenance) {}

                                            sumWeightsDaily =
                                                sumWeightsDaily +
                                                (pagoPaRecordedTimeoutDTO.getTotReq() * pagoPaRecordedTimeoutDTO.getAvgTime());

                                            if (pagoPaRecordedTimeoutDTO.getAvgTime() > kpiConfigurationDTO.getAverageTimeLimit()) {
                                                double monthWeight =
                                                    (double) (pagoPaRecordedTimeoutDTO.getTotReq() * 100) / totRecordMonth.get();
                                                double totalWeight =
                                                    (double) (pagoPaRecordedTimeoutDTO.getTotReq() * 100) / totRecordInstance;

                                                totMonthWeight.set(totMonthWeight.get() + monthWeight);
                                                totPeriodWeight.set(totPeriodWeight.get() + totalWeight);
                                            }
                                        }

                                        double weightedAverageDaily = sumWeightsDaily / sumTotReqDaily;

                                        KpiB2AnalyticDataDTO kpiB2AnalyticDataDTO = new KpiB2AnalyticDataDTO();
                                        kpiB2AnalyticDataDTO.setInstanceId(instanceDTO.getId());
                                        kpiB2AnalyticDataDTO.setInstanceModuleId(instanceModuleDTO.getId());
                                        kpiB2AnalyticDataDTO.setAnalysisDate(LocalDate.now());
                                        kpiB2AnalyticDataDTO.setStationId(idStation);
                                        kpiB2AnalyticDataDTO.setMethod(method);
                                        kpiB2AnalyticDataDTO.setEvaluationDate(date);
                                        kpiB2AnalyticDataDTO.setTotReq(sumTotReqDaily);
                                        kpiB2AnalyticDataDTO.setReqOk(sumReqOkDaily);
                                        kpiB2AnalyticDataDTO.setReqTimeout(sumReqTimeoutDaily);
                                        kpiB2AnalyticDataDTO.setAvgTime(roundToNDecimalPlaces(weightedAverageDaily));

                                        kpiB2AnalyticDataDTOS.add(kpiB2AnalyticDataDTO);

                                        if (date.isEqual(lastDayOfMonth)) {
                                            long sumTotReqMontly = 0;
                                            double sumWeightsMontly = 0L;

                                            for (KpiB2AnalyticDataDTO kpiB2AnalyticData : kpiB2AnalyticDataDTOS) {
                                                sumTotReqMontly = sumTotReqMontly + kpiB2AnalyticData.getTotReq();
                                                sumWeightsMontly =
                                                    sumWeightsMontly + (kpiB2AnalyticData.getTotReq() * kpiB2AnalyticData.getAvgTime());
                                            }

                                            double weightedAverageMontly = sumWeightsMontly / sumTotReqMontly;

                                            KpiB2DetailResultDTO kpiB2DetailResultDTO = new KpiB2DetailResultDTO();
                                            kpiB2DetailResultDTO.setInstanceId(instanceDTO.getId());
                                            kpiB2DetailResultDTO.setInstanceModuleId(instanceModuleDTO.getId());
                                            kpiB2DetailResultDTO.setAnalysisDate(LocalDate.now());
                                            kpiB2DetailResultDTO.setStationId(idStation);
                                            kpiB2DetailResultDTO.setMethod(method);
                                            kpiB2DetailResultDTO.setEvaluationType(EvaluationType.MESE);
                                            kpiB2DetailResultDTO.setEvaluationStartDate(firstDayOfMonth);
                                            kpiB2DetailResultDTO.setEvaluationEndDate(lastDayOfMonth);
                                            kpiB2DetailResultDTO.setTotReq(sumTotReqMontly);
                                            kpiB2DetailResultDTO.setAvgTime(roundToNDecimalPlaces(weightedAverageMontly));
                                            kpiB2DetailResultDTO.setOverTimeLimit(roundToNDecimalPlaces(totMonthWeight.get()));

                                            OutcomeStatus outcomeStatus = OutcomeStatus.OK;

                                            if (kpiConfigurationDTO.getEvaluationType().compareTo(EvaluationType.MESE) == 0) {
                                                if (
                                                    totMonthWeight.get() >
                                                    (kpiConfigurationDTO.getEligibilityThreshold() +
                                                        ((totMonthWeight.get() * kpiConfigurationDTO.getTollerance()) / 100))
                                                ) {
                                                    outcomeStatus = OutcomeStatus.KO;
                                                }
                                            }

                                            kpiB2DetailResultDTO.setOutcome(outcomeStatus);

                                            kpiB2DetailResultDTO = kpiB2DetailResultService.save(kpiB2DetailResultDTO);

                                            kpiB2DetailResultDTOS.add(kpiB2DetailResultDTO);

                                            KpiB2DetailResultDTO finalKpiB2DetailResultDTO = kpiB2DetailResultDTO;

                                            kpiB2AnalyticDataDTOS.forEach(kpiB2AnalyticData -> {
                                                kpiB2AnalyticData.setKpiB2DetailResultId(finalKpiB2DetailResultDTO.getId());
                                            });

                                            kpiB2AnalyticDataService.saveAll(kpiB2AnalyticDataDTOS);
                                        }

                                        prevMonth.set(currentMonth);
                                    });

                                long sumTotReqPeriod = 0;
                                double sumWeightsPeriod = 0L;

                                for (KpiB2DetailResultDTO kpiB2DetailResult : kpiB2DetailResultDTOS) {
                                    sumTotReqPeriod = sumTotReqPeriod + kpiB2DetailResult.getTotReq();
                                    sumWeightsPeriod = sumWeightsPeriod + (kpiB2DetailResult.getTotReq() * kpiB2DetailResult.getAvgTime());
                                }

                                double weightedAveragePeriod = sumWeightsPeriod / sumTotReqPeriod;

                                KpiB2DetailResultDTO kpiB2DetailResultDTO = new KpiB2DetailResultDTO();
                                kpiB2DetailResultDTO.setInstanceId(instanceDTO.getId());
                                kpiB2DetailResultDTO.setInstanceModuleId(instanceModuleDTO.getId());
                                kpiB2DetailResultDTO.setAnalysisDate(LocalDate.now());
                                kpiB2DetailResultDTO.setStationId(idStation);
                                kpiB2DetailResultDTO.setMethod(method);
                                kpiB2DetailResultDTO.setEvaluationType(EvaluationType.MESE);
                                kpiB2DetailResultDTO.setEvaluationStartDate(instanceDTO.getAnalysisPeriodStartDate());
                                kpiB2DetailResultDTO.setEvaluationEndDate(instanceDTO.getAnalysisPeriodEndDate());
                                kpiB2DetailResultDTO.setTotReq(totRecordInstance);
                                kpiB2DetailResultDTO.setAvgTime(roundToNDecimalPlaces(weightedAveragePeriod));
                                kpiB2DetailResultDTO.setOverTimeLimit(roundToNDecimalPlaces(totPeriodWeight.get()));

                                OutcomeStatus outcomeStatus = OutcomeStatus.OK;

                                if (kpiConfigurationDTO.getEvaluationType().compareTo(EvaluationType.TOTALE) == 0) {
                                    if (
                                        totMonthWeight.get() >
                                        (kpiConfigurationDTO.getEligibilityThreshold() +
                                            ((totMonthWeight.get() * kpiConfigurationDTO.getTollerance()) / 100))
                                    ) {
                                        outcomeStatus = OutcomeStatus.KO;
                                    }
                                }

                                kpiB2DetailResultDTO.setOutcome(outcomeStatus);

                                kpiB2DetailResultService.save(kpiB2DetailResultDTO);
                            }
                        });
                    });
                }
            });
        }
        LOGGER.info("End");
    }

    //    private KpiB2DetailResultDTO saveKpiB2DetailResultDTO(KpiB2DetailResultDTO kpiB2DetailResultDTO) {
    //
    //
    //        return kpiB2DetailResultService.save(kpiB2DetailResultDTO);
    //    }

    private static double roundToNDecimalPlaces(double value) {
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(5, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
