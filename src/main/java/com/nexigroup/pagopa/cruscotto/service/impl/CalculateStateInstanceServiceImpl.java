package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.AnalysisOutcome;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.AnalysisType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleStatus;
import com.nexigroup.pagopa.cruscotto.job.config.JobConstant;
import com.nexigroup.pagopa.cruscotto.service.AnagPartnerService;
import com.nexigroup.pagopa.cruscotto.service.CalculateStateInstanceService;
import com.nexigroup.pagopa.cruscotto.service.InstanceModuleService;
import com.nexigroup.pagopa.cruscotto.service.InstanceService;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceModuleDTO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalculateStateInstanceServiceImpl implements CalculateStateInstanceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CalculateStateInstanceServiceImpl.class);

    private final InstanceService instanceService;
    private final InstanceModuleService instanceModuleService;
    private final AnagPartnerService anagPartnerService;

    @Transactional
    @Override
    public InstanceModuleDTO updateModuleAndInstanceState(InstanceModuleDTO moduleDTO, com.nexigroup.pagopa.cruscotto.domain.AuthUser user) {
        // Update the module
        InstanceModuleDTO updatedModule = instanceModuleService.updateInstanceModule(moduleDTO, user);
        // Update the instance state
        InstanceDTO instanceDTO = instanceService.findOne(updatedModule.getInstanceId()).orElse(null);
        if (instanceDTO != null) {
            calculateStateInstance(instanceDTO, user.getLogin());
        }
        return updatedModule;
    }

    @Override
    public void calculateStateInstance(InstanceDTO instanceDTO) {
        calculateStateInstance(instanceDTO, JobConstant.CALCULATE_STATE_INSTANCE_JOB);
    }

    public void calculateStateInstance(InstanceDTO instanceDTO, String currentUser) {
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
                                break;
                            case KO:
                                if (instanceModuleDTO.getManualOutcome() != null) {
                                    if (instanceModuleDTO.getManualOutcome().compareTo(AnalysisOutcome.KO) == 0) {
                                        ko++;
                                    } else {
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
                            case OK:
                                // No action needed for OK
                                break;
                        }
                    }
                }
            }
        }
        if (standBy == 0) {
            Instant now = Instant.now();
            instanceService.updateExecuteStateAndLastAnalysis(
                instanceDTO.getId(),
                now,
                ko > 0 ? AnalysisOutcome.KO : AnalysisOutcome.OK,
                currentUser
            );
            anagPartnerService.updateLastAnalysisDate(instanceDTO.getPartnerId(), now);
            if (BooleanUtils.toBooleanDefaultIfNull(instanceDTO.getChangePartnerQualified(), false)) {
                anagPartnerService.changePartnerQualified(instanceDTO.getPartnerId(), ko > 0 ? Boolean.FALSE : Boolean.TRUE);
            }
            anagPartnerService.updateAnalysisPeriodDates(
                instanceDTO.getPartnerId(),
                instanceDTO.getAnalysisPeriodStartDate(),
                instanceDTO.getAnalysisPeriodEndDate()
            );
        }
    }
}
