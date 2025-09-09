
package com.nexigroup.pagopa.cruscotto.job.kpi;

import com.nexigroup.pagopa.cruscotto.service.InstanceService;
import com.nexigroup.pagopa.cruscotto.service.CalculateStateInstanceService;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceDTO;
import java.util.List;
import lombok.AllArgsConstructor;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@DisallowConcurrentExecution
public class CalculateStateInstanceJob extends QuartzJobBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(CalculateStateInstanceJob.class);

    private static final Integer LIMIT = 50;

    private final InstanceService instanceService;
    private final CalculateStateInstanceService calculateStateInstanceService;

    @Override
    protected void executeInternal(@org.springframework.lang.NonNull JobExecutionContext context) {
        LOGGER.info("Start calculate state instance");

        Long instanceId = (context.getTrigger().getJobDataMap().get("instanceId") != null)
            ? context.getTrigger().getJobDataMap().getLong("instanceId")
            : null;

        LOGGER.info("Data map received: {}", instanceId);

        if (instanceId != null) {
            InstanceDTO instanceDTO = instanceService.findOne(instanceId).orElseThrow(() -> new NullPointerException("Instance not found"));
            calculateStateInstanceService.calculateStateInstance(instanceDTO);
        } else {
            List<InstanceDTO> instanceDTOS = instanceService.findInstanceToCalculate(LIMIT);
            instanceDTOS.forEach(calculateStateInstanceService::calculateStateInstance);
        }
        LOGGER.info("End");
    }

    // Business logic moved to CalculateStateInstanceService
}
