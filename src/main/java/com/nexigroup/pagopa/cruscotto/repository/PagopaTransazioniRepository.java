package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.PagopaTransazioni;
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
public interface PagopaTransazioniRepository extends JpaRepository<PagopaTransazioni, Long>, JpaSpecificationExecutor<PagopaTransazioni> {
    
    @Query("SELECT p FROM PagopaTransazioni p WHERE p.instance.id = :instanceId AND p.referenceDate BETWEEN :startDate AND :endDate")
    List<PagopaTransazioni> findByInstanceIdAndDateRange(
        @Param("instanceId") Long instanceId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    @Query("SELECT DISTINCT p.partnerCode FROM PagopaTransazioni p WHERE p.instance.id = :instanceId AND p.referenceDate BETWEEN :startDate AND :endDate")
    List<String> findDistinctPartnerCodesByInstanceIdAndDateRange(
        @Param("instanceId") Long instanceId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    @Query("SELECT COUNT(DISTINCT p.entityCode) FROM PagopaTransazioni p WHERE p.instance.id = :instanceId AND p.partnerCode = :partnerCode AND p.referenceDate BETWEEN :startDate AND :endDate")
    Integer countDistinctEntitiesByPartnerAndDateRange(
        @Param("instanceId") Long instanceId,
        @Param("partnerCode") String partnerCode,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    @Query("SELECT COUNT(p) FROM PagopaTransazioni p WHERE p.instance.id = :instanceId AND p.partnerCode = :partnerCode AND p.referenceDate BETWEEN :startDate AND :endDate")
    Long countTransactionsByPartnerAndDateRange(
        @Param("instanceId") Long instanceId,
        @Param("partnerCode") String partnerCode,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
}