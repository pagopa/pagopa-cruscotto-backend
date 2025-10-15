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
import com.nexigroup.pagopa.cruscotto.service.KpiB1AnalyticDrillDownService;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB1AnalyticDrillDownDTO;

import java.util.List;
import java.util.Optional;

import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing KPI B1 PagoPA data drilldown.
 */
@RestController
@RequestMapping("/api")
public class KpiB1PagopaDataResource {

    private final Logger log = LoggerFactory.getLogger(KpiB1PagopaDataResource.class);

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final KpiB1AnalyticDrillDownService kpiB1AnalyticDrillDownService;

    public KpiB1PagopaDataResource(KpiB1AnalyticDrillDownService kpiB1AnalyticDrillDownService) {
        this.kpiB1AnalyticDrillDownService = kpiB1AnalyticDrillDownService;
    }

    /**
     * {@code GET  /kpi-b1-pagopa-data/{analyticDataId}} : get the KpiB1AnalyticDrillDownDTOs
     * associated with the specified analyticDataId.
     *
     * @param analyticDataId the ID of the KPI B.1 analytic data
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of KpiB1AnalyticDrillDownDTOs in the body,
     *         or with status {@code 404 (Not Found)} if no data is found for the provided id.
     */
    @GetMapping("kpi-b1-pagopa-data/{analyticDataId}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.KPI_B1_ANALITIC_DATA_DETAIL + "\")")
    public ResponseEntity<List<KpiB1AnalyticDrillDownDTO>> getByAnalyticDataId(@PathVariable Long analyticDataId) {
        log.debug("REST request to get PagoPA data for KPI B.1 analytic data : {}", analyticDataId);
        List<KpiB1AnalyticDrillDownDTO> analyticDrillDowns = kpiB1AnalyticDrillDownService.findByAnalyticDataId(analyticDataId);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(analyticDrillDowns));
    }
}