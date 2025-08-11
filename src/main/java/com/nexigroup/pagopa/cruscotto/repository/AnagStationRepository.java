package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.AnagStation;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Data repository for the AnagStation entity.
 */
@Repository
public interface AnagStationRepository
    extends JpaRepository<AnagStation, Long>, JpaSpecificationExecutor<AnagStation>, QueryByExampleExecutor<AnagStation> {
    
    Optional<AnagStation> findOneByName(String name);
    
    @Modifying
    @Transactional
    @Query("UPDATE AnagStation s SET s.associatedInstitutes = :count WHERE s.id = :stationId")
    void updateAssociatedInstitutesCount(@Param("stationId") Long stationId, @Param("count") Integer count);
}
