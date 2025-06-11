package com.nexigroup.pagopa.cruscotto.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.nexigroup.pagopa.cruscotto.security.AuthoritiesConstants;
import com.nexigroup.pagopa.cruscotto.service.TaxonomyService;
import com.nexigroup.pagopa.cruscotto.service.dto.TaxonomyDTO;
import com.nexigroup.pagopa.cruscotto.service.filter.TaxonomyFilter;

import java.util.List;
import java.util.Optional;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link 'Taxonomy'}.
 */
@RestController
@RequestMapping("/api")
public class TaxonomyResource {

    private final Logger log = LoggerFactory.getLogger(TaxonomyResource.class);

    private static final String ENTITY_NAME = "taxonomy";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TaxonomyService taxonomyService;
    

    public TaxonomyResource(TaxonomyService taxonomyService) {
        this.taxonomyService = taxonomyService;
    }

    /**
     * Retrieves a list of taxonomy objects based on the provided filter and pagination details.
     *
     * @param filter   an optional filter object containing criteria for querying taxonomy records
     * @param pageable the pagination details specifying page size, page number, and sorting options
     * @return a ResponseEntity containing a list of TaxonomyDTO objects and pagination headers
     */
    @GetMapping("/taxonomies")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.TAXONOMY_LIST + "\")")
    public ResponseEntity<List<TaxonomyDTO>> getAllTaxonomy(
        @Parameter(description = "Filtro", required = false) @Valid @ParameterObject TaxonomyFilter filter,
        @Parameter(description = "Pageable", required = true) @ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Taxonomy by filter: {}", filter);
        Page<TaxonomyDTO> page = taxonomyService.findAll(filter, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * Retrieves the taxonomy data for the given ID.
     *
     * @param id the unique identifier of the taxonomy to retrieve
     * @return a {@link ResponseEntity} containing the {@link TaxonomyDTO}, or an empty {@link ResponseEntity} if not found
     */
    @GetMapping("/taxonomies/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.TAXONOMY_DETAIL + "\")")
    public ResponseEntity<TaxonomyDTO> getTaxonomy(@PathVariable Long id) {
        log.debug("REST request to get Taxonomy : {}", id);
        Optional<TaxonomyDTO> shutdown = taxonomyService.findOne(id);
        return ResponseUtil.wrapOrNotFound(shutdown);
    }
}
