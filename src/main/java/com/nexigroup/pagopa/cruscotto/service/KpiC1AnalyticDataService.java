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
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
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

    /* METODI COMMENTATI - usavano il campo cfInstitution che è stato rimosso
    
    @Transactional(readOnly = true)
    public List<KpiC1AnalyticData> findByCfInstitutionAndDataBetween(
            String cfInstitution, LocalDate startDate, LocalDate endDate) {
        log.debug("Finding KpiC1AnalyticData for cfInstitution: {} between {} and {}", 
                cfInstitution, startDate, endDate);
        return kpiC1AnalyticDataRepository.findByCfInstitutionAndDataBetween(
                cfInstitution, startDate, endDate);
    }
    */

    /**
     * Trova i dati analitici per una data specifica
     */
    @Transactional(readOnly = true)
    public List<KpiC1AnalyticData> findByData(LocalDate data) {
        log.debug("Finding KpiC1AnalyticData for data: {}", data);
        return kpiC1AnalyticDataRepository.findByData(data);
    }

    /* METODI COMMENTATI - usavano il campo cfInstitution che è stato rimosso
    
    @Transactional(readOnly = true)
    public List<Object[]> aggregateByReferenceDateAndCfInstitution(LocalDate referenceDate) {
        log.debug("Aggregating KpiC1AnalyticData by CF institution for referenceDate: {}", referenceDate);
        return kpiC1AnalyticDataRepository.aggregateByReferenceDateAndCfInstitution(referenceDate);
    }
    */

    /**
     * Calcola totali globali per una data di riferimento
     */
    @Transactional(readOnly = true)
    public Object[] calculateTotalsByReferenceDate(LocalDate referenceDate) {
        log.debug("Calculating totals for referenceDate: {}", referenceDate);
        return kpiC1AnalyticDataRepository.calculateTotalsByReferenceDate(referenceDate);
    }

    /* METODI COMMENTATI - usavano il campo cfInstitution che è stato rimosso
    
    @Transactional(readOnly = true)
    public List<KpiC1AnalyticData> findByInstanceIdAndReferenceDateAndCfInstitution(
            Long instanceId, LocalDate referenceDate, String cfInstitution) {
        log.debug("Finding KpiC1AnalyticData for instanceId: {}, referenceDate: {}, cfInstitution: {}", 
                instanceId, referenceDate, cfInstitution);
        return kpiC1AnalyticDataRepository.findByInstanceIdAndReferenceDateAndCfInstitution(
                instanceId, referenceDate, cfInstitution);
    }

    @Transactional(readOnly = true)
    public List<String> findDistinctCfInstitutionByReferenceDate(LocalDate referenceDate) {
        log.debug("Finding distinct CF institutions for referenceDate: {}", referenceDate);
        return kpiC1AnalyticDataRepository.findDistinctCfInstitutionByReferenceDate(referenceDate);
    }

    @Transactional(readOnly = true)
    public long countByReferenceDateAndCfInstitution(LocalDate referenceDate, String cfInstitution) {
        log.debug("Counting records for referenceDate: {} and cfInstitution: {}", 
                referenceDate, cfInstitution);
        return kpiC1AnalyticDataRepository.countByReferenceDateAndCfInstitution(referenceDate, cfInstitution);
    }

    @Transactional(readOnly = true)
    public List<LocalDate> findDistinctDataByCfInstitutionAndPeriod(
            String cfInstitution, LocalDate startDate, LocalDate endDate) {
        log.debug("Finding distinct dates for cfInstitution: {} between {} and {}", 
                cfInstitution, startDate, endDate);
        return kpiC1AnalyticDataRepository.findDistinctDataByCfInstitutionAndPeriod(
                cfInstitution, startDate, endDate);
    }
    */

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
                    var dr = optDetail.orElseThrow();
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
        // Recupero diretto della soglia configurata dal dettaglio (già espressa come percentuale richiesta di invio messaggi)
        double requiredMessagePercentage = 100.0; // fallback se non reperibile
        try {
            if (kpiC1DetailResultRepository != null) {
                var opt = kpiC1DetailResultRepository.findById(detailResultId);
                if (opt.isPresent()) {
                    var dr = opt.orElseThrow();
                    if (dr.getConfiguredThreshold() != null) {
                        requiredMessagePercentage = dr.getConfiguredThreshold().doubleValue(); // uso diretto
                    }
                }
            }
        } catch (Exception ex) {
            log.warn("Unable to read configuredThreshold for detailResultId {}: {}", detailResultId, ex.getMessage());
        }
        log.debug("Required message percentage={} for detailResultId={}", requiredMessagePercentage, detailResultId);

        // Aggrega i dati per giorno
        Map<LocalDate, List<KpiC1AnalyticData>> dataByDay = rows.stream()
            .collect(Collectors.groupingBy(KpiC1AnalyticData::getData));

        return dataByDay.entrySet().stream()
            .map(entry -> {
                LocalDate day = entry.getKey();
                List<KpiC1AnalyticData> dayRows = entry.getValue();
                
                // Prendi il primo record per i dati comuni
                KpiC1AnalyticData firstRow = dayRows.get(0);
                
                // Aggrega i conteggi per il giorno
                int totalInstitutions = dayRows.size();
                long totalPositions = dayRows.stream()
                    .mapToLong(r -> r.getPositionNumber() != null ? r.getPositionNumber() : 0L)
                    .sum();
                long totalMessages = dayRows.stream()
                    .mapToLong(r -> r.getMessageNumber() != null ? r.getMessageNumber() : 0L)
                    .sum();
                
                KpiC1AnalyticDataDTO dto = new KpiC1AnalyticDataDTO();
                dto.setId(firstRow.getId());
                dto.setInstanceId(firstRow.getInstance().getId());
                dto.setInstanceModuleId(firstRow.getInstanceModule().getId());
                dto.setKpiC1DetailResultId(detailResultId);
                dto.setAnalysisDate(firstRow.getReferenceDate());
                dto.setDataDate(day);
                dto.setInstitutionCount(totalInstitutions);
                dto.setPositionsCount(totalPositions);
                dto.setMessagesCount(totalMessages);
                return dto;
            })
            .sorted(Comparator.comparing(KpiC1AnalyticDataDTO::getDataDate))
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