package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.InstanceModule;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.InstanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data repository for the InstanceModule entity.
 */
@Repository
public interface InstanceModuleRepository extends JpaRepository<InstanceModule, Long>, JpaSpecificationExecutor<InstanceModule> {

    @Query("SELECT i FROM InstanceModule i  WHERE i.instance.id = :idInstance and i.moduleCode = :moduleCode ")
    InstanceModule findByInstanceAndModuleCode(@Param("idInstance") Long id, @Param("moduleCode")  String moduleCode);



}

