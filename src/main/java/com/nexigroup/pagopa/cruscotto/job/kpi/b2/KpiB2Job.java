package com.nexigroup.pagopa.cruscotto.job.kpi.b2;

import com.nexigroup.pagopa.cruscotto.config.ApplicationProperties;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.service.AnagStationService;
import com.nexigroup.pagopa.cruscotto.service.InstanceService;
import com.nexigroup.pagopa.cruscotto.service.KpiConfigurationService;
import com.nexigroup.pagopa.cruscotto.service.PagoPaRecordedTimeoutService;
import com.nexigroup.pagopa.cruscotto.service.dto.AnagStationDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiConfigurationDTO;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.AllArgsConstructor;
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
                        methods.forEach(method -> {
                            LOGGER.info("Method {}", method);
                            instanceDTO
                                .getAnalysisPeriodStartDate()
                                .datesUntil(instanceDTO.getAnalysisPeriodEndDate().plusDays(1))
                                .forEach(date -> {
                                    LOGGER.info("Date {}", date);
                                });
                        });
                    });
                }
            });
        }
        LOGGER.info("End");
    }
}
