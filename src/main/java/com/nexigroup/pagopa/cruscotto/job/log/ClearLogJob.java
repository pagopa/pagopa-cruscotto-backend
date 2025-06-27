package com.nexigroup.pagopa.cruscotto.job.log;

import com.nexigroup.pagopa.cruscotto.config.ApplicationProperties;
import com.nexigroup.pagopa.cruscotto.service.QrtzLogTriggerExecutedService;
import java.time.LocalDate;
import java.time.ZoneOffset;
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
public class ClearLogJob extends QuartzJobBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClearLogJob.class);

    private final QrtzLogTriggerExecutedService qrtzLogTriggerExecutedService;

    private final ApplicationProperties applicationProperties;

    @Override
    protected void executeInternal(@NotNull JobExecutionContext context) {
        LOGGER.info("Clear log job started");

        List<Long> idsToDelete = qrtzLogTriggerExecutedService.findByScheduledTimeBefore(
            LocalDate.now().atStartOfDay().minusDays(applicationProperties.getJob().getClearLogJob().getDays()).toInstant(ZoneOffset.UTC)
        );

        for (Long id : idsToDelete) {
            LOGGER.debug("Deleting id {}", id);
            try {
                qrtzLogTriggerExecutedService.deleteById(id);
            } catch (Exception e) {
                LOGGER.error("Exception during remove old data {}", id, e);
            }
        }

        LOGGER.info("End");
    }
}
