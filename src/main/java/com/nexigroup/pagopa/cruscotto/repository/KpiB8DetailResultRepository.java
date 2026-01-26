package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.KpiA2DetailResult;
import com.nexigroup.pagopa.cruscotto.domain.KpiB8DetailResult;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the KpiB8DetailResult entity.
 */
@Repository
public interface KpiB8DetailResultRepository extends JpaRepository<KpiB8DetailResult, Long> {

    /**
     * Find all KpiB8DetailResult by instance ID.
     *
     * @param instanceId the instance ID
     * @return the list of KpiB8DetailResult
     */
    List<KpiB8DetailResult> findByInstanceIdOrderByAnalysisDateDesc(Long instanceId);

    /**
     * Find KpiB8DetailResult by instance ID and analysis date.
     *
     * @param instanceId the instance ID
     * @param analysisDate the analysis date
     * @return the list of KpiB8DetailResult
     */
    List<KpiB8DetailResult> findByInstanceIdAndAnalysisDate(Long instanceId, LocalDateTime analysisDate);

    /**
     * Find KpiB8DetailResult by instance ID, analysis date and evaluation type.
     *
     * @param instanceId the instance ID
     * @param analysisDate the analysis date
     * @param evaluationType the evaluation type (MESE/TOTALE)
     * @return the list of KpiB8DetailResult
     */
    List<KpiB8DetailResult> findByInstanceIdAndAnalysisDateAndEvaluationType(
        Long instanceId,
        LocalDateTime analysisDate,
        String evaluationType
    );

    /**
     * Find KpiB8DetailResult by instance ID with pagination.
     *
     * @param instanceId the instance ID
     * @param pageable the pagination information
     * @return the page of KpiB8DetailResult
     */
    Page<KpiB8DetailResult> findByInstanceIdOrderByAnalysisDateDesc(Long instanceId, Pageable pageable);

    /**
     * Find monthly results for an instance and analysis date.
     *
     * @param instanceId the instance ID
     * @param analysisDate the analysis date
     * @return the list of monthly KpiB8DetailResult
     */
    @Query("SELECT k FROM KpiB8DetailResult k WHERE k.instanceId = :instanceId AND k.analysisDate = :analysisDate AND k.evaluationType = 'MESE' ORDER BY k.evaluationStartDate ASC")
    List<KpiB8DetailResult> findMonthlyResultsByInstanceIdAndAnalysisDate(
        @Param("instanceId") Long instanceId,
        @Param("analysisDate") LocalDateTime analysisDate
    );

    /**
     * Find total result for an instance and analysis date.
     *
     * @param instanceId the instance ID
     * @param analysisDate the analysis date
     * @return the total KpiB8DetailResult
     */
    @Query("SELECT k FROM KpiB8DetailResult k WHERE k.instanceId = :instanceId AND k.analysisDate = :analysisDate AND k.evaluationType = 'TOTALE'")
    KpiB8DetailResult findTotalResultByInstanceIdAndAnalysisDate(
        @Param("instanceId") Long instanceId,
        @Param("analysisDate") LocalDateTime analysisDate
    );

    /**
     * Delete KpiB8DetailResult by instance ID.
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
    @Query("SELECT COUNT(k) > 0 FROM KpiB8DetailResult k WHERE k.instanceId = :instanceId AND k.analysisDate = :analysisDate AND k.outcome = 'KO'")
    boolean existsNonCompliantByInstanceIdAndAnalysisDate(
        @Param("instanceId") Long instanceId,
        @Param("analysisDate") LocalDateTime analysisDate
    );

    /**
     * Find KpiB8DetailResult by KpiB8Result.
     *
     * @param kpiB8Result the KpiB8Result
     * @return the list of KpiB8DetailResult
     */
    List<KpiB8DetailResult> findByKpiB8Result(com.nexigroup.pagopa.cruscotto.domain.KpiB8Result kpiB8Result);

    /**
     * Find all KpiB8DetailResult by KpiB8Result ID ordered by analysis date descending.
     *
     * @param resultId the KpiB8Result ID
     * @return the list of KpiB8DetailResult
     */
    @Query("SELECT kdr FROM KpiB8DetailResult kdr WHERE kdr.kpiB8Result.id = :resultId ORDER BY kdr.analysisDate DESC")
    List<KpiB8DetailResult> findAllByResultIdOrderByAnalysisDateDesc(@Param("resultId") Long resultId);

    /**
     * Check if there are any detail results with KO outcome for a specific KpiB8Result ID.
     * Used for MONTHLY evaluation type to determine if the overall outcome should be KO.
     *
     * @param resultId the KpiB8Result ID
     * @return true if at least one detail result has KO outcome
     */
    @Query("SELECT COUNT(kdr) > 0 FROM KpiB8DetailResult kdr WHERE kdr.kpiB8Result.id = :resultId AND kdr.outcome = 'KO'")
    boolean existsKoOutcomeByResultId(@Param("resultId") Long resultId);

    List<KpiB8DetailResult> findLatestByInstanceId(
        @Param("instanceId") Long instanceId
    );
}
