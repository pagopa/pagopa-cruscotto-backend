package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.PagopaApiLog;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PagopaApiLog entity.
 */
@Repository
public interface PagopaApiLogRepository extends JpaRepository<PagopaApiLog, PagopaApiLog.PagopaApiLogId> {

    /**
     * Find API logs by partner and date range.
     *
     * @param cfPartner the partner CF
     * @param fromDate the start date
     * @param toDate the end date
     * @return the list of PagopaApiLog
     */
    List<PagopaApiLog> findByCfPartnerAndDateBetween(String cfPartner, LocalDate fromDate, LocalDate toDate);

    /**
     * Find API logs by partner, date range and API type.
     *
     * @param cfPartner the partner CF
     * @param fromDate the start date
     * @param toDate the end date
     * @param apiType the API type
     * @return the list of PagopaApiLog
     */
    List<PagopaApiLog> findByCfPartnerAndDateBetweenAndApi(String cfPartner, LocalDate fromDate, LocalDate toDate, String apiType);

    /**
     * Find API logs for drill-down by partner and specific date.
     *
     * @param cfPartner the partner CF
     * @param date the specific date
     * @return the list of PagopaApiLog for drill-down
     */
    List<PagopaApiLog> findByCfPartnerAndDate(String cfPartner, LocalDate date);

    /**
     * Find API logs for drill-down by partner, date and API type with pagination.
     *
     * @param cfPartner the partner CF
     * @param date the specific date
     * @param apiType the API type
     * @param pageable the pagination information
     * @return the page of PagopaApiLog
     */
    Page<PagopaApiLog> findByCfPartnerAndDateAndApi(String cfPartner, LocalDate date, String apiType, Pageable pageable);

    /**
     * Calculate aggregated GPD/ACA requests for a partner in a date range.
     *
     * @param cfPartner the partner CF
     * @param fromDate the start date
     * @param toDate the end date
     * @return the total GPD/ACA requests
     */
    @Query("SELECT SUM(p.totReq) FROM PagopaApiLog p WHERE p.cfPartner = :cfPartner AND p.date BETWEEN :fromDate AND :toDate AND (p.api = 'GPD' OR p.api = 'ACA')")
    Long calculateTotalGpdAcaRequests(@Param("cfPartner") String cfPartner, @Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);

    /**
     * Calculate aggregated paCreate requests for a partner in a date range.
     *
     * @param cfPartner the partner CF
     * @param fromDate the start date
     * @param toDate the end date
     * @return the total paCreate requests
     */
    @Query("SELECT SUM(p.totReq) FROM PagopaApiLog p WHERE p.cfPartner = :cfPartner AND p.date BETWEEN :fromDate AND :toDate AND p.api = 'paCreate'")
    Long calculateTotalPaCreateRequests(@Param("cfPartner") String cfPartner, @Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);

    /**
     * Calculate daily aggregated data for analytic purposes.
     *
     * @param cfPartner the partner CF
     * @param fromDate the start date
     * @param toDate the end date
     * @return the list of daily aggregated data
     */
    @Query("SELECT p.date, " +
           "SUM(CASE WHEN (p.api = 'GPD' OR p.api = 'ACA') THEN p.totReq ELSE 0 END) as gpdTotal, " +
           "SUM(CASE WHEN p.api = 'paCreate' THEN p.totReq ELSE 0 END) as paCreateTotal " +
           "FROM PagopaApiLog p " +
           "WHERE p.cfPartner = :cfPartner " +
           "AND p.date BETWEEN :fromDate AND :toDate " +
           "GROUP BY p.date " +
           "ORDER BY p.date ASC")
    List<Object[]> calculateDailyAggregatedData(
        @Param("cfPartner") String cfPartner,
        @Param("fromDate") LocalDate fromDate,
        @Param("toDate") LocalDate toDate
    );

    /**
     * Calculate monthly aggregated data for monthly analysis.
     *
     * @param cfPartner the partner CF
     * @param year the year
     * @param month the month
     * @return the monthly aggregated data
     */
    @Query("SELECT " +
           "SUM(CASE WHEN (p.api = 'GPD' OR p.api = 'ACA') THEN p.totReq ELSE 0 END) as gpdTotal, " +
           "SUM(CASE WHEN p.api = 'paCreate' THEN p.totReq ELSE 0 END) as paCreateTotal " +
           "FROM PagopaApiLog p " +
           "WHERE p.cfPartner = :cfPartner " +
           "AND YEAR(p.date) = :year " +
           "AND MONTH(p.date) = :month")
    Object[] calculateMonthlyAggregatedData(
        @Param("cfPartner") String cfPartner,
        @Param("year") int year,
        @Param("month") int month
    );

    /**
     * Check if partner has API usage data in date range.
     *
     * @param cfPartner the partner CF
     * @param fromDate the start date
     * @param toDate the end date
     * @return true if data exists
     */
    boolean existsByCfPartnerAndDateBetween(String cfPartner, LocalDate fromDate, LocalDate toDate);

    /**
     * Check if any API usage data exists in the specified date range for any partner.
     * This method is used to verify if pagopa_apilog table has data for the analysis period.
     *
     * @param fromDate the start date
     * @param toDate the end date
     * @return true if any data exists in the period
     */
    @Query("SELECT COUNT(p) > 0 FROM PagopaApiLog p WHERE p.date BETWEEN :fromDate AND :toDate")
    boolean existsDataInPeriod(@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);

    /**
     * Find distinct dates with paCreate usage for a partner in date range.
     *
     * @param cfPartner the partner CF
     * @param fromDate the start date
     * @param toDate the end date
     * @return the list of dates with paCreate usage
     */
    @Query("SELECT DISTINCT p.date FROM PagopaApiLog p WHERE p.cfPartner = :cfPartner AND p.date BETWEEN :fromDate AND :toDate AND p.api = 'paCreate' ORDER BY p.date ASC")
    List<LocalDate> findDatesWithPaCreateUsage(
        @Param("cfPartner") String cfPartner,
        @Param("fromDate") LocalDate fromDate,
        @Param("toDate") LocalDate toDate
    );
}