package com.nexigroup.pagopa.cruscotto.web.rest;

import com.nexigroup.pagopa.cruscotto.domain.AnagPlannedShutdown;
import com.nexigroup.pagopa.cruscotto.service.AnagPlannedShutdownService;
import com.nexigroup.pagopa.cruscotto.service.bean.ShutdownRequestBean;
import com.nexigroup.pagopa.cruscotto.service.dto.AnagPlannedShutdownDTO;
import com.nexigroup.pagopa.cruscotto.service.filter.AnagPlannedShutdownFilter;
import com.nexigroup.pagopa.cruscotto.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link AnagPlannedShutdown}.
 */
@RestController
@RequestMapping("/api")
public class AnagShutdownResource {

    private static final String ENTITY_NAME = "shutdown";

    private final Logger log = LoggerFactory.getLogger(AnagShutdownResource.class);

    @Value("${jhipster.clientApp.name}")
    private String applicationName;


    private final AnagPlannedShutdownService anagPlannedShutdownService;

    public AnagShutdownResource(AnagPlannedShutdownService anagPlannedShutdownService) {
        this.anagPlannedShutdownService = anagPlannedShutdownService;
    }

    /**
     * {@code GET  /shutdowns} : get all the shutdowns.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of shutdowns in body.
     */
    @GetMapping("/shutdowns")
//    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.GTW_LIST_FUNCTION + "\")")
    public ResponseEntity<List<AnagPlannedShutdownDTO>> getAllShutdowns(@Valid AnagPlannedShutdownFilter filter, Pageable pageable) {
        log.debug("REST request to get Shutdown");
        Page<AnagPlannedShutdownDTO> page = anagPlannedShutdownService.findAll(filter, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /shutdowns/:id} : get the "id" shutdown.
     *
     * @param id the id of the anagPlannedShutdownDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the anagPlannedShutdownDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/shutdowns/{id}")
    //@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.GTW_DETAIL_FUNCTION + "\")")
    public ResponseEntity<AnagPlannedShutdownDTO> getShutdown(@PathVariable Long id) {
        log.debug("REST request to get Shutdown : {}", id);
        Optional<AnagPlannedShutdownDTO> shutdown = anagPlannedShutdownService.findOne(id);
        return ResponseUtil.wrapOrNotFound(shutdown);
    }

    /**
     * {@code POST  /shutdowns} : Create a new shutdown.
     *
     * @param shutdownRequestBean the shutdown to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new AnagPlannedShutdownDTO,
     * or with status {@code 400 (Bad Request)} if the shutdown has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/shutdowns")
    //  @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.GTW_ADMIN_INSTANCE + "\")")
    public ResponseEntity<AnagPlannedShutdownDTO> createAShutdown(@Valid @RequestBody ShutdownRequestBean shutdownRequestBean) throws URISyntaxException {
        log.debug("REST request to save Shutdown : {}", shutdownRequestBean);

        if (shutdownRequestBean.getId() != null) {
            throw new BadRequestAlertException("A new shutdown cannot already have an ID", ENTITY_NAME, "idexists");
        }

        AnagPlannedShutdownDTO result = anagPlannedShutdownService.saveNew(shutdownRequestBean);

        return ResponseEntity.created(new URI("/api/shutdowns/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /shutdowns} : Updates an existing shutdown.
     *
     * @param shutdownRequestBean the shutdown to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated AnagPlannedShutdownDTO,
     * or with status {@code 400 (Bad Request)} if the shutdownDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the shutdownDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/shutdowns")
    //@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.GTW_ADMIN_INSTANCE + "\")")
    public ResponseEntity<AnagPlannedShutdownDTO> updateShutdown(@Valid @RequestBody ShutdownRequestBean shutdownRequestBean) throws URISyntaxException {
        log.debug("REST request to update Shutdown : {}", shutdownRequestBean);

        if (shutdownRequestBean.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }

        AnagPlannedShutdownDTO result = anagPlannedShutdownService.update(shutdownRequestBean);

        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }



    /**
     * {@code DELETE  /shutdowns/:id} : delete the "id" shutdown.
     *
     * @param id the id of the shutdownDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/shutdowns/{id}")
    //@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.GTW_DELETE_FUNCTION + "\")")
    public ResponseEntity<Void> deleteShutdown(@PathVariable Long id) {
        log.debug("REST request to delete shutdown : {}", id);
        anagPlannedShutdownService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

}
