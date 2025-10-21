package com.nexigroup.pagopa.cruscotto.web.rest;

import com.nexigroup.pagopa.cruscotto.security.AuthoritiesConstants;
import com.nexigroup.pagopa.cruscotto.service.KpiA1AnalyticDrillDownService;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiA1AnalyticDrillDownDTO;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import tech.jhipster.web.util.ResponseUtil;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/kpi-a1-analytic-drilldown")
@RequiredArgsConstructor
public class KpiA1AnalyticDrillDownResource {
    private final KpiA1AnalyticDrillDownService drillDownService;

    @GetMapping("/{analyticDataId}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.KPI_A1_ANALITIC_DATA_DETAIL + "\")")
    public ResponseEntity<List<KpiA1AnalyticDrillDownDTO>> getByAnalyticDataId(@PathVariable Long analyticDataId) {
        List<KpiA1AnalyticDrillDownDTO> result = drillDownService.findByKpiA1AnalyticDataId(analyticDataId);
        
        return ResponseUtil.wrapOrNotFound(
            Optional.ofNullable(result == null || result.isEmpty() ? null : result)
        );
        
    }
}
