package com.nexigroup.pagopa.cruscotto.web.rest;

import com.nexigroup.pagopa.cruscotto.service.KpiB8Service;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB8AnalyticDataDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB8DetailResultDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB8ResultDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST controller for managing KPI B.8 "Zero Incident" calculations and results.
 *
 * Provides endpoints for:
 * - Retrieving KPI B8 calculation results
 * - Accessing detail results (monthly/total analysis)
 * - Drill-down into analytic data
 * - Managing KPI B8 calculations
 */
@RestController
@RequestMapping("/api/kpi-b8")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "KPI B8", description = "KPI B.8 Zero Incident API")
public class KpiB8Resource {

    private final KpiB8Service kpiB8Service;

    /**
     * GET /api/kpi-b8/results/{instanceId} : Get KPI B8 results for an instance
     *
     * @param instanceId the instance ID
     * @param pageable pagination parameters
     * @return the ResponseEntity with status 200 (OK) and the list of KPI B8 results
     */
    @GetMapping("/results/{instanceId}")
    @PreAuthorize("hasAuthority('GTW.KPI_B8_RESULT_READ')")
    @Operation(summary = "Get KPI B8 results", description = "Get all KPI B8 calculation results for a specific instance")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "KPI B8 results retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Instance not found")
    })
    public ResponseEntity<Page<KpiB8ResultDTO>> getKpiB8Results(
        @Parameter(description = "Instance ID", required = true) @PathVariable String instanceId,
        @Parameter(description = "Pagination parameters") Pageable pageable
    ) {
        log.debug("REST request to get KPI B8 results for instance: {}", instanceId);
        Page<KpiB8ResultDTO> results = kpiB8Service.getKpiB8Results(instanceId, pageable);
        return ResponseEntity.ok(results);
    }

    /**
     * GET /api/kpi-b8/results/{instanceId}/latest : Get latest KPI B8 result for an instance
     *
     * @param instanceId the instance ID
     * @return the ResponseEntity with status 200 (OK) and the latest KPI B8 result
     */
    @GetMapping("/results/{instanceId}/latest")
    @PreAuthorize("hasAuthority('GTW.KPI_B8_RESULT_READ')")
    @Operation(summary = "Get latest KPI B8 result", description = "Get the most recent KPI B8 calculation result for a specific instance")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Latest KPI B8 result retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "No results found for instance")
    })
    public ResponseEntity<List<KpiB8ResultDTO>> getLatestKpiB8Results(
        @Parameter(description = "Instance ID", required = true) @PathVariable String instanceId
    ) {
        log.debug("REST request to get latest KPI B8 results for instance: {}", instanceId);
        List<KpiB8ResultDTO> results = kpiB8Service.getKpiB8Results(instanceId);
        return ResponseEntity.ok(results);
    }

    /**
     * GET /api/kpi-b8/detail-results/{instanceId} : Get KPI B8 detail results
     *
     * @param instanceId the instance ID
     * @param analysisDate the analysis date
     * @param pageable pagination parameters
     * @return the ResponseEntity with status 200 (OK) and the list of detail results
     */
    @GetMapping("/detail-results/{instanceId}")
    @PreAuthorize("hasAuthority('GTW.KPI_B8_DETAIL_RESULT_READ')")
    @Operation(summary = "Get KPI B8 detail results", description = "Get detailed results (monthly/total) for a specific KPI B8 analysis")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "KPI B8 detail results retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid analysis date format"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Results not found")
    })
    public ResponseEntity<Page<KpiB8DetailResultDTO>> getKpiB8DetailResults(
        @Parameter(description = "Instance ID", required = true) @PathVariable String instanceId,
        @Parameter(description = "Analysis date (ISO format)", required = true)
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime analysisDate,
        @Parameter(description = "Pagination parameters") Pageable pageable
    ) {
        log.debug("REST request to get KPI B8 detail results for instance: {} and date: {}", instanceId, analysisDate);
        Page<KpiB8DetailResultDTO> results = kpiB8Service.getKpiB8DetailResults(instanceId, analysisDate, pageable);
        return ResponseEntity.ok(results);
    }

    /**
     * GET /api/kpi-b8/analytic-data/{instanceId} : Get KPI B8 analytic data for drill-down
     *
     * @param instanceId the instance ID
     * @param analysisDate the analysis date
     * @param pageable pagination parameters
     * @return the ResponseEntity with status 200 (OK) and the list of analytic data
     */
    @GetMapping("/analytic-data/{instanceId}")
    @PreAuthorize("hasAuthority('GTW.KPI_B8_ANALYTIC_DATA_READ')")
    @Operation(summary = "Get KPI B8 analytic data", description = "Get granular analytic data for drill-down functionality")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "KPI B8 analytic data retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid analysis date format"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Data not found")
    })
    public ResponseEntity<Page<KpiB8AnalyticDataDTO>> getKpiB8AnalyticData(
        @Parameter(description = "Instance ID", required = true) @PathVariable String instanceId,
        @Parameter(description = "Analysis date (ISO format)", required = true)
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime analysisDate,
        @Parameter(description = "Pagination parameters") Pageable pageable
    ) {
        log.debug("REST request to get KPI B8 analytic data for instance: {} and date: {}", instanceId, analysisDate);
        Page<KpiB8AnalyticDataDTO> data = kpiB8Service.getKpiB8AnalyticData(instanceId, analysisDate, pageable);
        return ResponseEntity.ok(data);
    }

    /**
     * POST /api/kpi-b8/recalculate/{instanceId} : Recalculate KPI B8 for an instance
     *
     * @param instanceId the instance ID
     * @return the ResponseEntity with status 200 (OK) and the recalculated result
     */
    @PostMapping("/recalculate/{instanceId}")
    @PreAuthorize("hasAuthority('GTW.KPI_B8_RESULT_WRITE')")
    @Operation(summary = "Recalculate KPI B8", description = "Force recalculation of KPI B8 for a specific instance")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "KPI B8 recalculated successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Instance not found"),
        @ApiResponse(responseCode = "500", description = "Calculation failed")
    })
    public ResponseEntity<KpiB8ResultDTO> recalculateKpiB8(
        @Parameter(description = "Instance ID", required = true) @PathVariable String instanceId
    ) {
        log.info("REST request to recalculate KPI B8 for instance: {}", instanceId);
        KpiB8ResultDTO result = kpiB8Service.recalculateKpiB8(instanceId);
        return ResponseEntity.ok(result);
    }

    /**
     * DELETE /api/kpi-b8/data/{instanceId} : Delete KPI B8 data for an instance
     *
     * @param instanceId the instance ID
     * @return the ResponseEntity with status 204 (NO_CONTENT)
     */
    @DeleteMapping("/data/{instanceId}")
    @PreAuthorize("hasAuthority('GTW.KPI_B8_RESULT_DELETE')")
    @Operation(summary = "Delete KPI B8 data", description = "Delete all KPI B8 calculation data for a specific instance")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "KPI B8 data deleted successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Instance not found")
    })
    public ResponseEntity<Void> deleteKpiB8Data(
        @Parameter(description = "Instance ID", required = true) @PathVariable String instanceId
    ) {
        log.info("REST request to delete KPI B8 data for instance: {}", instanceId);
        kpiB8Service.deleteKpiB8Data(instanceId);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/kpi-b8/results/{instanceId}/exists : Check if KPI B8 calculation exists
     *
     * @param instanceId the instance ID
     * @param analysisDate the analysis date
     * @return the ResponseEntity with status 200 (OK) and boolean result
     */
    @GetMapping("/results/{instanceId}/exists")
    @PreAuthorize("hasAuthority('GTW.KPI_B8_RESULT_READ')")
    @Operation(summary = "Check KPI B8 calculation exists", description = "Check if a KPI B8 calculation exists for the specified instance and date")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Check completed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid analysis date format"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<Boolean> existsKpiB8Calculation(
        @Parameter(description = "Instance ID", required = true) @PathVariable String instanceId,
        @Parameter(description = "Analysis date (ISO format)", required = true)
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime analysisDate
    ) {
        log.debug("REST request to check if KPI B8 calculation exists for instance: {} and date: {}", instanceId, analysisDate);
        boolean exists = kpiB8Service.existsKpiB8Calculation(instanceId, analysisDate);
        return ResponseEntity.ok(exists);
    }
}
