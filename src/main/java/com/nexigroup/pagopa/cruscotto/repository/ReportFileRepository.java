package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.ReportFile;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ReportFile entity.
 */
@Repository
public interface ReportFileRepository extends JpaRepository<ReportFile, Long> {
    /**
     * Find report file by report generation ID.
     */
    @Query("SELECT rf FROM ReportFile rf WHERE rf.reportGeneration.id = :reportGenerationId")
    Optional<ReportFile> findByReportGenerationId(@Param("reportGenerationId") Long reportGenerationId);

    /**
     * Find report file by blob name (Azure Blob Storage identifier).
     */
    Optional<ReportFile> findByBlobName(String blobName);
}
