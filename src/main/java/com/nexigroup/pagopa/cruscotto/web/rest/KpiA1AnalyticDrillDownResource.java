package com.nexigroup.pagopa.cruscotto.web.rest;

import com.nexigroup.pagopa.cruscotto.service.KpiA1AnalyticDrillDownService;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiA1AnalyticDrillDownDTO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<KpiA1AnalyticDrillDownDTO>> getByAnalyticDataId(@PathVariable Long analyticDataId) {
        List<KpiA1AnalyticDrillDownDTO> result = drillDownService.findByKpiA1AnalyticDataId(analyticDataId);
        return ResponseEntity.ok(result);
    }
}
