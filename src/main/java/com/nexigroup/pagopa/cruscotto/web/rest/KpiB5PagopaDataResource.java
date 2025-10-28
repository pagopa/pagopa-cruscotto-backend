package com.nexigroup.pagopa.cruscotto.web.rest;

import com.nexigroup.pagopa.cruscotto.security.AuthoritiesConstants;
import com.nexigroup.pagopa.cruscotto.service.KpiB5Service;
import com.nexigroup.pagopa.cruscotto.service.dto.PagopaSpontaneiDTO;
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
@RequestMapping("/api/kpi-b5-pagopa-data")
@RequiredArgsConstructor
public class KpiB5PagopaDataResource {
    private final KpiB5Service kpiB5Service;

    /**
     * {@code GET  /{analyticDataId}} : get the PagopaSpontaneiDTOs
     * associated with the specified "analyticDataId" of KpiB5AnalyticData.
     *
     * @param analyticDataId the ID of the KPI B.5 analytic data for which the pagopa spontaneous data should be retrieved
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the body containing the list of PagopaSpontaneiDTOs,
     *         or with status {@code 404 (Not Found)} if no data is found for the provided id.
     */
    @GetMapping("/{analyticDataId}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.KPI_B5_PAGOPA_DATA_DETAIL + "\")")
    public ResponseEntity<List<PagopaSpontaneiDTO>> getkpib5ByAnalyticDataId(@PathVariable Long analyticDataId) {
        List<PagopaSpontaneiDTO> result = kpiB5Service.findDrillDownByAnalyticDataId(analyticDataId);
        
        return ResponseUtil.wrapOrNotFound(
            Optional.ofNullable(result == null || result.isEmpty() ? null : result)
        );
    }
}