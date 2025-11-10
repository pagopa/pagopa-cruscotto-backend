package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.KpiC1Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository per l'entità KpiC1Result.
 * Gestisce le operazioni di accesso ai dati per i risultati principali del KPI C.1
 */
@Repository
public interface KpiC1ResultRepository extends JpaRepository<KpiC1Result, Long> {

    /**
     * Trova il risultato KPI C.1 per instance, instance module e data di riferimento
     */
    @Query("SELECT k FROM KpiC1Result k WHERE k.instance.id = :instanceId " +
           "AND k.instanceModule.id = :instanceModuleId " +
           "AND k.referenceDate = :referenceDate")
    Optional<KpiC1Result> findByInstanceAndInstanceModuleAndReferenceDate(
            @Param("instanceId") Long instanceId,
            @Param("instanceModuleId") Long instanceModuleId,
            @Param("referenceDate") LocalDate referenceDate);

    /**
     * Trova tutti i risultati KPI C.1 per una specifica instance
     */
    @Query("SELECT k FROM KpiC1Result k WHERE k.instance.id = :instanceId " +
           "ORDER BY k.referenceDate DESC")
    List<KpiC1Result> findByInstanceId(@Param("instanceId") Long instanceId);

    /**
     * Trova tutti i risultati KPI C.1 per un periodo specifico
     */
    @Query("SELECT k FROM KpiC1Result k WHERE k.referenceDate BETWEEN :startDate AND :endDate " +
           "ORDER BY k.referenceDate DESC, k.instance.id")
    List<KpiC1Result> findByReferenceDateBetween(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * Trova tutti i risultati KPI C.1 per instance module
     */
    @Query("SELECT k FROM KpiC1Result k WHERE k.instanceModule.id = :instanceModuleId " +
           "ORDER BY k.referenceDate DESC")
    List<KpiC1Result> findByInstanceModuleId(@Param("instanceModuleId") Long instanceModuleId);

    /**
     * Trova tutti i risultati KPI C.1 per instance e instance module
     */
    @Query("SELECT k FROM KpiC1Result k WHERE k.instance.id = :instanceId " +
           "AND k.instanceModule.id = :instanceModuleId " +
           "ORDER BY k.referenceDate DESC")
    List<KpiC1Result> findByInstanceAndInstanceModule(
            @Param("instanceId") Long instanceId,
            @Param("instanceModuleId") Long instanceModuleId);

    /**
     * Trova i risultati più recenti per ogni instance
     */
    @Query("SELECT k FROM KpiC1Result k WHERE k.referenceDate = " +
           "(SELECT MAX(k2.referenceDate) FROM KpiC1Result k2 WHERE k2.instance.id = k.instance.id) " +
           "ORDER BY k.instance.id")
    List<KpiC1Result> findLatestResultsPerInstance();

    /**
     * Conta i risultati compliant nel periodo specificato
     */
    @Query("SELECT COUNT(k) FROM KpiC1Result k WHERE k.compliant = true " +
           "AND k.referenceDate BETWEEN :startDate AND :endDate")
    long countCompliantResultsByPeriod(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * Trova i risultati non compliant per un periodo specifico
     */
    @Query("SELECT k FROM KpiC1Result k WHERE k.compliant = false " +
           "AND k.referenceDate BETWEEN :startDate AND :endDate " +
           "ORDER BY k.referenceDate DESC, k.instance.id")
    List<KpiC1Result> findNonCompliantResultsByPeriod(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * Elimina i risultati più vecchi di una certa data
     */
    void deleteByReferenceDateBefore(LocalDate cutoffDate);
}