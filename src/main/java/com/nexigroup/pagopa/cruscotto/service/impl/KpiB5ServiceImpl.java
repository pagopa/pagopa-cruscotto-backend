package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.InstanceModule;
import com.nexigroup.pagopa.cruscotto.domain.KpiB5AnalyticData;
import com.nexigroup.pagopa.cruscotto.domain.KpiB5AnalyticDrillDown;
import com.nexigroup.pagopa.cruscotto.domain.KpiB5DetailResult;
import com.nexigroup.pagopa.cruscotto.domain.KpiB5Result;
import com.nexigroup.pagopa.cruscotto.domain.KpiConfiguration;
import com.nexigroup.pagopa.cruscotto.domain.PagopaSpontaneous;
import com.nexigroup.pagopa.cruscotto.domain.SpontaneousDrilldown;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.SpontaneousPayments;
import com.nexigroup.pagopa.cruscotto.repository.InstanceRepository;
import com.nexigroup.pagopa.cruscotto.repository.InstanceModuleRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiB5AnalyticDataRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiB5AnalyticDrillDownRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiB5DetailResultRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiB5ResultRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiConfigurationRepository;
import com.nexigroup.pagopa.cruscotto.repository.PagopaSpontaneousRepository;
import com.nexigroup.pagopa.cruscotto.repository.SpontaneousDrilldownRepository;
import com.nexigroup.pagopa.cruscotto.service.KpiB5Service;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB5AnalyticDataDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB5DetailResultDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB5ResultDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.PagopaSpontaneiDTO;
import com.nexigroup.pagopa.cruscotto.service.mapper.KpiB5AnalyticDataMapper;
import com.nexigroup.pagopa.cruscotto.service.mapper.KpiB5DetailResultMapper;
import com.nexigroup.pagopa.cruscotto.service.mapper.KpiB5ResultMapper;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing KPI B.5 - Utilizzo funzionalità pagamenti spontanei.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class KpiB5ServiceImpl implements KpiB5Service {

    private final KpiB5ResultRepository kpiB5ResultRepository;
    private final KpiB5DetailResultRepository kpiB5DetailResultRepository;
    private final KpiB5AnalyticDataRepository kpiB5AnalyticDataRepository;
    private final KpiB5AnalyticDrillDownRepository kpiB5AnalyticDrillDownRepository;
    private final PagopaSpontaneousRepository pagopaSpontaneousRepository;
    private final SpontaneousDrilldownRepository spontaneousDrilldownRepository;
    private final KpiConfigurationRepository kpiConfigurationRepository;
    private final InstanceRepository instanceRepository;
    private final InstanceModuleRepository instanceModuleRepository;

    private final KpiB5ResultMapper kpiB5ResultMapper;
    private final KpiB5DetailResultMapper kpiB5DetailResultMapper;
    private final KpiB5AnalyticDataMapper kpiB5AnalyticDataMapper;

    @Override
    public KpiB5ResultDTO save(KpiB5ResultDTO kpiB5ResultDTO) {
        log.debug("Request to save KpiB5Result : {}", kpiB5ResultDTO);
        KpiB5Result kpiB5Result = kpiB5ResultMapper.toEntity(kpiB5ResultDTO);
        kpiB5Result = kpiB5ResultRepository.save(kpiB5Result);
        return kpiB5ResultMapper.toDto(kpiB5Result);
    }

    @Override
    public KpiB5ResultDTO update(KpiB5ResultDTO kpiB5ResultDTO) {
        log.debug("Request to update KpiB5Result : {}", kpiB5ResultDTO);
        KpiB5Result kpiB5Result = kpiB5ResultMapper.toEntity(kpiB5ResultDTO);
        kpiB5Result = kpiB5ResultRepository.save(kpiB5Result);
        return kpiB5ResultMapper.toDto(kpiB5Result);
    }

    @Override
    public Optional<KpiB5ResultDTO> partialUpdate(KpiB5ResultDTO kpiB5ResultDTO) {
        log.debug("Request to partially update KpiB5Result : {}", kpiB5ResultDTO);

        return kpiB5ResultRepository
            .findById(kpiB5ResultDTO.getId())
            .map(existingKpiB5Result -> {
                // TODO: Implement partial update logic
                return existingKpiB5Result;
            })
            .map(kpiB5ResultRepository::save)
            .map(kpiB5ResultMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<KpiB5ResultDTO> findAll(Pageable pageable) {
        log.debug("Request to get all KpiB5Results");
        return kpiB5ResultRepository.findAll(pageable).map(kpiB5ResultMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<KpiB5ResultDTO> findOne(Long id) {
        log.debug("Request to get KpiB5Result : {}", id);
        return kpiB5ResultRepository.findById(id).map(kpiB5ResultMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete KpiB5Result : {}", id);
        kpiB5ResultRepository.deleteById(id);
    }

    @Override
    public void deleteAllByInstanceModuleId(Long instanceModuleId) {
        log.debug("Request to delete all KpiB5Result by instanceModuleId : {}", instanceModuleId);
        // Delete in correct order to respect foreign key constraints
        spontaneousDrilldownRepository.deleteByInstanceModuleId(instanceModuleId);
        kpiB5AnalyticDrillDownRepository.deleteAllByInstanceModuleId(instanceModuleId);
        kpiB5AnalyticDataRepository.deleteAllByInstanceModuleId(instanceModuleId);
        kpiB5DetailResultRepository.deleteAllByInstanceModuleId(instanceModuleId);
        kpiB5ResultRepository.deleteAllByInstanceModuleId(instanceModuleId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<KpiB5DetailResultDTO> findDetailsByKpiB5ResultId(Long kpiB5ResultId) {
        log.debug("Request to get KpiB5DetailResults by kpiB5ResultId : {}", kpiB5ResultId);
        return kpiB5DetailResultRepository.selectByKpiB5ResultId(kpiB5ResultId)
            .stream()
            .map(kpiB5DetailResultMapper::toDto)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<KpiB5AnalyticDataDTO> findAnalyticsByDetailResultId(Long detailResultId) {
        log.debug("Request to get KpiB5AnalyticData by detailResultId : {}", detailResultId);
        return kpiB5AnalyticDataRepository.selectByKpiB5DetailResultId(detailResultId)
            .stream()
            .map(kpiB5AnalyticDataMapper::toDto)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PagopaSpontaneiDTO> findDrillDownByAnalyticDataId(Long analyticDataId) {
        log.debug("Request to get drill-down data by analyticDataId : {}", analyticDataId);
        
        // Use historical snapshot data from SpontaneousDrilldown table
        return spontaneousDrilldownRepository.findByKpiB5AnalyticDataId(analyticDataId)
            .stream()
            .map(drillDown -> {
                // Convert historical snapshot to PagopaSpontaneiDTO
                PagopaSpontaneiDTO dto = new PagopaSpontaneiDTO();
                dto.setId(drillDown.getId());
                dto.setKpiB5AnalyticDataId(drillDown.getKpiB5AnalyticData().getId());
                dto.setPartnerId(drillDown.getPartnerId());
                dto.setPartnerName(drillDown.getPartnerName());
                dto.setPartnerFiscalCode(drillDown.getPartnerFiscalCode());
                dto.setStationCode(drillDown.getStationCode());
                dto.setFiscalCode(drillDown.getFiscalCode());
                
                // Use the enum's getValue() method to get the string representation
                dto.setSpontaneousPayments(drillDown.getSpontaneousPayments().getValue());
                
                return dto;
            })
            .toList();
    }

    @Override
    public OutcomeStatus calculateKpiB5(Long instanceId, Long instanceModuleId, LocalDate analysisDate) {
        log.info("Starting KPI B.5 calculation for instanceId: {}, instanceModuleId: {}, analysisDate: {}", 
                 instanceId, instanceModuleId, analysisDate);

        try {
            // 1. Elimina eventuali dati precedenti per questo instanceModuleId
            deleteAllByInstanceModuleId(instanceModuleId);

            // 2. Recupera l'InstanceModule e il codice fiscale del partner
            InstanceModule instanceModule = instanceModuleRepository.findById(instanceModuleId)
                .orElseThrow(() -> new RuntimeException("InstanceModule not found with id: " + instanceModuleId));
            String partnerFiscalCode = instanceModule.getInstance().getPartner().getFiscalCode();
            log.debug("Processing KPI B.5 for partner with fiscal code: {}", partnerFiscalCode);

            // 3. Recupera configurazione KPI B.5 per soglia e tolleranza
            KpiConfiguration configuration = kpiConfigurationRepository
                .findByModuleCode(ModuleCode.B5.code)
                .orElseThrow(() -> new RuntimeException("KPI B.5 configuration not found"));

            // 4. Ottieni solo le stazioni del partner specifico
            List<PagopaSpontaneous> allPartners = pagopaSpontaneousRepository.findByCfPartner(partnerFiscalCode);
            log.debug("Found {} stations for partner {}", allPartners.size(), partnerFiscalCode);

            // 5. Calcola statistiche per il partner specifico
            long partnersWithoutSpontaneous = allPartners.stream()
                .filter(partner -> Boolean.FALSE.equals(partner.getSpontaneousPayment()))
                .count();
            
            BigDecimal percentageWithoutSpontaneous = BigDecimal.ZERO;
            if (allPartners.size() > 0) {
                percentageWithoutSpontaneous = BigDecimal.valueOf(partnersWithoutSpontaneous)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(allPartners.size()), 2, RoundingMode.HALF_UP);
            }

            // 6. Recupera soglie dalla configurazione
            Double thresholdValue = configuration.getEligibilityThreshold(); // Soglia configurabile
            Double toleranceValue = configuration.getTolerance(); // Tolleranza configurabile
            
            // Valori di default se non configurati (come negli altri KPI)
            BigDecimal thresholdIndex = thresholdValue != null ? BigDecimal.valueOf(thresholdValue) : BigDecimal.ZERO;
            BigDecimal toleranceIndex = toleranceValue != null ? BigDecimal.valueOf(toleranceValue) : BigDecimal.ZERO;
            
            // 7. Determina outcome: OK se %_senza_spontanei <= (soglia + tolleranza)
            OutcomeStatus outcome = determineOutcomeWithThresholds(percentageWithoutSpontaneous, thresholdIndex, toleranceIndex);
            
            log.debug("KPI B.5 calculation for partner {}: stationsTotal={}, stationsWithoutSpontaneous={}, percentage={}%, threshold={}%, tolerance={}%, outcome={}", 
                     partnerFiscalCode, allPartners.size(), partnersWithoutSpontaneous, percentageWithoutSpontaneous, thresholdValue, toleranceValue, outcome);

            // 8. Crea risultato principale
            KpiB5Result mainResult = createMainResult(instanceId, instanceModuleId, analysisDate, thresholdIndex, toleranceIndex, outcome);

            // 9. Crea dettaglio aggregato
            createDetailResult(mainResult, instanceId, instanceModuleId, allPartners, analysisDate);

            log.info("KPI B.5 calculation completed successfully for instanceModuleId: {} with outcome: {}", instanceModuleId, outcome);
            
            return outcome;

        } catch (Exception e) {
            log.error("Error calculating KPI B.5 for instanceModuleId: {}", instanceModuleId, e);
            return OutcomeStatus.KO;
        }
    }

    private OutcomeStatus determineOutcomeWithThresholds(BigDecimal percentage, BigDecimal threshold, BigDecimal tolerance) {
        BigDecimal maxAllowed = threshold.add(tolerance);
        return percentage.compareTo(maxAllowed) <= 0 ? OutcomeStatus.OK : OutcomeStatus.KO;
    }

    private KpiB5Result createMainResult(Long instanceId, Long instanceModuleId, LocalDate analysisDate, 
                                        BigDecimal thresholdIndex, BigDecimal toleranceIndex, OutcomeStatus outcome) {
        
        // Recupera le entità Instance e InstanceModule
        Instance instance = instanceRepository.findById(instanceId)
            .orElseThrow(() -> new RuntimeException("Instance not found with id: " + instanceId));
        InstanceModule instanceModule = instanceModuleRepository.findById(instanceModuleId)
            .orElseThrow(() -> new RuntimeException("InstanceModule not found with id: " + instanceModuleId));
        
        KpiB5Result result = new KpiB5Result();
        result.setInstance(instance);
        result.setInstanceModule(instanceModule);
        result.setAnalysisDate(analysisDate);
        result.setThresholdIndex(thresholdIndex);
        result.setToleranceIndex(toleranceIndex);
        result.setOutcome(outcome);

        return kpiB5ResultRepository.save(result);
    }

    private void createDetailResult(KpiB5Result mainResult, Long instanceId, Long instanceModuleId, 
                                   List<PagopaSpontaneous> allPartners, LocalDate analysisDate) {
        // Conta stazioni senza pagamenti spontanei
        int stationsWithoutSpontaneous = (int) allPartners.stream()
            .filter(partner -> Boolean.FALSE.equals(partner.getSpontaneousPayment()))
            .count();
        
        BigDecimal percentageNoSpontaneous = BigDecimal.ZERO;
        if (allPartners.size() > 0) {
            percentageNoSpontaneous = BigDecimal.valueOf(stationsWithoutSpontaneous)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(allPartners.size()), 2, RoundingMode.HALF_UP);
        }

        OutcomeStatus detailOutcome = percentageNoSpontaneous.compareTo(BigDecimal.ZERO) == 0 ? OutcomeStatus.OK : OutcomeStatus.KO;

        // Crea detail result
        KpiB5DetailResult detailResult = new KpiB5DetailResult();
        detailResult.setInstance(mainResult.getInstance());
        detailResult.setInstanceModule(mainResult.getInstanceModule());
        detailResult.setKpiB5Result(mainResult);
        detailResult.setAnalysisDate(analysisDate);
        detailResult.setStationsPresent(allPartners.size());
        detailResult.setStationsWithoutSpontaneous(stationsWithoutSpontaneous);
        detailResult.setPercentageNoSpontaneous(percentageNoSpontaneous);
        detailResult.setOutcome(detailOutcome);

        detailResult = kpiB5DetailResultRepository.save(detailResult);

        // Crea analytic data (sembra essere una replica del detail result)
        createAnalyticData(detailResult, allPartners, analysisDate);
    }

    private void createAnalyticData(KpiB5DetailResult detailResult, List<PagopaSpontaneous> allPartners, LocalDate analysisDate) {
        KpiB5AnalyticData analyticData = new KpiB5AnalyticData();
        analyticData.setInstance(detailResult.getInstance());
        analyticData.setInstanceModule(detailResult.getInstanceModule());
        analyticData.setKpiB5DetailResult(detailResult);
        analyticData.setAnalysisDate(analysisDate);
        analyticData.setStationsPresent(allPartners.size());
        analyticData.setStationsWithoutSpontaneous(detailResult.getStationsWithoutSpontaneous());
        analyticData.setPercentageNoSpontaneous(detailResult.getPercentageNoSpontaneous());
        analyticData.setOutcome(detailResult.getOutcome());

        analyticData = kpiB5AnalyticDataRepository.save(analyticData);

        // Crea drill-down per ogni partner (legacy)
        createDrillDownData(analyticData, allPartners);
        
        // Crea snapshot dei dati per drill-down storico
        createSpontaneousSnapshot(analyticData, allPartners);
    }

    private void createDrillDownData(KpiB5AnalyticData analyticData, List<PagopaSpontaneous> partners) {
        for (PagopaSpontaneous partner : partners) {
            KpiB5AnalyticDrillDown drillDown = new KpiB5AnalyticDrillDown();
            
            // Imposta la relazione con KpiB5AnalyticData
            drillDown.setKpiB5AnalyticData(analyticData);
            
            // Copia i dati dal partner come snapshot
            drillDown.setPartnerFiscalCode(partner.getCfPartner());
            drillDown.setStationCode(partner.getStation());
            drillDown.setFiscalCode(partner.getStation());
            drillDown.setSpontaneousPayment(partner.getSpontaneousPayment());
            
            // Determina l'enum dal boolean
            if (Boolean.TRUE.equals(partner.getSpontaneousPayment())) {
                drillDown.setSpontaneousPayments(SpontaneousPayments.ATTIVI);
            } else {
                drillDown.setSpontaneousPayments(SpontaneousPayments.NON_ATTIVI);
            }

            kpiB5AnalyticDrillDownRepository.save(drillDown);
        }
    }

    /**
     * Creates a historical snapshot of pagopa_spontaneous data at the time of KPI calculation.
     * This ensures that drilldown data remains consistent over time even if the original data changes.
     */
    private void createSpontaneousSnapshot(KpiB5AnalyticData analyticData, List<PagopaSpontaneous> partners) {
        log.debug("Creating spontaneous snapshot for analyticData: {}, partners count: {}", 
                  analyticData.getId(), partners.size());
        
        for (PagopaSpontaneous partner : partners) {
            SpontaneousDrilldown snapshot = new SpontaneousDrilldown();
            
            // Link to all related entities for consistency and cleanup
            snapshot.setInstance(analyticData.getInstance());
            snapshot.setInstanceModule(analyticData.getInstanceModule());
            snapshot.setKpiB5AnalyticData(analyticData);
            
            // Copy current state as snapshot
            snapshot.setPartnerId(partner.getId());
            snapshot.setPartnerName(null); // PagopaSpontaneous doesn't have partnerName
            snapshot.setPartnerFiscalCode(partner.getCfPartner());
            snapshot.setStationCode(partner.getStation());
            snapshot.setFiscalCode(partner.getStation());
            snapshot.setSpontaneousPayment(partner.getSpontaneousPayment());
            
            // Note: getSpontaneousPayments() is a @Transient method that derives enum from boolean
            // No need to set enum separately as it's computed from spontaneousPayment boolean

            spontaneousDrilldownRepository.save(snapshot);
        }
        
        log.debug("Spontaneous snapshot created successfully for {} partners", partners.size());
    }
}