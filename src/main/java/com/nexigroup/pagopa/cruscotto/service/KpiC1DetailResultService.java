package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.KpiC1DetailResult;
import com.nexigroup.pagopa.cruscotto.repository.KpiC1DetailResultRepository;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiC1DetailResultDTO;
import com.nexigroup.pagopa.cruscotto.service.mapper.KpiC1DetailResultMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service per la gestione dei risultati dettagliati del KPI C.1
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class KpiC1DetailResultService {

    private final KpiC1DetailResultRepository kpiC1DetailResultRepository;
    private final KpiC1DetailResultMapper kpiC1DetailResultMapper;

    /**
     * Salva un risultato dettagliato KPI C.1
     */
    public KpiC1DetailResult save(KpiC1DetailResult kpiC1DetailResult) {
        log.debug("Saving KpiC1DetailResult: {}", kpiC1DetailResult);
        return kpiC1DetailResultRepository.save(kpiC1DetailResult);
    }

    /**
     * Salva una lista di risultati dettagliati
     */
    public List<KpiC1DetailResult> saveAll(List<KpiC1DetailResult> kpiC1DetailResults) {
        log.debug("Saving {} KpiC1DetailResults", kpiC1DetailResults.size());
        return kpiC1DetailResultRepository.saveAll(kpiC1DetailResults);
    }

    /**
     * Trova un risultato dettagliato specifico
     */
    @Transactional(readOnly = true)
    public Optional<KpiC1DetailResult> findByInstanceAndInstanceModuleAndReferenceDateAndCfInstitution(
            Long instanceId, Long instanceModuleId, LocalDate referenceDate, String cfInstitution) {
        log.debug("Finding KpiC1DetailResult for instanceId: {}, instanceModuleId: {}, " +
                "referenceDate: {}, cfInstitution: {}",
                instanceId, instanceModuleId, referenceDate, cfInstitution);
        return kpiC1DetailResultRepository.findByInstanceAndInstanceModuleAndReferenceDateAndCfInstitution(
                instanceId, instanceModuleId, referenceDate, cfInstitution);
    }

    /**
     * Trova tutti i risultati dettagliati per una instance e data di riferimento
     */
    @Transactional(readOnly = true)
    public List<KpiC1DetailResult> findByInstanceIdAndReferenceDate(Long instanceId, LocalDate referenceDate) {
        log.debug("Finding KpiC1DetailResults for instanceId: {} and referenceDate: {}", 
                instanceId, referenceDate);
        return kpiC1DetailResultRepository.findByInstanceIdAndReferenceDate(instanceId, referenceDate);
    }

    /**
     * Trova tutti i risultati per un CF institution specifico
     */
    @Transactional(readOnly = true)
    public List<KpiC1DetailResult> findByCfInstitution(String cfInstitution) {
        log.debug("Finding KpiC1DetailResults for cfInstitution: {}", cfInstitution);
        return kpiC1DetailResultRepository.findByCfInstitution(cfInstitution);
    }

    /**
     * Trova i risultati dettagliati per un periodo specifico
     */
    @Transactional(readOnly = true)
    public List<KpiC1DetailResult> findByInstanceIdAndReferenceDateBetween(
            Long instanceId, LocalDate startDate, LocalDate endDate) {
        log.debug("Finding KpiC1DetailResults for instanceId: {} between {} and {}", 
                instanceId, startDate, endDate);
        return kpiC1DetailResultRepository.findByInstanceIdAndReferenceDateBetween(
                instanceId, startDate, endDate);
    }

    /**
     * Trova i risultati non compliant per una data specifica
     */
    @Transactional(readOnly = true)
    public List<KpiC1DetailResult> findNonCompliantByReferenceDate(LocalDate referenceDate) {
        log.debug("Finding non-compliant KpiC1DetailResults for referenceDate: {}", referenceDate);
        return kpiC1DetailResultRepository.findNonCompliantByReferenceDate(referenceDate);
    }

    /**
     * Trova i risultati compliant per una data specifica
     */
    @Transactional(readOnly = true)
    public List<KpiC1DetailResult> findCompliantByReferenceDate(LocalDate referenceDate) {
        log.debug("Finding compliant KpiC1DetailResults for referenceDate: {}", referenceDate);
        return kpiC1DetailResultRepository.findCompliantByReferenceDate(referenceDate);
    }

    /**
     * Conta i CF institution compliant per una data specifica
     */
    @Transactional(readOnly = true)
    public long countCompliantByReferenceDate(LocalDate referenceDate) {
        log.debug("Counting compliant CF institutions for referenceDate: {}", referenceDate);
        return kpiC1DetailResultRepository.countCompliantByReferenceDate(referenceDate);
    }

    /**
     * Conta il totale dei CF institution per una data specifica
     */
    @Transactional(readOnly = true)
    public long countTotalByReferenceDate(LocalDate referenceDate) {
        log.debug("Counting total CF institutions for referenceDate: {}", referenceDate);
        return kpiC1DetailResultRepository.countTotalByReferenceDate(referenceDate);
    }

    /**
     * Trova i risultati sotto una soglia specifica
     */
    @Transactional(readOnly = true)
    public List<KpiC1DetailResult> findBelowThresholdByReferenceDate(
            LocalDate referenceDate, BigDecimal soglia) {
        log.debug("Finding KpiC1DetailResults below threshold {} for referenceDate: {}", 
                soglia, referenceDate);
        return kpiC1DetailResultRepository.findBelowThresholdByReferenceDate(referenceDate, soglia);
    }

    /**
     * Elimina tutti i risultati dettagliati
     */
    public void deleteAll() {
        log.debug("Deleting all KpiC1DetailResults");
        kpiC1DetailResultRepository.deleteAll();
    }

    /**
     * Trova i risultati dettagliati correlati a un KpiC1Result specifico (DTOs)
     */
    @Transactional(readOnly = true)
    public List<KpiC1DetailResultDTO> findByResultId(Long resultId) {
        log.debug("Finding KpiC1DetailResults DTOs for resultId: {}", resultId);
        return kpiC1DetailResultRepository.findByResultId(resultId)
            .stream()
            .map(kpiC1DetailResultMapper::toDto)
            .collect(Collectors.toList());
    }

    /**
     * Elimina i risultati più vecchi di una data specifica
     */
    public void deleteByReferenceDateBefore(LocalDate cutoffDate) {
        log.debug("Deleting KpiC1DetailResults before {}", cutoffDate);
        kpiC1DetailResultRepository.deleteByReferenceDateBefore(cutoffDate);
    }
}