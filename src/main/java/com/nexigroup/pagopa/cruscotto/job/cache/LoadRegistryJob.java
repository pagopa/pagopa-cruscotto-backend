package com.nexigroup.pagopa.cruscotto.job.cache;

import com.nexigroup.pagopa.cruscotto.service.AnagInstitutionService;
import com.nexigroup.pagopa.cruscotto.service.AnagPartnerService;
import com.nexigroup.pagopa.cruscotto.service.AnagStationService;
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
public class LoadRegistryJob extends QuartzJobBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadRegistryJob.class);
    
    private final AnagPartnerService anagPartnerService;
    private final AnagStationService anagStationService;
    private final AnagInstitutionService anagInstitutionService;

    @Override
    protected void executeInternal(@NotNull JobExecutionContext context) {
        LOGGER.info("LoadRegistryJob triggered - Start load cache from PagoPA");
        
        anagPartnerService.loadFromPagoPA();
        anagStationService.loadFromPagoPA();
        anagInstitutionService.loadFromPagoPA();
        anagStationService.loadAssociationsFromPagoPA();
        
        LOGGER.info("LoadRegistryJob completed");
    }
}
