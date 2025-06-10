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
import com.nexigroup.pagopa.cruscotto.service.KpiA1DetailResultService;
import com.nexigroup.pagopa.cruscotto.service.KpiA2DetailResultService;
import com.nexigroup.pagopa.cruscotto.service.KpiB2DetailResultService;
import com.nexigroup.pagopa.cruscotto.service.KpiB9DetailResultService;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiA1DetailResultDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiA2DetailResultDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB2DetailResultDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB9DetailResultDTO;

import java.util.List;
import java.util.Optional;

import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link 'KpiA1Result, KpiA2Result, KpiB2Result'}.
 */
@RestController
@RequestMapping("/api")
public class KpiDetailResultResource {

    private final Logger log = LoggerFactory.getLogger(KpiDetailResultResource.class);

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final KpiA1DetailResultService kpiA1DetailResultService;

    private final KpiA2DetailResultService kpiA2DetailResultService;

    private final KpiB2DetailResultService kpiB2DetailResultService;

    private final KpiB9DetailResultService kpiB9DetailResultService;

    
    public KpiDetailResultResource(
        KpiA1DetailResultService kpiA1DetailResultService,
        KpiB2DetailResultService kpiB2DetailResultService,
        KpiA2DetailResultService kpiA2DetailResultService,
        KpiB9DetailResultService kpiB9DetailResultService
    ) {
        this.kpiA1DetailResultService = kpiA1DetailResultService;
        this.kpiB2DetailResultService = kpiB2DetailResultService;
        this.kpiA2DetailResultService = kpiA2DetailResultService;
        this.kpiB9DetailResultService = kpiB9DetailResultService;
    }

    /**
     * Retrieves the KPI detail results of type A1 associated with the specified result ID.
     *
     * @param resultId the ID of the instanceModuleDTO for which the KPI detail results are to be retrieved
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of KpiA1DetailResultDTOs in the body,
     *         or with status {@code 404 (Not Found)} if no results are found
     */
    @GetMapping("kpi-detail-results/a1/module/{resultId}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.KPI_A1_DETAIL_RESULT_DETAIL + "\")")
    public ResponseEntity<List<KpiA1DetailResultDTO>> getKpiA1DetailResults(@PathVariable Long resultId) {
        log.debug("REST request to get kpi detail results of instanceModule : {} of type a1", resultId);
        List<KpiA1DetailResultDTO> kpiA1DetailResults = kpiA1DetailResultService.findByResultId(resultId);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(kpiA1DetailResults));
    }

    /**
     * {@code GET  /kpi-detail-results/a2/module/{resultId}} : get the KpiA2DetailResultDTOs
     * associated with the "resultId" of type A2.
     *
     * @param resultId the id of the instanceModuleDTO for which the KPI detail results are to be retrieved
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of KpiA2DetailResultDTOs in the body,
     *         or with status {@code 404 (Not Found)} if no results are found
     */
    @GetMapping("kpi-detail-results/a2/module/{resultId}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.KPI_A2_DETAIL_RESULT_DETAIL + "\")")
    public ResponseEntity<List<KpiA2DetailResultDTO>> getKpiA2DetailResults(@PathVariable Long resultId) {
        log.debug("REST request to get kpi detail results of instanceModule : {} of type a2", resultId);
        List<KpiA2DetailResultDTO> kpiA2DetailResults = kpiA2DetailResultService.findByResultId(resultId);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(kpiA2DetailResults));
    }

    /**
     * {@code GET  /kpi-detail-results/b2/module/{resultId}} : get the KpiB2DetailResultDTOs
     * associated with the "resultId" of type B2.
     *
     * @param resultId the id of the instanceModuleDTO for which the KPI detail results are to be retrieved
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of KpiB2DetailResultDTOs in the body,
     *         or with status {@code 404 (Not Found)} if no results are found
     */
    @GetMapping("kpi-detail-results/b2/module/{resultId}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.KPI_B2_DETAIL_RESULT_DETAIL + "\")")
    public ResponseEntity<List<KpiB2DetailResultDTO>> getKpiB2DetailResults(@PathVariable Long resultId) {
        log.debug("REST request to get kpi detail results of instanceModule : {} of type b2", resultId);
        List<KpiB2DetailResultDTO> kpiB2DetailResults = kpiB2DetailResultService.findByResultId(resultId);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(kpiB2DetailResults));
    }

    /**
     * {@code GET  /kpi-detail-results/b9/module/{resultId}} : get the KpiB9DetailResultDTOs
     * associated with the "resultId" of type B9.
     *
     * @param resultId the id of the instanceModuleDTO for which the KPI detail results are to be retrieved
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of KpiB9DetailResultDTOs in the body,
     *         or with status {@code 404 (Not Found)} if no results are found
     */
    @GetMapping("kpi-detail-results/b9/module/{resultId}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.KPI_B9_DETAIL_RESULT_DETAIL + "\")")
    public ResponseEntity<List<KpiB9DetailResultDTO>> getKpiB9DetailResults(@PathVariable Long resultId) {
        log.debug("REST request to get kpi detail results of instanceModule : {} of type b9", resultId);
        List<KpiB9DetailResultDTO> kpiB9DetailResults = kpiB9DetailResultService.findByResultId(resultId);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(kpiB9DetailResults));
    }
}
