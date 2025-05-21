package com.nexigroup.pagopa.cruscotto.web.rest;

import com.nexigroup.pagopa.cruscotto.service.*;
import com.nexigroup.pagopa.cruscotto.service.dto.*;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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

    public KpiDetailResultResource(
        KpiA1DetailResultService kpiA1DetailResultService,
        KpiB2DetailResultService kpiB2DetailResultService,
        KpiA2DetailResultService kpiA2DetailResultService
    ) {
        this.kpiA1DetailResultService = kpiA1DetailResultService;
        this.kpiB2DetailResultService = kpiB2DetailResultService;
        this.kpiA2DetailResultService = kpiA2DetailResultService;
    }

    /**
     * {@code GET  /kpi-detail-results/a1/module/{moduleId}} : get the kpiA1DetailResultDTOs associated to the "id" instanceModule of type A1.
     *
     * @param moduleId the id of the instanceModuleDTO the kpi detail results to retrieve are associated to
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the KpiA1DetailResultDTOs, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("kpi-detail-results/a1/module/{moduleId}")
    //@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.GTW_DETAIL_FUNCTION + "\")")
    public ResponseEntity<List<KpiA1DetailResultDTO>> getKpiA1DetailResults(@PathVariable Long moduleId) {
        log.debug("REST request to get kpi detail results of instanceModule : {} of type a1", moduleId);
        List<KpiA1DetailResultDTO> kpiA1DetailResults = kpiA1DetailResultService.findByInstanceModuleId(moduleId);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(kpiA1DetailResults));
    }

    /**
     * {@code GET  /kpi-results/a2/module/{moduleId}} : get the kpiA2DetailResultDTOs associated to the "id" instanceModule of type A2.
     *
     * @param moduleId the id of the instanceModuleDTO the kpi detail results to retrieve are associated to
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the KpiA2DetailResultDTOs, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("kpi-detail-results/a2/module/{moduleId}")
    //@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.GTW_DETAIL_FUNCTION + "\")")
    public ResponseEntity<List<KpiA2DetailResultDTO>> getKpiA2DetailResults(@PathVariable Long moduleId) {
        log.debug("REST request to get kpi detail results of instanceModule : {} of type a2", moduleId);
        List<KpiA2DetailResultDTO> kpiA2DetailResults = kpiA2DetailResultService.findByInstanceModuleId(moduleId);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(kpiA2DetailResults));
    }

    /**
     * {@code GET  /kpi-detial-results/b2/module/{moduleId}} : get the kpiB2DetailResultDTOs associated to the "id" instanceModule of type B2.
     *
     * @param moduleId the id of the instanceModuleDTO the kpi detail results to retrieve are associated to
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the KpiB2DetailResultDTOs, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("kpi-detail-results/b2/module/{moduleId}")
    //@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.GTW_DETAIL_FUNCTION + "\")")
    public ResponseEntity<List<KpiB2DetailResultDTO>> getKpiB2DetailResults(@PathVariable Long moduleId) {
        log.debug("REST request to get kpi detail results of instanceModule : {} of type b2", moduleId);
        List<KpiB2DetailResultDTO> kpiB2DetailResults = kpiB2DetailResultService.findByInstanceModuleId(moduleId);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(kpiB2DetailResults));
    }
}
