package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.KpiC1AnalyticData;
import com.nexigroup.pagopa.cruscotto.repository.KpiC1AnalyticDataRepository;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiC1AnalyticDataDTO;
import com.nexigroup.pagopa.cruscotto.service.mapper.KpiC1AnalyticDataMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service per la gestione dei dati analitici del KPI C.1
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class KpiC1AnalyticDataService {

    private final KpiC1AnalyticDataRepository kpiC1AnalyticDataRepository;
    private final KpiC1AnalyticDataMapper kpiC1AnalyticDataMapper;
    @jakarta.annotation.Resource
    private com.nexigroup.pagopa.cruscotto.repository.KpiC1DetailResultRepository kpiC1DetailResultRepository;

    /**
     * Salva un dato analitico KPI C.1
     */
    public KpiC1AnalyticData save(KpiC1AnalyticData kpiC1AnalyticData) {
        log.debug("Saving KpiC1AnalyticData: {}", kpiC1AnalyticData);
        return kpiC1AnalyticDataRepository.save(kpiC1AnalyticData);
    }

    /**
     * Salva una lista di dati analitici
     */
    public List<KpiC1AnalyticData> saveAll(List<KpiC1AnalyticData> kpiC1AnalyticDataList) {
        log.debug("Saving {} KpiC1AnalyticData records", kpiC1AnalyticDataList.size());
        return kpiC1AnalyticDataRepository.saveAll(kpiC1AnalyticDataList);
    }

    /**
     * Trova tutti i dati analitici per una instance e data di riferimento
     */
    @Transactional(readOnly = true)
    public List<KpiC1AnalyticData> findByInstanceIdAndReferenceDate(Long instanceId, LocalDate referenceDate) {
        log.debug("Finding KpiC1AnalyticData for instanceId: {} and referenceDate: {}", 
                instanceId, referenceDate);
        return kpiC1AnalyticDataRepository.findByInstanceIdAndReferenceDate(instanceId, referenceDate);
    }

    /**
     * Trova i dati analitici per un CF institution e periodo specifici
     */
    @Transactional(readOnly = true)
    public List<KpiC1AnalyticData> findByCfInstitutionAndDataBetween(
            String cfInstitution, LocalDate startDate, LocalDate endDate) {
        log.debug("Finding KpiC1AnalyticData for cfInstitution: {} between {} and {}", 
                cfInstitution, startDate, endDate);
        return kpiC1AnalyticDataRepository.findByCfInstitutionAndDataBetween(
                cfInstitution, startDate, endDate);
    }

    /**
     * Trova i dati analitici per una data specifica
     */
    @Transactional(readOnly = true)
    public List<KpiC1AnalyticData> findByData(LocalDate data) {
        log.debug("Finding KpiC1AnalyticData for data: {}", data);
        return kpiC1AnalyticDataRepository.findByData(data);
    }

    /**
     * Aggrega i dati per CF institution in una data di riferimento
     */
    @Transactional(readOnly = true)
    public List<Object[]> aggregateByReferenceDateAndCfInstitution(LocalDate referenceDate) {
        log.debug("Aggregating KpiC1AnalyticData by CF institution for referenceDate: {}", referenceDate);
        return kpiC1AnalyticDataRepository.aggregateByReferenceDateAndCfInstitution(referenceDate);
    }

    /**
     * Calcola totali globali per una data di riferimento
     */
    @Transactional(readOnly = true)
    public Object[] calculateTotalsByReferenceDate(LocalDate referenceDate) {
        log.debug("Calculating totals for referenceDate: {}", referenceDate);
        return kpiC1AnalyticDataRepository.calculateTotalsByReferenceDate(referenceDate);
    }

    /**
     * Trova i dati per instance, data di riferimento e CF institution specifici
     */
    @Transactional(readOnly = true)
    public List<KpiC1AnalyticData> findByInstanceIdAndReferenceDateAndCfInstitution(
            Long instanceId, LocalDate referenceDate, String cfInstitution) {
        log.debug("Finding KpiC1AnalyticData for instanceId: {}, referenceDate: {}, cfInstitution: {}", 
                instanceId, referenceDate, cfInstitution);
        return kpiC1AnalyticDataRepository.findByInstanceIdAndReferenceDateAndCfInstitution(
                instanceId, referenceDate, cfInstitution);
    }

    /**
     * Trova i CF institution unici per una data di riferimento
     */
    @Transactional(readOnly = true)
    public List<String> findDistinctCfInstitutionByReferenceDate(LocalDate referenceDate) {
        log.debug("Finding distinct CF institutions for referenceDate: {}", referenceDate);
        return kpiC1AnalyticDataRepository.findDistinctCfInstitutionByReferenceDate(referenceDate);
    }

    /**
     * Conta i record per CF institution in una data di riferimento
     */
    @Transactional(readOnly = true)
    public long countByReferenceDateAndCfInstitution(LocalDate referenceDate, String cfInstitution) {
        log.debug("Counting records for referenceDate: {} and cfInstitution: {}", 
                referenceDate, cfInstitution);
        return kpiC1AnalyticDataRepository.countByReferenceDateAndCfInstitution(referenceDate, cfInstitution);
    }

    /**
     * Trova le date per cui esistono dati per un CF institution specifico
     */
    @Transactional(readOnly = true)
    public List<LocalDate> findDistinctDataByCfInstitutionAndPeriod(
            String cfInstitution, LocalDate startDate, LocalDate endDate) {
        log.debug("Finding distinct dates for cfInstitution: {} between {} and {}", 
                cfInstitution, startDate, endDate);
        return kpiC1AnalyticDataRepository.findDistinctDataByCfInstitutionAndPeriod(
                cfInstitution, startDate, endDate);
    }

    /**
     * Elimina tutti i dati analitici
     */
    public void deleteAll() {
        log.debug("Deleting all KpiC1AnalyticData");
        kpiC1AnalyticDataRepository.deleteAll();
    }

    /**
     * Trova i dati analitici correlati a un KpiC1DetailResult specifico (DTOs)
     */
    @Transactional(readOnly = true)
    public List<KpiC1AnalyticDataDTO> findByDetailResultId(Long detailResultId) {
        log.debug("Finding KpiC1AnalyticData DTOs for detailResultId: {}", detailResultId);
    List<KpiC1AnalyticData> fetchedRows = kpiC1AnalyticDataRepository.findByDetailResultId(detailResultId);
    List<KpiC1AnalyticData> rows = fetchedRows; // will be reassigned after optional period filtering
        // Safeguard: further restrict analytic rows to the evaluation period of the detail result (in case query is broadened)
        try {
            if (kpiC1DetailResultRepository != null) {
                var optDetail = kpiC1DetailResultRepository.findById(detailResultId);
                if (optDetail.isPresent()) {
                    var dr = optDetail.get();
                    LocalDate start = dr.getEvaluationStartDate();
                    LocalDate end = dr.getEvaluationEndDate();
                    if (start != null && end != null) {
                        List<KpiC1AnalyticData> oldRows = rows;
                        rows = oldRows.stream()
                            .filter(r -> !r.getData().isBefore(start) && !r.getData().isAfter(end))
                            .collect(Collectors.toList());
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Unable to apply evaluation period filter for detailResultId {}: {}", detailResultId, e.getMessage());
        }
        // (Second safeguard block removed to avoid duplicate filtering and variable capture issues)
        if (rows.isEmpty()) {
            return List.of();
        }

        // Per calcolare institutionCount e koInstitutionCount dobbiamo conoscere la soglia di compliance.
        // Recuperiamo il detail result per ottenere la soglia configurata (percentageCompliantInstitutions non serve qui).
        // Poiché non abbiamo un repository qui, riusiamo la reference date e instance/module dalle prime righe.
    // Prima riga disponibile (non utilizzata direttamente: manteniamo solo per eventuale estensione futura)

        // Tentiamo di inferire la soglia dai detail results aggregati salvati (AGGREGATED) se disponibile.
        // In assenza di accesso diretto al detail service qui, calcoleremo i KO sul principio business: required = 100 - tolerance.
        // Non avendo la tolerance sul singolo analytic row, la lasciamo a 100 (nessun margine) come fallback.
        double computedRequiredMessagePercentage = 100.0; // Fallback conservativo.
        try {
            if (kpiC1DetailResultRepository != null) {
                var opt = kpiC1DetailResultRepository.findById(detailResultId);
                if (opt.isPresent() && opt.get().getConfiguredThreshold() != null) {
                    double tolerance = opt.get().getConfiguredThreshold().doubleValue();
                    computedRequiredMessagePercentage = 100.0 - tolerance;
                    if (computedRequiredMessagePercentage < 0.0) computedRequiredMessagePercentage = 0.0;
                    if (computedRequiredMessagePercentage > 100.0) computedRequiredMessagePercentage = 100.0;
                }
            }
        } catch (Exception ex) {
            log.debug("Unable to derive tolerance from detailResultId {}: {}", detailResultId, ex.getMessage());
        }
        final double requiredMessagePercentage = computedRequiredMessagePercentage;

        // Calcolo per giorno: percentuale = messageNumber/positionNumber (regole speciali 0 -> 0% o 100%).
        // Raggruppiamo per ente per contare gli enti KO almeno in un giorno.
        Map<String, Boolean> entityCompliant = rows.stream().collect(Collectors.groupingBy(KpiC1AnalyticData::getCfInstitution,
            Collectors.collectingAndThen(Collectors.toList(), list -> {
                long sumPositions = list.stream().mapToLong(KpiC1AnalyticData::getPositionNumber).sum();
                long sumMessages = list.stream().mapToLong(KpiC1AnalyticData::getMessageNumber).sum();
                double pct;
                if (sumPositions == 0) {
                    pct = (sumMessages > 0) ? 100.0 : 0.0;
                } else {
                    pct = (double) sumMessages / (double) sumPositions * 100.0;
                }
                return pct >= requiredMessagePercentage; // confronto con soglia derivata da tolerance
            })));

        int totalInstitutions = entityCompliant.size();
        int koInstitutions = (int) entityCompliant.values().stream().filter(b -> !b).count();

        return rows.stream().map(kpiC1AnalyticDataMapper::toDto)
            .peek(dto -> {
                dto.setKpiC1DetailResultId(detailResultId);
                dto.setInstitutionCount(totalInstitutions);
                dto.setKoInstitutionCount(koInstitutions);
            })
            .collect(Collectors.toList());
    }

    /**
     * Elimina i dati più vecchi di una data specifica
     */
    public void deleteByReferenceDateBefore(LocalDate cutoffDate) {
        log.debug("Deleting KpiC1AnalyticData before {}", cutoffDate);
        kpiC1AnalyticDataRepository.deleteByReferenceDateBefore(cutoffDate);
    }
}