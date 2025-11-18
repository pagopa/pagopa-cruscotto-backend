package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.KpiC1DetailResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository per l'entità KpiC1DetailResult.
 * Gestisce le operazioni di accesso ai dati per i risultati dettagliati del KPI C.1
 */
@Repository
public interface KpiC1DetailResultRepository extends JpaRepository<KpiC1DetailResult, Long> {

    /**
     * Trova il risultato dettagliato per instance, instance module, data di riferimento e CF institution
     */
    @Query("SELECT k FROM KpiC1DetailResult k WHERE k.instance.id = :instanceId " +
           "AND k.instanceModule.id = :instanceModuleId " +
           "AND k.referenceDate = :referenceDate " +
           "AND k.cfInstitution = :cfInstitution")
    Optional<KpiC1DetailResult> findByInstanceAndInstanceModuleAndReferenceDateAndCfInstitution(
            @Param("instanceId") Long instanceId,
            @Param("instanceModuleId") Long instanceModuleId,
            @Param("referenceDate") LocalDate referenceDate,
            @Param("cfInstitution") String cfInstitution);

    /**
     * Trova tutti i risultati dettagliati per una specifica data di riferimento e instance
     */
    @Query("SELECT k FROM KpiC1DetailResult k WHERE k.instance.id = :instanceId " +
           "AND k.referenceDate = :referenceDate " +
           "ORDER BY k.cfInstitution")
    List<KpiC1DetailResult> findByInstanceIdAndReferenceDate(
            @Param("instanceId") Long instanceId,
            @Param("referenceDate") LocalDate referenceDate);

    /**
     * Trova tutti i risultati dettagliati per un CF institution specifico
     */
    @Query("SELECT k FROM KpiC1DetailResult k WHERE k.cfInstitution = :cfInstitution " +
           "ORDER BY k.referenceDate DESC")
    List<KpiC1DetailResult> findByCfInstitution(@Param("cfInstitution") String cfInstitution);

    /**
     * Trova i risultati dettagliati per un periodo e instance specifici
     */
    @Query("SELECT k FROM KpiC1DetailResult k WHERE k.instance.id = :instanceId " +
           "AND k.referenceDate BETWEEN :startDate AND :endDate " +
           "ORDER BY k.referenceDate DESC, k.cfInstitution")
    List<KpiC1DetailResult> findByInstanceIdAndReferenceDateBetween(
            @Param("instanceId") Long instanceId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * Trova tutti i risultati dettagliati non compliant per una data specifica
     */
    @Query("SELECT k FROM KpiC1DetailResult k WHERE k.referenceDate = :referenceDate " +
           "AND k.compliant = false " +
           "ORDER BY k.cfInstitution")
    List<KpiC1DetailResult> findNonCompliantByReferenceDate(@Param("referenceDate") LocalDate referenceDate);

    /**
     * Trova i risultati dettagliati compliant per una data specifica
     */
    @Query("SELECT k FROM KpiC1DetailResult k WHERE k.referenceDate = :referenceDate " +
           "AND k.compliant = true " +
           "ORDER BY k.cfInstitution")
    List<KpiC1DetailResult> findCompliantByReferenceDate(@Param("referenceDate") LocalDate referenceDate);

    /**
     * Conta i CF institution compliant per una data specifica
     */
    @Query("SELECT COUNT(k) FROM KpiC1DetailResult k WHERE k.referenceDate = :referenceDate " +
           "AND k.compliant = true")
    long countCompliantByReferenceDate(@Param("referenceDate") LocalDate referenceDate);

    /**
     * Conta il totale dei CF institution per una data specifica
     */
    @Query("SELECT COUNT(k) FROM KpiC1DetailResult k WHERE k.referenceDate = :referenceDate")
    long countTotalByReferenceDate(@Param("referenceDate") LocalDate referenceDate);

    /**
     * Trova i risultati dettagliati correlati a un KpiC1Result specifico
     * usando la foreign key diretta (come gli altri KPI)
     */
    @Query("SELECT dr FROM KpiC1DetailResult dr " +
           "WHERE dr.kpiC1Result.id = :resultId " +
           "ORDER BY dr.cfInstitution")
    List<KpiC1DetailResult> findByResultId(@Param("resultId") Long resultId);

    /**
     * Trova i risultati con percentuale di invio sotto una soglia specifica
     */
    @Query("SELECT k FROM KpiC1DetailResult k WHERE k.referenceDate = :referenceDate " +
           "AND k.sendingPercentage < :threshold " +
           "ORDER BY k.sendingPercentage ASC")
    List<KpiC1DetailResult> findBelowThresholdByReferenceDate(
            @Param("referenceDate") LocalDate referenceDate,
            @Param("threshold") java.math.BigDecimal threshold);

    /**
     * Elimina i risultati dettagliati più vecchi di una certa data
     */
    void deleteByReferenceDateBefore(LocalDate cutoffDate);
}