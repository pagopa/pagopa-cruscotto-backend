package com.nexigroup.pagopa.cruscotto.web.rest;

import com.nexigroup.pagopa.cruscotto.service.KpiC2Service;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiC2AnalyticDataDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiC2DetailResultDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiC2ResultDTO;
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
 * - Retrieving KPI C2 calculation results
 * - Accessing detail results (monthly/total analysis)
 * - Drill-down into analytic data
 * - Managing KPI C2 calculations
 */
@RestController
@RequestMapping("/api/kpi-C2")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "KPI C2", description = "KPI B.8 Zero Incident API")
public class KpiC2Resource {

    private final KpiC2Service kpiC2Service;

    /**
     * GET /api/kpi-C2/results/{instanceId} : Get KPI C2 results for an instance
     *
     * @param instanceId the instance ID
     * @param pageable pagination parameters
     * @return the ResponseEntity with status 200 (OK) and the list of KPI C2 results
     */
    @GetMapping("/results/{instanceId}")
    @PreAuthorize("hasAuthority('KPI_C2_RESULT_READ')")
    @Operation(summary = "Get KPI C2 results", description = "Get all KPI C2 calculation results for a specific instance")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "KPI C2 results retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Instance not found")
    })
    public ResponseEntity<Page<KpiC2ResultDTO>> getKpiC2Results(
        @Parameter(description = "Instance ID", required = true) @PathVariable String instanceId,
        @Parameter(description = "Pagination parameters") Pageable pageable
    ) {
        log.debug("REST request to get KPI C2 results for instance: {}", instanceId);
        Page<KpiC2ResultDTO> results = kpiC2Service.getKpiC2Results(instanceId, pageable);
        return ResponseEntity.ok(results);
    }

    /**
     * GET /api/kpi-C2/results/{instanceId}/latest : Get latest KPI C2 result for an instance
     *
     * @param instanceId the instance ID
     * @return the ResponseEntity with status 200 (OK) and the latest KPI C2 result
     */
    @GetMapping("/results/{instanceId}/latest")
    @PreAuthorize("hasAuthority('KPI_C2_RESULT_READ')")
    @Operation(summary = "Get latest KPI C2 result", description = "Get the most recent KPI C2 calculation result for a specific instance")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Latest KPI C2 result retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "No results found for instance")
    })
    public ResponseEntity<List<KpiC2ResultDTO>> getLatestKpiC2Results(
        @Parameter(description = "Instance ID", required = true) @PathVariable String instanceId
    ) {
        log.debug("REST request to get latest KPI C2 results for instance: {}", instanceId);
        List<KpiC2ResultDTO> results = kpiC2Service.getKpiC2Results(instanceId);
        return ResponseEntity.ok(results);
    }

    /**
     * GET /api/kpi-C2/detail-results/{instanceId} : Get KPI C2 detail results
     *
     * @param instanceId the instance ID
     * @param analysisDate the analysis date
     * @param pageable pagination parameters
     * @return the ResponseEntity with status 200 (OK) and the list of detail results
     */
    @GetMapping("/detail-results/{instanceId}")
    @PreAuthorize("hasAuthority('KPI_C2_DETAIL_RESULT_READ')")
    @Operation(summary = "Get KPI C2 detail results", description = "Get detailed results (monthly/total) for a specific KPI C2 analysis")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "KPI C2 detail results retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid analysis date format"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Results not found")
    })
    public ResponseEntity<Page<KpiC2DetailResultDTO>> getKpiC2DetailResults(
        @Parameter(description = "Instance ID", required = true) @PathVariable String instanceId,
        @Parameter(description = "Analysis date (ISO format)", required = true)
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime analysisDate,
        @Parameter(description = "Pagination parameters") Pageable pageable
    ) {
        log.debug("REST request to get KPI C2 detail results for instance: {} and date: {}", instanceId, analysisDate);
        Page<KpiC2DetailResultDTO> results = kpiC2Service.getKpiC2DetailResults(instanceId, analysisDate, pageable);
        return ResponseEntity.ok(results);
    }

    /**
     * GET /api/kpi-C2/analytic-data/{instanceId} : Get KPI C2 analytic data for drill-down
     *
     * @param instanceId the instance ID
     * @param analysisDate the analysis date
     * @param pageable pagination parameters
     * @return the ResponseEntity with status 200 (OK) and the list of analytic data
     */
    @GetMapping("/analytic-data/{instanceId}")
    @PreAuthorize("hasAuthority('KPI_C2_ANALYTIC_DATA_READ')")
    @Operation(summary = "Get KPI C2 analytic data", description = "Get granular analytic data for drill-down functionality")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "KPI C2 analytic data retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid analysis date format"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Data not found")
    })
    public ResponseEntity<Page<KpiC2AnalyticDataDTO>> getKpiC2AnalyticData(
        @Parameter(description = "Instance ID", required = true) @PathVariable String instanceId,
        @Parameter(description = "Analysis date (ISO format)", required = true)
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime analysisDate,
        @Parameter(description = "Pagination parameters") Pageable pageable
    ) {
        log.debug("REST request to get KPI C2 analytic data for instance: {} and date: {}", instanceId, analysisDate);
        Page<KpiC2AnalyticDataDTO> data = kpiC2Service.getKpiC2AnalyticData(instanceId, analysisDate, pageable);
        return ResponseEntity.ok(data);
    }

    /**
     * POST /api/kpi-C2/recalculate/{instanceId} : Recalculate KPI C2 for an instance
     *
     * @param instanceId the instance ID
     * @return the ResponseEntity with status 200 (OK) and the recalculated result
     */
    @PostMapping("/recalculate/{instanceId}")
    @PreAuthorize("hasAuthority('KPI_C2_RESULT_WRITE')")
    @Operation(summary = "Recalculate KPI C2", description = "Force recalculation of KPI C2 for a specific instance")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "KPI C2 recalculated successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Instance not found"),
        @ApiResponse(responseCode = "500", description = "Calculation failed")
    })
    public ResponseEntity<KpiC2ResultDTO> recalculateKpiC2(
        @Parameter(description = "Instance ID", required = true) @PathVariable String instanceId
    ) {
        log.info("REST request to recalculate KPI C2 for instance: {}", instanceId);
        KpiC2ResultDTO result = kpiC2Service.recalculateKpiC2(instanceId);
        return ResponseEntity.ok(result);
    }

    /**
     * DELETE /api/kpi-C2/data/{instanceId} : Delete KPI C2 data for an instance
     *
     * @param instanceId the instance ID
     * @return the ResponseEntity with status 204 (NO_CONTENT)
     */
    @DeleteMapping("/data/{instanceId}")
    @PreAuthorize("hasAuthority('KPI_C2_RESULT_DELETE')")
    @Operation(summary = "Delete KPI C2 data", description = "Delete all KPI C2 calculation data for a specific instance")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "KPI C2 data deleted successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Instance not found")
    })
    public ResponseEntity<Void> deleteKpiC2Data(
        @Parameter(description = "Instance ID", required = true) @PathVariable String instanceId
    ) {
        log.info("REST request to delete KPI C2 data for instance: {}", instanceId);
        kpiC2Service.deleteKpiC2Data(instanceId);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/kpi-C2/results/{instanceId}/exists : Check if KPI C2 calculation exists
     *
     * @param instanceId the instance ID
     * @param analysisDate the analysis date
     * @return the ResponseEntity with status 200 (OK) and boolean result
     */
    @GetMapping("/results/{instanceId}/exists")
    @PreAuthorize("hasAuthority('KPI_C2_RESULT_READ')")
    @Operation(summary = "Check KPI C2 calculation exists", description = "Check if a KPI C2 calculation exists for the specified instance and date")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Check completed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid analysis date format"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<Boolean> existsKpiC2Calculation(
        @Parameter(description = "Instance ID", required = true) @PathVariable String instanceId,
        @Parameter(description = "Analysis date (ISO format)", required = true)
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime analysisDate
    ) {
        log.debug("REST request to check if KPI C2 calculation exists for instance: {} and date: {}", instanceId, analysisDate);
        boolean exists = kpiC2Service.existsKpiC2Calculation(instanceId, analysisDate);
        return ResponseEntity.ok(exists);
    }
}
