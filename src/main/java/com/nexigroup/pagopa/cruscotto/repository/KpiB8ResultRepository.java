package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.KpiB8Result;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the KpiB8Result entity.
 */
@Repository
public interface KpiB8ResultRepository extends JpaRepository<KpiB8Result, Long> {

    /**
     * Find all KpiB8Result by instance.
     *
     * @param instance the instance
     * @return the list of KpiB8Result
     */
    List<KpiB8Result> findByInstanceOrderByAnalysisDateDesc(Instance instance);

    /**
     * Find KpiB8Result by instance and analysis date.
     *
     * @param instance the instance
     * @param analysisDate the analysis date
     * @return the KpiB8Result
     */
    KpiB8Result findByInstanceAndAnalysisDate(Instance instance, LocalDate analysisDate);

    /**
     * Find all KpiB8Result with pagination and filtering.
     *
     * @param pageable the pagination information
     * @return the page of KpiB8Result
     */
    @Query("SELECT k FROM KpiB8Result k ORDER BY k.analysisDate DESC")
    Page<KpiB8Result> findAllWithPagination(Pageable pageable);

    /**
     * Find KpiB8Result by instance with pagination.
     *
     * @param instance the instance
     * @param pageable the pagination information
     * @return the page of KpiB8Result
     */
    Page<KpiB8Result> findByInstanceOrderByAnalysisDateDesc(Instance instance, Pageable pageable);

    /**
     * Check if exists KpiB8Result for instance and analysis date.
     *
     * @param instance the instance
     * @param analysisDate the analysis date
     * @return true if exists
     */
    boolean existsByInstanceAndAnalysisDate(Instance instance, LocalDate analysisDate);

    /**
     * Delete KpiB8Result by instance.
     *
     * @param instance the instance
     */
    void deleteByInstance(Instance instance);

    /**
     * Find latest KpiB8Result by instance.
     *
     * @param instance the instance
     * @return the latest KpiB8Result
     */
    @Query("SELECT k FROM KpiB8Result k WHERE k.instance = :instance ORDER BY k.analysisDate DESC LIMIT 1")
    KpiB8Result findLatestByInstance(@Param("instance") Instance instance);

    /**
     * Delete all KpiB8Result by instanceModule id.
     *
     * @param instanceModuleId the instanceModule id
     * @return the number of deleted records
     */
    int deleteAllByInstanceModuleId(Long instanceModuleId);

    /**
     * Find all KpiB8Result by instanceModule id ordered by analysis date desc.
     *
     * @param instanceModuleId the instanceModule id
     * @return the list of KpiB8Result
     */
    List<KpiB8Result> findAllByInstanceModuleIdOrderByAnalysisDateDesc(Long instanceModuleId);
}
