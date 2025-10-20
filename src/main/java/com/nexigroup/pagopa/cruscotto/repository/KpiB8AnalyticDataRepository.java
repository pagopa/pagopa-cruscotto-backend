package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.KpiB8AnalyticData;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the KpiB8AnalyticData entity.
 */
@Repository
public interface KpiB8AnalyticDataRepository extends JpaRepository<KpiB8AnalyticData, Long> {

    /**
     * Find all KpiB8AnalyticData by instance ID.
     *
     * @param instanceId the instance ID
     * @return the list of KpiB8AnalyticData
     */
    List<KpiB8AnalyticData> findByInstanceIdOrderByEvaluationDateDesc(Long instanceId);

    /**
     * Find KpiB8AnalyticData by instance ID and analysis date.
     *
     * @param instanceId the instance ID
     * @param analysisDate the analysis date
     * @return the list of KpiB8AnalyticData
     */
    List<KpiB8AnalyticData> findByInstanceIdAndAnalysisDate(Long instanceId, LocalDate analysisDate);

    /**
     * Find KpiB8AnalyticData by instance ID and analysis date with pagination.
     *
     * @param instanceId the instance ID
     * @param analysisDate the analysis date
     * @param pageable the pagination information
     * @return the page of KpiB8AnalyticData
     */
    Page<KpiB8AnalyticData> findByInstanceIdAndAnalysisDateOrderByEvaluationDateDesc(
        Long instanceId,
        LocalDate analysisDate,
        Pageable pageable
    );

    /**
     * Find KpiB8AnalyticData by instance ID, analysis date and specific date.
     *
     * @param instanceId the instance ID
     * @param analysisDate the analysis date
     * @param evaluationDate the specific date
     * @return the KpiB8AnalyticData
     */
    KpiB8AnalyticData findByInstanceIdAndAnalysisDateAndEvaluationDate(
        Long instanceId,
        LocalDate analysisDate,
        LocalDate evaluationDate
    );

    /**
     * Find KpiB8AnalyticData by instance ID and date range.
     *
     * @param instanceId the instance ID
     * @param analysisDate the analysis date
     * @param fromDate the start date
     * @param toDate the end date
     * @return the list of KpiB8AnalyticData
     */
    @Query("SELECT k FROM KpiB8AnalyticData k WHERE k.instanceId = :instanceId AND k.analysisDate = :analysisDate AND k.evaluationDate BETWEEN :fromDate AND :toDate ORDER BY k.evaluationDate ASC")
    List<KpiB8AnalyticData> findByInstanceIdAndAnalysisDateAndDataDateBetween(
        @Param("instanceId") Long instanceId,
        @Param("analysisDate") LocalDate analysisDate,
        @Param("fromDate") LocalDate fromDate,
        @Param("toDate") LocalDate toDate
    );

    /**
     * Find daily aggregated data for drill-down functionality.
     *
     * @param instanceId the instance ID
     * @param analysisDate the analysis date
     * @param evaluationDate the specific date
     * @return the list of KpiB8AnalyticData for drill-down
     */
    @Query("SELECT k FROM KpiB8AnalyticData k WHERE k.instanceId = :instanceId AND k.analysisDate = :analysisDate AND k.evaluationDate = :evaluationDate ORDER BY k.evaluationDate ASC")
    List<KpiB8AnalyticData> findDrillDownDataByInstanceIdAndAnalysisDateAndDate(
        @Param("instanceId") Long instanceId,
        @Param("analysisDate") LocalDate analysisDate,
        @Param("evaluationDate") LocalDate evaluationDate
    );

    /**
     * Calculate summary statistics for a period.
     *
     * @param instanceId the instance ID
     * @param analysisDate the analysis date
     * @param fromDate the start date
     * @param toDate the end date
     * @return the summary data
     */
    @Query("SELECT " +
        "SUM(k.totReq) as totalRequests, " +
        "SUM(k.reqKO) as totalKORequests " +
        "FROM KpiB8AnalyticData k " +
        "WHERE k.instanceId = :instanceId " +
        "AND k.analysisDate = :analysisDate " +
        "AND k.evaluationDate BETWEEN :fromDate AND :toDate")
    Object[] calculateSummaryStatistics(
        @Param("instanceId") Long instanceId,
        @Param("analysisDate") LocalDate analysisDate,
        @Param("fromDate") LocalDate fromDate,
        @Param("toDate") LocalDate toDate
    );

    /**
     * Delete KpiB8AnalyticData by instance ID.
     *
     * @param instanceId the instance ID
     */
    void deleteByInstanceId(Long instanceId);

    /**
     * Find dates with paCreate usage for highlighting in UI.
     *
     * @param instanceId the instance ID
     * @param analysisDate the analysis date
     * @param fromDate the start date
     * @param toDate the end date
     * @return the list of dates with paCreate usage
     */
    @Query("SELECT DISTINCT k.evaluationDate FROM KpiB8AnalyticData k WHERE k.instanceId = :instanceId AND k.analysisDate = :analysisDate AND k.evaluationDate BETWEEN :fromDate AND :toDate AND k.numRequestCp > 0 ORDER BY k.evaluationDate ASC")
    List<LocalDate> findDatesWithPaCreateUsage(
        @Param("instanceId") Long instanceId,
        @Param("analysisDate") LocalDate analysisDate,
        @Param("fromDate") LocalDate fromDate,
        @Param("toDate") LocalDate toDate
    );

    /**
     * Find all KpiB8AnalyticData by KpiB8DetailResult ID ordered by evaluation date descending.
     *
     * @param detailResultId the KpiB8DetailResult ID
     * @return the list of KpiB8AnalyticData
     */
    @Query("SELECT k FROM KpiB8AnalyticData k WHERE k.kpiB8DetailResult.id = :detailResultId ORDER BY k.evaluationDate DESC")
    List<KpiB8AnalyticData> findAllByDetailResultIdOrderByEvaluationDateDesc(@Param("detailResultId") Long detailResultId);
}
