package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.KpiB4AnalyticData;
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
 * Spring Data JPA repository for the KpiB4AnalyticData entity.
 */
@Repository
public interface KpiB4AnalyticDataRepository extends JpaRepository<KpiB4AnalyticData, Long> {

    /**
     * Find all KpiB4AnalyticData by instance ID.
     *
     * @param instanceId the instance ID
     * @return the list of KpiB4AnalyticData
     */
    List<KpiB4AnalyticData> findByInstanceIdOrderByDataDateDesc(String instanceId);

    /**
     * Find KpiB4AnalyticData by instance ID and analysis date.
     *
     * @param instanceId the instance ID
     * @param analysisDate the analysis date
     * @return the list of KpiB4AnalyticData
     */
    List<KpiB4AnalyticData> findByInstanceIdAndAnalysisDate(String instanceId, LocalDateTime analysisDate);

    /**
     * Find KpiB4AnalyticData by instance ID and analysis date with pagination.
     *
     * @param instanceId the instance ID
     * @param analysisDate the analysis date
     * @param pageable the pagination information
     * @return the page of KpiB4AnalyticData
     */
    Page<KpiB4AnalyticData> findByInstanceIdAndAnalysisDateOrderByDataDateDesc(
        String instanceId, 
        LocalDateTime analysisDate, 
        Pageable pageable
    );

    /**
     * Find KpiB4AnalyticData by instance ID, analysis date and specific date.
     *
     * @param instanceId the instance ID
     * @param analysisDate the analysis date
     * @param dataDate the specific date
     * @return the KpiB4AnalyticData
     */
    KpiB4AnalyticData findByInstanceIdAndAnalysisDateAndDataDate(
        String instanceId, 
        LocalDateTime analysisDate, 
        LocalDate dataDate
    );

    /**
     * Find KpiB4AnalyticData by instance ID and date range.
     *
     * @param instanceId the instance ID
     * @param analysisDate the analysis date
     * @param fromDate the start date
     * @param toDate the end date
     * @return the list of KpiB4AnalyticData
     */
    @Query("SELECT k FROM KpiB4AnalyticData k WHERE k.instanceId = :instanceId AND k.analysisDate = :analysisDate AND k.dataDate BETWEEN :fromDate AND :toDate ORDER BY k.dataDate ASC")
    List<KpiB4AnalyticData> findByInstanceIdAndAnalysisDateAndDataDateBetween(
        @Param("instanceId") String instanceId,
        @Param("analysisDate") LocalDateTime analysisDate,
        @Param("fromDate") LocalDate fromDate,
        @Param("toDate") LocalDate toDate
    );

    /**
     * Find daily aggregated data for drill-down functionality.
     *
     * @param instanceId the instance ID
     * @param analysisDate the analysis date
     * @param dataDate the specific date
     * @return the list of KpiB4AnalyticData for drill-down
     */
    @Query("SELECT k FROM KpiB4AnalyticData k WHERE k.instanceId = :instanceId AND k.analysisDate = :analysisDate AND k.dataDate = :dataDate ORDER BY k.dataDate ASC")
    List<KpiB4AnalyticData> findDrillDownDataByInstanceIdAndAnalysisDateAndDate(
        @Param("instanceId") String instanceId,
        @Param("analysisDate") LocalDateTime analysisDate,
        @Param("dataDate") LocalDate dataDate
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
           "SUM(k.numRequestGpd) as totalGpdRequests, " +
           "SUM(k.numRequestCp) as totalCpRequests " +
           "FROM KpiB4AnalyticData k " +
           "WHERE k.instanceId = :instanceId " +
           "AND k.analysisDate = :analysisDate " +
           "AND k.dataDate BETWEEN :fromDate AND :toDate")
    Object[] calculateSummaryStatistics(
        @Param("instanceId") String instanceId,
        @Param("analysisDate") LocalDateTime analysisDate,
        @Param("fromDate") LocalDate fromDate,
        @Param("toDate") LocalDate toDate
    );

    /**
     * Delete KpiB4AnalyticData by instance ID.
     *
     * @param instanceId the instance ID
     */
    void deleteByInstanceId(String instanceId);

    /**
     * Find dates with paCreate usage for highlighting in UI.
     *
     * @param instanceId the instance ID
     * @param analysisDate the analysis date
     * @param fromDate the start date
     * @param toDate the end date
     * @return the list of dates with paCreate usage
     */
    @Query("SELECT DISTINCT k.dataDate FROM KpiB4AnalyticData k WHERE k.instanceId = :instanceId AND k.analysisDate = :analysisDate AND k.dataDate BETWEEN :fromDate AND :toDate AND k.numRequestCp > 0 ORDER BY k.dataDate ASC")
    List<LocalDate> findDatesWithPaCreateUsage(
        @Param("instanceId") String instanceId,
        @Param("analysisDate") LocalDateTime analysisDate,
        @Param("fromDate") LocalDate fromDate,
        @Param("toDate") LocalDate toDate
    );
}