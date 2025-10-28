package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.PagopaSpontaneous;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository for the PagopaSpontaneous entity.
 */
@Repository
public interface PagopaSpontaneousRepository extends JpaRepository<PagopaSpontaneous, Long>, JpaSpecificationExecutor<PagopaSpontaneous> {
    
    @Query("SELECT p FROM PagopaSpontaneous p WHERE p.cfPartner = :cfPartner")
    List<PagopaSpontaneous> findByCfPartner(@Param("cfPartner") String cfPartner);

    @Query("SELECT p FROM PagopaSpontaneous p WHERE p.cfPartner = :cfPartner AND p.spontaneousPayment = false")
    List<PagopaSpontaneous> findByCfPartnerAndSpontaneousPaymentFalse(@Param("cfPartner") String cfPartner);

    @Query("SELECT DISTINCT p.cfPartner FROM PagopaSpontaneous p")
    List<String> findDistinctCfPartner();

    @Query("SELECT COUNT(p) FROM PagopaSpontaneous p WHERE p.cfPartner = :cfPartner")
    long countByCfPartner(@Param("cfPartner") String cfPartner);

    @Query("SELECT COUNT(p) FROM PagopaSpontaneous p WHERE p.cfPartner = :cfPartner AND p.spontaneousPayment = false")
    long countByCfPartnerAndSpontaneousPaymentFalse(@Param("cfPartner") String cfPartner);
}