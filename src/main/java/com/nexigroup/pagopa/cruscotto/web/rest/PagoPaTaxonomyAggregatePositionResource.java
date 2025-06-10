package com.nexigroup.pagopa.cruscotto.web.rest;

import com.nexigroup.pagopa.cruscotto.service.PagoPaTaxonomyAggregatePositionService;
import com.nexigroup.pagopa.cruscotto.service.dto.PagoPaTaxonomyAggregatePositionDTO;
import io.swagger.v3.oas.annotations.Parameter;
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

import java.util.List;

/**
 * REST controller for managing {@link 'PagoPaRecordedTimeout'}.
 */
@RestController
@RequestMapping("/api")
public class PagoPaTaxonomyAggregatePositionResource {

    private final Logger log = LoggerFactory.getLogger(PagoPaTaxonomyAggregatePositionResource.class);

    private static final String ENTITY_NAME = "pagopa_taxonomy_aggregate_position";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PagoPaTaxonomyAggregatePositionService pagoPaTaxonomyAggregatePositionService;

    public PagoPaTaxonomyAggregatePositionResource(PagoPaTaxonomyAggregatePositionService pagoPaTaxonomyAggregatePositionService) {
        this.pagoPaTaxonomyAggregatePositionService = pagoPaTaxonomyAggregatePositionService;
    }

    @GetMapping("/pagoPaTaxonomyAggregatePositions")
    //@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.MODULE_LIST + "\")")
    public ResponseEntity<List<PagoPaTaxonomyAggregatePositionDTO>> getAllPagoPaTaxonomyAggregatePosition(
        @Parameter(description = "Pageable", required = true) @ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get all pagopa taxonomy aggregate positions");
        Page<PagoPaTaxonomyAggregatePositionDTO> page = pagoPaTaxonomyAggregatePositionService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

}
