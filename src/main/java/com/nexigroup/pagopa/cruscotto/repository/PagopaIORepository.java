package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.PagopaIO;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PagopaIO entity.
 */
@Repository
public interface PagopaIORepository extends JpaRepository<PagopaIO, Long>, JpaSpecificationExecutor<PagopaIO> {

    /**
     * Find all IO data for a specific partner within a date range (first access method)
     */
    @Query("SELECT p FROM PagopaIO p WHERE p.cfPartner = :cfPartner " +
           "AND p.date >= :startDate AND p.date <= :endDate " +
           "ORDER BY p.cfInstitution, p.date")
    List<PagopaIO> findByCfPartnerAndDateRange(
        @Param("cfPartner") String cfPartner,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate);

    /**
     * Find all IO data for a specific entity within a date range (second access method)
     */
    @Query("SELECT p FROM PagopaIO p WHERE p.cfInstitution = :cfInstitution " +
           "AND (p.cfPartner IS NULL OR p.cfPartner = '') " +
           "AND p.date >= :startDate AND p.date <= :endDate " +
           "ORDER BY p.date")
    List<PagopaIO> findByCfInstitutionAndDateRangeWithoutCfPartner(
        @Param("cfInstitution") String cfInstitution,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate);

    /**
     * Find all IO data for multiple entities within a date range (second access method)
     */
    @Query("SELECT p FROM PagopaIO p WHERE p.cfInstitution IN :cfInstitutions " +
           "AND (p.cfPartner IS NULL OR p.cfPartner = '') " +
           "AND p.date >= :startDate AND p.date <= :endDate " +
           "ORDER BY p.cfInstitution, p.date")
    List<PagopaIO> findByCfInstitutionListAndDateRangeWithoutCfPartner(
        @Param("cfInstitutions") List<String> cfInstitutions,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate);

    /**
     * Find all IO data for a specific partner and entity within a date range
     */
    @Query("SELECT p FROM PagopaIO p WHERE p.cfPartner = :cfPartner " +
           "AND p.cfInstitution = :cfInstitution " +
           "AND p.date >= :startDate AND p.date <= :endDate " +
           "ORDER BY p.date")
    List<PagopaIO> findByCfPartnerAndCfInstitutionAndDateRange(
        @Param("cfPartner") String cfPartner,
        @Param("cfInstitution") String cfInstitution,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate);

    /**
     * Check if data exists for a specific partner
     */
    boolean existsByCfPartner(String cfPartner);

    /**
     * Check if data exists for a specific entity without cfPartner
     */
    @Query("SELECT COUNT(p) > 0 FROM PagopaIO p WHERE p.cfInstitution = :cfInstitution " +
           "AND (p.cfPartner IS NULL OR p.cfPartner = '')")
    boolean existsByCfInstitutionWithoutCfPartner(@Param("cfInstitution") String cfInstitution);
}