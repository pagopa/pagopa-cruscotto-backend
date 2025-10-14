package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.PagopaTransaction;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository for the PagopaTransazioni entity.
 */
@Repository
public interface PagopaTransazioniRepository extends JpaRepository<PagopaTransaction, Long>, JpaSpecificationExecutor<PagopaTransaction> {
    
    @Query("SELECT p FROM PagopaTransaction p WHERE p.date BETWEEN :startDate AND :endDate")
    List<PagopaTransaction> findByDateRange(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    @Query("SELECT DISTINCT p.cfPartner FROM PagopaTransaction p WHERE p.date BETWEEN :startDate AND :endDate")
    List<String> findDistinctPartnersByDateRange(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    @Query("SELECT COUNT(DISTINCT p.cfInstitution) FROM PagopaTransaction p WHERE p.cfPartner = :partner AND p.date BETWEEN :startDate AND :endDate")
    Integer countDistinctEntitiesByPartnerAndDateRange(
        @Param("partner") String partner,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    @Query("SELECT SUM(p.transactionTotal) FROM PagopaTransaction p WHERE p.cfPartner = :partner AND p.date BETWEEN :startDate AND :endDate")
    Long countTransactionsByPartnerAndDateRange(
        @Param("partner") String partner,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    @Query("SELECT p FROM PagopaTransaction p WHERE p.cfPartner = :partner AND p.date BETWEEN :startDate AND :endDate")
    List<PagopaTransaction> findByPartnerAndDateRange(
        @Param("partner") String partner,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    @Query("SELECT p FROM PagopaTransaction p WHERE p.cfInstitution = :ente AND p.date BETWEEN :startDate AND :endDate")
    List<PagopaTransaction> findByEnteAndDateRange(
        @Param("ente") String ente,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
}