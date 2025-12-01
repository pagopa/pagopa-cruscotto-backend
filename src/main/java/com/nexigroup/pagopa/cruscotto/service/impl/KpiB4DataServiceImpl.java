package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.repository.InstanceRepository;
import com.nexigroup.pagopa.cruscotto.service.KpiB4DataService;
import com.nexigroup.pagopa.cruscotto.service.KpiB4Service;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceModuleDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB4ResultDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiConfigurationDTO;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing KPI B.4 data.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class KpiB4DataServiceImpl implements KpiB4DataService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KpiB4DataServiceImpl.class);
    
    private final KpiB4Service kpiB4Service;
    private final InstanceRepository instanceRepository;

    /**
     * Save KPI B.4 results in the three tables (Result, DetailResult, AnalyticData)
     * This method is transactional and will handle the delete and save operations correctly.
     * 
     * @param instanceDTO the instance
     * @param instanceModuleDTO the instance module
     * @param kpiConfigurationDTO the KPI configuration
     * @param analysisDate the analysis date
     * @param outcome the calculation outcome
     */
    @Override
    public OutcomeStatus saveKpiB4Results(InstanceDTO instanceDTO, InstanceModuleDTO instanceModuleDTO, 
                                         KpiConfigurationDTO kpiConfigurationDTO, LocalDate analysisDate, 
                                         OutcomeStatus outcome) {
        
        LOGGER.info("Starting KPI B.4 calculation and save for instance: {}, module: {}, date: {}", 
                   instanceDTO.getId(), instanceModuleDTO.getId(), analysisDate);

        try {
            // Recupero l'entità Instance completa per il calcolo
            Instance instance = instanceRepository.findById(instanceDTO.getId())
                .orElseThrow(() -> new RuntimeException("Instance not found: " + instanceDTO.getId()));

            // Eseguo il calcolo completo del KPI B.4 che salva automaticamente i risultati
            // Il metodo executeKpiB4Calculation già calcola correttamente l'outcome basandosi sui detail results
            KpiB4ResultDTO result = kpiB4Service.executeKpiB4Calculation(instance);
            
            // Restituisci l'outcome calcolato da executeKpiB4Calculation (non sovrascriverlo)
            OutcomeStatus finalOutcome = result.getOutcome();
            
            LOGGER.info("KPI B.4 calculation completed successfully for instance: {}. Final outcome: {}", 
                       instanceDTO.getId(), finalOutcome);
            
            return finalOutcome;
                       
        } catch (Exception e) {
            LOGGER.error("Error calculating KPI B.4 for instance: {} - {}", instanceDTO.getId(), e.getMessage());
            throw new RuntimeException("KPI B.4 calculation failed: " + e.getMessage(), e);
        }
    }
}