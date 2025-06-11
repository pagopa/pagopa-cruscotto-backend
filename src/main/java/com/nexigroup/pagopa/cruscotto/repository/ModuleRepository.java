package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.Module;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleStatus;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository for the Module entity.
 */

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long>, JpaSpecificationExecutor<Module> {
    @Query("SELECT module FROM Module module WHERE module.status =:status ORDER BY module.code")
    List<Module> findAllByStatus(@Param("status") ModuleStatus status);

    @Query("SELECT module FROM Module module WHERE module.code =:code")
    Optional<Module> findByCode(@Param("code") String code);

    @Query("Select m from Module m where m.id = :id and m.deleted = false")
    Optional<Module> findOneByIdAndNotDeleted(@Param("id") Long id);

}
