package com.nexigroup.pagopa.cruscotto.web.rest;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.nexigroup.pagopa.cruscotto.security.AuthoritiesConstants;
import com.nexigroup.pagopa.cruscotto.service.PagoPaTaxonomyAggregatePositionService;
import com.nexigroup.pagopa.cruscotto.service.dto.PagoPaTaxonomyAggregatePositionDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.PagoPaTaxonomyIncorrectDTO;
import com.nexigroup.pagopa.cruscotto.service.filter.PagoPaTaxonomyAggregatePositionFilter;

import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
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
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.PAGOPA_TAXONOMY_AGGREGATE_POSITION_LIST + "\")")
    public ResponseEntity<List<PagoPaTaxonomyAggregatePositionDTO>> getAllPagoPaTaxonomyAggregatePosition(
        @Parameter(description = "Filtro") @Valid @ParameterObject PagoPaTaxonomyAggregatePositionFilter filter,
        @Parameter(description = "Pageable", required = true) @ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get all pagopa taxonomy aggregate positions by filter {}", filter);
        Page<PagoPaTaxonomyAggregatePositionDTO> page = pagoPaTaxonomyAggregatePositionService.findAll(filter, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * Retrieves aggregated records with incorrect taxonomic codes for a specific partner and day.
     * 
     * Data is aggregated by transfer_category directly in the database with the following logic:
     * - fromHour: MIN(start_date) for each transfer_category
     * - endHour: MAX(end_date) for each transfer_category
     * - total: SUM(total) for each transfer_category
     *
     * @param cfPartner the fiscal code of the partner.
     * @param day the specific day to query (format: yyyy-MM-dd).
     *
     * @return a {@link ResponseEntity} containing a list of {@link PagoPaTaxonomyIncorrectDTO}
     *         with only aggregated records that have incorrect taxonomic codes.
     */
    @GetMapping("/pago-pa/taxonomy-aggregate-position/drilldown")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.PAGOPA_TAXONOMY_AGGREGATE_POSITION_LIST + "\")")
    public ResponseEntity<List<PagoPaTaxonomyIncorrectDTO>> getIncorrectTaxonomyRecords(
        @Parameter(description = "Codice fiscale del partner", required = true) @RequestParam String cfPartner,
        @Parameter(description = "Giornata di riferimento (formato: yyyy-MM-dd)", required = true) @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate day
    ) {
        log.debug("REST request to get incorrect taxonomy records for partner {} and day {}", cfPartner, day);
        List<PagoPaTaxonomyIncorrectDTO> incorrectRecords = pagoPaTaxonomyAggregatePositionService.findIncorrectTaxonomyRecordsForPartnerAndDay(cfPartner, day);
        return ResponseEntity.ok().body(incorrectRecords);
    }
}
