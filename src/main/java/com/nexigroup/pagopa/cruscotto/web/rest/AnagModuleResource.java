package com.nexigroup.pagopa.cruscotto.web.rest;

import com.nexigroup.pagopa.cruscotto.service.ModuleService;
import com.nexigroup.pagopa.cruscotto.service.dto.ModuleDTO;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;

/**
 * REST controller for managing {@link Module}.
 */
@RestController
@RequestMapping("/api")
public class AnagModuleResource {

    private final Logger log = LoggerFactory.getLogger(AnagModuleResource.class);

    private static final String ENTITY_NAME = "module";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ModuleService moduleService;

    public AnagModuleResource(ModuleService moduleService) {
        this.moduleService = moduleService;
    }

    @GetMapping("/modules/no-configuration")
    //    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.GTW_LIST_FUNCTION + "\")")
    public ResponseEntity<List<ModuleDTO>> getAllModulesWithoutConfiguration(
        @Parameter(description = "Pageable", required = true) @ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Module with no configuration");
        Page<ModuleDTO> page = moduleService.findAllWithoutConfiguration(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
