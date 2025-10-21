package com.nexigroup.pagopa.cruscotto.web.rest;

import com.nexigroup.pagopa.cruscotto.security.AuthoritiesConstants;
import com.nexigroup.pagopa.cruscotto.service.PagoPaPaymentReceiptDrilldownService;
import com.nexigroup.pagopa.cruscotto.service.dto.PagoPaPaymentReceiptDrilldownDTO;
import java.time.LocalDate;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing {@link com.nexigroup.pagopa.cruscotto.domain.PagoPaPaymentReceiptDrilldown}.
 */
@RestController
@RequestMapping("/api/kpi-b9")
public class PagoPaPaymentReceiptDrilldownResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(PagoPaPaymentReceiptDrilldownResource.class);

    private final PagoPaPaymentReceiptDrilldownService drilldownService;

    public PagoPaPaymentReceiptDrilldownResource(PagoPaPaymentReceiptDrilldownService drilldownService) {
        this.drilldownService = drilldownService;
    }

    /**
     * {@code GET /api/kpi-b9/drilldown/instance/{instanceId}/station/{stationId}} : Get quarter-hour drilldown data.
     *
     * @param instanceId the instance ID
     * @param stationId the station ID
     * @param evaluationDate the evaluation date (specific day to get quarter-hour breakdown)
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the quarter-hour drilldown data
     */
    @GetMapping("/drilldown/instance/{instanceId}/station/{stationId}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.KPI_B9_ANALITIC_DATA_DETAIL + "\")")
    public ResponseEntity<List<PagoPaPaymentReceiptDrilldownDTO>> getQuarterHourDrilldown(
        @PathVariable Long instanceId,
        @PathVariable Long stationId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate evaluationDate
    ) {
        LOGGER.debug("REST request to get quarter-hour drilldown data for instance: {}, station: {}, evaluation date: {}", 
                     instanceId, stationId, evaluationDate);

        List<PagoPaPaymentReceiptDrilldownDTO> result = drilldownService.getDrilldownData(
            instanceId, 
            stationId, 
            evaluationDate
        );

        LOGGER.info("Returning {} drilldown records for instance {} station {} on evaluation date {}", 
                    result.size(), instanceId, stationId, evaluationDate);

        return ResponseEntity.ok(result);
    }
}