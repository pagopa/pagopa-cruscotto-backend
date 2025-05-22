package com.nexigroup.pagopa.cruscotto.job.kpi;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.AnalysisOutcome;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.AnalysisType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.InstanceStatus;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleStatus;
import com.nexigroup.pagopa.cruscotto.service.*;
import com.nexigroup.pagopa.cruscotto.service.dto.*;
import java.time.Instant;
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
public class CalculateStateInstanceJob extends QuartzJobBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(CalculateStateInstanceJob.class);

    private static final Integer LIMIT = 50;

    private final InstanceService instanceService;

    private final InstanceModuleService instanceModuleService;

    @Override
    protected void executeInternal(@NotNull JobExecutionContext context) {
        LOGGER.info("Start calculate state instance");

        Long instanceId = (context.getTrigger().getJobDataMap().get("instanceId") != null)
            ? context.getTrigger().getJobDataMap().getLong("instanceId")
            : null;

        LOGGER.info("Data map received: {}", instanceId);

        if (instanceId != null) {
            InstanceDTO instanceDTO = instanceService.findOne(instanceId).orElseThrow(() -> new NullPointerException("Instance not found"));

            calculateStateInstance(instanceDTO);
        } else {
            List<InstanceDTO> instanceDTOS = instanceService.findInstanceToCalculate(LIMIT);

            instanceDTOS.forEach(this::calculateStateInstance);
        }
        LOGGER.info("End");
    }

    private void calculateStateInstance(InstanceDTO instanceDTO) {
        LOGGER.info("Instance id {}", instanceDTO.getId());

        List<InstanceModuleDTO> instanceModuleDTOS = instanceModuleService.findAllByInstanceId(instanceDTO.getId());

        int ko = 0;
        int standBy = 0;

        if (instanceModuleDTOS.isEmpty()) {
            LOGGER.info("No instance module found");
        } else {
            for (InstanceModuleDTO instanceModuleDTO : instanceModuleDTOS) {
                LOGGER.info("Instance module {} status {}", instanceModuleDTO.getModuleCode(), instanceModuleDTO.getStatus());
                if (instanceModuleDTO.getStatus().compareTo(ModuleStatus.ATTIVO) == 0) {
                    if (instanceModuleDTO.getAnalysisType().compareTo(AnalysisType.AUTOMATICA) == 0) {
                        switch (instanceModuleDTO.getAutomaticOutcome()) {
                            case STANDBY:
                                standBy++;
                                break;
                            case OK:
                                if (instanceModuleDTO.getManualOutcome() != null) {
                                    if (instanceModuleDTO.getManualOutcome().compareTo(AnalysisOutcome.KO) == 0) {
                                        ko++;
                                    }
                                }
                            case KO:
                                if (instanceModuleDTO.getManualOutcome() != null) {
                                    if (instanceModuleDTO.getManualOutcome().compareTo(AnalysisOutcome.KO) == 0) {
                                        ko++;
                                    }
                                } else {
                                    ko++;
                                }
                                break;
                        }
                    } else if (instanceModuleDTO.getAnalysisType().compareTo(AnalysisType.MANUALE) == 0) {
                        switch (instanceModuleDTO.getManualOutcome()) {
                            case STANDBY:
                                standBy++;
                                break;
                            case KO:
                                ko++;
                                break;
                        }
                    }
                }
            }
        }

        if (standBy == 0) {
            instanceService.updateExecuteStateAndLastAnalysis(
                instanceDTO.getId(),
                Instant.now(),
                ko > 0 ? AnalysisOutcome.KO : AnalysisOutcome.OK
            );
        }
    }
}
