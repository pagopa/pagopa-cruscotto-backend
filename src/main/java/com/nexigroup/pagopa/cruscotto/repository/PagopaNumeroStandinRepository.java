package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.PagopaNumeroStandin;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PagopaNumeroStandin entity.
 */
@Repository
public interface PagopaNumeroStandinRepository
    extends JpaRepository<PagopaNumeroStandin, Long>, JpaSpecificationExecutor<PagopaNumeroStandin> {

    /**
     * Find all stand-in events for a specific station within a date range
     */
    @Query("SELECT p FROM PagopaNumeroStandin p WHERE p.stationCode = :stationCode " +
           "AND p.dataDate >= :startDate AND p.dataDate <= :endDate " +
           "AND p.eventType = 'ADD_TO_STANDIN' " +
           "ORDER BY p.intervalStart")
    List<PagopaNumeroStandin> findByStationCodeAndDateRange(
        @Param("stationCode") String stationCode,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate);

    /**
     * Find all stand-in events within a date range
     */
    @Query("SELECT p FROM PagopaNumeroStandin p WHERE p.dataDate >= :startDate AND p.dataDate <= :endDate " +
           "AND p.eventType = 'ADD_TO_STANDIN' " +
           "ORDER BY p.stationCode, p.intervalStart")
    List<PagopaNumeroStandin> findByDateRange(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate);

    /**
     * Count total stand-in events for a station within a date range
     */
    @Query("SELECT COALESCE(SUM(p.standInCount), 0) FROM PagopaNumeroStandin p " +
           "WHERE p.stationCode = :stationCode " +
           "AND p.dataDate >= :startDate AND p.dataDate <= :endDate " +
           "AND p.eventType = 'ADD_TO_STANDIN'")
    Integer countStandInEventsByStationAndDateRange(
        @Param("stationCode") String stationCode,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate);

    /**
     * Delete old data before a specific date (for cleanup)
     */
    void deleteByDataDateBefore(LocalDateTime cutoffDate);

    /**
     * Check if data exists for a specific date
     */
    boolean existsByDataDate(LocalDateTime dataDate);
}