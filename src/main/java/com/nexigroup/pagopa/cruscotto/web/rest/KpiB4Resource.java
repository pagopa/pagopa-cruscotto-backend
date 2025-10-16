package com.nexigroup.pagopa.cruscotto.web.rest;

import com.nexigroup.pagopa.cruscotto.service.KpiB4Service;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB4AnalyticDataDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB4DetailResultDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB4ResultDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing KPI B.4 "Zero Incident" calculations and results.
 * 
 * Provides endpoints for:
 * - Retrieving KPI B4 calculation results
 * - Accessing detail results (monthly/total analysis)  
 * - Drill-down into analytic data
 * - Managing KPI B4 calculations
 */
@RestController
@RequestMapping("/api/kpi-b4")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "KPI B4", description = "KPI B.4 Zero Incident API")
public class KpiB4Resource {

    private final KpiB4Service kpiB4Service;

    /**
     * GET /api/kpi-b4/results/{instanceId} : Get KPI B4 results for an instance
     *
     * @param instanceId the instance ID
     * @param pageable pagination parameters
     * @return the ResponseEntity with status 200 (OK) and the list of KPI B4 results
     */
    @GetMapping("/results/{instanceId}")
    @PreAuthorize("hasAuthority('KPI_B4_RESULT_READ')")
    @Operation(summary = "Get KPI B4 results", description = "Get all KPI B4 calculation results for a specific instance")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "KPI B4 results retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Instance not found")
    })
    public ResponseEntity<Page<KpiB4ResultDTO>> getKpiB4Results(
        @Parameter(description = "Instance ID", required = true) @PathVariable String instanceId,
        @Parameter(description = "Pagination parameters") Pageable pageable
    ) {
        log.debug("REST request to get KPI B4 results for instance: {}", instanceId);
        Page<KpiB4ResultDTO> results = kpiB4Service.getKpiB4Results(instanceId, pageable);
        return ResponseEntity.ok(results);
    }

    /**
     * GET /api/kpi-b4/results/{instanceId}/latest : Get latest KPI B4 result for an instance
     *
     * @param instanceId the instance ID
     * @return the ResponseEntity with status 200 (OK) and the latest KPI B4 result
     */
    @GetMapping("/results/{instanceId}/latest")
    @PreAuthorize("hasAuthority('KPI_B4_RESULT_READ')")
    @Operation(summary = "Get latest KPI B4 result", description = "Get the most recent KPI B4 calculation result for a specific instance")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Latest KPI B4 result retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "No results found for instance")
    })
    public ResponseEntity<List<KpiB4ResultDTO>> getLatestKpiB4Results(
        @Parameter(description = "Instance ID", required = true) @PathVariable String instanceId
    ) {
        log.debug("REST request to get latest KPI B4 results for instance: {}", instanceId);
        List<KpiB4ResultDTO> results = kpiB4Service.getKpiB4Results(instanceId);
        return ResponseEntity.ok(results);
    }

    /**
     * GET /api/kpi-b4/detail-results/{instanceId} : Get KPI B4 detail results
     *
     * @param instanceId the instance ID
     * @param analysisDate the analysis date
     * @param pageable pagination parameters
     * @return the ResponseEntity with status 200 (OK) and the list of detail results
     */
    @GetMapping("/detail-results/{instanceId}")
    @PreAuthorize("hasAuthority('KPI_B4_DETAIL_RESULT_READ')")
    @Operation(summary = "Get KPI B4 detail results", description = "Get detailed results (monthly/total) for a specific KPI B4 analysis")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "KPI B4 detail results retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid analysis date format"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Results not found")
    })
    public ResponseEntity<Page<KpiB4DetailResultDTO>> getKpiB4DetailResults(
        @Parameter(description = "Instance ID", required = true) @PathVariable String instanceId,
        @Parameter(description = "Analysis date (ISO format)", required = true) 
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime analysisDate,
        @Parameter(description = "Pagination parameters") Pageable pageable
    ) {
        log.debug("REST request to get KPI B4 detail results for instance: {} and date: {}", instanceId, analysisDate);
        Page<KpiB4DetailResultDTO> results = kpiB4Service.getKpiB4DetailResults(instanceId, analysisDate, pageable);
        return ResponseEntity.ok(results);
    }

    /**
     * GET /api/kpi-b4/analytic-data/{instanceId} : Get KPI B4 analytic data for drill-down
     *
     * @param instanceId the instance ID
     * @param analysisDate the analysis date
     * @param pageable pagination parameters
     * @return the ResponseEntity with status 200 (OK) and the list of analytic data
     */
    @GetMapping("/analytic-data/{instanceId}")
    @PreAuthorize("hasAuthority('KPI_B4_ANALYTIC_DATA_READ')")
    @Operation(summary = "Get KPI B4 analytic data", description = "Get granular analytic data for drill-down functionality")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "KPI B4 analytic data retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid analysis date format"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Data not found")
    })
    public ResponseEntity<Page<KpiB4AnalyticDataDTO>> getKpiB4AnalyticData(
        @Parameter(description = "Instance ID", required = true) @PathVariable String instanceId,
        @Parameter(description = "Analysis date (ISO format)", required = true) 
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime analysisDate,
        @Parameter(description = "Pagination parameters") Pageable pageable
    ) {
        log.debug("REST request to get KPI B4 analytic data for instance: {} and date: {}", instanceId, analysisDate);
        Page<KpiB4AnalyticDataDTO> data = kpiB4Service.getKpiB4AnalyticData(instanceId, analysisDate, pageable);
        return ResponseEntity.ok(data);
    }

    /**
     * POST /api/kpi-b4/recalculate/{instanceId} : Recalculate KPI B4 for an instance
     *
     * @param instanceId the instance ID
     * @return the ResponseEntity with status 200 (OK) and the recalculated result
     */
    @PostMapping("/recalculate/{instanceId}")
    @PreAuthorize("hasAuthority('KPI_B4_RESULT_WRITE')")
    @Operation(summary = "Recalculate KPI B4", description = "Force recalculation of KPI B4 for a specific instance")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "KPI B4 recalculated successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Instance not found"),
        @ApiResponse(responseCode = "500", description = "Calculation failed")
    })
    public ResponseEntity<KpiB4ResultDTO> recalculateKpiB4(
        @Parameter(description = "Instance ID", required = true) @PathVariable String instanceId
    ) {
        log.info("REST request to recalculate KPI B4 for instance: {}", instanceId);
        KpiB4ResultDTO result = kpiB4Service.recalculateKpiB4(instanceId);
        return ResponseEntity.ok(result);
    }

    /**
     * DELETE /api/kpi-b4/data/{instanceId} : Delete KPI B4 data for an instance
     *
     * @param instanceId the instance ID
     * @return the ResponseEntity with status 204 (NO_CONTENT)
     */
    @DeleteMapping("/data/{instanceId}")
    @PreAuthorize("hasAuthority('KPI_B4_RESULT_DELETE')")
    @Operation(summary = "Delete KPI B4 data", description = "Delete all KPI B4 calculation data for a specific instance")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "KPI B4 data deleted successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Instance not found")
    })
    public ResponseEntity<Void> deleteKpiB4Data(
        @Parameter(description = "Instance ID", required = true) @PathVariable String instanceId
    ) {
        log.info("REST request to delete KPI B4 data for instance: {}", instanceId);
        kpiB4Service.deleteKpiB4Data(instanceId);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/kpi-b4/results/{instanceId}/exists : Check if KPI B4 calculation exists
     *
     * @param instanceId the instance ID
     * @param analysisDate the analysis date
     * @return the ResponseEntity with status 200 (OK) and boolean result
     */
    @GetMapping("/results/{instanceId}/exists")
    @PreAuthorize("hasAuthority('KPI_B4_RESULT_READ')")
    @Operation(summary = "Check KPI B4 calculation exists", description = "Check if a KPI B4 calculation exists for the specified instance and date")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Check completed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid analysis date format"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<Boolean> existsKpiB4Calculation(
        @Parameter(description = "Instance ID", required = true) @PathVariable String instanceId,
        @Parameter(description = "Analysis date (ISO format)", required = true) 
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime analysisDate
    ) {
        log.debug("REST request to check if KPI B4 calculation exists for instance: {} and date: {}", instanceId, analysisDate);
        boolean exists = kpiB4Service.existsKpiB4Calculation(instanceId, analysisDate);
        return ResponseEntity.ok(exists);
    }
}