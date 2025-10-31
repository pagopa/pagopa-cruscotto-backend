package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.KpiConfiguration;
import com.nexigroup.pagopa.cruscotto.domain.Module;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository for the KpiConfiguration entity.
 */
@Repository
public interface KpiConfigurationRepository extends JpaRepository<KpiConfiguration, Long>, JpaSpecificationExecutor<KpiConfiguration> {

    /**
     * Find KpiConfiguration by module.
     *
     * @param module the module
     * @return the KpiConfiguration
     */
    Optional<KpiConfiguration> findByModule(Module module);

    /**
     * Find KpiConfiguration by module code.
     *
     * @param moduleCode the module code
     * @return the KpiConfiguration
     */
    @Query("SELECT kc FROM KpiConfiguration kc JOIN kc.module m WHERE m.code = :moduleCode")
    Optional<KpiConfiguration> findByModuleCode(@Param("moduleCode") String moduleCode);
}
