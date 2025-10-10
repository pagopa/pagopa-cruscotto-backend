package com.nexigroup.pagopa.cruscotto.job.kpi.b3;

import com.nexigroup.pagopa.cruscotto.config.ApplicationProperties;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.domain.PagopaNumeroStandin;
import com.nexigroup.pagopa.cruscotto.repository.PagopaNumeroStandinRepository;
import com.nexigroup.pagopa.cruscotto.service.InstanceService;
import com.nexigroup.pagopa.cruscotto.service.InstanceModuleService;
import com.nexigroup.pagopa.cruscotto.service.KpiConfigurationService;
import com.nexigroup.pagopa.cruscotto.service.AnagPlannedShutdownService;
import com.nexigroup.pagopa.cruscotto.service.KpiB3DataService;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceModuleDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiConfigurationDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.AnagPlannedShutdownDTO;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.TypePlanned;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import lombok.AllArgsConstructor;
import org.springframework.lang.NonNull;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobDetail;
import org.quartz.JobBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@DisallowConcurrentExecution
public class KpiB3Job extends QuartzJobBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(KpiB3Job.class);

    private final InstanceService instanceService;
    private final InstanceModuleService instanceModuleService;
    private final ApplicationProperties applicationProperties;
    private final KpiConfigurationService kpiConfigurationService;
    private final AnagPlannedShutdownService anagPlannedShutdownService;
    private final PagopaNumeroStandinRepository pagopaNumeroStandinRepository;
    private final KpiB3DataService kpiB3DataService;
    private final Scheduler scheduler;

    @Override
    protected void executeInternal(@NonNull JobExecutionContext context) {
        LOGGER.info("Start calculate kpi B.3");

        try {
            if (!applicationProperties.getJob().getKpiB3Job().isEnabled()) {
                LOGGER.info("Job calculate kpi B.3 disabled. Exit...");
                return;
            }

            List<InstanceDTO> instanceDTOS = instanceService.findInstanceToCalculate(
                ModuleCode.B3,
                applicationProperties.getJob().getKpiB3Job().getLimit()
            );

            if (instanceDTOS.isEmpty()) {
                LOGGER.info("No instance to calculate B.3. Exit....");
            } else {
                KpiConfigurationDTO kpiConfigurationDTO = kpiConfigurationService
                    .findKpiConfigurationByCode(ModuleCode.B3.code)
                    .orElseThrow(() -> new NullPointerException("KPI B.3 Configuration not found"));

                LOGGER.info("Kpi configuration {}", kpiConfigurationDTO);

                instanceDTOS.forEach(instanceDTO -> {
                    try {
                        LOGGER.info(
                            "Start elaboration instance {} for partner {} - {} with period {} - {}",
                            instanceDTO.getInstanceIdentification(),
                            instanceDTO.getPartnerFiscalCode(),
                            instanceDTO.getPartnerName(),
                            instanceDTO.getAnalysisPeriodStartDate(),
                            instanceDTO.getAnalysisPeriodEndDate()
                        );

                        // Update instance status from "planned" to "in progress"
                        instanceService.updateInstanceStatusInProgress(instanceDTO.getId());

                        InstanceModuleDTO instanceModuleDTO = instanceModuleService
                            .findOne(instanceDTO.getId(), kpiConfigurationDTO.getModuleId())
                            .orElseThrow(() -> new NullPointerException("KPI B.3 InstanceModule not found"));

                        LOGGER.info("Deletion phase for any previous processing in error (handled by KpiB3DataService)");

                        // Calculate analysis date
                        LocalDate analysisDate = LocalDate.now();

                        // Get configuration for shutdown exclusions
                        Double eligibilityThreshold = kpiConfigurationDTO.getEligibilityThreshold() != null
                            ? kpiConfigurationDTO.getEligibilityThreshold()
                            : 0.0;
                        
                        Boolean excludePlannedShutdown = kpiConfigurationDTO.getExcludePlannedShutdown() != null
                            ? kpiConfigurationDTO.getExcludePlannedShutdown()
                            : false;
                            
                        Boolean excludeUnplannedShutdown = kpiConfigurationDTO.getExcludeUnplannedShutdown() != null
                            ? kpiConfigurationDTO.getExcludeUnplannedShutdown()
                            : false;

                        LOGGER.info("Configuration - Eligibility threshold: {}, Exclude planned shutdown: {}, Exclude unplanned shutdown: {}", 
                                   eligibilityThreshold, excludePlannedShutdown, excludeUnplannedShutdown);

                        // 1. Retrieve Stand-In data from standin_number table
                        LocalDateTime startDateTime = instanceDTO.getAnalysisPeriodStartDate().atStartOfDay();
                        LocalDateTime endDateTime = instanceDTO.getAnalysisPeriodEndDate().atTime(23, 59, 59);

                        LOGGER.info("Querying Stand-In data from {} to {}", startDateTime, endDateTime);
                        
                        List<PagopaNumeroStandin> standInData = pagopaNumeroStandinRepository.findByDateRange(
                            startDateTime, endDateTime
                        );

                        LOGGER.info("Found {} Stand-In records for the analysis period", standInData.size());

                        // 2. Filter data excluding shutdowns if configured
                        List<PagopaNumeroStandin> filteredStandInData = filterShutdownPeriods(
                            standInData, 
                            instanceDTO, 
                            excludePlannedShutdown, 
                            excludeUnplannedShutdown
                        );

                        LOGGER.info("After filtering shutdowns: {} Stand-In records remain", filteredStandInData.size());

                        // 3. Calculate final outcome based on Stand-In data
                        AtomicReference<OutcomeStatus> kpiB3ResultFinalOutcome = new AtomicReference<>(OutcomeStatus.OK);
                        
                        OutcomeStatus outcome = calculateKpiB3Outcome(
                            filteredStandInData, 
                            eligibilityThreshold,
                            instanceDTO
                        );

                        kpiB3ResultFinalOutcome.set(outcome);

                        LOGGER.info("Calculated KPI B.3 outcome: {}", outcome);

                        // 4. Salva risultati nelle tabelle KPI B.3 usando il data service
                        kpiB3DataService.saveKpiB3Results(
                            instanceDTO,
                            instanceModuleDTO,
                            kpiConfigurationDTO,
                            analysisDate,
                            outcome,
                            filteredStandInData
                        );

                        LOGGER.info("KPI B.3 results saved successfully");

                        // 5. Update automatic outcome of instance module
                        instanceModuleService.updateAutomaticOutcome(instanceModuleDTO.getId(), kpiB3ResultFinalOutcome.get());

                        LOGGER.info("Instance module {} updated with outcome: {}", instanceModuleDTO.getId(), kpiB3ResultFinalOutcome.get());

                    } catch (Exception e) {
                        LOGGER.error(
                            "Exception during calculate kpi B.3 for instance {} - {}",
                            instanceDTO.getInstanceIdentification(),
                            instanceDTO.getPartnerName(),
                            e
                        );
                    }
                });
            }
        } catch (Exception exception) {
            LOGGER.error("Problem during calculate kpi B.3", exception);
        }

        LOGGER.info("End");
    }

    /**
     * Filters Stand-In data excluding shutdown periods if configured
     */
    private List<PagopaNumeroStandin> filterShutdownPeriods(
            List<PagopaNumeroStandin> standInData,
            InstanceDTO instanceDTO,
            Boolean excludePlannedShutdown,
            Boolean excludeUnplannedShutdown) {

        // If no filters to apply, return original data
        if (!excludePlannedShutdown && !excludeUnplannedShutdown) {
            LOGGER.info("No shutdown exclusions configured, using all Stand-In data");
            return standInData;
        }

        try {
            // Retrieve planned shutdowns for the analysis period
            List<AnagPlannedShutdownDTO> plannedShutdowns = anagPlannedShutdownService.findAllByTypePlannedIntoPeriod(
                instanceDTO.getPartnerId(),
                TypePlanned.NON_PROGRAMMATO,
                instanceDTO.getAnalysisPeriodStartDate(),
                instanceDTO.getAnalysisPeriodEndDate()
            );

            LOGGER.info("Found {} planned shutdowns for partner {}", plannedShutdowns.size(), instanceDTO.getPartnerFiscalCode());

            // Filter Stand-In events excluding those in shutdown periods
            return standInData.stream()
                .filter(standIn -> !isInShutdownPeriod(standIn, plannedShutdowns, excludePlannedShutdown, excludeUnplannedShutdown))
                .toList();

        } catch (Exception e) {
            LOGGER.error("Error filtering shutdown periods, using original Stand-In data", e);
            return standInData;
        }
    }

    /**
     * Checks if a Stand-In event occurred during a shutdown period
     */
    private boolean isInShutdownPeriod(
            PagopaNumeroStandin standIn,
            List<AnagPlannedShutdownDTO> plannedShutdowns,
            Boolean excludePlanned,
            Boolean excludeUnplanned) {

        LocalDateTime standInDateTime = standIn.getIntervalStart();

        for (AnagPlannedShutdownDTO shutdown : plannedShutdowns) {
            LocalDateTime shutdownStart = LocalDateTime.ofInstant(shutdown.getShutdownStartDate(), ZoneId.systemDefault());
            LocalDateTime shutdownEnd = LocalDateTime.ofInstant(shutdown.getShutdownEndDate(), ZoneId.systemDefault());

            // Check if the Stand-In event is in the shutdown period
            if (isDateTimeInRange(standInDateTime, shutdownStart, shutdownEnd)) {
                
                // Check shutdown type and exclusion configuration
                if (excludePlanned && shutdown.getTypePlanned() == TypePlanned.PROGRAMMATO) {
                    LOGGER.debug("Excluding Stand-In event {} during planned shutdown", standIn.getId());
                    return true;
                }
                
                if (excludeUnplanned && shutdown.getTypePlanned() == TypePlanned.NON_PROGRAMMATO) {
                    LOGGER.debug("Excluding Stand-In event {} during unplanned shutdown", standIn.getId());
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Checks if a date-time is within a range (inclusive)
     */
    private boolean isDateTimeInRange(LocalDateTime dateTimeToCheck, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return (dateTimeToCheck.isEqual(startDateTime) || dateTimeToCheck.isAfter(startDateTime)) &&
               (dateTimeToCheck.isEqual(endDateTime) || dateTimeToCheck.isBefore(endDateTime));
    }

    /**
     * Calculates KPI B.3 outcome based on Stand-In data and configured threshold
     */
    private OutcomeStatus calculateKpiB3Outcome(
            List<PagopaNumeroStandin> filteredStandInData,
            Double eligibilityThreshold,
            InstanceDTO instanceDTO) {

        try {
            // If no Stand-In events, outcome is OK (Zero Incident achieved)
            if (filteredStandInData == null || filteredStandInData.isEmpty()) {
                LOGGER.info("No Stand-In events found - Zero Incident achieved, outcome OK");
                return OutcomeStatus.OK;
            }

            // Calculate total Stand-In events for all partner stations
            int totalStandInEvents = filteredStandInData.stream()
                .mapToInt(PagopaNumeroStandin::getStandInCount)
                .sum();

            LOGGER.info("Total Stand-In events for partner {} in period {} to {}: {}", 
                       instanceDTO.getPartnerFiscalCode(),
                       instanceDTO.getAnalysisPeriodStartDate(),
                       instanceDTO.getAnalysisPeriodEndDate(),
                       totalStandInEvents);

            // Compare with configured threshold
            // KPI B.3 "Zero Incident": if Stand-In events exceed threshold → KO
            if (totalStandInEvents > eligibilityThreshold) {
                LOGGER.info("Stand-In events {} exceed threshold {} - outcome KO", totalStandInEvents, eligibilityThreshold);
                return OutcomeStatus.KO;
            } else {
                LOGGER.info("Stand-In events {} within threshold {} - outcome OK", totalStandInEvents, eligibilityThreshold);
                return OutcomeStatus.OK;
            }

        } catch (Exception e) {
            LOGGER.error("Error calculating KPI B.3 outcome", e);
            return OutcomeStatus.KO;
        }
    }

    /**
     * Schedules KPI B.3 job execution for a single instance
     * 
     * @param instanceIdentification the instance identification to process
     */
    public void scheduleJobForSingleInstance(String instanceIdentification) throws Exception {
        LOGGER.info("Scheduling KpiB3Job for instance: {}", instanceIdentification);
        
        // Create job detail for this specific instance
        JobDetail jobDetail = JobBuilder.newJob(KpiB3Job.class)
            .withIdentity("KpiB3Job-" + instanceIdentification, "KPI_JOBS")
            .usingJobData("instanceIdentification", instanceIdentification)
            .build();

        // Create trigger to run immediately
        Trigger trigger = TriggerBuilder.newTrigger()
            .withIdentity("KpiB3Trigger-" + instanceIdentification, "KPI_TRIGGERS")
            .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                .withMisfireHandlingInstructionFireNow()
                .withRepeatCount(0))
            .forJob(jobDetail)
            .build();

        // Schedule the job
        scheduler.scheduleJob(jobDetail, trigger);
        LOGGER.info("Successfully scheduled KpiB3Job for instance: {}", instanceIdentification);
    }
}