package com.nexigroup.pagopa.cruscotto.job.kpi.b5;

import com.nexigroup.pagopa.cruscotto.config.ApplicationProperties;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.service.InstanceService;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceDTO;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.lang.NonNull;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@DisallowConcurrentExecution
public class KpiB5Job extends QuartzJobBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(KpiB5Job.class);

    private final InstanceService instanceService;
    private final ApplicationProperties applicationProperties;

    @Override
    protected void executeInternal(@NonNull JobExecutionContext context) {
        LOGGER.info("Start calculate kpi B.5");

        try {
            if (!applicationProperties.getJob().getKpiB5Job().isEnabled()) {
                LOGGER.info("Job calculate kpi B.5 disabled. Exit...");
                return;
            }

            List<InstanceDTO> instanceDTOS = instanceService.findInstanceToCalculate(
                ModuleCode.B5,
                applicationProperties.getJob().getKpiB5Job().getLimit()
            );

            if (instanceDTOS.isEmpty()) {
                LOGGER.info("No instances found to calculate KPI B.5");
                return;
            }

            LOGGER.info("Found {} instances to calculate KPI B.5", instanceDTOS.size());

            // TODO: Implement KPI B.5 business logic here
            // For now, just log the instances that would be processed
            instanceDTOS.forEach(instanceDTO -> {
                LOGGER.info("Would process instance: {} - {} (Partner: {})", 
                    instanceDTO.getInstanceIdentification(),
                    instanceDTO.getId(),
                    instanceDTO.getPartnerName());
            });

            LOGGER.info("KPI B.5 calculation completed (no business logic implemented yet)");

        } catch (Exception exception) {
            LOGGER.error("Problem during calculate kpi B.5", exception);
        }

        LOGGER.info("End calculate kpi B.5");
    }
}