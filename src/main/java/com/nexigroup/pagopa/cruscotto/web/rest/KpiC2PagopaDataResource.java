package com.nexigroup.pagopa.cruscotto.web.rest;

import com.nexigroup.pagopa.cruscotto.security.AuthoritiesConstants;
import com.nexigroup.pagopa.cruscotto.service.KpiC2AnalyticDrillDownService;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiC2AnalyticDrillDownDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.jhipster.web.util.ResponseUtil;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing KPI B.4 PagoPA Raw Data Drilldown.
 * Provides endpoint to retrieve historical API log data snapshots from pagopa_api_log_drilldown table
 * as the final drilldown level in KPI B.4 analysis.
 */
@RestController
@RequestMapping("/api")
public class KpiC2PagopaDataResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(KpiC2PagopaDataResource.class);

    private final KpiC2AnalyticDrillDownService kpiC2AnalyticDrillDownService;

    public KpiC2PagopaDataResource(KpiC2AnalyticDrillDownService kpiC2AnalyticDrillDownService) {
        this.kpiC2AnalyticDrillDownService = kpiC2AnalyticDrillDownService;
    }

    /**
     * GET  /kpi-C2-pagopa-data/{analyticDataId} : Get historical API log data snapshots for a given KPI B.4 analytic data ID.
     * This represents the final drilldown level showing the historical snapshot data preserved during analysis.
     *
     * @param analyticDataId the ID of the KpiB4AnalyticData record
     * @return the list of historical API log snapshot records wrapped in ResponseEntity, or 404 Not Found if none exist
     */
    @GetMapping("kpi-c2-pagopa-data/{analyticDataId}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.KPI_C2_ANALITIC_DATA_DETAIL + "\")")
    public ResponseEntity<List<KpiC2AnalyticDrillDownDTO>> getkpiC2ByAnalyticDataId(@PathVariable Long analyticDataId) {
        LOGGER.debug("REST request to get pagopa API log drilldown data for KPI B.4 analytic data ID: {}", analyticDataId);

        List<KpiC2AnalyticDrillDownDTO> pagopaData = kpiC2AnalyticDrillDownService.findByKpiC2AnalyticDataId(analyticDataId);

        return ResponseUtil.wrapOrNotFound(
            Optional.ofNullable(pagopaData.isEmpty() ? null : pagopaData)
        );
    }
}
