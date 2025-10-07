package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.KpiB4DetailResult;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the KpiB4DetailResult entity.
 */
@Repository
public interface KpiB4DetailResultRepository extends JpaRepository<KpiB4DetailResult, Long> {

    /**
     * Find all KpiB4DetailResult by instance ID.
     *
     * @param instanceId the instance ID
     * @return the list of KpiB4DetailResult
     */
    List<KpiB4DetailResult> findByInstanceIdOrderByAnalysisDateDesc(String instanceId);

    /**
     * Find KpiB4DetailResult by instance ID and analysis date.
     *
     * @param instanceId the instance ID
     * @param analysisDate the analysis date
     * @return the list of KpiB4DetailResult
     */
    List<KpiB4DetailResult> findByInstanceIdAndAnalysisDate(String instanceId, LocalDateTime analysisDate);

    /**
     * Find KpiB4DetailResult by instance ID, analysis date and period type.
     *
     * @param instanceId the instance ID
     * @param analysisDate the analysis date
     * @param periodType the period type (MESE/TOTALE)
     * @return the list of KpiB4DetailResult
     */
    List<KpiB4DetailResult> findByInstanceIdAndAnalysisDateAndPeriodType(
        String instanceId, 
        LocalDateTime analysisDate, 
        String periodType
    );

    /**
     * Find KpiB4DetailResult by instance ID with pagination.
     *
     * @param instanceId the instance ID
     * @param pageable the pagination information
     * @return the page of KpiB4DetailResult
     */
    Page<KpiB4DetailResult> findByInstanceIdOrderByAnalysisDateDesc(String instanceId, Pageable pageable);

    /**
     * Find monthly results for an instance and analysis date.
     *
     * @param instanceId the instance ID
     * @param analysisDate the analysis date
     * @return the list of monthly KpiB4DetailResult
     */
    @Query("SELECT k FROM KpiB4DetailResult k WHERE k.instanceId = :instanceId AND k.analysisDate = :analysisDate AND k.periodType = 'MESE' ORDER BY k.periodFrom ASC")
    List<KpiB4DetailResult> findMonthlyResultsByInstanceIdAndAnalysisDate(
        @Param("instanceId") String instanceId, 
        @Param("analysisDate") LocalDateTime analysisDate
    );

    /**
     * Find total result for an instance and analysis date.
     *
     * @param instanceId the instance ID
     * @param analysisDate the analysis date
     * @return the total KpiB4DetailResult
     */
    @Query("SELECT k FROM KpiB4DetailResult k WHERE k.instanceId = :instanceId AND k.analysisDate = :analysisDate AND k.periodType = 'TOTALE'")
    KpiB4DetailResult findTotalResultByInstanceIdAndAnalysisDate(
        @Param("instanceId") String instanceId, 
        @Param("analysisDate") LocalDateTime analysisDate
    );

    /**
     * Delete KpiB4DetailResult by instance ID.
     *
     * @param instanceId the instance ID
     */
    void deleteByInstanceId(String instanceId);

    /**
     * Check if non-compliant results exist for instance and analysis date.
     *
     * @param instanceId the instance ID
     * @param analysisDate the analysis date
     * @return true if non-compliant results exist
     */
    @Query("SELECT COUNT(k) > 0 FROM KpiB4DetailResult k WHERE k.instanceId = :instanceId AND k.analysisDate = :analysisDate AND k.outcome = false")
    boolean existsNonCompliantByInstanceIdAndAnalysisDate(
        @Param("instanceId") String instanceId, 
        @Param("analysisDate") LocalDateTime analysisDate
    );
}