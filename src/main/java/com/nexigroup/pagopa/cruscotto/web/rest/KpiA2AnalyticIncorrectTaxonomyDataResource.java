package com.nexigroup.pagopa.cruscotto.web.rest;

import com.nexigroup.pagopa.cruscotto.security.AuthoritiesConstants;
import com.nexigroup.pagopa.cruscotto.service.KpiA2AnalyticIncorrectTaxonomyDataService;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiA2AnalyticIncorrectTaxonomyDataDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB9AnalyticDataDTO;

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
 * REST controller for managing KPI A.2 Analytic Incorrect Taxonomy Data.
 * Provides endpoint to retrieve incorrect taxonomy records by analytic data ID.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class KpiA2AnalyticIncorrectTaxonomyDataResource {
    
    private final KpiA2AnalyticIncorrectTaxonomyDataService service;

    /**
     * GET  /kpi-a2-analytic-incorrect-taxonomy-data/{analyticDataId} : Get all incorrect taxonomy records for a given analytic data ID.
     *
     * @param analyticDataId the ID of the analytic data
     * @return the list of incorrect taxonomy records wrapped in ResponseEntity, or 404 Not Found if none exist
     */
    @GetMapping("kpi-a2-analytic-incorrect-taxonomy-data/{analyticDataId}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.PAGOPA_TAXONOMY_AGGREGATE_POSITION_LIST + "\")")
    public ResponseEntity<List<KpiA2AnalyticIncorrectTaxonomyDataDTO>> getByAnalyticDataId(@PathVariable Long analyticDataId) {
        List<KpiA2AnalyticIncorrectTaxonomyDataDTO> kpiA2AnalyticIncTaxData = service.findByKpiA2AnalyticDataId(analyticDataId);
        return ResponseUtil.wrapOrNotFound(
                Optional.ofNullable(kpiA2AnalyticIncTaxData == null || kpiA2AnalyticIncTaxData.isEmpty() ? null
                        : kpiA2AnalyticIncTaxData));
    }
}
