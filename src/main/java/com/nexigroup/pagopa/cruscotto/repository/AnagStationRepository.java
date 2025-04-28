package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.AnagStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository for the AnagStation entity.
 */
@Repository
public interface AnagStationRepository extends JpaRepository<AnagStation, Long>, JpaSpecificationExecutor<AnagStation>, QueryByExampleExecutor<AnagStation> {

}
