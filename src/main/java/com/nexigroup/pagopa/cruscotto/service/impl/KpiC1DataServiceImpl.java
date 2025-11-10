package com.nexigroup.pagopa.cruscotto.service.impl;


import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import java.math.BigDecimal;
import com.nexigroup.pagopa.cruscotto.repository.PagopaIORepository;
import com.nexigroup.pagopa.cruscotto.domain.IoDrilldown;
import com.nexigroup.pagopa.cruscotto.service.IoDrilldownService;
import com.nexigroup.pagopa.cruscotto.domain.KpiC1DetailResult;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;

import com.nexigroup.pagopa.cruscotto.service.KpiC1ResultService;
import com.nexigroup.pagopa.cruscotto.service.KpiC1DetailResultService;
import com.nexigroup.pagopa.cruscotto.service.KpiC1AnalyticDataService;
import com.nexigroup.pagopa.cruscotto.service.AnagPartnerService;
import com.nexigroup.pagopa.cruscotto.service.AnagInstitutionService;
import com.nexigroup.pagopa.cruscotto.service.dto.AnagPartnerDTO;
import com.nexigroup.pagopa.cruscotto.service.filter.AnagInstitutionFilter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.nexigroup.pagopa.cruscotto.service.KpiC1DataService;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceModuleDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiConfigurationDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.PagoPaIODTO;
import com.nexigroup.pagopa.cruscotto.service.mapper.PagopaIOMapper;
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
import java.util.stream.Collectors;

@Service
@Transactional
public class KpiC1DataServiceImpl implements KpiC1DataService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KpiC1DataServiceImpl.class);

    private final PagopaIORepository pagopaIORepository;
    private final PagopaIOMapper pagopaIOMapper;
    private final KpiC1ResultService kpiC1ResultService;
    private final KpiC1DetailResultService kpiC1DetailResultService;
    private final KpiC1AnalyticDataService kpiC1AnalyticDataService;
    private final AnagPartnerService anagPartnerService;
    private final AnagInstitutionService anagInstitutionService;
    private final IoDrilldownService ioDrilldownService;
    @jakarta.annotation.Resource
    private com.nexigroup.pagopa.cruscotto.repository.KpiC1DetailResultRepository kpiC1DetailResultRepository;

    public KpiC1DataServiceImpl(
        PagopaIORepository pagopaIORepository,
        PagopaIOMapper pagopaIOMapper,
        KpiC1ResultService kpiC1ResultService,
        KpiC1DetailResultService kpiC1DetailResultService,
        KpiC1AnalyticDataService kpiC1AnalyticDataService,
        AnagPartnerService anagPartnerService,
        AnagInstitutionService anagInstitutionService,
        IoDrilldownService ioDrilldownService
    ) {
        this.pagopaIORepository = pagopaIORepository;
        this.pagopaIOMapper = pagopaIOMapper;
        this.kpiC1ResultService = kpiC1ResultService;
        this.kpiC1DetailResultService = kpiC1DetailResultService;
        this.kpiC1AnalyticDataService = kpiC1AnalyticDataService;
        this.anagPartnerService = anagPartnerService;
        this.anagInstitutionService = anagInstitutionService;
        this.ioDrilldownService = ioDrilldownService;
    }

    @Override
    @Transactional
    public OutcomeStatus executeKpiC1Calculation(
        InstanceDTO instanceDTO,
        InstanceModuleDTO instanceModuleDTO,
        KpiConfigurationDTO kpiConfigurationDTO,
        LocalDate analysisDate
    ) {
        try {
            LOGGER.info("Starting KPI C.1 calculation for instance: {}", instanceDTO.getInstanceIdentification());

            // Get configuration parameters
            double entityThreshold = kpiConfigurationDTO.getEligibilityThreshold() != null 
                ? kpiConfigurationDTO.getEligibilityThreshold() : 50.0; // percentage of compliant entities required
            double configuredTolerance = kpiConfigurationDTO.getTolerance() != null 
                ? kpiConfigurationDTO.getTolerance() : 0.0; // tolerance margin below 100% message coverage

            // Business spec: tolerance expresses how much BELOW 100% an entity may fall and still be OK.
            // Effective required message percentage = 100 - configuredTolerance
            double requiredMessagePercentage = 100.0 - configuredTolerance;
            if (requiredMessagePercentage < 0.0) {
                requiredMessagePercentage = 0.0; // clamp
            } else if (requiredMessagePercentage > 100.0) {
                requiredMessagePercentage = 100.0; // clamp
            }

            LOGGER.info("KPI C.1 configuration - Entity threshold (OK entities %): {}%, Configured tolerance: {}%, Required message %: {}%", 
                       entityThreshold, configuredTolerance, requiredMessagePercentage);

            // Retrieve partner and its institutions
            AnagPartnerDTO partnerDTO = anagPartnerService.findOneByFiscalCode(instanceDTO.getPartnerFiscalCode())
                .orElseThrow(() -> new RuntimeException("Partner not found: " + instanceDTO.getPartnerFiscalCode()));
            
            List<String> entiList = getPartnerInstitutions(partnerDTO.getPartnerIdentification().getId());
            LOGGER.info("Found {} institutions for partner {}: {}", entiList.size(), 
                       instanceDTO.getPartnerFiscalCode(), entiList);

            // Retrieve IO data using double access logic
            List<PagoPaIODTO> ioDataList = retrieveIODataForPartner(
                instanceDTO.getPartnerFiscalCode(),
                entiList,
                instanceDTO.getAnalysisPeriodStartDate(),
                instanceDTO.getAnalysisPeriodEndDate()
            );

            if (ioDataList.isEmpty()) {
                LOGGER.info("No IO data found for partner {}. KPI is considered OK by default.", 
                           instanceDTO.getPartnerFiscalCode());
                
                // Save results with OK outcome
                saveKpiC1Results(instanceDTO, instanceModuleDTO, kpiConfigurationDTO, 
                               analysisDate, OutcomeStatus.OK, ioDataList, new HashMap<>(), true);
                return OutcomeStatus.OK;
            }

            LOGGER.info("Found {} IO data records for analysis", ioDataList.size());

            // Calculate monthly compliance
            Map<YearMonth, Boolean> monthlyCompliance = calculateMonthlyCompliance(
                ioDataList, entityThreshold, requiredMessagePercentage);

            // Calculate total compliance
            boolean totalCompliance = calculateTotalCompliance(
                ioDataList, entityThreshold, requiredMessagePercentage);

            // Determine final outcome based on evaluation type
            OutcomeStatus finalOutcome = determineFinalOutcome(
                kpiConfigurationDTO, monthlyCompliance, totalCompliance);

            LOGGER.info("KPI C.1 calculation completed. Final outcome: {}", finalOutcome);

            // Save all results
            saveKpiC1Results(instanceDTO, instanceModuleDTO, kpiConfigurationDTO,
                           analysisDate, finalOutcome, ioDataList, monthlyCompliance, totalCompliance);

            return finalOutcome;

        } catch (Exception e) {
            LOGGER.error("Error in KPI C.1 calculation for instance {}: {}", 
                        instanceDTO.getInstanceIdentification(), e.getMessage(), e);
            throw new RuntimeException("KPI C.1 calculation failed", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PagoPaIODTO> retrieveIODataForPartner(
        String partnerFiscalCode,
        List<String> entiList,
        LocalDate startDate,
        LocalDate endDate
    ) {
        LOGGER.info("Retrieving IO data for partner {} with double access logic", partnerFiscalCode);

        List<PagoPaIODTO> ioDataList = new ArrayList<>();

        // First access: try with CF_Partner
        if (pagopaIORepository.existsByCfPartner(partnerFiscalCode)) {
            LOGGER.info("Using first access method - CF_Partner exists");
            List<com.nexigroup.pagopa.cruscotto.domain.PagopaIO> dataWithPartner = 
                pagopaIORepository.findByCfPartnerAndDateRange(partnerFiscalCode, startDate, endDate);
            ioDataList.addAll(dataWithPartner.stream()
                .map(pagopaIOMapper::toDto)
                .collect(Collectors.toList()));
        } else {
            // Second access: try with individual entities without CF_Partner
            LOGGER.info("Using second access method - searching by entities without CF_Partner");
            List<com.nexigroup.pagopa.cruscotto.domain.PagopaIO> dataWithoutPartner = 
                pagopaIORepository.findByCfInstitutionListAndDateRangeWithoutCfPartner(entiList, startDate, endDate);
            ioDataList.addAll(dataWithoutPartner.stream()
                .map(pagopaIOMapper::toDto)
                .collect(Collectors.toList()));
        }

        LOGGER.info("Retrieved {} IO data records", ioDataList.size());
        return ioDataList;
    }

    @Override
    public Map<String, Boolean> calculateEntityCompliance(
        List<PagoPaIODTO> ioDataList,
        double requiredMessagePercentage
    ) {
        Map<String, Boolean> entityCompliance = new HashMap<>();

        // Group by entity
        Map<String, List<PagoPaIODTO>> dataByEntity = ioDataList.stream()
            .collect(Collectors.groupingBy(PagoPaIODTO::getEnte));

        for (Map.Entry<String, List<PagoPaIODTO>> entry : dataByEntity.entrySet()) {
            String ente = entry.getKey();
            List<PagoPaIODTO> entityData = entry.getValue();

            // Aggregate monthly totals for the entity (spec: ratio must hold over the period, not necessarily per day)
            long sumPositions = entityData.stream()
                .filter(d -> d.getNumeroPosizioni() != null)
                .mapToLong(d -> d.getNumeroPosizioni().longValue())
                .sum();
            long sumMessages = entityData.stream()
                .filter(d -> d.getNumeroMessaggi() != null)
                .mapToLong(d -> d.getNumeroMessaggi().longValue())
                .sum();

            double percentage;
            if (sumPositions == 0) {
                // Spec: absence of data should not create negative evidence; if messages also 0 => 0%, else 100%
                percentage = (sumMessages > 0) ? 100.0 : 0.0;
            } else {
                percentage = (double) sumMessages / (double) sumPositions * 100.0;
            }

            boolean isCompliant = percentage >= requiredMessagePercentage;
            entityCompliance.put(ente, isCompliant);

            LOGGER.debug("Entity {} aggregated compliance: {} (positions={}, messages={}, percentage={}) requiredMessage%={}", 
                ente, isCompliant, sumPositions, sumMessages, String.format("%.2f", percentage), requiredMessagePercentage);
        }

        return entityCompliance;
    }

    @Override
    public Map<YearMonth, Boolean> calculateMonthlyCompliance(
        List<PagoPaIODTO> ioDataList,
        double entityThreshold,
        double toleranceThreshold
    ) {
        Map<YearMonth, Boolean> monthlyCompliance = new HashMap<>();

        // Group by month
        Map<YearMonth, List<PagoPaIODTO>> dataByMonth = ioDataList.stream()
            .collect(Collectors.groupingBy(data -> YearMonth.from(data.getData())));

        for (Map.Entry<YearMonth, List<PagoPaIODTO>> entry : dataByMonth.entrySet()) {
            YearMonth month = entry.getKey();
            List<PagoPaIODTO> monthData = entry.getValue();
            
            Map<String, Boolean> monthlyEntityCompliance = 
                calculateEntityCompliance(monthData, toleranceThreshold);
            
            // Calculate compliance percentage for this month
            long compliantEntities = monthlyEntityCompliance.values().stream()
                .mapToLong(compliant -> compliant ? 1 : 0)
                .sum();
            double compliancePercentage = monthlyEntityCompliance.isEmpty() 
                ? 100.0 
                : (double) compliantEntities / monthlyEntityCompliance.size() * 100.0;
            
            boolean monthCompliant = compliancePercentage >= entityThreshold;
            monthlyCompliance.put(month, monthCompliant);
            
            LOGGER.info("Month {} compliance: {} ({}% of entities compliant, threshold: {}%)", 
                       month, monthCompliant, String.format("%.2f", compliancePercentage), entityThreshold);
        }

        return monthlyCompliance;
    }

    @Override
    public boolean calculateTotalCompliance(
        List<PagoPaIODTO> ioDataList,
        double entityThreshold,
        double toleranceThreshold
    ) {
        Map<String, Boolean> totalEntityCompliance = 
            calculateEntityCompliance(ioDataList, toleranceThreshold);

        long compliantEntities = totalEntityCompliance.values().stream()
            .mapToLong(compliant -> compliant ? 1 : 0)
            .sum();
        
        double compliancePercentage = totalEntityCompliance.isEmpty() 
            ? 100.0 
            : (double) compliantEntities / totalEntityCompliance.size() * 100.0;
        
        boolean totalCompliant = compliancePercentage >= entityThreshold;
        
        LOGGER.info("Total period compliance: {} ({}% of entities compliant, threshold: {}%)", 
                   totalCompliant, String.format("%.2f", compliancePercentage), entityThreshold);
        
        return totalCompliant;
    }

    @Override
    @Transactional
    public void saveKpiC1Results(
        InstanceDTO instanceDTO,
        InstanceModuleDTO instanceModuleDTO,
        KpiConfigurationDTO kpiConfigurationDTO,
        LocalDate analysisDate,
        OutcomeStatus outcome,
        List<PagoPaIODTO> ioDataList,
        Map<YearMonth, Boolean> monthlyCompliance,
        boolean totalCompliance
    ) {
        LOGGER.info("Saving KPI C.1 results for instance: {}", instanceDTO.getInstanceIdentification());

        try {
            // Calculate totals from IO data
            long numeroEntiTotali = ioDataList.stream()
                .map(PagoPaIODTO::getEnte)
                .distinct()
                .count();
            
            long numeroEntiComplianti = ioDataList.stream()
                .filter(data -> data.meetsToleranceThreshold(kpiConfigurationDTO.getTolerance()))
                .map(PagoPaIODTO::getEnte)
                .distinct()
                .count();
            
            long numeroPosizioniTotali = ioDataList.stream()
                .mapToLong(PagoPaIODTO::getNumeroPosizioni)
                .sum();
                
            long numeroMessaggiInviati = ioDataList.stream()
                .mapToLong(PagoPaIODTO::getNumeroMessaggi)
                .sum();

            // Check for existing results for this instance module
            LOGGER.info("Checking existing KPI C.1 results for instance module: {}", instanceModuleDTO.getId());
            List<com.nexigroup.pagopa.cruscotto.domain.KpiC1Result> existingResults = 
                kpiC1ResultService.findByInstanceAndInstanceModule(instanceDTO.getId(), instanceModuleDTO.getId());
            if (!existingResults.isEmpty()) {
                LOGGER.warn("Found {} existing KPI C.1 results for this instance module", existingResults.size());
            }

            // Create and save main KPI C.1 result
            com.nexigroup.pagopa.cruscotto.domain.KpiC1Result kpiC1Result = createKpiC1ResultEntity(
                instanceDTO, instanceModuleDTO, kpiConfigurationDTO, analysisDate, outcome,
                numeroEntiTotali, numeroEntiComplianti, numeroPosizioniTotali, numeroMessaggiInviati);
            
            com.nexigroup.pagopa.cruscotto.domain.KpiC1Result savedResult = kpiC1ResultService.save(kpiC1Result);
            LOGGER.info("Saved KPI C.1 main result with ID: {}", savedResult.getId());

            // Create detail results for each CF_INSTITUTION
            if (ioDataList.isEmpty()) {
                LOGGER.info("No IO data found - creating empty detail records for traceability");
                saveEmptyDetailResults(savedResult, instanceDTO, instanceModuleDTO, 
                                     analysisDate, kpiConfigurationDTO.getTolerance());
            } else {
                // Pass full configuration to detail saving so thresholds can be recomputed locally
                saveKpiC1DetailResults(savedResult, instanceDTO, instanceModuleDTO,
                    analysisDate, ioDataList, kpiConfigurationDTO);
            }

            // Create analytic data for traceability  
            if (ioDataList.isEmpty()) {
                LOGGER.info("No IO data found - creating empty analytic record for traceability");
                saveEmptyAnalyticData(savedResult, instanceDTO, instanceModuleDTO, analysisDate);
            } else {
                // Save analytic data and negative evidences snapshot
                saveKpiC1AnalyticDataAndNegativeEvidences(savedResult, instanceDTO, instanceModuleDTO,
                    analysisDate, ioDataList, kpiConfigurationDTO);
            }

            LOGGER.info("KPI C.1 results saved successfully for instance: {}", instanceDTO.getInstanceIdentification());

        } catch (Exception e) {
            LOGGER.error("Error saving KPI C.1 results: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save KPI C.1 results", e);
        }
    }

    @Override
    public List<PagoPaIODTO> getNegativeEvidences(List<PagoPaIODTO> ioDataList, double toleranceThreshold) {
        return ioDataList.stream()
            .filter(data -> !data.meetsToleranceThreshold(toleranceThreshold))
            .collect(Collectors.toList());
    }

    /**
     * Create main KpiC1Result entity
     */
    private com.nexigroup.pagopa.cruscotto.domain.KpiC1Result createKpiC1ResultEntity(
        InstanceDTO instanceDTO,
        InstanceModuleDTO instanceModuleDTO,
        KpiConfigurationDTO kpiConfigurationDTO,
        LocalDate analysisDate,
        OutcomeStatus outcome,
        long numeroEntiTotali,
        long numeroEntiComplianti,
        long numeroPosizioniTotali,
        long numeroMessaggiInviati
    ) {
        // Create Instance and InstanceModule references (simplified - in real scenario get from repos)
        com.nexigroup.pagopa.cruscotto.domain.Instance instance = new com.nexigroup.pagopa.cruscotto.domain.Instance();
        instance.setId(instanceDTO.getId());
        
        com.nexigroup.pagopa.cruscotto.domain.InstanceModule instanceModule = new com.nexigroup.pagopa.cruscotto.domain.InstanceModule();
        instanceModule.setId(instanceModuleDTO.getId());

        // Create KpiC1Result
        BigDecimal sogliaConfigurata = kpiConfigurationDTO.getEligibilityThreshold() != null 
            ? BigDecimal.valueOf(kpiConfigurationDTO.getEligibilityThreshold())
            : null;
            
        com.nexigroup.pagopa.cruscotto.domain.KpiC1Result kpiC1Result = new com.nexigroup.pagopa.cruscotto.domain.KpiC1Result(
            instance, instanceModule, analysisDate, outcome,
            outcome == OutcomeStatus.OK, // compliance based on success
            sogliaConfigurata
        );
        
        // Set calculated totals
        kpiC1Result.updateTotals(numeroEntiComplianti, numeroEntiTotali, numeroPosizioniTotali, numeroMessaggiInviati);
        
        return kpiC1Result;
    }

    /**
     * Save detail results - one per month (MESE) and one total (TOTALE)
     * Following the same pattern as KpiB3
     */
    private void saveKpiC1DetailResults(
        com.nexigroup.pagopa.cruscotto.domain.KpiC1Result savedResult,
        InstanceDTO instanceDTO,
        InstanceModuleDTO instanceModuleDTO,
        LocalDate analysisDate,
        List<PagoPaIODTO> ioDataList,
        KpiConfigurationDTO kpiConfigurationDTO
    ) {
        // Create Instance and InstanceModule references
        com.nexigroup.pagopa.cruscotto.domain.Instance instance = new com.nexigroup.pagopa.cruscotto.domain.Instance();
        instance.setId(instanceDTO.getId());
        
        com.nexigroup.pagopa.cruscotto.domain.InstanceModule instanceModule = new com.nexigroup.pagopa.cruscotto.domain.InstanceModule();
        instanceModule.setId(instanceModuleDTO.getId());

        // Recompute thresholds locally (avoid referencing variables from outer scope)
        double entityThreshold = kpiConfigurationDTO.getEligibilityThreshold() != null
            ? kpiConfigurationDTO.getEligibilityThreshold() : 50.0; // default 50%
        double configuredTolerance = kpiConfigurationDTO.getTolerance() != null
            ? kpiConfigurationDTO.getTolerance() : 0.0; // default 0 margin
        double requiredMessagePercentage = 100.0 - configuredTolerance;
        if (requiredMessagePercentage < 0.0) {
            requiredMessagePercentage = 0.0;
        } else if (requiredMessagePercentage > 100.0) {
            requiredMessagePercentage = 100.0;
        }

        // Get all months in the analysis period
        List<YearMonth> monthsInPeriod = getMonthsInPeriod(instanceDTO.getAnalysisPeriodStartDate(), 
                                                           instanceDTO.getAnalysisPeriodEndDate());
        
        // Group IO data by month
        Map<YearMonth, List<PagoPaIODTO>> dataByMonth = ioDataList.stream()
            .collect(Collectors.groupingBy(data -> YearMonth.from(data.getData())));
        
        // Create monthly detail results (one per month, aggregated for all institutions)
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
            
            // Get data for this month
            List<PagoPaIODTO> monthData = dataByMonth.getOrDefault(yearMonth, new ArrayList<>());
            
            // Calculate compliance for this month (aggregated message % vs requiredMessagePercentage)
            Map<String, Boolean> monthlyEntityCompliance = calculateEntityCompliance(monthData, requiredMessagePercentage);
            long compliantEntities = monthlyEntityCompliance.values().stream()
                .filter(Boolean::booleanValue)
                .count();
            long totalEntities = monthlyEntityCompliance.size();
            double compliancePercentage = totalEntities > 0 
                ? (double) compliantEntities / totalEntities * 100.0 : 100.0;
            
            // Create monthly detail result
            com.nexigroup.pagopa.cruscotto.domain.KpiC1DetailResult monthlyDetailResult = 
                new com.nexigroup.pagopa.cruscotto.domain.KpiC1DetailResult(
                    instance, instanceModule, analysisDate,
                    com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType.MESE,
                    monthStart, monthEnd,
                    "AGGREGATED", // No specific institution - this is aggregated
                    compliancePercentage >= entityThreshold ? OutcomeStatus.OK : OutcomeStatus.KO,
                    compliancePercentage >= entityThreshold,
                    java.math.BigDecimal.valueOf(configuredTolerance),
                    savedResult
                );

            // Aggregate totals for the month
            long totalPositions = monthData.stream().mapToLong(PagoPaIODTO::getNumeroPosizioni).sum();
            long totalMessages = monthData.stream().mapToLong(PagoPaIODTO::getNumeroMessaggi).sum();
            monthlyDetailResult.updateDetails(totalPositions, totalMessages);
            monthlyDetailResult.updateInstitutions(totalEntities, compliantEntities);
            
            kpiC1DetailResultService.save(monthlyDetailResult);
            LOGGER.info("Saved monthly result for {}: {} institutions ({} compliant, {}%), outcome: {}", 
                        yearMonth, totalEntities, compliantEntities, String.format("%.2f", compliancePercentage),
                        monthlyDetailResult.getOutcome());
        }
        
    // Create total detail result (entire analysis period)
    Map<String, Boolean> totalEntityCompliance = calculateEntityCompliance(ioDataList, requiredMessagePercentage);
        long totalCompliantEntities = totalEntityCompliance.values().stream()
            .filter(Boolean::booleanValue)
            .count();
        long totalEntities = totalEntityCompliance.size();
        double totalCompliancePercentage = totalEntities > 0 
            ? (double) totalCompliantEntities / totalEntities * 100.0 : 100.0;
        
        com.nexigroup.pagopa.cruscotto.domain.KpiC1DetailResult totalDetailResult = 
            new com.nexigroup.pagopa.cruscotto.domain.KpiC1DetailResult(
                instance, instanceModule, analysisDate,
                com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType.TOTALE,
                instanceDTO.getAnalysisPeriodStartDate(), instanceDTO.getAnalysisPeriodEndDate(),
                "AGGREGATED", // No specific institution - this is aggregated
                totalCompliancePercentage >= entityThreshold ? OutcomeStatus.OK : OutcomeStatus.KO,
                totalCompliancePercentage >= entityThreshold,
                java.math.BigDecimal.valueOf(configuredTolerance),
                savedResult
            );

        // Aggregate totals for entire period
        long totalPositions = ioDataList.stream().mapToLong(PagoPaIODTO::getNumeroPosizioni).sum();
        long totalMessages = ioDataList.stream().mapToLong(PagoPaIODTO::getNumeroMessaggi).sum();
        totalDetailResult.updateDetails(totalPositions, totalMessages);
        totalDetailResult.updateInstitutions(totalEntities, totalCompliantEntities);
        
        kpiC1DetailResultService.save(totalDetailResult);
        LOGGER.info("Saved total result: {} institutions ({} compliant, {}%), outcome: {}", 
                    totalEntities, totalCompliantEntities, String.format("%.2f", totalCompliancePercentage),
                    totalDetailResult.getOutcome());
        
        LOGGER.info("Saved {} detail results ({} monthly + 1 total)", 
                   monthsInPeriod.size() + 1, monthsInPeriod.size());
    }
    
    /**
     * Get all months in the analysis period
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

    /**
     * Save analytic data for traceability
     */
    private void saveKpiC1AnalyticDataAndNegativeEvidences(
        com.nexigroup.pagopa.cruscotto.domain.KpiC1Result savedResult,
        InstanceDTO instanceDTO,
        InstanceModuleDTO instanceModuleDTO,
        LocalDate analysisDate,
        List<PagoPaIODTO> ioDataList,
        KpiConfigurationDTO kpiConfigurationDTO
    ) {
        com.nexigroup.pagopa.cruscotto.domain.Instance instance = new com.nexigroup.pagopa.cruscotto.domain.Instance();
        instance.setId(instanceDTO.getId());
        com.nexigroup.pagopa.cruscotto.domain.InstanceModule instanceModule = new com.nexigroup.pagopa.cruscotto.domain.InstanceModule();
        instanceModule.setId(instanceModuleDTO.getId());

    double configuredTolerance = kpiConfigurationDTO.getTolerance() != null ? kpiConfigurationDTO.getTolerance() : 0.0;
    // Compute required message percentage clamped to [0,100] and keep it effectively final for lambda usage
    final double requiredMessagePercentage = Math.min(100.0, Math.max(0.0, 100.0 - configuredTolerance));

        List<IoDrilldown> negatives = new ArrayList<>();

        // Pre-carichiamo i detail results (monthly + total) per collegare ogni riga analitica.
        List<KpiC1DetailResult> detailResults = kpiC1DetailResultRepository != null
            ? kpiC1DetailResultRepository.findByResultId(savedResult.getId())
            : List.of();
        List<KpiC1DetailResult> monthlyDetailResults = detailResults.stream()
            .filter(dr -> dr.getEvaluationType() == EvaluationType.MESE)
            .collect(Collectors.toList());
        KpiC1DetailResult totalDetailResult = detailResults.stream()
            .filter(dr -> dr.getEvaluationType() == EvaluationType.TOTALE)
            .findFirst().orElse(null);

        ioDataList.forEach(ioData -> {
            com.nexigroup.pagopa.cruscotto.domain.KpiC1AnalyticData analyticData = new com.nexigroup.pagopa.cruscotto.domain.KpiC1AnalyticData(
                instance, instanceModule, analysisDate, ioData.getData(),
                ioData.getEnte(), ioData.getNumeroPosizioni().longValue(), ioData.getNumeroMessaggi().longValue()
            );
            // Associa il dettaglio mensile che copre la data della riga (fallback al totale se non trovato)
            KpiC1DetailResult matchedMonthly = monthlyDetailResults.stream()
                .filter(dr -> !ioData.getData().isBefore(dr.getEvaluationStartDate()) && !ioData.getData().isAfter(dr.getEvaluationEndDate()))
                .findFirst().orElse(null);
            analyticData.setDetailResult(matchedMonthly != null ? matchedMonthly : totalDetailResult);
            com.nexigroup.pagopa.cruscotto.domain.KpiC1AnalyticData savedAnalytic = kpiC1AnalyticDataService.save(analyticData);

            // Negative evidence if NOT meeting threshold
            double percentage = ioData.getPercentualeMessaggi();
            boolean meetsTolerance = percentage >= requiredMessagePercentage;
            if (!meetsTolerance) {
                IoDrilldown drill = new IoDrilldown(
                    instance, instanceModule, savedAnalytic,
                    analysisDate, ioData.getData(), ioData.getEnte(), ioData.getCfPartner(),
                    ioData.getNumeroPosizioni().longValue(), ioData.getNumeroMessaggi().longValue(), percentage, meetsTolerance);
                negatives.add(drill);
            }
        });

        if (!negatives.isEmpty()) {
            ioDrilldownService.saveAll(negatives);
            LOGGER.info("Saved {} IO negative evidence drilldown rows", negatives.size());
        } else {
            LOGGER.info("No negative evidences for KPI C.1; io_drilldown remains empty for this execution.");
        }

        LOGGER.debug("Saved {} analytic data records (FK detailResult valorizzata)", ioDataList.size());
    }

    /**
     * Save empty detail results when no IO data is found (for traceability)
     * Following the same pattern as KpiB3
     */
    private void saveEmptyDetailResults(
        com.nexigroup.pagopa.cruscotto.domain.KpiC1Result savedResult,
        InstanceDTO instanceDTO,
        InstanceModuleDTO instanceModuleDTO,
        LocalDate analysisDate,
        Double toleranceThreshold
    ) {
        // Create Instance and InstanceModule references
        com.nexigroup.pagopa.cruscotto.domain.Instance instance = new com.nexigroup.pagopa.cruscotto.domain.Instance();
        instance.setId(instanceDTO.getId());
        
        com.nexigroup.pagopa.cruscotto.domain.InstanceModule instanceModule = new com.nexigroup.pagopa.cruscotto.domain.InstanceModule();
        instanceModule.setId(instanceModuleDTO.getId());

        // Get all months in the analysis period
        List<YearMonth> monthsInPeriod = getMonthsInPeriod(instanceDTO.getAnalysisPeriodStartDate(), 
                                                           instanceDTO.getAnalysisPeriodEndDate());
        
        // Create empty monthly detail results
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
            
            com.nexigroup.pagopa.cruscotto.domain.KpiC1DetailResult monthlyDetailResult = 
                new com.nexigroup.pagopa.cruscotto.domain.KpiC1DetailResult(
                    instance, instanceModule, analysisDate,
                    com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType.MESE,
                    monthStart, monthEnd,
                    "NO_DATA_FOUND",
                    OutcomeStatus.OK, // OK because no data means compliant by default
                    true,
                    toleranceThreshold != null ? java.math.BigDecimal.valueOf(toleranceThreshold) : null,
                    savedResult
                );

            monthlyDetailResult.updateDetails(0L, 0L);
            kpiC1DetailResultService.save(monthlyDetailResult);
        }
        
        // Create empty total detail result
        com.nexigroup.pagopa.cruscotto.domain.KpiC1DetailResult totalDetailResult = 
            new com.nexigroup.pagopa.cruscotto.domain.KpiC1DetailResult(
                instance, instanceModule, analysisDate,
                com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType.TOTALE,
                instanceDTO.getAnalysisPeriodStartDate(), instanceDTO.getAnalysisPeriodEndDate(),
                "NO_DATA_FOUND",
                OutcomeStatus.OK, // OK because no data means compliant by default
                true,
                toleranceThreshold != null ? java.math.BigDecimal.valueOf(toleranceThreshold) : null,
                savedResult
            );

        totalDetailResult.updateDetails(0L, 0L);
        kpiC1DetailResultService.save(totalDetailResult);
        
        LOGGER.info("Saved {} empty detail results ({} monthly + 1 total) for traceability", 
                   monthsInPeriod.size() + 1, monthsInPeriod.size());
    }

    /**
     * Save empty analytic data when no IO data is found (for traceability)
     */
    private void saveEmptyAnalyticData(
        com.nexigroup.pagopa.cruscotto.domain.KpiC1Result savedResult,
        InstanceDTO instanceDTO,
        InstanceModuleDTO instanceModuleDTO,
        LocalDate analysisDate
    ) {
        // Create Instance and InstanceModule references
        com.nexigroup.pagopa.cruscotto.domain.Instance instance = new com.nexigroup.pagopa.cruscotto.domain.Instance();
        instance.setId(instanceDTO.getId());
        
        com.nexigroup.pagopa.cruscotto.domain.InstanceModule instanceModule = new com.nexigroup.pagopa.cruscotto.domain.InstanceModule();
        instanceModule.setId(instanceModuleDTO.getId());

        // Create a single "empty" analytic record to indicate no data was processed
        com.nexigroup.pagopa.cruscotto.domain.KpiC1AnalyticData analyticData = new com.nexigroup.pagopa.cruscotto.domain.KpiC1AnalyticData(
            instance, instanceModule, analysisDate, analysisDate, // Use analysis date as data date
            "NO_DATA_FOUND", 0L, 0L // Zero positions and messages
        );
        
        kpiC1AnalyticDataService.save(analyticData);
        LOGGER.info("Saved empty analytic data for traceability");
    }

    // Private helper methods

    /**
     * Retrieve all institutions (fiscal codes) associated with a partner
     * 
     * @param partnerId the partner ID
     * @return list of institution fiscal codes
     */
    private List<String> getPartnerInstitutions(Long partnerId) {
        try {
            AnagInstitutionFilter filter = new AnagInstitutionFilter();
            filter.setPartnerId(partnerId);
            filter.setShowNotEnabled(false); // Only enabled institutions
            
            Pageable pageable = PageRequest.of(0, 1000); // Get up to 1000 institutions
            
            return anagInstitutionService.findAll(filter, pageable)
                .getContent()
                .stream()
                .map(dto -> dto.getInstitutionIdentification().getFiscalCode())
                .distinct()
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            LOGGER.error("Error retrieving institutions for partner {}: {}", partnerId, e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    private OutcomeStatus determineFinalOutcome(
        KpiConfigurationDTO kpiConfigurationDTO,
        Map<YearMonth, Boolean> monthlyCompliance,
        boolean totalCompliance
    ) {
        // Determine based on evaluation type (MESE/TOTALE)
        String evaluationType = kpiConfigurationDTO.getEvaluationType() != null 
            ? kpiConfigurationDTO.getEvaluationType().toString() : "MESE";

        if ("TOTALE".equalsIgnoreCase(evaluationType)) {
            return totalCompliance ? OutcomeStatus.OK : OutcomeStatus.KO;
        } else {
            // Monthly evaluation - all months must be compliant
            boolean allMonthsCompliant = monthlyCompliance.values().stream()
                .allMatch(Boolean::booleanValue);
            return allMonthsCompliant ? OutcomeStatus.OK : OutcomeStatus.KO;
        }
    }


}
