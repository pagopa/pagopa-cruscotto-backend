package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.KpiC2DetailResult;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the KpiC2DetailResult entity.
 */
@Repository
public interface KpiC2DetailResultRepository extends JpaRepository<KpiC2DetailResult, Long> {

    /**
     * Find all KpiC2DetailResult by instance ID.
     *
     * @param instanceId the instance ID
     * @return the list of KpiC2DetailResult
     */
    List<KpiC2DetailResult> findByInstanceIdOrderByAnalysisDateDesc(Long instanceId);

    /**
     * Find KpiC2DetailResult by instance ID and analysis date.
     *
     * @param instanceId the instance ID
     * @param analysisDate the analysis date
     * @return the list of KpiC2DetailResult
     */
    List<KpiC2DetailResult> findByInstanceIdAndAnalysisDate(Long instanceId, LocalDateTime analysisDate);

    /**
     * Find KpiC2DetailResult by instance ID, analysis date and evaluation type.
     *
     * @param instanceId the instance ID
     * @param analysisDate the analysis date
     * @param evaluationType the evaluation type (MESE/TOTALE)
     * @return the list of KpiC2DetailResult
     */
    List<KpiC2DetailResult> findByInstanceIdAndAnalysisDateAndEvaluationType(
        Long instanceId,
        LocalDateTime analysisDate,
        String evaluationType
    );

    /**
     * Find KpiC2DetailResult by instance ID with pagination.
     *
     * @param instanceId the instance ID
     * @param pageable the pagination information
     * @return the page of KpiC2DetailResult
     */
    Page<KpiC2DetailResult> findByInstanceIdOrderByAnalysisDateDesc(Long instanceId, Pageable pageable);

    /**
     * Find monthly results for an instance and analysis date.
     *
     * @param instanceId the instance ID
     * @param analysisDate the analysis date
     * @return the list of monthly KpiC2DetailResult
     */
    @Query("SELECT k FROM KpiC2DetailResult k WHERE k.instanceId = :instanceId AND k.analysisDate = :analysisDate AND k.evaluationType = 'MESE' ORDER BY k.evaluationStartDate ASC")
    List<KpiC2DetailResult> findMonthlyResultsByInstanceIdAndAnalysisDate(
        @Param("instanceId") Long instanceId,
        @Param("analysisDate") LocalDateTime analysisDate
    );

    /**
     * Find total result for an instance and analysis date.
     *
     * @param instanceId the instance ID
     * @param analysisDate the analysis date
     * @return the total KpiC2DetailResult
     */
    @Query("SELECT k FROM KpiC2DetailResult k WHERE k.instanceId = :instanceId AND k.analysisDate = :analysisDate AND k.evaluationType = 'TOTALE'")
    KpiC2DetailResult findTotalResultByInstanceIdAndAnalysisDate(
        @Param("instanceId") Long instanceId,
        @Param("analysisDate") LocalDateTime analysisDate
    );

    /**
     * Delete KpiC2DetailResult by instance ID.
     *
     * @param instanceId the instance ID
     */
    void deleteByInstanceId(Long instanceId);

    /**
     * Check if non-compliant results exist for instance and analysis date.
     *
     * @param instanceId the instance ID
     * @param analysisDate the analysis date
     * @return true if non-compliant results exist
     */
    @Query("SELECT COUNT(k) > 0 FROM KpiC2DetailResult k WHERE k.instanceId = :instanceId AND k.analysisDate = :analysisDate AND k.outcome = 'KO'")
    boolean existsNonCompliantByInstanceIdAndAnalysisDate(
        @Param("instanceId") Long instanceId,
        @Param("analysisDate") LocalDateTime analysisDate
    );

    /**
     * Find KpiC2DetailResult by KpiC2Result.
     *
     * @param kpiC2Result the KpiC2Result
     * @return the list of KpiC2DetailResult
     */
    List<KpiC2DetailResult> findByKpiC2Result(com.nexigroup.pagopa.cruscotto.domain.KpiC2Result kpiC2Result);

    /**
     * Find all KpiC2DetailResult by KpiC2Result ID ordered by analysis date descending.
     *
     * @param resultId the KpiC2Result ID
     * @return the list of KpiC2DetailResult
     */
    @Query("SELECT kdr FROM KpiC2DetailResult kdr WHERE kdr.kpiC2Result.id = :resultId ORDER BY kdr.analysisDate DESC")
    List<KpiC2DetailResult> findAllByResultIdOrderByAnalysisDateDesc(@Param("resultId") Long resultId);

    /**
     * Check if there are any detail results with KO outcome for a specific KpiC2Result ID.
     * Used for MONTHLY evaluation type to determine if the overall outcome should be KO.
     *
     * @param resultId the KpiC2Result ID
     * @return true if at least one detail result has KO outcome
     */
    @Query("SELECT COUNT(kdr) > 0 FROM KpiC2DetailResult kdr WHERE kdr.kpiC2Result.id = :resultId AND kdr.outcome = 'KO'")
    boolean existsKoOutcomeByResultId(@Param("resultId") Long resultId);

    @Query("""
        SELECT d
        FROM KpiC2DetailResult d
        WHERE d.instanceId = :instanceId
          AND d.analysisDate = (
              SELECT MAX(dd.analysisDate)
              FROM KpiC2DetailResult dd
              WHERE dd.instanceId = :instanceId
          )
        ORDER BY d.instanceModuleId ASC
    """)
    List<KpiC2DetailResult> findLatestByInstanceId(
        @Param("instanceId") Long instanceId
    );
}
