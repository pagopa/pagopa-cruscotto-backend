package com.nexigroup.pagopa.cruscotto.job.kpi.b2;

import com.nexigroup.pagopa.cruscotto.config.ApplicationProperties;
import com.nexigroup.pagopa.cruscotto.service.InstanceService;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceDTO;
import java.util.List;
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

    @Override
    protected void executeInternal(@NotNull JobExecutionContext context) {
        LOGGER.info("Start calculate kpi B.2");

        /* Recupero le istanze in stato PIANIFICATA con data di analisi minore o uguale alla data odierna */
        List<InstanceDTO> instanceDTOS = instanceService.findInstanceToCalculate(applicationProperties.getJob().getKpiB2Job().getLimit());

        instanceDTOS.forEach(instanceDTO -> {
            LOGGER.info(
                "Start elaboration instance {} for pattern {} - {} with period {} - {}",
                instanceDTO.getInstanceIdentification(),
                instanceDTO.getPartnerFiscalCode(),
                instanceDTO.getPartnerName(),
                instanceDTO.getAnalysisPeriodStartDate(),
                instanceDTO.getAnalysisPeriodEndDate()
            );

            /* Controllo che il modulo B.2 sia impostato ad automatico */
            instanceDTO
                .getAnalysisPeriodStartDate()
                .datesUntil(instanceDTO.getAnalysisPeriodEndDate().plusDays(1))
                .forEach(date -> {
                    LOGGER.info("Date {}", date);
                });
        });

        LOGGER.info("End");
    }
}
