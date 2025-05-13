package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.KpiConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository for the KpiConfiguration entity.
 */
@Repository
public interface KpiConfigurationRepository extends JpaRepository<KpiConfiguration, Long>, JpaSpecificationExecutor<KpiConfiguration> {}
