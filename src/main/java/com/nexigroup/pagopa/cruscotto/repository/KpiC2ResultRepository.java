package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.KpiC2Result;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the KpiC2Result entity.
 */
@Repository
public interface KpiC2ResultRepository extends JpaRepository<KpiC2Result, Long> {

    /**
     * Find all KpiC2Result by instance.
     *
     * @param instance the instance
     * @return the list of KpiC2Result
     */
    List<KpiC2Result> findByInstanceOrderByAnalysisDateDesc(Instance instance);

    /**
     * Find KpiC2Result by instance and analysis date.
     *
     * @param instance the instance
     * @param analysisDate the analysis date
     * @return the KpiC2Result
     */
    KpiC2Result findByInstanceAndAnalysisDate(Instance instance, LocalDate analysisDate);

    /**
     * Find all KpiC2Result with pagination and filtering.
     *
     * @param pageable the pagination information
     * @return the page of KpiC2Result
     */
    @Query("SELECT k FROM KpiC2Result k ORDER BY k.analysisDate DESC")
    Page<KpiC2Result> findAllWithPagination(Pageable pageable);

    /**
     * Find KpiC2Result by instance with pagination.
     *
     * @param instance the instance
     * @param pageable the pagination information
     * @return the page of KpiC2Result
     */
    Page<KpiC2Result> findByInstanceOrderByAnalysisDateDesc(Instance instance, Pageable pageable);

    /**
     * Check if exists KpiC2Result for instance and analysis date.
     *
     * @param instance the instance
     * @param analysisDate the analysis date
     * @return true if exists
     */
    boolean existsByInstanceAndAnalysisDate(Instance instance, LocalDate analysisDate);

    /**
     * Delete KpiC2Result by instance.
     *
     * @param instance the instance
     */
    void deleteByInstance(Instance instance);

    /**
     * Find latest KpiC2Result by instance.
     *
     * @param instance the instance
     * @return the latest KpiC2Result
     */
    @Query("SELECT k FROM KpiC2Result k WHERE k.instance = :instance ORDER BY k.analysisDate DESC LIMIT 1")
    KpiC2Result findLatestByInstance(@Param("instance") Instance instance);

    /**
     * Delete all KpiC2Result by instanceModule id.
     *
     * @param instanceModuleId the instanceModule id
     * @return the number of deleted records
     */
    int deleteAllByInstanceModuleId(Long instanceModuleId);

    /**
     * Find all KpiC2Result by instanceModule id ordered by analysis date desc.
     *
     * @param instanceModuleId the instanceModule id
     * @return the list of KpiC2Result
     */
    List<KpiC2Result> findAllByInstanceModuleIdOrderByAnalysisDateDesc(Long instanceModuleId);
}
