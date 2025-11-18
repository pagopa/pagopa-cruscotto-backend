package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.PagopaSend;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository for the PagopaSend entity.
 */
@Repository
public interface PagopaSendRepository extends JpaRepository<PagopaSend, Long>, JpaSpecificationExecutor<PagopaSend> {

    /**
     * Trova tutti i record per un dato cfPartner.
     */
    @Query("SELECT p FROM PagopaSend p WHERE p.cfPartner = :cfPartner")
    List<PagopaSend> findByCfPartner(@Param("cfPartner") String cfPartner);

    /**
     * Trova tutti i record per un dato cfInstitution.
     */
    @Query("SELECT p FROM PagopaSend p WHERE p.cfInstitution = :cfInstitution")
    List<PagopaSend> findByCfInstitution(@Param("cfInstitution") String cfInstitution);

    /**
     * Trova tutti i record per un cfPartner e un intervallo di date.
     */
    @Query("SELECT p FROM PagopaSend p WHERE p.cfPartner = :cfPartner AND p.date BETWEEN :startDate AND :endDate")
    List<PagopaSend> findByCfPartnerAndDateBetween(
        @Param("cfPartner") String cfPartner,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    /**
     * Restituisce tutti i cfPartner distinti presenti in tabella.
     */
    @Query("SELECT DISTINCT p.cfPartner FROM PagopaSend p")
    List<String> findDistinctCfPartner();

    /**
     * Conta il numero totale di record per un dato cfPartner.
     */
    @Query("SELECT COUNT(p) FROM PagopaSend p WHERE p.cfPartner = :cfPartner")
    long countByCfPartner(@Param("cfPartner") String cfPartner);

    /**
     * Conta il numero totale di record per un dato cfInstitution.
     */
    @Query("SELECT COUNT(p) FROM PagopaSend p WHERE p.cfInstitution = :cfInstitution")
    long countByCfInstitution(@Param("cfInstitution") String cfInstitution);

    @Query("""
    SELECT COUNT(p.cfInstitution)
    FROM PagopaSend p
    WHERE (COALESCE(:listInstitutionFiscalCode, NULL) IS NULL OR p.cfInstitution IN :listInstitutionFiscalCode)
    """)
    Long calculateTotalNumberInsitution(        @Param("cfPartner") String cfPartner,
                                                @Param("listInstitutionFiscalCode") List<String> listInstitutionFiscalCode);

    @Query("""
    SELECT COUNT(DISTINCT p.cfInstitution)
    FROM PagopaSend p
    WHERE  (COALESCE(:listInstitutionFiscalCode, NULL) IS NULL OR p.cfInstitution IN :listInstitutionFiscalCode)
      AND p.date BETWEEN :startDate AND :endDate
      AND p.paymentsNumber > 0
    """)
    Long calculateTotalNumberInstitutionSend(
        @Param("cfPartner") String cfPartner,
        @Param("listInstitutionFiscalCode") List<String> listInstitutionFiscalCode,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    @Query("""
    SELECT SUM(p.paymentsNumber)
    FROM PagopaSend p
    WHERE (COALESCE(:listInstitutionFiscalCode, NULL) IS NULL OR p.cfInstitution IN :listInstitutionFiscalCode)
      AND p.date BETWEEN :startDate AND :endDate
    """)
    Long calculateTotalNumberPayment(
        @Param("cfPartner") String cfPartner,
        @Param("listInstitutionFiscalCode") List<String> listInstitutionFiscalCode,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    @Query("""
    SELECT SUM(DISTINCT p.notificationNumber)
    FROM PagopaSend p
    WHERE (COALESCE(:listInstitutionFiscalCode, NULL) IS NULL OR p.cfInstitution IN :listInstitutionFiscalCode)
      AND p.date BETWEEN :startDate AND :endDate
    """)
    Long calculateTotalNumberNotification(
        @Param("cfPartner") String cfPartner,
        @Param("listInstitutionFiscalCode") List<String> listInstitutionFiscalCode,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    @Query("""
    SELECT
        DATE(p.date) AS day,
        COUNT(DISTINCT p.cfInstitution) AS totalInstitutions,
        COUNT(DISTINCT CASE WHEN p.paymentsNumber > 0 THEN p.cfInstitution END) AS institutionsWithPayments,
        SUM(p.paymentsNumber) AS totalPayments,
        SUM(p.notificationNumber) AS totalNotifications
    FROM PagopaSend p
    WHERE (COALESCE(:listInstitutionFiscalCode, NULL) IS NULL OR p.cfInstitution IN :listInstitutionFiscalCode)
      AND p.date BETWEEN :fromDate AND :toDate
    GROUP BY DATE(p.date)
    ORDER BY DATE(p.date) ASC
    """)
    List<Object[]> calculateDailyAggregatedDataAndInstitutionAndNotification(
        @Param("cfPartner") String cfPartner,
        @Param("listInstitutionFiscalCode") List<String> listInstitutionFiscalCode,
        @Param("fromDate") LocalDateTime fromDate,
        @Param("toDate") LocalDateTime toDate
    );


    /**
     * Find detailed API log records for drilldown snapshot by partner and specific date.
     * Returns all fields needed for populating the drilldown table.
     *
     * @param cfPartner the partner CF
     * @param date the specific date
     * @return the list of detailed API log data for drilldown
     */
    @Query("""
    SELECT p.cfPartner, p.cfInstitution, p.date, p.paymentsNumber, p.notificationNumber
    FROM PagopaSend p
    WHERE (COALESCE(:listInstitutionFiscalCode, NULL) IS NULL OR p.cfInstitution IN :listInstitutionFiscalCode)
      AND p.date = :date
    ORDER BY p.cfInstitution
    """)
    List<Object[]> findDetailedPagopaSendByPartnerAndDate(
        @Param("cfPartner") String cfPartner,
        @Param("listInstitutionFiscalCode") List<String> listInstitutionFiscalCode,
        @Param("date") LocalDateTime date
    );


}
