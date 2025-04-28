package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.AuthFunction;
import com.nexigroup.pagopa.cruscotto.domain.Taxonomy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data repository for the Taxonomy entity.
 */
@Repository
public interface TaxonomyRepository extends JpaRepository<Taxonomy, Long>, JpaSpecificationExecutor<Taxonomy> {

    @Modifying
    @Query("delete Taxonomy taxonomy")
    void delete();
}
