package com.nexigroup.pagopa.cruscotto.web.rest;

import com.nexigroup.pagopa.cruscotto.service.*;
import com.nexigroup.pagopa.cruscotto.service.dto.*;
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
import com.nexigroup.pagopa.cruscotto.service.KpiB1ResultService;
import com.nexigroup.pagopa.cruscotto.service.KpiB2ResultService;
import com.nexigroup.pagopa.cruscotto.service.KpiB9ResultService;
import com.nexigroup.pagopa.cruscotto.service.KpiB3ResultService;
import com.nexigroup.pagopa.cruscotto.service.KpiB4ResultService;
import com.nexigroup.pagopa.cruscotto.service.GenericKpiResultService;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiA1ResultDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiA2ResultDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB1ResultDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB2ResultDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB9ResultDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB3ResultDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB4ResultDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiResultDTO;

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

    private final KpiB1ResultService kpiB1ResultService;

    private final KpiB2ResultService kpiB2ResultService;

    private final KpiB9ResultService kpiB9ResultService;

    private final KpiB3ResultService kpiB3ResultService;

    private final KpiB8ResultService kpiB8ResultService;


    private final KpiB4ResultService kpiB4ResultService;

    private final KpiB5ResultService kpiB5ResultService;

    private final GenericKpiResultService genericKpiResultService;

    public KpiResultResource(
            KpiA1ResultService kpiA1ResultService,
            KpiB1ResultService kpiB1ResultService,
            KpiB2ResultService kpiB2ResultService,
            KpiA2ResultService kpiA2ResultService,
            KpiB9ResultService kpiB9ResultService,
            KpiB3ResultService kpiB3ResultService,
            KpiB4ResultService kpiB4ResultService,
        GenericKpiResultService genericKpiResultService,
            KpiB5ResultService kpiB5ResultService,
            KpiB8ResultService kpiB8ResultService

            ) {
            this.kpiA1ResultService = kpiA1ResultService;
            this.kpiB1ResultService = kpiB1ResultService;
            this.kpiB2ResultService = kpiB2ResultService;
            this.kpiA2ResultService = kpiA2ResultService;
            this.kpiB9ResultService = kpiB9ResultService;
            this.kpiB3ResultService = kpiB3ResultService;
            this.kpiB8ResultService = kpiB8ResultService;
            this.kpiB4ResultService = kpiB4ResultService;
            this.kpiB5ResultService = kpiB5ResultService;
            this.genericKpiResultService = genericKpiResultService;
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
     * {@code GET  /kpi-results/b1/module/{moduleId}} : get the kpiB1ResultDTOs associated to the "id" instanceModule of type B1.
     *
     * @param moduleId the id of the instanceModuleDTO the kpi results to retrieve are associated to
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the KpiB1ResultDTOs, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("kpi-results/b1/module/{moduleId}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.KPI_B1_RESULT_DETAIL + "\")")
    public ResponseEntity<List<KpiB1ResultDTO>> getKpiB1Results(@PathVariable Long moduleId) {
        log.debug("REST request to get kpi results of instanceModule : {} of type b1", moduleId);
        List<KpiB1ResultDTO> kpiB1Results = kpiB1ResultService.findByInstanceModuleId(moduleId);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(kpiB1Results));
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

    /**
     * {@code GET  /kpi-results/b3/module/{moduleId}} : get the kpiB3ResultDTOs associated to the "id" instanceModule of type B3.
     *
     * @param moduleId the id of the instanceModuleDTO the kpi results to retrieve are associated to
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the KpiB3ResultDTOs, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("kpi-results/b3/module/{moduleId}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.KPI_B3_RESULT_DETAIL + "\")")
    public ResponseEntity<List<KpiB3ResultDTO>> getKpiB3Results(@PathVariable Long moduleId) {
        log.debug("REST request to get kpi results of instanceModule : {} of type b3", moduleId);
        List<KpiB3ResultDTO> kpiB3Results = kpiB3ResultService.findByInstanceModuleId(moduleId);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(kpiB3Results));
    }

    /**
     * {@code GET  /kpi-results/b3/module/{moduleId}} : get the kpiB3ResultDTOs associated to the "id" instanceModule of type B3.
     *
     * @param moduleId the id of the instanceModuleDTO the kpi results to retrieve are associated to
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the KpiB3ResultDTOs, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("kpi-results/b8/module/{moduleId}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.KPI_B3_RESULT_DETAIL + "\")")
    public ResponseEntity<List<KpiB8ResultDTO>> getKpiB8Results(@PathVariable Long moduleId) {
        log.debug("REST request to get kpi results of instanceModule : {} of type b8", moduleId);
        List<KpiB8ResultDTO> kpiB3Results = kpiB8ResultService.findByInstanceModuleId(moduleId);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(kpiB3Results));
    }



    /**
     * {@code GET  /kpi-results/b4/module/{moduleId}} : get the kpiB4ResultDTOs associated to the "id" instanceModule of type B4.
     *
     * @param moduleId the id of the instanceModuleDTO the kpi results to retrieve are associated to
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the KpiB4ResultDTOs, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("kpi-results/b4/module/{moduleId}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.KPI_B4_RESULT_DETAIL + "\")")
    public ResponseEntity<List<KpiB4ResultDTO>> getKpiB4Results(@PathVariable Long moduleId) {
        log.debug("REST request to get kpi results of instanceModule : {} of type b4", moduleId);
        List<KpiB4ResultDTO> kpiB4Results = kpiB4ResultService.findByInstanceModuleId(moduleId);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(kpiB4Results));
    }

    /**
     * {@code GET  /kpi-results/b5/module/{moduleId}} : get the kpiB5ResultDTOs associated to the "id" instanceModule of type B5.
     *
     * @param moduleId the id of the instanceModuleDTO the kpi results to retrieve are associated to
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the KpiB5ResultDTOs, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("kpi-results/b5/module/{moduleId}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.KPI_B5_RESULT_DETAIL + "\")")
    public ResponseEntity<List<KpiB5ResultDTO>> getKpiB5Results(@PathVariable Long moduleId) {
        log.debug("REST request to get kpi results of instanceModule : {} of type b5", moduleId);
        List<KpiB5ResultDTO> kpiB5Results = kpiB5ResultService.findByInstanceModuleId(moduleId);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(kpiB5Results));
    }

    /**
     * {@code GET  /kpi-results/b6/module/{moduleId}} : get the kpiB6ResultDTOs associated to the "id" instanceModule of type B6.
     *
     * @param moduleId the id of the instanceModuleDTO the kpi results to retrieve are associated to
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the KpiResultDTOs, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("kpi-results/b6/module/{moduleId}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.KPI_B6_RESULT_DETAIL + "\")")
    public ResponseEntity<List<KpiResultDTO>> getKpiB6Results(@PathVariable Long moduleId) {
        log.debug("REST request to get kpi results of instanceModule : {} of type b6", moduleId);
        List<KpiResultDTO> kpiB6Results = genericKpiResultService.findByInstanceModuleId(ModuleCode.B6, moduleId);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(kpiB6Results));
    }
}
