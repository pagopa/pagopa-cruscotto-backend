package com.nexigroup.pagopa.cruscotto.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nexigroup.pagopa.cruscotto.security.AuthoritiesConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing KPI C1 PagoPA data drilldown.
 */
@RestController
@RequestMapping("/api")
public class KpiC1PagopaDataResource {

    private final Logger log = LoggerFactory.getLogger(KpiC1PagopaDataResource.class);

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    /**
     * {@code GET  /kpi-c1-pagopa-data/{analyticDataId}} : get the KpiC1AnalyticDrillDownDTOs
     * associated with the specified analyticDataId.
     *
     * @param analyticDataId the ID of the KPI C.1 analytic data
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of KpiC1AnalyticDrillDownDTOs in the body,
     *         or with status {@code 404 (Not Found)} if no data is found for the provided id.
     */
    @GetMapping("kpi-c1-pagopa-data/{analyticDataId}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.KPI_C1_PAGOPA_DATA_DETAIL + "\")")
    public ResponseEntity<List<Object>> getByAnalyticDataId(@PathVariable Long analyticDataId) {
        log.debug("REST request to get PagoPA drill-down data for KPI C.1 analytic data : {}", analyticDataId);
        // TODO: Implement actual drill-down logic when KpiC1AnalyticDrillDown entity and service are available
        // For now, return an empty list to satisfy the API contract
        List<Object> emptyList = new ArrayList<>();
        return ResponseUtil.wrapOrNotFound(Optional.of(emptyList));
    }
}