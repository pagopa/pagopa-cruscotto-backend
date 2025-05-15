package com.nexigroup.pagopa.cruscotto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

import com.nexigroup.pagopa.cruscotto.domain.AnagPartner;

/**
 * Spring Data repository for the AnagPartner entity.
 */
@Repository
public interface AnagPartnerRepository extends JpaRepository<AnagPartner, Long>, JpaSpecificationExecutor<AnagPartner>, QueryByExampleExecutor<AnagPartner> {

}
