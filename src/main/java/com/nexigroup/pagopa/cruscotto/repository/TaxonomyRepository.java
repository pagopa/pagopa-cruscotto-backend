package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.Taxonomy;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository for the Taxonomy entity.
 */

@Repository
public interface TaxonomyRepository extends JpaRepository<Taxonomy, Long>, JpaSpecificationExecutor<Taxonomy> {
    @Modifying
    @Query("delete Taxonomy taxonomy")
    void delete();

    @Query("SELECT taxonomy.takingsIdentifier FROM Taxonomy taxonomy WHERE taxonomy.createdDate >= CURRENT_DATE")
    List<String> findAllUpdatedTakingsIdentifiers();
}
