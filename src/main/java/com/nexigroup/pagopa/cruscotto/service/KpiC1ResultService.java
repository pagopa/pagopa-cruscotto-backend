package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.KpiC1Result;
import com.nexigroup.pagopa.cruscotto.domain.KpiConfiguration;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.repository.KpiC1ResultRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiConfigurationRepository;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiC1ResultDTO;
import com.nexigroup.pagopa.cruscotto.service.mapper.KpiC1ResultMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service per la gestione dei risultati principali del KPI C.1
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class KpiC1ResultService {

    private final KpiC1ResultRepository kpiC1ResultRepository;
    private final KpiC1ResultMapper kpiC1ResultMapper;
    private final KpiConfigurationRepository kpiConfigurationRepository;

    /**
     * Salva un risultato KPI C.1
     */
    public KpiC1Result save(KpiC1Result kpiC1Result) {
        log.debug("Saving KpiC1Result: {}", kpiC1Result);
        return kpiC1ResultRepository.save(kpiC1Result);
    }

    /**
     * Trova un risultato per instance, instance module e data di riferimento
     */
    @Transactional(readOnly = true)
    public Optional<KpiC1Result> findByInstanceAndInstanceModuleAndReferenceDate(
            Long instanceId, Long instanceModuleId, LocalDate referenceDate) {
        log.debug("Finding KpiC1Result for instanceId: {}, instanceModuleId: {}, referenceDate: {}",
                instanceId, instanceModuleId, referenceDate);
        return kpiC1ResultRepository.findByInstanceAndInstanceModuleAndReferenceDate(
                instanceId, instanceModuleId, referenceDate);
    }

    /**
     * Trova tutti i risultati per una instance specifica
     */
    @Transactional(readOnly = true)
    public List<KpiC1Result> findByInstanceId(Long instanceId) {
        log.debug("Finding KpiC1Results for instanceId: {}", instanceId);
        return kpiC1ResultRepository.findByInstanceId(instanceId);
    }

    /**
     * Trova tutti i risultati per un periodo specifico
     */
    @Transactional(readOnly = true)
    public List<KpiC1Result> findByReferenceDateBetween(LocalDate startDate, LocalDate endDate) {
        log.debug("Finding KpiC1Results between {} and {}", startDate, endDate);
        return kpiC1ResultRepository.findByReferenceDateBetween(startDate, endDate);
    }

    /**
     * Trova i risultati più recenti per ogni instance
     */
    @Transactional(readOnly = true)
    public List<KpiC1Result> findLatestResultsPerInstance() {
        log.debug("Finding latest KpiC1Results per instance");
        return kpiC1ResultRepository.findLatestResultsPerInstance();
    }

    /**
     * Conta i risultati compliant in un periodo
     */
    @Transactional(readOnly = true)
    public long countCompliantResultsByPeriod(LocalDate startDate, LocalDate endDate) {
        log.debug("Counting compliant results between {} and {}", startDate, endDate);
        return kpiC1ResultRepository.countCompliantResultsByPeriod(startDate, endDate);
    }

    /**
     * Trova i risultati non compliant per un periodo
     */
    @Transactional(readOnly = true)
    public List<KpiC1Result> findNonCompliantResultsByPeriod(LocalDate startDate, LocalDate endDate) {
        log.debug("Finding non-compliant results between {} and {}", startDate, endDate);
        return kpiC1ResultRepository.findNonCompliantResultsByPeriod(startDate, endDate);
    }

    /**
     * Elimina tutti i risultati
     */
    public void deleteAll() {
        log.debug("Deleting all KpiC1Results");
        kpiC1ResultRepository.deleteAll();
    }

    /**
     * Elimina i risultati più vecchi di una data specifica
     */
    public void deleteByReferenceDateBefore(LocalDate cutoffDate) {
        log.debug("Deleting KpiC1Results before {}", cutoffDate);
        kpiC1ResultRepository.deleteByReferenceDateBefore(cutoffDate);
    }

    /**
     * Trova tutti i risultati per instance module (entità)
     */
    @Transactional(readOnly = true)
    public List<KpiC1Result> findByInstanceModuleIdEntities(Long instanceModuleId) {
        log.debug("Finding KpiC1Results entities for instanceModuleId: {}", instanceModuleId);
        return kpiC1ResultRepository.findByInstanceModuleId(instanceModuleId);
    }

    /**
     * Trova tutti i risultati per instance module (DTOs)
     */
    @Transactional(readOnly = true)
    public List<KpiC1ResultDTO> findByInstanceModuleId(Long instanceModuleId) {
        log.debug("Finding KpiC1Results DTOs for instanceModuleId: {}", instanceModuleId);
        return kpiC1ResultRepository.findByInstanceModuleId(instanceModuleId)
            .stream()
            .map(result -> {
                KpiC1ResultDTO dto = kpiC1ResultMapper.toDto(result);
                try {
                    if (result.getInstanceModule() != null && result.getInstanceModule().getModule() != null) {
                        KpiConfiguration configuration = kpiConfigurationRepository
                            .findByModule(result.getInstanceModule().getModule())
                            .orElse(null);
                        if (configuration != null) {
                            // tolerance = notificationTolerance (default 0 if null)
                            dto.setTolerance(configuration.getNotificationTolerance() != null ? configuration.getNotificationTolerance() : java.math.BigDecimal.ZERO);
                            // evaluationType (default MESE if null)
                            dto.setEvaluationType(configuration.getEvaluationType() != null ? configuration.getEvaluationType() : EvaluationType.MESE);
                        } else {
                            dto.setTolerance(java.math.BigDecimal.ZERO);
                            dto.setEvaluationType(EvaluationType.MESE);
                        }
                    } else {
                        dto.setTolerance(java.math.BigDecimal.ZERO);
                        dto.setEvaluationType(EvaluationType.MESE);
                    }
                } catch (Exception e) {
                    log.warn("Unable to enrich KpiC1ResultDTO with configuration for instanceModuleId {}: {}", instanceModuleId, e.getMessage());
                    if (dto.getTolerance() == null) dto.setTolerance(java.math.BigDecimal.ZERO);
                    if (dto.getEvaluationType() == null) dto.setEvaluationType(EvaluationType.MESE);
                }
                return dto;
            })
            .collect(Collectors.toList());
    }

    /**
     * Trova tutti i risultati per instance e instance module
     */
    @Transactional(readOnly = true)
    public List<KpiC1Result> findByInstanceAndInstanceModule(Long instanceId, Long instanceModuleId) {
        log.debug("Finding KpiC1Results for instanceId: {} and instanceModuleId: {}", 
                instanceId, instanceModuleId);
        return kpiC1ResultRepository.findByInstanceAndInstanceModule(instanceId, instanceModuleId);
    }
}