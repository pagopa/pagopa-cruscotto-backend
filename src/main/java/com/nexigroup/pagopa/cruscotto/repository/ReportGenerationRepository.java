package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.ReportGeneration;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ReportStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ReportGeneration entity.
 */
@Repository
public interface ReportGenerationRepository extends JpaRepository<ReportGeneration, Long> {
    /**
     * Find report by instance ID and status.
     * Used to check for active reports (PENDING, IN_PROGRESS, COMPLETED).
     */
    @Query(
        "SELECT rg FROM ReportGeneration rg WHERE rg.instance.id = :instanceId AND rg.status IN :statuses ORDER BY rg.requestedDate DESC"
    )
    Optional<ReportGeneration> findByInstanceIdAndStatusIn(@Param("instanceId") Long instanceId, @Param("statuses") List<ReportStatus> statuses);

    /**
     * Find all reports by status.
     * Used by scheduled job to find PENDING reports.
     */
    List<ReportGeneration> findByStatus(ReportStatus status);

    /**
     * Find reports by status and instance status.
     * Used to find PENDING reports for instances in specific status (e.g., "eseguita").
     */
    @Query(
        "SELECT rg FROM ReportGeneration rg JOIN rg.instance i WHERE rg.status = :reportStatus AND i.status = :instanceStatus"
    )
    List<ReportGeneration> findByStatusAndInstanceStatusEquals(
        @Param("reportStatus") ReportStatus reportStatus,
        @Param("instanceStatus") String instanceStatus
    );

    /**
     * Find all reports by instance ID.
     */
    @Query("SELECT rg FROM ReportGeneration rg WHERE rg.instance.id = :instanceId ORDER BY rg.requestedDate DESC")
    List<ReportGeneration> findAllByInstanceId(@Param("instanceId") Long instanceId);
}
