package com.nexigroup.pagopa.cruscotto.job.kpi.b3;

import com.nexigroup.pagopa.cruscotto.config.ApplicationProperties;
import com.nexigroup.pagopa.cruscotto.domain.AnagStation;
import com.nexigroup.pagopa.cruscotto.domain.PagopaNumeroStandin;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.job.config.JobConstant;

import com.nexigroup.pagopa.cruscotto.repository.AnagStationRepository;
import com.nexigroup.pagopa.cruscotto.repository.AnagPlannedShutdownRepository;
import com.nexigroup.pagopa.cruscotto.repository.PagopaNumeroStandinRepository;

import com.nexigroup.pagopa.cruscotto.service.InstanceService;
import com.nexigroup.pagopa.cruscotto.service.InstanceModuleService;
import com.nexigroup.pagopa.cruscotto.service.KpiConfigurationService;
import com.nexigroup.pagopa.cruscotto.service.KpiB3DataService;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceModuleDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiConfigurationDTO;

import java.util.Map;
import java.time.LocalDate;
import java.time.LocalDateTime;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.lang.NonNull;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Component
@DisallowConcurrentExecution
public class KpiB3Job extends QuartzJobBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(KpiB3Job.class);

    private final InstanceService instanceService;

    private final InstanceModuleService instanceModuleService;

    private final ApplicationProperties applicationProperties;

    private final KpiConfigurationService kpiConfigurationService;

    private final KpiB3DataService kpiB3DataService;

    private final PagopaNumeroStandinRepository pagopaNumeroStandinRepository;
    
    private final AnagStationRepository anagStationRepository;
    
    private final AnagPlannedShutdownRepository anagPlannedShutdownRepository;

    private final Scheduler scheduler;

    @PersistenceContext
    private EntityManager entityManager;
    
    /**
     * DEBUG METHOD: Check Stand-In data availability
     * This method helps debugging the issue with missing analytic data
     */
    public void debugStandInData() {
        LOGGER.info("=== DEBUG: Checking Stand-In data availability ===");
        
        try {
            // Check total records in database
            LocalDateTime startDate = LocalDateTime.of(2025, 9, 1, 0, 0, 0);
            LocalDateTime endDate = LocalDateTime.of(2025, 9, 30, 23, 59, 59);
            
            List<PagopaNumeroStandin> allData = pagopaNumeroStandinRepository.findByDateRange(startDate, endDate);
            LOGGER.info("Total Stand-In records in September 2025: {}", allData.size());
            
            if (!allData.isEmpty()) {
                LOGGER.info("Sample record: Station={}, EventType={}, Count={}, DataDate={}, Interval={} to {}", 
                           allData.get(0).getStationCode(),
                           allData.get(0).getEventType(),
                           allData.get(0).getStandInCount(),
                           allData.get(0).getDataDate(),
                           allData.get(0).getIntervalStart(),
                           allData.get(0).getIntervalEnd());
                           
                // Group by station
                Map<String, List<PagopaNumeroStandin>> byStation = allData.stream()
                    .collect(Collectors.groupingBy(PagopaNumeroStandin::getStationCode));
                
                LOGGER.info("Stations with Stand-In data: {}", byStation.keySet());
                byStation.forEach((station, records) -> 
                    LOGGER.info("Station {}: {} records", station, records.size()));
            }
            
        } catch (Exception e) {
            LOGGER.error("Error in debug method: {}", e.getMessage(), e);
        }
        
        LOGGER.info("=== END DEBUG ===");
    }

    public KpiB3Job(
        InstanceService instanceService,
        InstanceModuleService instanceModuleService,
        ApplicationProperties applicationProperties,
        KpiConfigurationService kpiConfigurationService,
        KpiB3DataService kpiB3DataService,
        PagopaNumeroStandinRepository pagopaNumeroStandinRepository,
        AnagStationRepository anagStationRepository,
        AnagPlannedShutdownRepository anagPlannedShutdownRepository,
        Scheduler scheduler) {
        this.instanceService = instanceService;
        this.instanceModuleService = instanceModuleService;
        this.applicationProperties = applicationProperties;
        this.kpiConfigurationService = kpiConfigurationService;
        this.kpiB3DataService = kpiB3DataService;
        this.pagopaNumeroStandinRepository = pagopaNumeroStandinRepository;
        this.anagStationRepository = anagStationRepository;
        this.anagPlannedShutdownRepository = anagPlannedShutdownRepository;
        this.scheduler = scheduler;
    }

    @Override
    @Transactional
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

                LOGGER.debug("Kpi configuration {}", kpiConfigurationDTO);

                Double eligibilityThreshold = kpiConfigurationDTO.getEligibilityThreshold() != null
                    ? kpiConfigurationDTO.getEligibilityThreshold()
                    : 0.0;
                Double tolerance = kpiConfigurationDTO.getTolerance() != null ? kpiConfigurationDTO.getTolerance() : 0.0;

                instanceDTOS.forEach(instanceDTO -> {
                    try {
                        LOGGER.debug(
                            "Start elaboration instance {} for partner {} - {} with period {} - {}",
                            instanceDTO.getInstanceIdentification(),
                            instanceDTO.getPartnerFiscalCode(),
                            instanceDTO.getPartnerName(),
                            instanceDTO.getAnalysisPeriodStartDate(),
                            instanceDTO.getAnalysisPeriodEndDate()
                        );

                        // Update instance status to IN_ESECUZIONE (like other KPI jobs)
                        instanceService.updateInstanceStatusInProgress(instanceDTO.getId());

                        // Find the instance module first
                        InstanceModuleDTO instanceModuleDTO = instanceModuleService
                            .findOne(instanceDTO.getId(), kpiConfigurationDTO.getModuleId())
                            .orElseThrow(() -> new NullPointerException("KPI B.3 InstanceModule not found"));

                        // Calculate KPI B.3 "Zero Incident" using database-based Stand-In data
                        OutcomeStatus calculatedOutcome = processKpiB3Calculation(instanceDTO, kpiConfigurationDTO, eligibilityThreshold, tolerance);

                        // Update the instance status with the calculated outcome
                        instanceModuleService.updateAutomaticOutcome(instanceModuleDTO.getId(), calculatedOutcome);

                        // Trigger next job in the workflow (like other KPI jobs)
                        JobDetail job = scheduler.getJobDetail(JobKey.jobKey(JobConstant.CALCULATE_STATE_INSTANCE_JOB, "DEFAULT"));
                        Trigger trigger = TriggerBuilder.newTrigger()
                            .usingJobData("instanceId", instanceDTO.getId())
                            .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow().withRepeatCount(0))
                            .forJob(job)
                            .build();
                        scheduler.scheduleJob(trigger);

                        LOGGER.info(
                            "End elaboration instance {} for partner {} - {}",
                            instanceDTO.getInstanceIdentification(),
                            instanceDTO.getPartnerFiscalCode(),
                            instanceDTO.getPartnerName()
                        );

                    } catch (Exception e) {
                        LOGGER.error("Error processing instance {} for KPI B.3: {}", instanceDTO.getInstanceIdentification(), e.getMessage(), e);
                        
                        // Find the instance module to update status in case of error
                        try {
                            InstanceModuleDTO instanceModuleDTO = instanceModuleService
                                .findOne(instanceDTO.getId(), kpiConfigurationDTO.getModuleId())
                                .orElseThrow(() -> new NullPointerException("KPI B.3 InstanceModule not found"));

                            // Update the instance status to KO
                            instanceModuleService.updateAutomaticOutcome(instanceModuleDTO.getId(), OutcomeStatus.KO);
                        } catch (Exception updateException) {
                            LOGGER.error("Error updating outcome status for instance {}: {}", instanceDTO.getInstanceIdentification(), updateException.getMessage());
                        }
                    }
                });
            }
        } catch (Exception e) {
            LOGGER.error("Error in KPI B.3 job execution: {}", e.getMessage(), e);
        }

        LOGGER.info("End calculate kpi B.3");
    }

    /**
     * Processes KPI B.3 "Zero Incident" calculation for a specific instance
     * 
     * @param instanceDTO the instance to process
     * @param kpiConfigurationDTO the KPI configuration
     * @param eligibilityThreshold the eligibility threshold
     * @param tolerance the tolerance
     * @return the KPI calculation outcome (OK if zero incidents, KO otherwise)
     */
    private OutcomeStatus processKpiB3Calculation(InstanceDTO instanceDTO, KpiConfigurationDTO kpiConfigurationDTO, 
                                                 Double eligibilityThreshold, Double tolerance) {
        
        LOGGER.debug("Processing KPI B.3 'Zero Incident' calculation for instance: {}", instanceDTO.getInstanceIdentification());
        
        try {
            // Convert analysis period to LocalDateTime for database queries
            LocalDateTime analysisStart = instanceDTO.getAnalysisPeriodStartDate().atStartOfDay();
            LocalDateTime analysisEnd = instanceDTO.getAnalysisPeriodEndDate().atTime(23, 59, 59);
            
            // Query Stand-In data from pagopa_numero_standin table
            LOGGER.debug("Querying Stand-In data for partner {} from {} to {}", 
                       instanceDTO.getPartnerFiscalCode(), analysisStart, analysisEnd);
            
            List<PagopaNumeroStandin> standInData = pagopaNumeroStandinRepository.findByDateRange(
                analysisStart,
                analysisEnd
            );
            
            LOGGER.info("Retrieved {} total Stand-In records from database for period {} to {}", 
                       standInData != null ? standInData.size() : 0, analysisStart, analysisEnd);
            
            // Calculate KPI B.3: Zero Incident based on aggregated Stand-In data
            // Filter data by partner's stations and exclude planned shutdowns if configured
            List<PagopaNumeroStandin> partnerStandInData = filterStandInDataByPartner(
                standInData, instanceDTO.getPartnerFiscalCode(), analysisStart, analysisEnd, kpiConfigurationDTO);
            
            // Calculate total stand-in incidents count
            int totalStandIn = partnerStandInData.stream()
                .mapToInt(PagopaNumeroStandin::getStandInCount)
                .sum();
            
            boolean hasIncidents = totalStandIn > 0;
            
            LOGGER.info("Found {} stand-in events for partner {} in period {} - {}", 
                       totalStandIn, instanceDTO.getPartnerFiscalCode(), analysisStart, analysisEnd);
            
            // Log details for debugging
            if (hasIncidents && LOGGER.isDebugEnabled()) {
                partnerStandInData.forEach(data -> 
                    LOGGER.debug("Stand-in data: stationCode={}, interval={} to {}, count={}, eventType={}", 
                               data.getStationCode(), data.getIntervalStart(), data.getIntervalEnd(), 
                               data.getStandInCount(), data.getEventType())
                );
            }
            
            // Determine KPI B.3 outcome
            // KPI B.3 "Zero Incident": OK if no stand-in events, KO otherwise
            OutcomeStatus outcome = hasIncidents ? OutcomeStatus.KO : OutcomeStatus.OK;
            
            LOGGER.info("KPI B.3 calculation result for instance {}: {} stand-in incidents found, outcome: {}", 
                       instanceDTO.getInstanceIdentification(), totalStandIn, outcome);
            
            // Save results in the three tables (Result, DetailResult, AnalyticData)
            // Use current date as analysis date (when the analysis is performed)
            // Note: instanceModuleDTO is passed from the caller to avoid duplicate queries
            InstanceModuleDTO instanceModuleDTO = instanceModuleService
                .findOne(instanceDTO.getId(), kpiConfigurationDTO.getModuleId())
                .orElseThrow(() -> new NullPointerException("KPI B.3 InstanceModule not found"));
            
            kpiB3DataService.saveKpiB3Results(instanceDTO, instanceModuleDTO, kpiConfigurationDTO, 
                                            LocalDate.now(), outcome, partnerStandInData);
            
            LOGGER.info("KPI B.3 'Zero Incident' calculation completed for instance: {} with outcome: {}", 
                       instanceDTO.getInstanceIdentification(), outcome);
            
            return outcome;
            
        } catch (Exception e) {
            LOGGER.error("Error in KPI B.3 calculation for instance {}: {}", 
                        instanceDTO.getInstanceIdentification(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Schedules a single job for a specific instance
     */
    public void scheduleJobForSingleInstance(String instanceIdentification) {
        try {
            LOGGER.info("Scheduling single KPI B.3 job for instance: {}", instanceIdentification);
            
            JobDetail jobDetail = org.quartz.JobBuilder.newJob(KpiB3Job.class)
                .withIdentity("kpiB3Job_" + instanceIdentification, "SINGLE_INSTANCE")
                .build();

            Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("kpiB3JobTrigger_" + instanceIdentification, "SINGLE_INSTANCE")
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withRepeatCount(0))
                .startNow()
                .build();

            scheduler.scheduleJob(jobDetail, trigger);
            
            LOGGER.info("Single KPI B.3 job scheduled successfully for instance: {}", instanceIdentification);
            
        } catch (Exception e) {
            LOGGER.error("Error scheduling single KPI B.3 job for instance {}: {}", 
                        instanceIdentification, e.getMessage(), e);
        }
    }





    /**
     * Filters Stand-In data by partner's fiscal code and excludes planned shutdowns if configured.
     * 
     * @param standInData All Stand-In data for the analysis period
     * @param partnerFiscalCode The partner's fiscal code to filter by
     * @param analysisStart Start of analysis period
     * @param analysisEnd End of analysis period
     * @param kpiConfigurationDTO KPI configuration with shutdown exclusion settings
     * @return Filtered Stand-In data for the partner
     */
    private List<PagopaNumeroStandin> filterStandInDataByPartner(List<PagopaNumeroStandin> standInData, 
                                                               String partnerFiscalCode, 
                                                               LocalDateTime analysisStart, 
                                                               LocalDateTime analysisEnd,
                                                               KpiConfigurationDTO kpiConfigurationDTO) {
        
        LOGGER.debug("Filtering Stand-In data for partner: {}", partnerFiscalCode);
        
        try {
            // Get stations for this specific partner using optimized query
            List<AnagStation> partnerStations = anagStationRepository.findByAnagPartnerFiscalCode(partnerFiscalCode);
            
            if (partnerStations.isEmpty()) {
                LOGGER.warn("No stations found for partner: {}", partnerFiscalCode);
                return Collections.emptyList();
            }
            
            Set<String> partnerStationNames = partnerStations.stream()
                .map(AnagStation::getName)
                .collect(Collectors.toSet());
            
            LOGGER.debug("Found {} stations for partner {}: {}", 
                       partnerStations.size(), partnerFiscalCode, partnerStationNames);
            
            // Filter Stand-In data by partner station names
            List<PagopaNumeroStandin> partnerData = standInData.stream()
                .filter(data -> partnerStationNames.contains(data.getStationCode()))
                .collect(Collectors.toList());
            
            LOGGER.debug("Filtered {} Stand-In records for partner {} before shutdown exclusion", partnerData.size(), partnerFiscalCode);
            
            // Apply planned shutdown exclusion if configured
            if (kpiConfigurationDTO.getExcludePlannedShutdown() || kpiConfigurationDTO.getExcludeUnplannedShutdown()) {
                partnerData = excludePlannedShutdowns(partnerData, partnerFiscalCode, analysisStart, analysisEnd, kpiConfigurationDTO);
                LOGGER.debug("Filtered {} Stand-In records for partner {} after shutdown exclusion", partnerData.size(), partnerFiscalCode);
            }
            
            return partnerData;
            
        } catch (Exception e) {
            LOGGER.error("Error filtering Stand-In data for partner {}: {}", partnerFiscalCode, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * Excludes Stand-In data that occurred during planned/unplanned shutdowns if configured.
     * According to the analysis document, if there's no detailed time info, exclude the entire day.
     * 
     * @param standInData Stand-In data to filter
     * @param partnerFiscalCode Partner's fiscal code
     * @param analysisStart Analysis period start
     * @param analysisEnd Analysis period end
     * @param kpiConfigurationDTO KPI configuration
     * @return Filtered Stand-In data excluding shutdown periods
     */
    private List<PagopaNumeroStandin> excludePlannedShutdowns(List<PagopaNumeroStandin> standInData, 
                                                            String partnerFiscalCode,
                                                            LocalDateTime analysisStart, 
                                                            LocalDateTime analysisEnd,
                                                            KpiConfigurationDTO kpiConfigurationDTO) {
        try {
            // Query planned shutdowns for the analysis period
            // Convert LocalDateTime to Instant for database query
            java.time.Instant startInstant = analysisStart.atZone(java.time.ZoneId.systemDefault()).toInstant();
            java.time.Instant endInstant = analysisEnd.atZone(java.time.ZoneId.systemDefault()).toInstant();
            
            // Find all planned shutdowns that overlap with the analysis period
            List<com.nexigroup.pagopa.cruscotto.domain.AnagPlannedShutdown> shutdowns = 
                anagPlannedShutdownRepository.findAll().stream()
                .filter(shutdown -> 
                    // Check if shutdown period overlaps with analysis period
                    (shutdown.getShutdownStartDate().isBefore(endInstant) || shutdown.getShutdownStartDate().equals(endInstant)) &&
                    (shutdown.getShutdownEndDate().isAfter(startInstant) || shutdown.getShutdownEndDate().equals(startInstant))
                )
                .collect(Collectors.toList());
            
            if (shutdowns.isEmpty()) {
                LOGGER.debug("No planned shutdowns found for analysis period");
                return standInData;
            }
            
            LOGGER.debug("Found {} planned shutdowns overlapping with analysis period", shutdowns.size());
            
            // Filter out Stand-In data that occurred during shutdown periods
            return standInData.stream()
                .filter(data -> {
                    // Check if this Stand-In data occurred during any shutdown
                    boolean duringShutdown = shutdowns.stream()
                        .anyMatch(shutdown -> {
                            boolean isPlannedShutdown = shutdown.getTypePlanned() == com.nexigroup.pagopa.cruscotto.domain.enumeration.TypePlanned.PROGRAMMATO;
                            boolean shouldExclude = (isPlannedShutdown && kpiConfigurationDTO.getExcludePlannedShutdown()) ||
                                                  (!isPlannedShutdown && kpiConfigurationDTO.getExcludeUnplannedShutdown());
                            
                            if (shouldExclude) {
                                // Check if data time falls within shutdown period
                                // According to analysis doc: if no detailed time, exclude entire day
                                java.time.LocalDate dataDate = data.getIntervalStart().toLocalDate();
                                java.time.LocalDate shutdownStartDate = shutdown.getShutdownStartDate().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                                java.time.LocalDate shutdownEndDate = shutdown.getShutdownEndDate().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                                
                                return !dataDate.isBefore(shutdownStartDate) && !dataDate.isAfter(shutdownEndDate);
                            }
                            
                            return false;
                        });
                    
                    return !duringShutdown; // Keep data that is NOT during shutdown
                })
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            LOGGER.error("Error excluding planned shutdowns for partner {}: {}", partnerFiscalCode, e.getMessage(), e);
            return standInData; // Return original data if error occurs
        }
    }

}