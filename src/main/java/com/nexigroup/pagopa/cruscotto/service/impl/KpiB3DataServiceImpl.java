package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.InstanceModule;
import com.nexigroup.pagopa.cruscotto.domain.KpiB3Result;
import com.nexigroup.pagopa.cruscotto.domain.KpiB3DetailResult;
import com.nexigroup.pagopa.cruscotto.domain.KpiB3AnalyticData;
import com.nexigroup.pagopa.cruscotto.domain.PagopaNumeroStandin;
import com.nexigroup.pagopa.cruscotto.domain.AnagStation;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.repository.InstanceRepository;
import com.nexigroup.pagopa.cruscotto.repository.InstanceModuleRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiB3ResultRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiB3DetailResultRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiB3AnalyticDataRepository;
import com.nexigroup.pagopa.cruscotto.repository.AnagStationRepository;
import com.nexigroup.pagopa.cruscotto.service.KpiB3DataService;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceModuleDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiConfigurationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class KpiB3DataServiceImpl implements KpiB3DataService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KpiB3DataServiceImpl.class);

    private final KpiB3ResultRepository kpiB3ResultRepository;
    private final KpiB3DetailResultRepository kpiB3DetailResultRepository;
    private final KpiB3AnalyticDataRepository kpiB3AnalyticDataRepository;
    private final InstanceRepository instanceRepository;
    private final InstanceModuleRepository instanceModuleRepository;
    private final AnagStationRepository anagStationRepository;

    public KpiB3DataServiceImpl(
        KpiB3ResultRepository kpiB3ResultRepository,
        KpiB3DetailResultRepository kpiB3DetailResultRepository,
        KpiB3AnalyticDataRepository kpiB3AnalyticDataRepository,
        InstanceRepository instanceRepository,
        InstanceModuleRepository instanceModuleRepository,
        AnagStationRepository anagStationRepository
    ) {
        this.kpiB3ResultRepository = kpiB3ResultRepository;
        this.kpiB3DetailResultRepository = kpiB3DetailResultRepository;
        this.kpiB3AnalyticDataRepository = kpiB3AnalyticDataRepository;
        this.instanceRepository = instanceRepository;
        this.instanceModuleRepository = instanceModuleRepository;
        this.anagStationRepository = anagStationRepository;
    }

    @Override
    @Transactional
    public void saveKpiB3Results(InstanceDTO instanceDTO, InstanceModuleDTO instanceModuleDTO, 
                                KpiConfigurationDTO kpiConfigurationDTO, LocalDate analysisDate, 
                                OutcomeStatus outcome, List<PagopaNumeroStandin> standInData) {
        try {
            LOGGER.info("Starting saveKpiB3Results for instance {} with {} Stand-In data records", 
                       instanceDTO.getInstanceIdentification(), 
                       standInData != null ? standInData.size() : 0);
            
            // Log some sample Stand-In data for debugging
            if (standInData != null && !standInData.isEmpty()) {
                LOGGER.info("Sample Stand-In data - First record: ID={}, StationCode={}, EventType={}, Count={}, Period={} to {}", 
                           standInData.get(0).getId(), 
                           standInData.get(0).getStationCode(),
                           standInData.get(0).getEventType(),
                           standInData.get(0).getStandInCount(),
                           standInData.get(0).getIntervalStart(),
                           standInData.get(0).getIntervalEnd());
            }
            
            // First delete previous results for this instanceModule
            kpiB3ResultRepository.deleteAllByInstanceModuleId(instanceModuleDTO.getId());
            kpiB3DetailResultRepository.deleteAllByInstanceModuleId(instanceModuleDTO.getId());
            kpiB3AnalyticDataRepository.deleteAllByInstanceModuleId(instanceModuleDTO.getId());

            // Load entities from database
            Instance instance = instanceRepository
                .findById(instanceModuleDTO.getInstanceId())
                .orElseThrow(() -> new IllegalArgumentException("Instance not found"));

            InstanceModule instanceModule = instanceModuleRepository
                .findById(instanceModuleDTO.getId())
                .orElseThrow(() -> new IllegalArgumentException("InstanceModule not found"));

            // 1. Save the main result
            KpiB3Result kpiResult = saveKpiB3Result(instance, instanceModule, kpiConfigurationDTO, analysisDate, outcome);

            // 2. Save station details for ALL stations of the partner (with or without Stand-In events)
            // This is required according to the analysis document
            saveKpiB3DetailResults(instance, instanceModule, kpiResult, analysisDate, standInData, instanceDTO.getPartnerFiscalCode());

            LOGGER.info("Successfully saved KPI B.3 results for instance {} with {} Stand-In records", 
                       instanceDTO.getInstanceIdentification(), 
                       standInData != null ? standInData.size() : 0);

        } catch (Exception e) {
            LOGGER.error("Error saving KPI B.3 results for instance {}: {}", 
                        instanceDTO.getInstanceIdentification(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Saves the main KPI B.3 result
     */
    private KpiB3Result saveKpiB3Result(Instance instance, InstanceModule instanceModule, 
                                       KpiConfigurationDTO kpiConfigurationDTO, LocalDate analysisDate, 
                                       OutcomeStatus outcome) {
        
        // Create the new result
        KpiB3Result kpiResult = new KpiB3Result();
        kpiResult.setInstance(instance);
        kpiResult.setInstanceModule(instanceModule);
        kpiResult.setAnalysisDate(analysisDate);
        kpiResult.setOutcome(outcome);
        
        // Set configuration values
        kpiResult.setEligibilityThreshold(kpiConfigurationDTO.getEligibilityThreshold());
        kpiResult.setTolerance(kpiConfigurationDTO.getTolerance());
        kpiResult.setEvaluationType(kpiConfigurationDTO.getEvaluationType());
        kpiResult.setExcludePlannedShutdown(kpiConfigurationDTO.getExcludePlannedShutdown());
        kpiResult.setExcludeUnplannedShutdown(kpiConfigurationDTO.getExcludeUnplannedShutdown());
        
        return kpiB3ResultRepository.save(kpiResult);
    }

    /**
     * Saves KPI B.3 detail results and analytic data for each station of the partner
     */
    private void saveKpiB3DetailResults(Instance instance, InstanceModule instanceModule, 
                                       KpiB3Result kpiResult, LocalDate analysisDate, 
                                       List<PagopaNumeroStandin> standInData, String partnerFiscalCode) {
        
        // Get ALL stations for this partner
        List<AnagStation> partnerStations = anagStationRepository.findByAnagPartnerFiscalCode(partnerFiscalCode);
        
        if (partnerStations.isEmpty()) {
            LOGGER.warn("No stations found for partner: {}", partnerFiscalCode);
            return;
        }
        
        // Group stand-in data by station code (may be empty for stations with no incidents)
        Map<String, List<PagopaNumeroStandin>> standInByStation = standInData != null ? 
            standInData.stream().collect(Collectors.groupingBy(PagopaNumeroStandin::getStationCode)) : 
            Collections.emptyMap();
        
        LOGGER.debug("Stand-In data grouped by station: {} stations have data", standInByStation.size());
        standInByStation.forEach((stationCode, records) -> 
            LOGGER.debug("Station {} has {} Stand-In records", stationCode, records.size()));
        
        LOGGER.debug("Partner {} has {} stations", partnerFiscalCode, partnerStations.size());
        partnerStations.forEach(station -> 
            LOGGER.debug("Partner station: {}", station.getName()));
        
        // Create detail results for ALL partner stations, regardless of whether they have Stand-In events
        for (AnagStation station : partnerStations) {
            String stationCode = station.getName();
            
            // Get Stand-In data for this specific station (may be empty)
            List<PagopaNumeroStandin> stationStandInData = standInByStation.getOrDefault(stationCode, Collections.emptyList());
            
            LOGGER.debug("Processing station {}: found {} Stand-In records", stationCode, stationStandInData.size());
            
            // Count total incidents and events for this station
            int totalIncidents = stationStandInData.stream()
                .mapToInt(PagopaNumeroStandin::getStandInCount)
                .sum();
            int totalEvents = stationStandInData.size(); // Number of Stand-In records for this station
            
            // Create detail result for this station
            KpiB3DetailResult detailResult = new KpiB3DetailResult();
            detailResult.setInstance(instance);
            detailResult.setInstanceModule(instanceModule);
            detailResult.setAnagStation(station);
            detailResult.setKpiB3Result(kpiResult);
            detailResult.setAnalysisDate(analysisDate);
            detailResult.setTotalIncidents(totalIncidents);
            detailResult.setTotalEvents(totalEvents);
            detailResult.setOutcome(totalIncidents == 0); // Boolean: true = OK (zero incidents), false = KO
            
            KpiB3DetailResult savedDetailResult = kpiB3DetailResultRepository.save(detailResult);
            
            LOGGER.debug("Saved KPI B.3 detail result for station {}: {} incidents, {} events, outcome: {}", 
                        station.getName(), totalIncidents, totalEvents, totalIncidents == 0 ? "OK" : "KO");
            
            // Save analytic data only for stations that have actual stand-in events
            LOGGER.debug("Station {} has {} stand-in records", station.getName(), stationStandInData.size());
            
            if (!stationStandInData.isEmpty()) {
                LOGGER.info("Creating analytic data for station {} with {} stand-in events", 
                           station.getName(), stationStandInData.size());
                
                for (PagopaNumeroStandin standIn : stationStandInData) {
                    LOGGER.debug("Processing stand-in event: ID={}, EventType={}, StationCode={}, Count={}", 
                                standIn.getId(), standIn.getEventType(), standIn.getStationCode(), standIn.getStandInCount());
                    
                    KpiB3AnalyticData analyticData = new KpiB3AnalyticData();
                    analyticData.setInstance(instance);
                    analyticData.setInstanceModule(instanceModule);
                    analyticData.setAnagStation(station);
                    analyticData.setKpiB3DetailResult(savedDetailResult);
                    analyticData.setEventId(standIn.getId().toString()); // Use ID as event ID
                    analyticData.setEventType(standIn.getEventType());
                    analyticData.setEventTimestamp(standIn.getIntervalStart());
                    analyticData.setStandInCount(standIn.getStandInCount());
                    
                    KpiB3AnalyticData savedAnalyticData = kpiB3AnalyticDataRepository.save(analyticData);
                    LOGGER.debug("Saved analytic data with ID: {}", savedAnalyticData.getId());
                }
                
                LOGGER.info("Successfully saved {} KPI B.3 analytic data records for station {}", 
                           stationStandInData.size(), station.getName());
            } else {
                LOGGER.debug("No stand-in data found for station {}, skipping analytic data creation", 
                           station.getName());
            }
        }
    }
}