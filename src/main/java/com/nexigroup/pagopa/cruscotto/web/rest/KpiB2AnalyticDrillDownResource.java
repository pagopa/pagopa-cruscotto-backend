package com.nexigroup.pagopa.cruscotto.web.rest;

import com.nexigroup.pagopa.cruscotto.security.AuthoritiesConstants;
import com.nexigroup.pagopa.cruscotto.service.KpiB2AnalyticDrillDownService;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB2AnalyticDrillDownDTO;
import lombok.RequiredArgsConstructor;
import tech.jhipster.web.util.ResponseUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing KPI B.2 Analytic DrillDown Data.
 * Provides endpoint to retrieve drilldown records by analytic data ID.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class KpiB2AnalyticDrillDownResource {

    private final KpiB2AnalyticDrillDownService service;

    /**
     * GET  /kpi-b2-analytic-drilldown/{analyticDataId} : Get all drilldown records for a given analytic data ID.
     *
     * @param analyticDataId the ID of the analytic data
     * @return the list of drilldown records wrapped in ResponseEntity, or 404 Not Found if none exist
     */
    @GetMapping("kpi-b2-analytic-drilldown/{analyticDataId}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.KPI_B2_ANALITIC_DATA_DETAIL + "\")")
    public ResponseEntity<List<KpiB2AnalyticDrillDownDTO>> getByAnalyticDataId(@PathVariable Long analyticDataId) {
        List<KpiB2AnalyticDrillDownDTO> drillDowns = service.findByKpiB2AnalyticDataId(analyticDataId);
        return ResponseUtil.wrapOrNotFound(
            Optional.ofNullable(drillDowns == null || drillDowns.isEmpty() ? null : drillDowns)
        );
    }
}
