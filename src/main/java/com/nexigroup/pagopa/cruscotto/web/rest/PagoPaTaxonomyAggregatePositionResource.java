package com.nexigroup.pagopa.cruscotto.web.rest;

import com.nexigroup.pagopa.cruscotto.service.PagoPaTaxonomyAggregatePositionService;
import com.nexigroup.pagopa.cruscotto.service.dto.PagoPaTaxonomyAggregatePositionDTO;
import com.nexigroup.pagopa.cruscotto.service.filter.PagoPaTaxonomyAggregatePositionFilter;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;

import jakarta.validation.Valid;
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
 * REST controller for managing PagoPa Taxonomy Aggregate Positions.
 *
 * This controller handles requests for retrieving a paginated list of PagoPa taxonomy aggregate positions.
 * It communicates with the {@link PagoPaTaxonomyAggregatePositionService} to fetch the required data.
 *
 * The resource endpoint is exposed under the base path `/api`.
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

    /**
     * Retrieves a paginated list of all PagoPa taxonomy aggregate positions  by filter and with pagination support.
     *
     * @param pageable pagination information, which includes page size and page number.
     * @param filter the filter the requested entities should match.
     *
     * @return a {@link ResponseEntity} containing a list of {@link PagoPaTaxonomyAggregatePositionDTO}
     *         and HTTP headers for pagination.
     */
    @GetMapping("/pago-pa/taxonomy-aggregate-position")
    public ResponseEntity<List<PagoPaTaxonomyAggregatePositionDTO>> getAllPagoPaTaxonomyAggregatePosition(
        @Parameter(description = "Filtro") @Valid @ParameterObject PagoPaTaxonomyAggregatePositionFilter filter,
        @Parameter(description = "Pageable", required = true) @ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get all pagopa taxonomy aggregate positions by filter {}", filter);
        Page<PagoPaTaxonomyAggregatePositionDTO> page = pagoPaTaxonomyAggregatePositionService.findAll(filter, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
