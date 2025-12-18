package com.nexigroup.pagopa.cruscotto.web.rest;


import com.nexigroup.pagopa.cruscotto.security.AuthoritiesConstants;

import com.nexigroup.pagopa.cruscotto.service.report.excel.DrillDownExcelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/kpi-analytic-drilldown")
@RequiredArgsConstructor
public class KpiAnalyticDrillDownExcelResource {

    private final DrillDownExcelService drillDownExcelService;

    /**
     * Export Excel DrillDown per Instance
     */
    @GetMapping("/export/{instanceId}")
    public ResponseEntity<byte[]> exportDrillDownExcel(
        @PathVariable Long instanceId
    ) {

        byte[] excel = drillDownExcelService.generateExcel(
            String.valueOf(instanceId)
        );

        return ResponseEntity.ok()
            .header(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=kpi-analytic-drilldown-" + instanceId + ".xlsx"
            )
            .contentType(
                MediaType.parseMediaType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                )
            )
            .body(excel);
    }
}
