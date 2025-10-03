package com.nexigroup.pagopa.cruscotto.web.rest;

import com.nexigroup.pagopa.cruscotto.security.AuthoritiesConstants;
import com.nexigroup.pagopa.cruscotto.service.PagopaNumeroStandinService;
import com.nexigroup.pagopa.cruscotto.service.dto.PagopaNumeroStandinDTO;

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
 * REST controller for managing KPI B.3 PagoPA Raw Data Drilldown.
 * Provides endpoint to retrieve raw Stand-In data from pagopa_numero_standin table
 * as the final drilldown level in KPI B.3 analysis.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class KpiB3PagopaDataResource {

    private final PagopaNumeroStandinService pagopaNumeroStandinService;

    /**
     * GET  /kpi-b3-pagopa-data/{analyticDataId} : Get raw PagoPA Stand-In data for a given KPI B.3 analytic data ID.
     * This represents the final drilldown level showing the original data from PagoPA API.
     *
     * @param analyticDataId the ID of the KpiB3AnalyticData record
     * @return the list of raw PagoPA Stand-In records wrapped in ResponseEntity, or 404 Not Found if none exist
     */
    @GetMapping("kpi-b3-pagopa-data/{analyticDataId}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.KPI_B3_ANALITIC_DATA_DETAIL + "\")")
    public ResponseEntity<List<PagopaNumeroStandinDTO>> getByAnalyticDataId(@PathVariable Long analyticDataId) {
        List<PagopaNumeroStandinDTO> pagopaData = pagopaNumeroStandinService.findByAnalyticDataId(analyticDataId);
        return ResponseUtil.wrapOrNotFound(
            Optional.ofNullable(pagopaData == null || pagopaData.isEmpty() ? null : pagopaData)
        );
    }
}