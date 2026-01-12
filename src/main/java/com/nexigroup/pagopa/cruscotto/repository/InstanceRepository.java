package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.InstanceStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data repository for the Instance entity.
 */
@Repository
public interface InstanceRepository extends JpaRepository<Instance, Long>, JpaSpecificationExecutor<Instance> {

    List<Instance> findByPartnerIdAndStatusOrderByLastAnalysisDateDesc(Long partnerId, InstanceStatus status);

    @Query("SELECT i FROM Instance i JOIN FETCH i.partner WHERE i.id = :id")
    Instance findByIdWithPartner(@Param("id") Long id);

}
