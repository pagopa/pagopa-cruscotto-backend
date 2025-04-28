package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.AnagPartner;
import com.nexigroup.pagopa.cruscotto.domain.Instance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository for the AnagPartner entity.
 */
@Repository
public interface AnagPartnerRepository extends JpaRepository<AnagPartner, Long>, JpaSpecificationExecutor<AnagPartner>, QueryByExampleExecutor<AnagPartner> {

}
