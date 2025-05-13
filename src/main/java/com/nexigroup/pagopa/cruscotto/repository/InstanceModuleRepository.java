package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.InstanceModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository for the InstanceModule entity.
 */
@Repository
public interface InstanceModuleRepository extends JpaRepository<InstanceModule, Long>, JpaSpecificationExecutor<InstanceModule> {}
