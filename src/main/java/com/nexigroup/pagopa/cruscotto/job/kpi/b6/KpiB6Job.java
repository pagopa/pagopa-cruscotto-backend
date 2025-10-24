package com.nexigroup.pagopa.cruscotto.job.kpi.b6;

import com.nexigroup.pagopa.cruscotto.config.ApplicationProperties;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.job.config.JobConstant;
import com.nexigroup.pagopa.cruscotto.kpi.b6.KpiB6Processor;
import com.nexigroup.pagopa.cruscotto.kpi.framework.KpiExecutionContext;
import com.nexigroup.pagopa.cruscotto.service.*;
import com.nexigroup.pagopa.cruscotto.service.dto.*;
import com.nexigroup.pagopa.cruscotto.service.filter.AnagStationFilter;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.StationStatus;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Job for calculating KPI B.6: "Payment Options"
 * 
 * Business Rule: Payment options functionality must be active and working on all 
 * active stations of the Technology Partner.
 * 
 * Uses the new generic KPI framework with composition over inheritance.
 */
@Component
@AllArgsConstructor
@DisallowConcurrentExecution
public class KpiB6Job extends QuartzJobBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(KpiB6Job.class);

    private final ApplicationProperties applicationProperties;
    private final InstanceService instanceService;
    private final InstanceModuleService instanceModuleService;
    private final KpiConfigurationService kpiConfigurationService;
    private final AnagStationService anagStationService;
    private final KpiB6Processor kpiB6Processor;
    private final KpiB6ResultService kpiB6ResultService;
    private final KpiB6DetailResultService kpiB6DetailResultService;
    private final KpiB6AnalyticDataService kpiB6AnalyticDataService;
    private final Scheduler scheduler;

    @Override
    public void executeInternal(@NonNull JobExecutionContext context) {
        LOGGER.info("Start calculate KPI B.6 - Payment Options");

        if (!applicationProperties.getJob().getKpiB6Job().isEnabled()) {
            LOGGER.info("Job calculate KPI B.6 disabled. Exit...");
            return;
        }

        List<InstanceDTO> instanceDTOS = instanceService.findInstanceToCalculate(
                ModuleCode.B6,
                applicationProperties.getJob().getKpiB6Job().getLimit()
        );

        if (instanceDTOS.isEmpty()) {
            LOGGER.info("No instance to calculate B.6. Exit....");
            return;
        }

        KpiConfigurationDTO kpiConfigurationDTO = kpiConfigurationService
                .findKpiConfigurationByCode(ModuleCode.B6.code)
                .orElseThrow(() -> new NullPointerException("KPI B.6 Configuration not found"));

        LOGGER.info("KPI configuration: {}", kpiConfigurationDTO);
        List<String> errors = new ArrayList<>();

        for (InstanceDTO instanceDTO : instanceDTOS) {
            try {
                processInstance(instanceDTO, kpiConfigurationDTO);
            } catch (Exception e) {
                String errorMessage = String.format(
                        "Error in elaboration instance %s for partner %s - %s with period %s - %s. Exception: %s",
                        instanceDTO.getInstanceIdentification(),
                        instanceDTO.getPartnerFiscalCode(),
                        instanceDTO.getPartnerName(),
                        instanceDTO.getAnalysisPeriodStartDate(),
                        instanceDTO.getAnalysisPeriodEndDate(),
                        e.getMessage()
                );
                errors.add(errorMessage);
                LOGGER.error(errorMessage, e);
            }
        }

        if (!errors.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            errors.forEach(error -> sb.append(error).append("\n"));
            throw new RuntimeException(sb.toString());
        }

        LOGGER.info("End KPI B.6 calculation");
    }

    private void processInstance(InstanceDTO instanceDTO, KpiConfigurationDTO kpiConfigurationDTO) throws Exception {
        LOGGER.info(
                "Start elaboration instance {} for partner {} - {} with period {} - {}",
                instanceDTO.getInstanceIdentification(),
                instanceDTO.getPartnerFiscalCode(),
                instanceDTO.getPartnerName(),
                instanceDTO.getAnalysisPeriodStartDate(),
                instanceDTO.getAnalysisPeriodEndDate()
        );

        instanceService.updateInstanceStatusInProgress(instanceDTO.getId());

        InstanceModuleDTO instanceModuleDTO = instanceModuleService
                .findOne(instanceDTO.getId(), kpiConfigurationDTO.getModuleId())
                .orElseThrow(() -> new NullPointerException("KPI B.6 InstanceModule not found"));

        // Delete previous data for this instanceModule
        LOGGER.info("Deletion phase for any previous processing in error");
        kpiB6ResultService.deleteAllByInstanceModuleId(instanceModuleDTO.getId());
        kpiB6DetailResultService.deleteAllByInstanceModule(instanceModuleDTO.getId());
        kpiB6AnalyticDataService.deleteAllByInstanceModuleId(instanceModuleDTO.getId());

        // Fetch station data for the partner
        AnagStationFilter filter = new AnagStationFilter();
        filter.setShowNotActive(false); // Only active stations
        // Note: We need to get partnerId from partnerFiscalCode, for now we'll filter by partnerFiscalCode
        
        Page<AnagStationDTO> stationPage = anagStationService.findAll(filter, PageRequest.of(0, Integer.MAX_VALUE));
        
        // Filter by partner fiscal code and only active stations
        List<AnagStationDTO> stationData = stationPage.getContent().stream()
                .filter(station -> instanceDTO.getPartnerFiscalCode().equals(station.getPartnerFiscalCode()))
                .filter(station -> StationStatus.ATTIVA.equals(station.getStatus()))
                .toList();

        LOGGER.info("Found {} stations for analysis", stationData.size());

        // Create execution context with station data
        Map<String, Object> additionalParams = new HashMap<>();
        additionalParams.put("stationData", stationData);

        KpiExecutionContext executionContext = KpiExecutionContext.builder()
                .instance(instanceDTO)
                .instanceModule(instanceModuleDTO)
                .configuration(kpiConfigurationDTO)
                .analysisStart(instanceDTO.getAnalysisPeriodStartDate())
                .analysisEnd(instanceDTO.getAnalysisPeriodEndDate())
                .partnerFiscalCode(instanceDTO.getPartnerFiscalCode())
                .additionalParameters(additionalParams)
                .build();

        // Process using the new framework
        KpiB6ResultDTO kpiResult = kpiB6Processor.processKpiResult(executionContext);
        kpiResult = kpiB6ResultService.save(kpiResult);
        
        // Update context with saved result ID
        additionalParams.put("kpiResultId", kpiResult.getId());

        List<KpiB6DetailResultDTO> detailResults = kpiB6Processor.processDetailResults(executionContext, kpiResult);
        detailResults = kpiB6DetailResultService.saveAll(detailResults);

        // Process analytic data for monthly results
        for (KpiB6DetailResultDTO detailResult : detailResults) {
            if (detailResult.getEvaluationType() == com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType.MESE) {
                List<KpiB6AnalyticDataDTO> analyticData = kpiB6Processor.processAnalyticData(executionContext, detailResult);
                kpiB6AnalyticDataService.saveAll(analyticData);
            }
        }

        // Calculate final outcome
        com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus finalOutcome = 
                kpiB6Processor.calculateFinalOutcome(executionContext, kpiResult, detailResults);

        LOGGER.info("Final outcome: {}", finalOutcome);

        // Update result and instance module with final outcome
        kpiB6ResultService.updateOutcome(kpiResult.getId(), finalOutcome);
        instanceModuleService.updateAutomaticOutcome(instanceModuleDTO.getId(), finalOutcome);

        // Trigger state calculation job
        triggerStateCalculationJob(instanceDTO.getId());

        LOGGER.info("Completed processing instance {} with outcome: {}", 
                instanceDTO.getInstanceIdentification(), finalOutcome);
    }

    private void triggerStateCalculationJob(Long instanceId) throws SchedulerException {
        JobDetail job = scheduler.getJobDetail(JobKey.jobKey(JobConstant.CALCULATE_STATE_INSTANCE_JOB, "DEFAULT"));
        Trigger trigger = TriggerBuilder.newTrigger()
                .usingJobData("instanceId", instanceId)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withMisfireHandlingInstructionFireNow()
                        .withRepeatCount(0))
                .forJob(job)
                .build();
        scheduler.scheduleJob(trigger);
    }
}