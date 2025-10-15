package com.nexigroup.pagopa.cruscotto.repository;

import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.KpiB4Result;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the KpiB4Result entity.
 */
@Repository
public interface KpiB4ResultRepository extends JpaRepository<KpiB4Result, Long> {

    /**
     * Find all KpiB4Result by instance.
     *
     * @param instance the instance
     * @return the list of KpiB4Result
     */
    List<KpiB4Result> findByInstanceOrderByAnalysisDateDesc(Instance instance);

    /**
     * Find KpiB4Result by instance and analysis date.
     *
     * @param instance the instance
     * @param analysisDate the analysis date
     * @return the KpiB4Result
     */
    KpiB4Result findByInstanceAndAnalysisDate(Instance instance, LocalDate analysisDate);

    /**
     * Find all KpiB4Result with pagination and filtering.
     *
     * @param pageable the pagination information
     * @return the page of KpiB4Result
     */
    @Query("SELECT k FROM KpiB4Result k ORDER BY k.analysisDate DESC")
    Page<KpiB4Result> findAllWithPagination(Pageable pageable);

    /**
     * Find KpiB4Result by instance with pagination.
     *
     * @param instance the instance
     * @param pageable the pagination information
     * @return the page of KpiB4Result
     */
    Page<KpiB4Result> findByInstanceOrderByAnalysisDateDesc(Instance instance, Pageable pageable);

    /**
     * Check if exists KpiB4Result for instance and analysis date.
     *
     * @param instance the instance
     * @param analysisDate the analysis date
     * @return true if exists
     */
    boolean existsByInstanceAndAnalysisDate(Instance instance, LocalDate analysisDate);

    /**
     * Delete KpiB4Result by instance.
     *
     * @param instance the instance
     */
    void deleteByInstance(Instance instance);

    /**
     * Find latest KpiB4Result by instance.
     *
     * @param instance the instance
     * @return the latest KpiB4Result
     */
    @Query("SELECT k FROM KpiB4Result k WHERE k.instance = :instance ORDER BY k.analysisDate DESC LIMIT 1")
    KpiB4Result findLatestByInstance(@Param("instance") Instance instance);

    /**
     * Delete all KpiB4Result by instanceModule id.
     *
     * @param instanceModuleId the instanceModule id
     * @return the number of deleted records
     */
    int deleteAllByInstanceModuleId(Long instanceModuleId);

    /**
     * Find all KpiB4Result by instanceModule id ordered by analysis date desc.
     *
     * @param instanceModuleId the instanceModule id
     * @return the list of KpiB4Result
     */
    List<KpiB4Result> findAllByInstanceModuleIdOrderByAnalysisDateDesc(Long instanceModuleId);
}