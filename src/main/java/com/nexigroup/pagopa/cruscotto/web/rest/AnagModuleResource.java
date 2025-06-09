package com.nexigroup.pagopa.cruscotto.web.rest;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.AnalysisType;
import com.nexigroup.pagopa.cruscotto.security.SecurityUtils;
import com.nexigroup.pagopa.cruscotto.service.ModuleService;
import com.nexigroup.pagopa.cruscotto.service.bean.ModuleRequestBean;
import com.nexigroup.pagopa.cruscotto.service.dto.ModuleDTO;
import com.nexigroup.pagopa.cruscotto.web.rest.errors.BadRequestAlertException;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
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
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

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

    @GetMapping("/modules")
    //    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.GTW_LIST_FUNCTION + "\")")
    public ResponseEntity<List<ModuleDTO>> getAllModules(
        @Parameter(description = "Pageable", required = true) @ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get all modules");
        Page<ModuleDTO> page = moduleService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /modules/:id} : get the "id" module.
     *
     * @param id the id of the moduleDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the moduleDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/modules/{id}")
    //@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.GTW_DETAIL_FUNCTION + "\")")
    public ResponseEntity<ModuleDTO> getModule(@PathVariable Long id) {
        log.debug("REST request to get module : {}", id);
        Optional<ModuleDTO> module = moduleService.findOne(id);
        return ResponseUtil.wrapOrNotFound(module);
    }

    /**
     * {@code POST  /modules} : Create a new modules.
     *
     * @param 'moduleDTO' the moduleDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new moduleDTO, or with status {@code 400 (Bad Request)} if the module has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/modules")
    //  @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.GTW_ADMIN_module + "\")")
    public ResponseEntity<ModuleDTO> createAModule(@Valid @RequestBody ModuleRequestBean module) throws URISyntaxException {
        log.debug("REST request to save module : {}", module);

        if (module.getId() != null) {
            throw new BadRequestAlertException("A new module cannot already have an ID", ENTITY_NAME, "idexists");
        }

        // force to Manual Type
        module.setAnalysisType(AnalysisType.MANUALE);

        ModuleDTO result = moduleService.saveNew(module);

        return ResponseEntity.created(new URI("/api/modules/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /modules} : Updates an existing module.
     *
     * @param 'moduleToUpdate' the module to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated moduleDTO,
     * or with status {@code 400 (Bad Request)} if the moduleDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the moduleDTO couldn't be updated.
     **/
    @PutMapping("/modules")
    //@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.GTW_ADMIN_module + "\")")
    public ResponseEntity<ModuleDTO> updateModule(@Valid @RequestBody ModuleRequestBean moduleToUpdate) {
        log.debug("REST request to update module : {}", moduleToUpdate);

        if (moduleToUpdate.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }

        // force to Manual Type
        moduleToUpdate.setAnalysisType(AnalysisType.MANUALE);

        ModuleDTO result = moduleService.update(moduleToUpdate);

        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code DELETE  /modules/:id} : delete the "id" module.
     *
     * @param id the id of the moduleDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/modules/{id}")
    //@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.GTW_DELETE_FUNCTION + "\")")
    public ResponseEntity<Void> deleteModule(@PathVariable Long id) {
        log.info("REST request to delete module: {}", id);

        SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new RuntimeException("Current user login not found"));

        if (!moduleService.deleteModule(id)) {
            throw new BadRequestAlertException("Stato modulo incompatibile con l'operazione richiesta", ENTITY_NAME, "module.notDeletable");
        }
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
