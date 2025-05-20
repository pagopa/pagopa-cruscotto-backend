package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.AnagPlannedShutdown;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository for the AnagPlannedShutdown entity.
 */
@Repository
public interface AnagPlannedShutdownRepository
    extends
        JpaRepository<AnagPlannedShutdown, Long>,
        JpaSpecificationExecutor<AnagPlannedShutdown>,
        QueryByExampleExecutor<AnagPlannedShutdown> {}
