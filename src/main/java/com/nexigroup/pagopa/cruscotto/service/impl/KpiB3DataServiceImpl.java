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
import com.nexigroup.pagopa.cruscotto.service.PagopaNumeroStandinDrilldownService;
import com.nexigroup.pagopa.cruscotto.service.KpiB3ResultService;
import com.nexigroup.pagopa.cruscotto.service.KpiB3DetailResultService;
import com.nexigroup.pagopa.cruscotto.service.KpiB3AnalyticDataService;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceModuleDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiConfigurationDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB3ResultDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB3DetailResultDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB3AnalyticDataDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
    private final PagopaNumeroStandinDrilldownService pagopaNumeroStandinDrilldownService;
    private final KpiB3ResultService kpiB3ResultService;
    private final KpiB3DetailResultService kpiB3DetailResultService;
    private final KpiB3AnalyticDataService kpiB3AnalyticDataService;

    public KpiB3DataServiceImpl(
        KpiB3ResultRepository kpiB3ResultRepository,
        KpiB3DetailResultRepository kpiB3DetailResultRepository,
        KpiB3AnalyticDataRepository kpiB3AnalyticDataRepository,
        InstanceRepository instanceRepository,
        InstanceModuleRepository instanceModuleRepository,
        AnagStationRepository anagStationRepository,
        PagopaNumeroStandinDrilldownService pagopaNumeroStandinDrilldownService,
        KpiB3ResultService kpiB3ResultService,
        KpiB3DetailResultService kpiB3DetailResultService,
        KpiB3AnalyticDataService kpiB3AnalyticDataService
    ) {
        this.kpiB3ResultRepository = kpiB3ResultRepository;
        this.kpiB3DetailResultRepository = kpiB3DetailResultRepository;
        this.kpiB3AnalyticDataRepository = kpiB3AnalyticDataRepository;
        this.instanceRepository = instanceRepository;
        this.instanceModuleRepository = instanceModuleRepository;
        this.anagStationRepository = anagStationRepository;
        this.pagopaNumeroStandinDrilldownService = pagopaNumeroStandinDrilldownService;
        this.kpiB3ResultService = kpiB3ResultService;
        this.kpiB3DetailResultService = kpiB3DetailResultService;
        this.kpiB3AnalyticDataService = kpiB3AnalyticDataService;
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
            pagopaNumeroStandinDrilldownService.deleteAllByInstanceModuleId(instanceModuleDTO.getId());
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
            saveKpiB3DetailResults(instance, instanceModule, kpiResult, analysisDate, standInData, 
                                 instanceDTO.getPartnerFiscalCode(), instanceDTO);

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
     * Saves KPI B.3 detail results aggregated at PARTNER level (not per station)
     * Creates separate results for monthly and total evaluations with correct period dates
     * KPI B.3 is evaluated at partner level, so we create aggregated records across all partner stations
     */
    private void saveKpiB3DetailResults(Instance instance, InstanceModule instanceModule, 
                                       KpiB3Result kpiResult, LocalDate analysisDate, 
                                       List<PagopaNumeroStandin> standInData, String partnerFiscalCode,
                                       InstanceDTO instanceDTO) {
        
        // Get ALL stations for this partner (for validation/logging)
        List<AnagStation> partnerStations = anagStationRepository.findByAnagPartnerFiscalCode(partnerFiscalCode);
        
        if (partnerStations.isEmpty()) {
            LOGGER.debug("No stations found for partner: {} - Creating detail results with zero Stand-In events", partnerFiscalCode);
        } else {
            LOGGER.debug("Processing KPI B.3 for partner {} with {} stations", partnerFiscalCode, partnerStations.size());
        }
        
        // Get eligibility threshold from the result configuration
        int eligibilityThreshold = kpiResult.getEligibilityThreshold() != null ? 
                                 kpiResult.getEligibilityThreshold().intValue() : 0;
        
        // Group stand-in data by month (aggregated across all stations of the partner)
        Map<YearMonth, Integer> standInByMonth = new HashMap<>();
        
        if (standInData != null && !standInData.isEmpty()) {
            standInData.forEach(standIn -> {
                YearMonth month = YearMonth.from(standIn.getIntervalStart().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
                standInByMonth.merge(month, standIn.getStandInCount(), Integer::sum);
            });
        }
        
        LOGGER.debug("Stand-In data grouped by month: {} months have data", standInByMonth.size());
        
        // Get all months in the analysis period
        List<YearMonth> monthsInPeriod = getMonthsInPeriod(instanceDTO.getAnalysisPeriodStartDate(), 
                                                           instanceDTO.getAnalysisPeriodEndDate());
        
        int totalStandInAllMonths = 0;
        Map<YearMonth, KpiB3DetailResult> savedMonthlyResults = new HashMap<>();
        
        // Create monthly detail results (one per month, aggregated for the entire partner)
        for (YearMonth yearMonth : monthsInPeriod) {
            LocalDate monthStart = yearMonth.atDay(1);
            LocalDate monthEnd = yearMonth.atEndOfMonth();
            
            // Ensure dates are within analysis period
            if (monthStart.isBefore(instanceDTO.getAnalysisPeriodStartDate())) {
                monthStart = instanceDTO.getAnalysisPeriodStartDate();
            }
            if (monthEnd.isAfter(instanceDTO.getAnalysisPeriodEndDate())) {
                monthEnd = instanceDTO.getAnalysisPeriodEndDate();
            }
            
            // Get aggregated stand-in count for this month across all partner stations
            int monthlyStandInCount = standInByMonth.getOrDefault(yearMonth, 0);
            totalStandInAllMonths += monthlyStandInCount;
            
            // Create monthly detail result (PARTNER LEVEL - no specific station)
            KpiB3DetailResult monthlyDetailResult = new KpiB3DetailResult();
            monthlyDetailResult.setInstance(instance);
            monthlyDetailResult.setInstanceModule(instanceModule);
            // No anagStation field - this represents partner-level aggregated data
            monthlyDetailResult.setKpiB3Result(kpiResult);
            monthlyDetailResult.setAnalysisDate(analysisDate);
            monthlyDetailResult.setEvaluationType(com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType.MESE);
            monthlyDetailResult.setEvaluationStartDate(monthStart);
            monthlyDetailResult.setEvaluationEndDate(monthEnd);
            monthlyDetailResult.setTotalStandIn(monthlyStandInCount);
            // Fixed bug: compare against eligibility threshold instead of just checking if zero
            monthlyDetailResult.setOutcome(monthlyStandInCount <= eligibilityThreshold ? 
                                         OutcomeStatus.OK : OutcomeStatus.KO);
            
            KpiB3DetailResult savedMonthlyResult = kpiB3DetailResultRepository.save(monthlyDetailResult);
            savedMonthlyResults.put(yearMonth, savedMonthlyResult);
            
            // Calculate outcome for logging
            boolean monthlyOutcomeOK = monthlyStandInCount <= eligibilityThreshold;
            LOGGER.info("Saved monthly result for partner {} in {}: {} stand-ins (threshold: {}), outcome: {}", 
                        partnerFiscalCode, yearMonth, monthlyStandInCount, 
                        eligibilityThreshold, monthlyOutcomeOK ? "OK" : "KO");
        }
        
        // Create total detail result (entire analysis period, partner level)
        KpiB3DetailResult totalDetailResult = new KpiB3DetailResult();
        totalDetailResult.setInstance(instance);
        totalDetailResult.setInstanceModule(instanceModule);
        // No anagStation field - this represents partner-level aggregated data
        totalDetailResult.setKpiB3Result(kpiResult);
        totalDetailResult.setAnalysisDate(analysisDate);
        totalDetailResult.setEvaluationType(com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType.TOTALE);
        totalDetailResult.setEvaluationStartDate(instanceDTO.getAnalysisPeriodStartDate());
        totalDetailResult.setEvaluationEndDate(instanceDTO.getAnalysisPeriodEndDate());
        totalDetailResult.setTotalStandIn(totalStandInAllMonths);
        // Fixed bug: compare against eligibility threshold instead of just checking if zero
        totalDetailResult.setOutcome(totalStandInAllMonths <= eligibilityThreshold ? 
                                   OutcomeStatus.OK : OutcomeStatus.KO);
        
        KpiB3DetailResult savedTotalResult = kpiB3DetailResultRepository.save(totalDetailResult);
        
        boolean totalOutcomeOK = totalStandInAllMonths <= eligibilityThreshold;
        LOGGER.info("Saved total result for partner {}: {} stand-ins (threshold: {}), outcome: {}", 
                    partnerFiscalCode, totalStandInAllMonths, eligibilityThreshold, totalOutcomeOK ? "OK" : "KO");
        
        LOGGER.info("Saved {} detail results (3 monthly + 1 total) for partner {} with {} stations", 
                   monthsInPeriod.size() + 1, partnerFiscalCode, partnerStations.size());
        
        // Save analytic data for all Stand-In events (aggregated by station)
        if (standInData != null && !standInData.isEmpty()) {
            LOGGER.info("Creating aggregated analytic data for {} stand-in events", standInData.size());
            
            // Group Stand-In data by station for aggregation
            Map<String, List<PagopaNumeroStandin>> standInByStation = new HashMap<>();
            for (PagopaNumeroStandin standIn : standInData) {
                standInByStation.computeIfAbsent(standIn.getStationCode(), k -> new ArrayList<>()).add(standIn);
            }
            
            LOGGER.info("Stand-In data grouped into {} unique stations for aggregation", standInByStation.size());
            
            for (Map.Entry<String, List<PagopaNumeroStandin>> stationEntry : standInByStation.entrySet()) {
                String stationCode = stationEntry.getKey();
                List<PagopaNumeroStandin> stationStandInData = stationEntry.getValue();
                
                // Find the station by station code
                AnagStation station = partnerStations.stream()
                    .filter(s -> s.getName().equals(stationCode))
                    .findFirst()
                    .orElse(null);
                
                if (station == null) {
                    LOGGER.warn("Station not found for station code: {}", stationCode);
                    continue;
                }
                
                // Aggregate Stand-In data by month for this station
                Map<YearMonth, AggregatedStandInData> monthlyAggregations = new HashMap<>();
                int totalStationStandInCount = 0;
                java.time.LocalDateTime latestEventTimestamp = null;
                
                for (PagopaNumeroStandin standIn : stationStandInData) {
                    YearMonth eventMonth = YearMonth.from(standIn.getIntervalStart());
                    
                    AggregatedStandInData monthlyAgg = monthlyAggregations.computeIfAbsent(eventMonth, 
                        k -> new AggregatedStandInData());
                    monthlyAgg.totalCount += standIn.getStandInCount();
                    monthlyAgg.eventIds.add(standIn.getId().toString());
                    
                    java.time.LocalDateTime eventDateTime = standIn.getIntervalStart();
                    if (monthlyAgg.latestTimestamp == null || 
                        eventDateTime.isAfter(monthlyAgg.latestTimestamp)) {
                        monthlyAgg.latestTimestamp = eventDateTime;
                    }
                    
                    totalStationStandInCount += standIn.getStandInCount();
                    
                    if (latestEventTimestamp == null || eventDateTime.isAfter(latestEventTimestamp)) {
                        latestEventTimestamp = eventDateTime;
                    }
                }
                
                // Create monthly aggregated analytic data
                for (Map.Entry<YearMonth, AggregatedStandInData> monthEntry : monthlyAggregations.entrySet()) {
                    YearMonth eventMonth = monthEntry.getKey();
                    AggregatedStandInData aggData = monthEntry.getValue();
                    KpiB3DetailResult monthlyResult = savedMonthlyResults.get(eventMonth);
                    
                    if (monthlyResult != null) {
                        KpiB3AnalyticData monthlyAnalyticData = new KpiB3AnalyticData();
                        monthlyAnalyticData.setInstance(instance);
                        monthlyAnalyticData.setInstanceModule(instanceModule);
                        monthlyAnalyticData.setAnagStation(station);
                        monthlyAnalyticData.setKpiB3DetailResult(monthlyResult);
                        monthlyAnalyticData.setEventId(String.join(",", aggData.eventIds));
                        monthlyAnalyticData.setEventType("ADD_TO_STANDIN");
                        monthlyAnalyticData.setEventTimestamp(aggData.latestTimestamp);
                        monthlyAnalyticData.setStandInCount(aggData.totalCount);
                        
                        KpiB3AnalyticData savedMonthlyAnalyticData = kpiB3AnalyticDataRepository.save(monthlyAnalyticData);
                        LOGGER.debug("Saved aggregated monthly analytic data with ID: {} for station {} in month: {} (count: {})", 
                                   savedMonthlyAnalyticData.getId(), stationCode, eventMonth, aggData.totalCount);
                        
                        // Save drilldown snapshot for monthly analytic data
                        List<PagopaNumeroStandin> monthlyStandInForStation = stationStandInData.stream()
                            .filter(s -> YearMonth.from(s.getIntervalStart()).equals(eventMonth))
                            .collect(java.util.stream.Collectors.toList());
                        
                        pagopaNumeroStandinDrilldownService.saveStandInSnapshot(
                            instance, instanceModule, station, savedMonthlyAnalyticData, 
                            analysisDate, monthlyStandInForStation
                        );
                    }
                }
                
                // Create total aggregated analytic data for this station
                KpiB3AnalyticData totalAnalyticData = new KpiB3AnalyticData();
                totalAnalyticData.setInstance(instance);
                totalAnalyticData.setInstanceModule(instanceModule);
                totalAnalyticData.setAnagStation(station);
                totalAnalyticData.setKpiB3DetailResult(savedTotalResult);
                
                // Aggregate all event IDs for this station
                List<String> allEventIds = stationStandInData.stream()
                    .map(s -> s.getId().toString())
                    .collect(java.util.stream.Collectors.toList());
                totalAnalyticData.setEventId(String.join(",", allEventIds));
                
                totalAnalyticData.setEventType("ADD_TO_STANDIN");
                totalAnalyticData.setEventTimestamp(latestEventTimestamp);
                totalAnalyticData.setStandInCount(totalStationStandInCount);
                
                KpiB3AnalyticData savedTotalAnalyticData = kpiB3AnalyticDataRepository.save(totalAnalyticData);
                LOGGER.debug("Saved aggregated total analytic data with ID: {} for station {} (count: {})", 
                           savedTotalAnalyticData.getId(), stationCode, totalStationStandInCount);
                
                // Save drilldown snapshot for total analytic data
                pagopaNumeroStandinDrilldownService.saveStandInSnapshot(
                    instance, instanceModule, station, savedTotalAnalyticData, 
                    analysisDate, stationStandInData
                );
            }
            
            LOGGER.info("Successfully saved {} aggregated KPI B.3 analytic data records for {} stations", 
                       standInByStation.size() * 2, standInByStation.size());
        }
    }
    
    @Override
    @Transactional
    public KpiB3ResultDTO createKpiB3Result(InstanceDTO instanceDTO, InstanceModuleDTO instanceModuleDTO, 
                                           KpiConfigurationDTO kpiConfigurationDTO, LocalDate analysisDate) {
        
        // Delete previous results for this instanceModule
        pagopaNumeroStandinDrilldownService.deleteAllByInstanceModuleId(instanceModuleDTO.getId());
        kpiB3ResultRepository.deleteAllByInstanceModuleId(instanceModuleDTO.getId());
        kpiB3DetailResultRepository.deleteAllByInstanceModuleId(instanceModuleDTO.getId());
        kpiB3AnalyticDataRepository.deleteAllByInstanceModuleId(instanceModuleDTO.getId());
        
        // Create KPI B.3 result DTO
        KpiB3ResultDTO kpiB3ResultDTO = new KpiB3ResultDTO();
        kpiB3ResultDTO.setInstanceId(instanceDTO.getId());
        kpiB3ResultDTO.setInstanceModuleId(instanceModuleDTO.getId());
        kpiB3ResultDTO.setAnalysisDate(analysisDate);
        kpiB3ResultDTO.setExcludePlannedShutdown(kpiConfigurationDTO.getExcludePlannedShutdown());
        kpiB3ResultDTO.setExcludeUnplannedShutdown(kpiConfigurationDTO.getExcludeUnplannedShutdown());
        kpiB3ResultDTO.setEligibilityThreshold(kpiConfigurationDTO.getEligibilityThreshold());
        kpiB3ResultDTO.setTolerance(kpiConfigurationDTO.getTolerance());
        kpiB3ResultDTO.setEvaluationType(kpiConfigurationDTO.getEvaluationType());
        kpiB3ResultDTO.setOutcome(OutcomeStatus.OK); // Initial outcome, will be updated later
        
        return kpiB3ResultService.save(kpiB3ResultDTO);
    }
    
    @Override
    @Transactional
    public KpiB3DetailResultDTO saveKpiB3DetailResult(KpiB3DetailResultDTO detailResult) {
        return kpiB3DetailResultService.save(detailResult);
    }
    
    /**
     * Helper method to get all months within the analysis period
     */
    private List<YearMonth> getMonthsInPeriod(LocalDate startDate, LocalDate endDate) {
        List<YearMonth> months = new ArrayList<>();
        YearMonth start = YearMonth.from(startDate);
        YearMonth end = YearMonth.from(endDate);
        
        YearMonth current = start;
        while (!current.isAfter(end)) {
            months.add(current);
            current = current.plusMonths(1);
        }
        
        return months;
    }
    
    @Override
    @Transactional
    public void saveKpiB3AnalyticData(InstanceDTO instanceDTO, InstanceModuleDTO instanceModuleDTO, 
                                     List<PagopaNumeroStandin> standInData) {
        
        if (standInData == null || standInData.isEmpty()) {
            LOGGER.debug("No stand-in data to save for analytic data");
            return;
        }
        
        // Convert PagopaNumeroStandin to KpiB3AnalyticDataDTO
        List<KpiB3AnalyticDataDTO> analyticDataList = standInData.stream()
            .map(standIn -> {
                // Find the station by station code
                AnagStation station = anagStationRepository.findOneByName(standIn.getStationCode())
                    .orElse(null);
                
                if (station == null) {
                    LOGGER.warn("Station not found for station code: {}", standIn.getStationCode());
                    return null; // Skip this record
                }
                
                KpiB3AnalyticDataDTO analyticData = new KpiB3AnalyticDataDTO();
                analyticData.setInstanceId(instanceDTO.getId());
                analyticData.setInstanceModuleId(instanceModuleDTO.getId());
                analyticData.setAnagStationId(station.getId());
                analyticData.setEventId(standIn.getId().toString()); // Use stand-in ID as event ID
                analyticData.setEventType(standIn.getEventType());
                analyticData.setEventTimestamp(standIn.getIntervalStart());
                analyticData.setStandInCount(standIn.getStandInCount());
                // Note: kpiB3DetailResultId will be set when we save individual detail results
                return analyticData;
            })
            .filter(java.util.Objects::nonNull) // Remove null entries
            .collect(java.util.stream.Collectors.toList());
        
        kpiB3AnalyticDataService.saveAll(analyticDataList);
        
        LOGGER.info("Saved {} KPI B.3 analytic data records", analyticDataList.size());
    }
    
    @Override
    @Transactional
    public void updateKpiB3ResultOutcome(Long resultId, OutcomeStatus outcome) {
        kpiB3ResultService.updateKpiB3ResultOutcome(resultId, outcome);
    }
    
    /**
     * Helper class to aggregate Stand-In data for a station within a specific month
     */
    private static class AggregatedStandInData {
        int totalCount = 0;
        java.util.List<String> eventIds = new java.util.ArrayList<>();
        java.time.LocalDateTime latestTimestamp = null;
    }
}