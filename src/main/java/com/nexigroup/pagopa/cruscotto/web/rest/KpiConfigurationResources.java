package com.nexigroup.pagopa.cruscotto.web.rest;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.service.KpiConfigurationService;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiConfigurationDTO;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;
import java.util.Optional;
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
}
