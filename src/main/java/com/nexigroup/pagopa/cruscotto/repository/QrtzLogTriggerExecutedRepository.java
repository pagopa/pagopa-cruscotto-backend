package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.QrtzLogTriggerExecuted;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository for the QrtzLogTriggerExecuted entity.
 */

@Repository
public interface QrtzLogTriggerExecutedRepository
    extends JpaRepository<QrtzLogTriggerExecuted, Long>, JpaSpecificationExecutor<QrtzLogTriggerExecuted> {
    Optional<QrtzLogTriggerExecuted> findFirstByFireInstanceId(String fireInstanceId);
}
