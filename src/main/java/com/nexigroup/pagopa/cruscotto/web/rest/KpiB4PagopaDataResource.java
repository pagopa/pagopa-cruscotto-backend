package com.nexigroup.pagopa.cruscotto.web.rest;

import com.nexigroup.pagopa.cruscotto.security.AuthoritiesConstants;
import com.nexigroup.pagopa.cruscotto.service.dto.PagopaAPILogDTO;

import tech.jhipster.web.util.ResponseUtil;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing KPI B.4 PagoPA Raw Data Drilldown.
 * Provides endpoint to retrieve historical API log data snapshots from pagopa_apilog table
 * as the final drilldown level in KPI B.4 analysis.
 */
@RestController
@RequestMapping("/api")
public class KpiB4PagopaDataResource {

    /**
     * GET  /kpi-b4-pagopa-data/{analyticDataId} : Get historical API log data snapshots for a given KPI B.4 analytic data ID.
     * This represents the final drilldown level showing the historical snapshot data preserved during analysis.
     *
     * @param analyticDataId the ID of the KpiB4AnalyticData record
     * @return the list of historical API log snapshot records wrapped in ResponseEntity, or 404 Not Found if none exist
     */
    @GetMapping("kpi-b4-pagopa-data/{analyticDataId}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.KPI_B4_ANALITIC_DATA_DETAIL + "\")")
    public ResponseEntity<List<PagopaAPILogDTO>> getkpib4ByAnalyticDataId(@PathVariable Long analyticDataId) {
        // TODO: Implement service to retrieve pagopa api log data by analytic data id
        // For now return empty list - this will be implemented later when the service is ready
        List<PagopaAPILogDTO> pagopaData = new ArrayList<>();
        return ResponseUtil.wrapOrNotFound(
            Optional.ofNullable(pagopaData.isEmpty() ? null : pagopaData)
        );
    }
}