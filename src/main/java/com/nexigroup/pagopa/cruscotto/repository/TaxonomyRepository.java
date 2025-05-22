package com.nexigroup.pagopa.cruscotto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nexigroup.pagopa.cruscotto.domain.Taxonomy;

import java.util.List;

/**
 * Spring Data repository for the Taxonomy entity.
 */

@Repository
public interface TaxonomyRepository extends JpaRepository<Taxonomy, Long>, JpaSpecificationExecutor<Taxonomy> {

    @Modifying
    @Query("delete Taxonomy taxonomy")
    void delete();
    
    @Query("SELECT taxonomy.takingsIdentifier FROM Taxonomy taxonomy")
    List<String> findAllTakingsIdentifiers();
}
