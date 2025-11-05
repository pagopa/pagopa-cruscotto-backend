package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.KpiC1AnalyticData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository per l'entità KpiC1AnalyticData.
 * Gestisce le operazioni di accesso ai dati analitici granulari del KPI C.1
 */
@Repository
public interface KpiC1AnalyticDataRepository extends JpaRepository<KpiC1AnalyticData, Long> {

    /**
     * Trova tutti i dati analitici per una specifica data di riferimento e instance
     */
    @Query("SELECT k FROM KpiC1AnalyticData k WHERE k.instance.id = :instanceId " +
           "AND k.referenceDate = :referenceDate " +
           "ORDER BY k.data, k.cfInstitution")
    List<KpiC1AnalyticData> findByInstanceIdAndReferenceDate(
            @Param("instanceId") Long instanceId,
            @Param("referenceDate") LocalDate referenceDate);

    /**
     * Trova i dati analitici per un CF institution e periodo specifici
     */
    @Query("SELECT k FROM KpiC1AnalyticData k WHERE k.cfInstitution = :cfInstitution " +
           "AND k.data BETWEEN :startDate AND :endDate " +
           "ORDER BY k.data")
    List<KpiC1AnalyticData> findByCfInstitutionAndDataBetween(
            @Param("cfInstitution") String cfInstitution,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * Trova i dati analitici per una data specifica
     */
    @Query("SELECT k FROM KpiC1AnalyticData k WHERE k.data = :data " +
           "ORDER BY k.cfInstitution")
    List<KpiC1AnalyticData> findByData(@Param("data") LocalDate data);

    /**
     * Aggrega i dati per CF institution in un periodo specifico
     */
    @Query("SELECT k.cfInstitution, " +
           "SUM(k.positionNumber) as totalPositions, " +
           "SUM(k.messageNumber) as totalMessages " +
           "FROM KpiC1AnalyticData k WHERE k.referenceDate = :referenceDate " +
           "GROUP BY k.cfInstitution " +
           "ORDER BY k.cfInstitution")
    List<Object[]> aggregateByReferenceDateAndCfInstitution(@Param("referenceDate") LocalDate referenceDate);

    /**
     * Calcola totali globali per una data di riferimento
     */
    @Query("SELECT SUM(k.positionNumber), SUM(k.messageNumber) " +
           "FROM KpiC1AnalyticData k WHERE k.referenceDate = :referenceDate")
    Object[] calculateTotalsByReferenceDate(@Param("referenceDate") LocalDate referenceDate);

    /**
     * Trova i dati analitici per instance, data di riferimento e CF institution specifici
     */
    @Query("SELECT k FROM KpiC1AnalyticData k WHERE k.instance.id = :instanceId " +
           "AND k.referenceDate = :referenceDate " +
           "AND k.cfInstitution = :cfInstitution " +
           "ORDER BY k.data")
    List<KpiC1AnalyticData> findByInstanceIdAndReferenceDateAndCfInstitution(
            @Param("instanceId") Long instanceId,
            @Param("referenceDate") LocalDate referenceDate,
            @Param("cfInstitution") String cfInstitution);

    /**
     * Trova i CF institution unici per una data di riferimento
     */
    @Query("SELECT DISTINCT k.cfInstitution FROM KpiC1AnalyticData k " +
           "WHERE k.referenceDate = :referenceDate " +
           "ORDER BY k.cfInstitution")
    List<String> findDistinctCfInstitutionByReferenceDate(@Param("referenceDate") LocalDate referenceDate);

    /**
     * Conta i record per CF institution in una data di riferimento
     */
    @Query("SELECT COUNT(k) FROM KpiC1AnalyticData k WHERE k.referenceDate = :referenceDate " +
           "AND k.cfInstitution = :cfInstitution")
    long countByReferenceDateAndCfInstitution(
            @Param("referenceDate") LocalDate referenceDate,
            @Param("cfInstitution") String cfInstitution);

    /**
     * Trova le date per cui esistono dati analitici per un CF institution specifico
     */
    @Query("SELECT DISTINCT k.data FROM KpiC1AnalyticData k " +
           "WHERE k.cfInstitution = :cfInstitution " +
           "AND k.data BETWEEN :startDate AND :endDate " +
           "ORDER BY k.data")
    List<LocalDate> findDistinctDataByCfInstitutionAndPeriod(
            @Param("cfInstitution") String cfInstitution,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * Trova i dati analitici correlati a un KpiC1DetailResult specifico
     */
    // NOTE: Aggregated detail results use a synthetic CF_INSTITUTION value (e.g. 'AGGREGATED'),
    // while analytic rows store the real CF codes. We purposely drop the cfInstitution join
    // to allow drill-down retrieval for aggregated detail result IDs.
    @Query("SELECT ad FROM KpiC1AnalyticData ad " +
           "WHERE EXISTS (SELECT 1 FROM KpiC1DetailResult dr " +
           "              WHERE dr.id = :detailResultId " +
           "              AND dr.instance.id = ad.instance.id " +
           "              AND dr.instanceModule.id = ad.instanceModule.id " +
           "              AND dr.referenceDate = ad.referenceDate " +
           "              AND ad.data BETWEEN dr.evaluationStartDate AND dr.evaluationEndDate) " +
           "ORDER BY ad.cfInstitution, ad.data")
    List<KpiC1AnalyticData> findByDetailResultId(@Param("detailResultId") Long detailResultId);

    /**
     * Elimina i dati analitici più vecchi di una certa data
     */
    void deleteByReferenceDateBefore(LocalDate cutoffDate);
}