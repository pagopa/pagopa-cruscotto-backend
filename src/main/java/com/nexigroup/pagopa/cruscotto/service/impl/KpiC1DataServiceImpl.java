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
            double entityThreshold = kpiConfigurationDTO.getInstitutionTolerance() != null 
                ? kpiConfigurationDTO.getInstitutionTolerance().doubleValue() : 50.0; // percentage of compliant entities required
            double requiredMessagePercentage = kpiConfigurationDTO.getNotificationTolerance() != null 
                ? kpiConfigurationDTO.getNotificationTolerance().doubleValue() : 100.0; // required message coverage percentage

            LOGGER.info("KPI C.1 configuration - Entity threshold (OK entities %): {}%, Required message %: {}%", 
                       entityThreshold, requiredMessagePercentage);

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

        // First access: get data with CF_Partner
        if (pagopaIORepository.existsByCfPartner(partnerFiscalCode)) {
            LOGGER.info("First access method - retrieving records with CF_Partner");
            List<com.nexigroup.pagopa.cruscotto.domain.PagopaIO> dataWithPartner = 
                pagopaIORepository.findByCfPartnerAndDateRange(partnerFiscalCode, startDate, endDate);
            ioDataList.addAll(dataWithPartner.stream()
                .map(pagopaIOMapper::toDto)
                .collect(Collectors.toList()));
            LOGGER.info("Retrieved {} records with CF_Partner", dataWithPartner.size());
        }
        
        // Second access: ALSO get data for entities without CF_Partner
        // This ensures we capture ALL data, even when some records have cfPartner and others don't
        LOGGER.info("Second access method - retrieving records by entities without CF_Partner");
        List<com.nexigroup.pagopa.cruscotto.domain.PagopaIO> dataWithoutPartner = 
            pagopaIORepository.findByCfInstitutionListAndDateRangeWithoutCfPartner(entiList, startDate, endDate);
        ioDataList.addAll(dataWithoutPartner.stream()
            .map(pagopaIOMapper::toDto)
            .collect(Collectors.toList()));
        LOGGER.info("Retrieved {} records without CF_Partner", dataWithoutPartner.size());

        LOGGER.info("Retrieved total {} IO data records", ioDataList.size());
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

            // NUOVA LOGICA: Aggregare posizioni e messaggi totali per l'ente nel periodo
            // poi calcolare percentuale = (totale_messaggi / totale_posizioni) * 100
            // Un ente è COMPLIANT (OK) se percentuale >= tolleranza
            // Un ente è NON COMPLIANT (KO) se percentuale < tolleranza
            long totalPositions = entityData.stream()
                .mapToLong(data -> data.getNumeroPosizioni() != null ? data.getNumeroPosizioni().longValue() : 0L)
                .sum();
            long totalMessages = entityData.stream()
                .mapToLong(data -> data.getNumeroMessaggi() != null ? data.getNumeroMessaggi().longValue() : 0L)
                .sum();
            
            double percentage;
            if (totalPositions == 0) {
                // Se non ci sono posizioni, consideriamo OK (100% >= tolleranza)
                percentage = 100.0;
            } else {
                percentage = ((double) totalMessages / (double) totalPositions) * 100.0;
            }
            
            // IMPORTANTE: un ente è OK se percentuale >= tolleranza
            // (cioè raggiunge o supera la soglia richiesta)
            boolean isCompliant = percentage >= requiredMessagePercentage;
            entityCompliance.put(ente, isCompliant);

            LOGGER.debug("Entity {} compliance: {} (totalPos={}, totalMsg={}, percentage={:.2f}%, tolerance={}%)", 
                ente, isCompliant, totalPositions, totalMessages, percentage, requiredMessagePercentage);
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
            
            // Calcola compliance per ogni ente nel mese
            // Un ente è OK se (totale_messaggi/totale_posizioni)*100 >= tolleranza
            Map<String, Boolean> monthlyEntityCompliance = 
                calculateEntityCompliance(monthData, toleranceThreshold);
            
            // Conta quanti enti sono OK (compliant) vs totali
            long compliantEntities = monthlyEntityCompliance.values().stream()
                .filter(Boolean::booleanValue)
                .count();
            long totalEntities = monthlyEntityCompliance.size();
            
            // Il mese è OK se la percentuale di enti OK >= soglia enti
            double compliancePercentage = totalEntities > 0 
                ? (double) compliantEntities / totalEntities * 100.0 
                : 100.0;
            
            boolean monthCompliant = compliancePercentage >= entityThreshold;
            monthlyCompliance.put(month, monthCompliant);
            
            LOGGER.info("Month {} compliance: {} ({} OK entities out of {}, {}%, threshold: {}%)", 
                       month, monthCompliant, compliantEntities, totalEntities, 
                       String.format("%.2f", compliancePercentage), entityThreshold);
        }

        return monthlyCompliance;
    }

    @Override
    public boolean calculateTotalCompliance(
        List<PagoPaIODTO> ioDataList,
        double entityThreshold,
        double toleranceThreshold
    ) {
        // Calcola compliance per ogni ente sull'intero periodo
        // Un ente è OK se (totale_messaggi/totale_posizioni)*100 >= tolleranza
        Map<String, Boolean> totalEntityCompliance = 
            calculateEntityCompliance(ioDataList, toleranceThreshold);

        // Conta quanti enti sono OK (compliant) vs totali
        long compliantEntities = totalEntityCompliance.values().stream()
            .filter(Boolean::booleanValue)
            .count();
        long totalEntities = totalEntityCompliance.size();
        
        // Il periodo totale è OK se la percentuale di enti OK >= soglia enti
        double compliancePercentage = totalEntities > 0 
            ? (double) compliantEntities / totalEntities * 100.0 
            : 100.0;
        
        boolean totalCompliant = compliancePercentage >= entityThreshold;
        
        LOGGER.info("Total period compliance: {} ({} OK entities out of {}, {}%, threshold: {}%)", 
                   totalCompliant, compliantEntities, totalEntities, 
                   String.format("%.2f", compliancePercentage), entityThreshold);
        
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
            // NUOVA LOGICA: Calcola il numero di enti compliant aggregando per l'intero periodo
            // Un ente è compliant se (totale_messaggi / totale_posizioni) * 100 <= tolleranza
            double toleranceThreshold = kpiConfigurationDTO.getNotificationTolerance() != null 
                ? kpiConfigurationDTO.getNotificationTolerance().doubleValue() : 100.0;
            
            // Raggruppa per ente e calcola compliance sull'intero periodo
            Map<String, Boolean> entityCompliance = calculateEntityCompliance(ioDataList, toleranceThreshold);
            
            long numeroEntiTotali = entityCompliance.size();
            long numeroEntiComplianti = entityCompliance.values().stream()
                .filter(Boolean::booleanValue)
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
                                     analysisDate, kpiConfigurationDTO.getNotificationTolerance() != null 
                                     ? kpiConfigurationDTO.getNotificationTolerance().doubleValue() : 0.0);
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
        BigDecimal sogliaConfigurata = kpiConfigurationDTO.getInstitutionTolerance() != null 
            ? kpiConfigurationDTO.getInstitutionTolerance()
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
     * Following the same pattern as KpiB3 and KpiC2
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
        double entityThreshold = kpiConfigurationDTO.getInstitutionTolerance() != null
            ? kpiConfigurationDTO.getInstitutionTolerance().doubleValue() : 50.0; // default 50%
        double requiredMessagePercentage = kpiConfigurationDTO.getNotificationTolerance() != null
            ? kpiConfigurationDTO.getNotificationTolerance().doubleValue() : 100.0; // default 100%

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
            // Only entities with data are evaluated for compliance
            Map<String, Boolean> monthlyEntityCompliance = calculateEntityCompliance(monthData, requiredMessagePercentage);
            long compliantEntities = monthlyEntityCompliance.values().stream()
                .filter(Boolean::booleanValue)
                .count();
            // CORREZIONE: Usare il numero di enti con dati NEL MESE, non tutti gli enti del partner
            long totalEntitiesInMonth = monthlyEntityCompliance.size();
            double compliancePercentage = totalEntitiesInMonth > 0 
                ? (double) compliantEntities / totalEntitiesInMonth * 100.0 : 100.0;
            
            // Create monthly detail result
            com.nexigroup.pagopa.cruscotto.domain.KpiC1DetailResult monthlyDetailResult = 
                new com.nexigroup.pagopa.cruscotto.domain.KpiC1DetailResult(
                    instance, instanceModule, analysisDate,
                    com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType.MESE,
                    monthStart, monthEnd,
                    "AGGREGATED", // No specific institution - this is aggregated
                    compliancePercentage >= entityThreshold ? OutcomeStatus.OK : OutcomeStatus.KO,
                    compliancePercentage >= entityThreshold,
                    java.math.BigDecimal.valueOf(requiredMessagePercentage),
                    savedResult
                );

            // Aggregate totals for the month
            long totalPositions = monthData.stream().mapToLong(PagoPaIODTO::getNumeroPosizioni).sum();
            long totalMessages = monthData.stream().mapToLong(PagoPaIODTO::getNumeroMessaggi).sum();
            monthlyDetailResult.updateDetails(totalPositions, totalMessages);
            // CORREZIONE: Usare il numero di enti con dati NEL MESE
            monthlyDetailResult.updateInstitutions(totalEntitiesInMonth, compliantEntities);
            
            kpiC1DetailResultService.save(monthlyDetailResult);
            LOGGER.info("Saved monthly result for {}: {} institutions ({} compliant, {}%), outcome: {}", 
                        yearMonth, totalEntitiesInMonth, compliantEntities, String.format("%.2f", compliancePercentage),
                        monthlyDetailResult.getOutcome());
        }
        
    // Create total detail result (entire analysis period)
    // Only entities with data are evaluated for compliance
    Map<String, Boolean> totalEntityCompliance = calculateEntityCompliance(ioDataList, requiredMessagePercentage);
        long totalCompliantEntities = totalEntityCompliance.values().stream()
            .filter(Boolean::booleanValue)
            .count();
        // CORREZIONE: Usare il numero di enti con dati NEL PERIODO TOTALE, non tutti gli enti del partner
        long totalEntitiesInPeriod = totalEntityCompliance.size();
        double totalCompliancePercentage = totalEntitiesInPeriod > 0 
            ? (double) totalCompliantEntities / totalEntitiesInPeriod * 100.0 : 100.0;
        
        com.nexigroup.pagopa.cruscotto.domain.KpiC1DetailResult totalDetailResult = 
            new com.nexigroup.pagopa.cruscotto.domain.KpiC1DetailResult(
                instance, instanceModule, analysisDate,
                com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType.TOTALE,
                instanceDTO.getAnalysisPeriodStartDate(), instanceDTO.getAnalysisPeriodEndDate(),
                "AGGREGATED", // No specific institution - this is aggregated
                totalCompliancePercentage >= entityThreshold ? OutcomeStatus.OK : OutcomeStatus.KO,
                totalCompliancePercentage >= entityThreshold,
                java.math.BigDecimal.valueOf(requiredMessagePercentage),
                savedResult
            );

        // Aggregate totals for entire period
        long totalPositions = ioDataList.stream().mapToLong(PagoPaIODTO::getNumeroPosizioni).sum();
        long totalMessages = ioDataList.stream().mapToLong(PagoPaIODTO::getNumeroMessaggi).sum();
        totalDetailResult.updateDetails(totalPositions, totalMessages);
        // CORREZIONE: Usare il numero di enti con dati NEL PERIODO TOTALE
        totalDetailResult.updateInstitutions(totalEntitiesInPeriod, totalCompliantEntities);
        
        kpiC1DetailResultService.save(totalDetailResult);
        LOGGER.info("Saved total result: {} institutions ({} compliant, {}%), outcome: {}", 
                    totalEntitiesInPeriod, totalCompliantEntities, String.format("%.2f", totalCompliancePercentage),
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
     * NUOVA LOGICA: salviamo righe aggregate per data (non per institution)
     * Ogni riga rappresenta i conteggi aggregati di tutte le istituzioni per una data
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

        double requiredMessagePercentage = kpiConfigurationDTO.getNotificationTolerance() != null 
            ? kpiConfigurationDTO.getNotificationTolerance().doubleValue() : 100.0;

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

        // NUOVA LOGICA: Aggrega i dati per GIORNO (tutti gli enti insieme)
        // Group by day only
        Map<LocalDate, List<PagoPaIODTO>> dataByDay = ioDataList.stream()
            .collect(Collectors.groupingBy(PagoPaIODTO::getData));

        List<IoDrilldown> allDrilldowns = new ArrayList<>();

        // Per ogni giorno, calcola i conteggi aggregati totali e salva una riga analitica
        dataByDay.forEach((day, dayDataList) -> {
            
            // Determina il detail result mensile corrispondente a questo giorno
            YearMonth yearMonth = YearMonth.from(day);
            KpiC1DetailResult matchedMonthly = monthlyDetailResults.stream()
                .filter(dr -> YearMonth.from(dr.getEvaluationStartDate()).equals(yearMonth))
                .findFirst().orElse(totalDetailResult);
            
            // Aggrega posizioni e messaggi per TUTTI gli enti in questo giorno
            long totalPositions = dayDataList.stream()
                .mapToLong(data -> data.getNumeroPosizioni() != null ? data.getNumeroPosizioni().longValue() : 0L)
                .sum();
            long totalMessages = dayDataList.stream()
                .mapToLong(data -> data.getNumeroMessaggi() != null ? data.getNumeroMessaggi().longValue() : 0L)
                .sum();
            
            // Calcola il numero di istituzioni DISTINTE per questo giorno
            int distinctInstitutions = (int) dayDataList.stream()
                .map(PagoPaIODTO::getEnte)
                .distinct()
                .count();
            
            // Crea UNA SOLA riga analitica per questo giorno (aggregata su tutti gli enti)
            com.nexigroup.pagopa.cruscotto.domain.KpiC1AnalyticData analyticData = 
                new com.nexigroup.pagopa.cruscotto.domain.KpiC1AnalyticData(
                    instance, instanceModule, analysisDate, day,
                    totalPositions, // positionNumber contiene positionsCount totale giornaliero
                    totalMessages,  // messageNumber contiene messagesCount totale giornaliero
                    distinctInstitutions // institutionCount contiene il numero di enti distinti
                );

            analyticData.setDetailResult(matchedMonthly);
            
            // Salva l'analytic data
            com.nexigroup.pagopa.cruscotto.domain.KpiC1AnalyticData savedAnalytic = 
                kpiC1AnalyticDataService.save(analyticData);
            
            // Crea drilldown per OGNI SINGOLO RECORD originale di pagopa_io per questo giorno
            // Questo permette di vedere il dettaglio completo dell'aggregazione
            for (PagoPaIODTO ioRecord : dayDataList) {
                long positionNumber = ioRecord.getNumeroPosizioni() != null ? 
                    ioRecord.getNumeroPosizioni().longValue() : 0L;
                long messageNumber = ioRecord.getNumeroMessaggi() != null ? 
                    ioRecord.getNumeroMessaggi().longValue() : 0L;
                
                // Calcola percentuale per questo singolo record
                double percentage;
                if (positionNumber == 0) {
                    percentage = 100.0; // Se non ci sono posizioni, consideriamo 100%
                } else {
                    percentage = ((double) messageNumber / (double) positionNumber) * 100.0;
                }
                
                // Determina se questo record soddisfa la tolleranza
                boolean meetsTolerance = percentage >= requiredMessagePercentage;
                
                // Crea drilldown per TUTTI i record (non solo quelli KO)
                IoDrilldown drill = new IoDrilldown(
                    instance,
                    instanceModule,
                    savedAnalytic,
                    analysisDate,
                    day,
                    ioRecord.getEnte(),
                    ioRecord.getCfPartner(),
                    positionNumber,
                    messageNumber,
                    percentage,
                    meetsTolerance
                );
                allDrilldowns.add(drill);
            }

            LOGGER.debug("Saved 1 analytic data record for day {} with {} detail drilldown records", 
                         day, dayDataList.size());
        });

        if (!allDrilldowns.isEmpty()) {
            ioDrilldownService.saveAll(allDrilldowns);
            long koCount = allDrilldowns.stream().filter(d -> !d.getMeetsTolerance()).count();
            LOGGER.info("Saved {} IO drilldown rows (dettaglio completo: {} KO, {} OK)", 
                       allDrilldowns.size(), koCount, allDrilldowns.size() - koCount);
        } else {
            LOGGER.info("Nessun drilldown salvato per questa esecuzione KPI C.1.");
        }

        LOGGER.info("Saved {} analytic data records (1 per giorno con totali aggregati)", dataByDay.size());
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
            0L, 0L, // Zero positions and messages
            0 // Zero institutions
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
