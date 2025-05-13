package com.nexigroup.pagopa.cruscotto.job.kpi.b2;

import com.nexigroup.pagopa.cruscotto.config.ApplicationProperties;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.TypePlanned;
import com.nexigroup.pagopa.cruscotto.service.*;
import com.nexigroup.pagopa.cruscotto.service.dto.AnagPlannedShutdownDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiConfigurationDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.PagoPaRecordedTimeoutDTO;
import java.time.Month;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    private final ApplicationProperties applicationProperties;

    private final PagoPaRecordedTimeoutService pagoPaRecordedTimeoutService;

    private final AnagStationService anagStationService;

    private final KpiConfigurationService kpiConfigurationService;

    private final AnagPlannedShutdownService anagPlannedShutdownService;

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
                            AtomicReference<Long> prevTotRecordMonth = new AtomicReference<>();
                            instanceDTO
                                .getAnalysisPeriodStartDate()
                                .datesUntil(instanceDTO.getAnalysisPeriodEndDate().plusDays(1))
                                .forEach(date -> {
                                    LOGGER.info(
                                        "Date {}: Range {} - {}",
                                        date,
                                        date.with(TemporalAdjusters.firstDayOfMonth()),
                                        date.with(TemporalAdjusters.lastDayOfMonth())
                                    );

                                    Month currentMonth = date.getMonth();

                                    if (prevMonth.get() == null || prevMonth.get().compareTo(currentMonth) != 0) {
                                        prevTotRecordMonth.set(
                                            pagoPaRecordedTimeoutService.sumRecordIntoPeriodForPartnerStationAndMethod(
                                                instanceDTO.getPartnerFiscalCode(),
                                                station,
                                                method,
                                                date.with(TemporalAdjusters.firstDayOfMonth()),
                                                date.with(TemporalAdjusters.lastDayOfMonth())
                                            )
                                        );

                                        LOGGER.info("Tot Record Month {}", prevTotRecordMonth.get());
                                    }

                                    List<PagoPaRecordedTimeoutDTO> pagoPaRecordedTimeoutDTOS =
                                        pagoPaRecordedTimeoutService.findAllRecordIntoDayForPartnerStationAndMethod(
                                            instanceDTO.getPartnerFiscalCode(),
                                            station,
                                            method,
                                            date
                                        );

                                    LOGGER.info("{}", pagoPaRecordedTimeoutDTOS);

                                    // calcolo media ponderata del giorno
                                    // somma del campo Sum(avg_time. * tot_req)/ SUM(Giorno)

                                    // solo per avg time maggiore di configurazione calcolo
                                    //               ROUND(tot_req::NUMERIC * 100 / 199591, 5) as peso_mese,
                                    //                ROUND(tot_req::NUMERIC * 100 / 394045, 5) as peso_totale,
                                    // se non rientra in fermo programmato

                                    // al cambio del mese calcolo la media ponderata del mese
                                    // somma del campo Sum(avg_time. * tot_req)/ SUM(Mese)

                                    prevMonth.set(currentMonth);
                                });
                        });
                    });
                }
            });
        }
        LOGGER.info("End");
    }
}
