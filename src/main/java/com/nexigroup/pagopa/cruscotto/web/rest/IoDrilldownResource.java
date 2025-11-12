package com.nexigroup.pagopa.cruscotto.web.rest;

import com.nexigroup.pagopa.cruscotto.security.AuthoritiesConstants;
import com.nexigroup.pagopa.cruscotto.service.IoDrilldownService;
import com.nexigroup.pagopa.cruscotto.service.mapper.IoDrilldownMapper;
import com.nexigroup.pagopa.cruscotto.service.dto.IoDrilldownDTO;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST resource for IO drilldown negative evidences of KPI C.1.
 */
@RestController
@RequestMapping("/api/kpi-c1-io-drilldown")
@RequiredArgsConstructor
public class IoDrilldownResource {
    private final IoDrilldownService ioDrilldownService;
    private final IoDrilldownMapper ioDrilldownMapper;

    @GetMapping("/{analyticDataId}")
    @PreAuthorize("hasAuthority('" + AuthoritiesConstants.KPI_C1_ANALITIC_DATA_DETAIL + "') or hasAuthority('" + AuthoritiesConstants.KPI_C1_IO_DRILLDOWN_DETAIL + "')")
    public ResponseEntity<List<IoDrilldownDTO>> getByAnalyticData(@PathVariable Long analyticDataId) {
        List<IoDrilldownDTO> list = ioDrilldownService.findByAnalyticDataId(analyticDataId).stream()
            .map(ioDrilldownMapper::toDto)
            .toList();
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(list.isEmpty() ? null : list));
    }
}
