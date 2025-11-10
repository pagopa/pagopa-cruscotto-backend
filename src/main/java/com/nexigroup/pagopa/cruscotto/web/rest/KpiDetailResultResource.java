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

import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.security.AuthoritiesConstants;
import com.nexigroup.pagopa.cruscotto.service.GenericKpiDetailResultService;
import com.nexigroup.pagopa.cruscotto.service.KpiA1DetailResultService;
import com.nexigroup.pagopa.cruscotto.service.KpiA2DetailResultService;
import com.nexigroup.pagopa.cruscotto.service.KpiB1DetailResultService;
import com.nexigroup.pagopa.cruscotto.service.KpiB2DetailResultService;
import com.nexigroup.pagopa.cruscotto.service.KpiB9DetailResultService;
import com.nexigroup.pagopa.cruscotto.service.KpiB3DetailResultService;
import com.nexigroup.pagopa.cruscotto.service.KpiB4DetailResultService;
import com.nexigroup.pagopa.cruscotto.service.KpiB5Service;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiDetailResultDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiA1DetailResultDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiA2DetailResultDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB1DetailResultDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB2DetailResultDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB9DetailResultDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB3DetailResultDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB4DetailResultDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB6DetailResultDTO;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    private final KpiB1DetailResultService kpiB1DetailResultService;

    private final KpiB2DetailResultService kpiB2DetailResultService;

    private final KpiB9DetailResultService kpiB9DetailResultService;

    private final KpiB3DetailResultService kpiB3DetailResultService;

    private final KpiB4DetailResultService kpiB4DetailResultService;

    private final GenericKpiDetailResultService genericKpiDetailResultService;

    private final KpiB5Service kpiB5Service;

    private final KpiB8DetailResultService kpiB8DetailResultService;

    private final KpiC2DetailResultService kpiC2DetailResultService;


    public KpiDetailResultResource(
        KpiA1DetailResultService kpiA1DetailResultService,
        KpiB1DetailResultService kpiB1DetailResultService,
        KpiB2DetailResultService kpiB2DetailResultService,
        KpiA2DetailResultService kpiA2DetailResultService,
        KpiB9DetailResultService kpiB9DetailResultService,
        KpiB3DetailResultService kpiB3DetailResultService,
        KpiB4DetailResultService kpiB4DetailResultService,
        KpiB5Service kpiB5Service,
        KpiB8DetailResultService kpiB8DetailResultService,
        GenericKpiDetailResultService genericKpiDetailResultService,
        KpiC2DetailResultService kpiC2DetailResultService
    ) {
        this.kpiA1DetailResultService = kpiA1DetailResultService;
        this.kpiB1DetailResultService = kpiB1DetailResultService;
        this.kpiB2DetailResultService = kpiB2DetailResultService;
        this.kpiA2DetailResultService = kpiA2DetailResultService;
        this.kpiB9DetailResultService = kpiB9DetailResultService;
        this.kpiB3DetailResultService = kpiB3DetailResultService;
        this.kpiB4DetailResultService = kpiB4DetailResultService;
        this.kpiB5Service = kpiB5Service;
        this.kpiB8DetailResultService = kpiB8DetailResultService;
        this.genericKpiDetailResultService = genericKpiDetailResultService;
        this.kpiC2DetailResultService = kpiC2DetailResultService;
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
     * {@code GET  /kpi-detail-results/b1/module/{resultId}} : get the KpiB1DetailResultDTOs
     * associated with the "resultId" of type B1.
     *
     * @param resultId the id of the instanceModuleDTO for which the KPI detail results are to be retrieved
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of KpiB1DetailResultDTOs in the body,
     *         or with status {@code 404 (Not Found)} if no results are found
     */
    @GetMapping("kpi-detail-results/b1/module/{resultId}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.KPI_B1_DETAIL_RESULT_DETAIL + "\")")
    public ResponseEntity<List<KpiB1DetailResultDTO>> getKpiB1DetailResults(@PathVariable Long resultId) {
        log.debug("REST request to get kpi detail results of instanceModule : {} of type b1", resultId);
        List<KpiB1DetailResultDTO> kpiB1DetailResults = kpiB1DetailResultService.findByResultId(resultId);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(kpiB1DetailResults));
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

    /**
     * {@code GET  /kpi-detail-results/b3/module/{resultId}} : get the KpiB3DetailResultDTOs
     * associated with the "resultId" of type B3.
     *
     * @param resultId the id of the kpiB3Result for which the KPI detail results are to be retrieved
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of KpiB3DetailResultDTOs in the body,
     *         or with status {@code 404 (Not Found)} if no results are found
     */
    @GetMapping("kpi-detail-results/b3/module/{resultId}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.KPI_B3_DETAIL_RESULT_DETAIL + "\")")
    public ResponseEntity<List<KpiB3DetailResultDTO>> getKpiB3DetailResults(@PathVariable Long resultId) {
        log.debug("REST request to get kpi detail results of kpiB3Result : {} of type b3", resultId);
        List<KpiB3DetailResultDTO> kpiB3DetailResults = kpiB3DetailResultService.findByResultId(resultId);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(kpiB3DetailResults));
    }

    /**
     * {@code GET  /kpi-detail-results/b4/module/{resultId}} : get the KpiB4DetailResultDTOs
     * associated with the "resultId" of type B4.
     *
     * @param resultId the id of the kpiB4Result for which the KPI detail results are to be retrieved
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of KpiB4DetailResultDTOs in the body,
     *         or with status {@code 404 (Not Found)} if no results are found
     */
    @GetMapping("kpi-detail-results/b4/module/{resultId}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.KPI_B4_DETAIL_RESULT_DETAIL + "\")")
    public ResponseEntity<List<KpiB4DetailResultDTO>> getKpiB4DetailResults(@PathVariable Long resultId) {
        log.debug("REST request to get kpi detail results of kpiB4Result : {} of type b4", resultId);
        List<KpiB4DetailResultDTO> kpiB4DetailResults = kpiB4DetailResultService.findByResultId(resultId);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(kpiB4DetailResults));
    }

    /**
     * {@code GET  /kpi-detail-results/b5/module/{resultId}} : get the KpiB5DetailResultDTOs
     * associated with the "resultId" of type B5.
     *
     * @param resultId the id of the kpiB5Result for which the KPI detail results are to be retrieved
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of KpiB5DetailResultDTOs in the body,
     *         or with status {@code 404 (Not Found)} if no results are found
     */
    @GetMapping("kpi-detail-results/b5/module/{resultId}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.KPI_B5_DETAIL_RESULT_DETAIL + "\")")
    public ResponseEntity<List<KpiB5DetailResultDTO>> getKpiB5DetailResults(@PathVariable Long resultId) {
        log.debug("REST request to get kpi detail results of kpiB5Result : {} of type b5", resultId);
        List<KpiB5DetailResultDTO> kpiB5DetailResults = kpiB5Service.findDetailsByKpiB5ResultId(resultId);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(kpiB5DetailResults));
    }

    /**
     * {@code GET  /kpi-detail-results/b4/module/{resultId}} : get the KpiB4DetailResultDTOs
     * associated with the "resultId" of type B4.
     *
     * @param resultId the id of the kpiB4Result for which the KPI detail results are to be retrieved
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of KpiB4DetailResultDTOs in the body,
     *         or with status {@code 404 (Not Found)} if no results are found
     */
    @GetMapping("kpi-detail-results/b8/module/{resultId}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.KPI_B8_DETAIL_RESULT_DETAIL + "\")")
    public ResponseEntity<List<KpiB8DetailResultDTO>> getKpiB8DetailResults(@PathVariable Long resultId) {
        log.debug("REST request to get kpi detail results of kpiB8Result : {} of type b8", resultId);
        List<KpiB8DetailResultDTO> kpiB8DetailResults = kpiB8DetailResultService.findByResultId(resultId);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(kpiB8DetailResults));
    }

    /**
     * {@code GET  /kpi-detail-results/b4/module/{resultId}} : get the KpiB4DetailResultDTOs
     * associated with the "resultId" of type B4.
     *
     * @param resultId the id of the kpiB4Result for which the KPI detail results are to be retrieved
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of KpiB4DetailResultDTOs in the body,
     *         or with status {@code 404 (Not Found)} if no results are found
     */
    @GetMapping("kpi-detail-results/c2/module/{resultId}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.KPI_C2_DETAIL_RESULT_DETAIL + "\")")
    public ResponseEntity<List<KpiC2DetailResultDTO>> getKpiC2DetailResults(@PathVariable Long resultId) {
        log.debug("REST request to get kpi detail results of kpiC2Result : {} of type c2", resultId);
        List<KpiC2DetailResultDTO> kpiC2DetailResults = kpiC2DetailResultService.findByResultId(resultId);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(kpiC2DetailResults));
    }



    /**
     * {@code GET  /kpi-detail-results/b6/module/{resultId}} : get the KpiB6DetailResultDTOs
     * associated with the "resultId" of type B6.
     *
     * @param resultId the id of the kpiB6Result for which the KPI detail results are to be retrieved
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of KpiB6DetailResultDTOs in the body,
     *         or with status {@code 404 (Not Found)} if no results are found
     */
    @GetMapping("kpi-detail-results/b6/module/{resultId}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.KPI_B6_DETAIL_RESULT_DETAIL + "\")")
    public ResponseEntity<List<KpiB6DetailResultDTO>> getKpiB6DetailResults(@PathVariable Long resultId) {
        log.debug("REST request to get kpi detail results of kpiB6Result : {} of type b6", resultId);
        List<KpiDetailResultDTO> genericData = genericKpiDetailResultService.findByKpiResultId(ModuleCode.B6, resultId);
        List<KpiB6DetailResultDTO> kpiB6DetailResults = genericData.stream()
                .map(KpiB6DetailResultDTO::new)
                .collect(Collectors.toList());
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(kpiB6DetailResults));
    }
}
