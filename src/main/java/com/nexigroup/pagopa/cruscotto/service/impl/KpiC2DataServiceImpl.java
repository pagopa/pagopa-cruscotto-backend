package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.repository.InstanceRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiC2DetailResultRepository;
import com.nexigroup.pagopa.cruscotto.service.KpiC2DataService;
import com.nexigroup.pagopa.cruscotto.service.KpiC2Service;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceModuleDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiC2ResultDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiConfigurationDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Service Implementation for managing KPI C.2 data.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class KpiC2DataServiceImpl implements KpiC2DataService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KpiC2DataServiceImpl.class);

    private final KpiC2Service kpiC2Service;
    private final InstanceRepository instanceRepository;
    private final KpiC2DetailResultRepository kpiC2DetailResultRepository;

    /**
     * Save KPI C.2 results in the three tables (Result, DetailResult, AnalyticData)
     * This method is transactional and will handle the delete and save operations correctly.
     *
     * @param instanceDTO the instance
     * @param instanceModuleDTO the instance module
     * @param kpiConfigurationDTO the KPI configuration
     * @param analysisDate the analysis date
     * @param outcome the calculation outcome
     */
    @Override
    public OutcomeStatus saveKpiC2Results(InstanceDTO instanceDTO, InstanceModuleDTO instanceModuleDTO,
                                          KpiConfigurationDTO kpiConfigurationDTO, LocalDate analysisDate,
                                          OutcomeStatus outcome) {

        LOGGER.info("Starting KPI C.2 calculation and save for instance: {}, module: {}, date: {}",
            instanceDTO.getId(), instanceModuleDTO.getId(), analysisDate);

        try {
            // Recupero l'entità Instance completa per il calcolo
            Instance instance = instanceRepository.findById(instanceDTO.getId())
                .orElseThrow(() -> new RuntimeException("Instance not found: " + instanceDTO.getId()));

            // Eseguo il calcolo completo del KPI C.2 che salva automaticamente i risultati
            KpiC2ResultDTO result = kpiC2Service.executeKpiC2Calculation(instance);

            // CORREZIONE BUG: Gestisce l'outcome basato sul tipo di valutazione
            OutcomeStatus finalOutcome = outcome; // Outcome calcolato dal job

            // Se il tipo di valutazione è MESE, controlla se ci sono detail results con outcome KO
            if (result.getId() != null && kpiConfigurationDTO.getEvaluationType() == EvaluationType.MESE) {
                boolean hasKoDetailResults = kpiC2DetailResultRepository.existsKoOutcomeByResultId(result.getId());

                if (hasKoDetailResults) {
                    LOGGER.warn("Found KO outcomes in detail results for monthly evaluation - setting overall outcome to KO for instance {}",
                        instanceDTO.getId());
                    finalOutcome = OutcomeStatus.KO;
                } else {
                    LOGGER.info("All detail results are OK for monthly evaluation - keeping outcome {} for instance {}",
                        outcome, instanceDTO.getId());
                }
            }

            // CORREZIONE BUG: Sovrascrivi l'outcome con quello corretto calcolato
            if (result.getId() != null) {
                kpiC2Service.updateKpiC2ResultOutcome(result.getId(), finalOutcome);
                LOGGER.info("Updated KPI C.2 result outcome from {} to {} for instance {} (evaluation type: {})",
                    result.getOutcome(), finalOutcome, instanceDTO.getId(),
                    kpiConfigurationDTO.getEvaluationType());
            }

            LOGGER.info("KPI C.2 calculation completed successfully for instance: {}. Final outcome: {}",
                instanceDTO.getId(), finalOutcome);

            return finalOutcome;

        } catch (Exception e) {
            LOGGER.error("Error calculating KPI C.2 for instance: {} - {}", instanceDTO.getId(), e.getMessage());
            throw new RuntimeException("KPI C.2 calculation failed: " + e.getMessage(), e);
        }
    }
}
