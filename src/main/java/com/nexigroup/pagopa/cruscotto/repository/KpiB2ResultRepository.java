package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.KpiB2Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository for the KpiB2Result entity.
 */
@Repository
public interface KpiB2ResultRepository extends JpaRepository<KpiB2Result, Long>, JpaSpecificationExecutor<KpiB2Result> {}
