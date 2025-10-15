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
import com.nexigroup.pagopa.cruscotto.service.KpiA1AnalyticDataService;
import com.nexigroup.pagopa.cruscotto.service.KpiA2AnalyticDataService;
import com.nexigroup.pagopa.cruscotto.service.KpiB1AnalyticDataService;
import com.nexigroup.pagopa.cruscotto.service.KpiB2AnalyticDataService;
import com.nexigroup.pagopa.cruscotto.service.KpiB9AnalyticDataService;
import com.nexigroup.pagopa.cruscotto.service.KpiB3AnalyticDataService;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiA1AnalyticDataDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiA2AnalyticDataDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB1AnalyticDataDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB2AnalyticDataDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB9AnalyticDataDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB3AnalyticDataDTO;

import java.util.List;
import java.util.Optional;

import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link 'KpiA1AnalyticData, KpiA2AnalyticData, KpiB2AnalyticData'}.
 */
@RestController
@RequestMapping("/api")
public class KpiAnalyticDataResource {

    private final Logger log = LoggerFactory.getLogger(KpiAnalyticDataResource.class);

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final KpiA1AnalyticDataService kpiA1AnalyticDataService;

    private final KpiA2AnalyticDataService kpiA2AnalyticDataService;

    private final KpiB1AnalyticDataService kpiB1AnalyticDataService;

    private final KpiB2AnalyticDataService kpiB2AnalyticDataService;

    private final KpiB9AnalyticDataService kpiB9AnalyticDataService;

    private final KpiB3AnalyticDataService kpiB3AnalyticDataService;
    

    public KpiAnalyticDataResource(
        KpiA1AnalyticDataService kpiA1AnalyticDataService,
        KpiB1AnalyticDataService kpiB1AnalyticDataService,
        KpiB2AnalyticDataService kpiB2AnalyticDataService,
        KpiA2AnalyticDataService kpiA2AnalyticDataService,
        KpiB9AnalyticDataService kpiB9AnalyticDataService,
        KpiB3AnalyticDataService kpiB3AnalyticDataService
    ) {
        this.kpiA1AnalyticDataService = kpiA1AnalyticDataService;
        this.kpiB1AnalyticDataService = kpiB1AnalyticDataService;
        this.kpiB2AnalyticDataService = kpiB2AnalyticDataService;
        this.kpiA2AnalyticDataService = kpiA2AnalyticDataService;
        this.kpiB9AnalyticDataService = kpiB9AnalyticDataService;
        this.kpiB3AnalyticDataService = kpiB3AnalyticDataService;
    }

    /**
     * {@code GET  /kpi-analytic-data/a1/module/{detailResultId}} : get the KpiA1AnalyticDataDTOs
     * associated with the specified "detailResultId".
     *
     * @param detailResultId the id of the instanceModule for which the kpi analytic data of type A1 should be retrieved
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body containing the KpiA1AnalyticDataDTOs,
     *         or with status {@code 404 (Not Found)} if no data is found for the provided id.
     */
    @GetMapping("kpi-analytic-data/a1/module/{detailResultId}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.KPI_A1_ANALITIC_DATA_DETAIL + "\")")
    public ResponseEntity<List<KpiA1AnalyticDataDTO>> getKpiA1AnalyticDataResults(@PathVariable Long detailResultId) {
        log.debug("REST request to get kpi analytic data of instanceModule : {} of type a1", detailResultId);
        List<KpiA1AnalyticDataDTO> kpiA1AnalyticData = kpiA1AnalyticDataService.findByDetailResultId(detailResultId);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(kpiA1AnalyticData));
    }

    /**
     * {@code GET  /kpi-analytic-data/a2/module/{detailResultId}} : Retrieves the KpiA2AnalyticDataDTOs
     * associated with the specified "detailResultId" of the instanceModule.
     *
     * @param detailResultId the identifier of the instanceModule for which the kpi analytic data of type A2 should be retrieved
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the body containing the list of KpiA2AnalyticDataDTOs,
     *         or with status {@code 404 (Not Found)} if no data is found for the provided id.
     */
    @GetMapping("kpi-analytic-data/a2/module/{detailResultId}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.KPI_A2_ANALITIC_DATA_DETAIL + "\")")
    public ResponseEntity<List<KpiA2AnalyticDataDTO>> getKpiA2AnalyticDataResults(@PathVariable Long detailResultId) {
        log.debug("REST request to get kpi analytic data of instanceModule : {} of type a2", detailResultId);
        List<KpiA2AnalyticDataDTO> kpiA2AnalyticData = kpiA2AnalyticDataService.findByDetailResultId(detailResultId);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(kpiA2AnalyticData));
    }

    /**
     * {@code GET  /kpi-analytic-data/b1/module/{detailResultId}} : Retrieves the KpiB1AnalyticDataDTOs
     * associated with the specified "detailResultId" of instanceModule.
     *
     * @param detailResultId the identifier of the instanceModule for which the kpi analytic data of type B1 should be retrieved
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the body containing the list of KpiB1AnalyticDataDTOs,
     *         or with status {@code 404 (Not Found)} if no data is found for the provided id.
     */
    @GetMapping("kpi-analytic-data/b1/module/{detailResultId}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.KPI_B1_ANALITIC_DATA_DETAIL + "\")")
    public ResponseEntity<List<KpiB1AnalyticDataDTO>> getKpiB1AnalyticDataResults(@PathVariable Long detailResultId) {
        log.debug("REST request to get kpi analytic data of instanceModule : {} of type b1", detailResultId);
        List<KpiB1AnalyticDataDTO> kpiB1AnalyticData = kpiB1AnalyticDataService.findByDetailResultId(detailResultId);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(kpiB1AnalyticData));
    }

    /**
     * {@code GET  /kpi-analytic-data/b2/module/{detailResultId}} : Retrieves the KpiB2AnalyticDataDTOs
     * associated with the specified "detailResultId" of instanceModule.
     *
     * @param detailResultId the identifier of the instanceModule for which the kpi analytic data of type B2 should be retrieved
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the body containing the list of KpiB2AnalyticDataDTOs,
     *         or with status {@code 404 (Not Found)} if no data is found for the provided id.
     */
    @GetMapping("kpi-analytic-data/b2/module/{detailResultId}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.KPI_B2_ANALITIC_DATA_DETAIL + "\")")
    public ResponseEntity<List<KpiB2AnalyticDataDTO>> getKpiB2AnalyticDataResults(@PathVariable Long detailResultId) {
        log.debug("REST request to get kpi analytic data of instanceModule : {} of type b2", detailResultId);
        List<KpiB2AnalyticDataDTO> kpiB2AnalyticData = kpiB2AnalyticDataService.findByDetailResultId(detailResultId);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(kpiB2AnalyticData));
    }

    /**
     * {@code GET  /kpi-analytic-data/b9/module/{detailResultId}} : Retrieves the KpiB9AnalyticDataDTOs
     * associated with the specified "detailResultId" of instanceModule.
     *
     * @param detailResultId the identifier of the instanceModule for which the kpi analytic data of type B9 should be retrieved
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the body containing the list of KpiB9AnalyticDataDTOs,
     *         or with status {@code 404 (Not Found)} if no data is found for the provided id.
     */
    @GetMapping("kpi-analytic-data/b9/module/{detailResultId}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.KPI_B9_ANALITIC_DATA_DETAIL + "\")")
    public ResponseEntity<List<KpiB9AnalyticDataDTO>> getKpiB9AnalyticDataResults(@PathVariable Long detailResultId) {
        log.debug("REST request to get kpi analytic data of instanceModule : {} of type b9", detailResultId);
        List<KpiB9AnalyticDataDTO> kpiB9AnalyticData = kpiB9AnalyticDataService.findByDetailResultId(detailResultId);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(kpiB9AnalyticData));
    }

    /**
     * {@code GET  /kpi-analytic-data/b3/module/{detailResultId}} : Retrieves the KpiB3AnalyticDataDTOs
     * associated with the specified "detailResultId" of instanceModule.
     *
     * @param detailResultId the identifier of the instanceModule for which the kpi analytic data of type B3 should be retrieved
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the body containing the list of KpiB3AnalyticDataDTOs,
     *         or with status {@code 404 (Not Found)} if no data is found for the provided id.
     */
    @GetMapping("kpi-analytic-data/b3/module/{detailResultId}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.KPI_B3_ANALITIC_DATA_DETAIL + "\")")
    public ResponseEntity<List<KpiB3AnalyticDataDTO>> getKpiB3AnalyticDataResults(@PathVariable Long detailResultId) {
        log.debug("REST request to get kpi analytic data of instanceModule : {} of type b3", detailResultId);
        List<KpiB3AnalyticDataDTO> kpiB3AnalyticData = kpiB3AnalyticDataService.findByDetailResultId(detailResultId);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(kpiB3AnalyticData));
    }
}
