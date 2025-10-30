package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.PagopaSend;
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
}
