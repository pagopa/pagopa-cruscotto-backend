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
import com.nexigroup.pagopa.cruscotto.service.KpiA1ResultService;
import com.nexigroup.pagopa.cruscotto.service.KpiA2ResultService;
import com.nexigroup.pagopa.cruscotto.service.KpiB2ResultService;
import com.nexigroup.pagopa.cruscotto.service.KpiB9ResultService;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiA1ResultDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiA2ResultDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB2ResultDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB9ResultDTO;

import java.util.List;
import java.util.Optional;

import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link 'KpiA1Result, KpiA2Result, KpiB2Result'}.
 */
@RestController
@RequestMapping("/api")
public class KpiResultResource {

    private final Logger log = LoggerFactory.getLogger(KpiResultResource.class);

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final KpiA1ResultService kpiA1ResultService;

    private final KpiA2ResultService kpiA2ResultService;

    private final KpiB2ResultService kpiB2ResultService;

    private final KpiB9ResultService kpiB9ResultService;

    public KpiResultResource(
        KpiA1ResultService kpiA1ResultService,
        KpiB2ResultService kpiB2ResultService,
        KpiA2ResultService kpiA2ResultService,
        KpiB9ResultService kpiB9ResultService
    ) {
        this.kpiA1ResultService = kpiA1ResultService;
        this.kpiB2ResultService = kpiB2ResultService;
        this.kpiA2ResultService = kpiA2ResultService;
        this.kpiB9ResultService = kpiB9ResultService;
    }

    /**
     * {@code GET  /kpi-results/a1/module/{moduleId}} : get the kpiA1ResultDTOs associated to the "id" instanceModule of type A1.
     *
     * @param moduleId the id of the instanceModuleDTO the kpi results to retrieve are associated to
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the KpiA1ResultDTOs, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("kpi-results/a1/module/{moduleId}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.KPI_A1_RESULT_DETAIL + "\")")
    public ResponseEntity<List<KpiA1ResultDTO>> getKpiA1Results(@PathVariable Long moduleId) {
        log.debug("REST request to get kpi results of instanceModule : {} of type a1", moduleId);
        List<KpiA1ResultDTO> kpiA1Results = kpiA1ResultService.findByInstanceModuleId(moduleId);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(kpiA1Results));
    }

    /**
     * {@code GET  /kpi-results/a2/module/{moduleId}} : get the kpiA2ResultDTOs associated to the "id" instanceModule of type A2.
     *
     * @param moduleId the id of the instanceModuleDTO the kpi results to retrieve are associated to
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the KpiA2ResultDTOs, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("kpi-results/a2/module/{moduleId}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.KPI_A2_RESULT_DETAIL + "\")")
    public ResponseEntity<List<KpiA2ResultDTO>> getKpiA2Results(@PathVariable Long moduleId) {
        log.debug("REST request to get kpi results of instanceModule : {} of type a2", moduleId);
        List<KpiA2ResultDTO> kpiA2Results = kpiA2ResultService.findByInstanceModuleId(moduleId);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(kpiA2Results));
    }

    /**
     * {@code GET  /kpi-results/b2/module/{moduleId}} : get the kpiB2ResultDTOs associated to the "id" instanceModule of type B2.
     *
     * @param moduleId the id of the instanceModuleDTO the kpi results to retrieve are associated to
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the KpiB2ResultDTOs, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("kpi-results/b2/module/{moduleId}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.KPI_B2_RESULT_DETAIL + "\")")
    public ResponseEntity<List<KpiB2ResultDTO>> getKpiB2Results(@PathVariable Long moduleId) {
        log.debug("REST request to get kpi results of instanceModule : {} of type b2", moduleId);
        List<KpiB2ResultDTO> kpiB2Results = kpiB2ResultService.findByInstanceModuleId(moduleId);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(kpiB2Results));
    }

    /**
     * {@code GET  /kpi-results/b9/module/{moduleId}} : get the kpiB9ResultDTOs associated to the "id" instanceModule of type B9.
     *
     * @param moduleId the id of the instanceModuleDTO the kpi results to retrieve are associated to
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the KpiB9ResultDTOs, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("kpi-results/b9/module/{moduleId}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.KPI_B9_RESULT_DETAIL + "\")")
    public ResponseEntity<List<KpiB9ResultDTO>> getKpiB9Results(@PathVariable Long moduleId) {
        log.debug("REST request to get kpi results of instanceModule : {} of type b9", moduleId);
        List<KpiB9ResultDTO> kpiB9Results = kpiB9ResultService.findByInstanceModuleId(moduleId);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(kpiB9Results));
    }
}
