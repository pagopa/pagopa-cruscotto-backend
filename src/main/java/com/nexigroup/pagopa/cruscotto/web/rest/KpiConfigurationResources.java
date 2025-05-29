package com.nexigroup.pagopa.cruscotto.web.rest;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.service.KpiConfigurationService;
import com.nexigroup.pagopa.cruscotto.service.bean.KpiConfigurationRequestBean;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiConfigurationDTO;
import com.nexigroup.pagopa.cruscotto.web.rest.errors.BadRequestAlertException;
import io.swagger.v3.oas.annotations.Parameter;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 *  REST controller for managing {@link 'KpiConfiguration'}.
 */
@RestController
@RequestMapping("/api")
public class KpiConfigurationResources {

    private static final Logger log = LoggerFactory.getLogger(KpiConfigurationResources.class);

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private static final String ENTITY_NAME = "kpiConfiguration";

    private final KpiConfigurationService kpiConfigurationService;

    public KpiConfigurationResources(KpiConfigurationService kpiConfigurationService) {
        this.kpiConfigurationService = kpiConfigurationService;
    }

    /**
     * {@code GET  /kpi-configurations} : Retrieves all KPI configurations with pagination support.
     *
     * @param pageable the pagination information including page number, size, and sorting options
     * @return a ResponseEntity containing a list of KpiConfigurationDTO objects and HTTP headers for pagination
     */
    @GetMapping("/kpi-configurations")
    public ResponseEntity<List<KpiConfigurationDTO>> getAllKpiConfigurations(
        @Parameter(description = "Pageable", required = true) @ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get KPI Configurations");
        Page<KpiConfigurationDTO> page = kpiConfigurationService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * Retrieves the KPI configuration associated with the specified module code.
     *
     * @param moduleCode the module code used to identify the KPI configuration
     * @return a ResponseEntity containing the KpiConfigurationDTO if found, or a 404 status if no configuration exists for the specified module code
     */
    @GetMapping("/kpi-configurations/{moduleCode}")
    public ResponseEntity<KpiConfigurationDTO> getKpiConfiguration(
        @Parameter(description = "Codice del modulo") @PathVariable String moduleCode
    ) {
        log.debug("REST request to get KPI Configuration : {}", moduleCode);
        Optional<KpiConfigurationDTO> kpiConfigurationDTO = kpiConfigurationService.findKpiConfigurationByCode(
            ModuleCode.fromCode(moduleCode)
        );
        return ResponseUtil.wrapOrNotFound(kpiConfigurationDTO);
    }

    /**
     * {@code POST  /kpi-configurations} : Create a new kpi configurations.
     *
     * @param 'KpiConfigurationDTO' the KpiConfigurationDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new KpiConfigurationDTO, or with status {@code 400 (Bad Request)} if the kpi configuration has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/kpi-configurations")
    //  @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.GTW_ADMIN_INSTANCE + "\")")
    public ResponseEntity<KpiConfigurationDTO> createAKpiConfiguration(@Valid @RequestBody KpiConfigurationRequestBean kpiConfigurationToCreate) throws URISyntaxException {
        log.debug("REST request to save kpi configuration : {}", kpiConfigurationToCreate);

        if (kpiConfigurationToCreate.getId() != null) {
            throw new BadRequestAlertException("A new kpi configuration cannot already have an ID", ENTITY_NAME, "idexists");
        }

        KpiConfigurationDTO result = kpiConfigurationService.saveNew(kpiConfigurationToCreate);

        return ResponseEntity.created(new URI("/api/kpi-configurations/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /kpi-configurations} : Updates an existing /kpi configurations.
     *
     * @param kpiConfigurationToUpdate represents kpiConfigurationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated kpiConfigurationDTO,
     * or with status {@code 400 (Bad Request)} if the kpiConfigurationDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the kpiConfigurationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/kpi-configurations")
    //@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.GTW_ADMIN_INSTANCE + "\")")
    public ResponseEntity<KpiConfigurationDTO> updateKpiConfiguration(@Valid @RequestBody KpiConfigurationRequestBean kpiConfigurationToUpdate) {
        log.debug("REST request to update kpi configuration : {}", kpiConfigurationToUpdate);
        if (kpiConfigurationToUpdate.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        KpiConfigurationDTO result = kpiConfigurationService.update(kpiConfigurationToUpdate);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }


    /**
     * {@code DELETE  /kpi-configuration/:id} : delete the "id" kpi configuration.
     *
     * @param id the id of the KpiConfigurationDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/kpi-configurations/{id}")
    //@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.GTW_DELETE_FUNCTION + "\")")
    public ResponseEntity<Void> deleteKpiConfiguration(@PathVariable Long id) {
        log.debug("REST request to delete kpi configuration : {}", id);
        kpiConfigurationService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
