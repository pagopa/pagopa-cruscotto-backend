package com.nexigroup.pagopa.cruscotto.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.nexigroup.pagopa.cruscotto.security.AuthoritiesConstants;
import com.nexigroup.pagopa.cruscotto.service.PagoPaRecordedTimeoutService;
import com.nexigroup.pagopa.cruscotto.service.dto.PagoPaRecordedTimeoutDTO;

import java.util.List;

import io.swagger.v3.oas.annotations.Parameter;
import tech.jhipster.web.util.PaginationUtil;

/**
 * REST controller for managing PagoPaRecordedTimeout entities.
 * This resource provides an API endpoint to retrieve all recorded timeout data related to PagoPa.
 */
@RestController
@RequestMapping("/api")
public class PagoPaRecordedTimeoutResource {

    private final Logger log = LoggerFactory.getLogger(PagoPaRecordedTimeoutResource.class);

    private final PagoPaRecordedTimeoutService pagoPaRecordedTimeoutService;

    public PagoPaRecordedTimeoutResource(PagoPaRecordedTimeoutService pagoPaRecordedTimeoutService) {
        this.pagoPaRecordedTimeoutService = pagoPaRecordedTimeoutService;
    }

    /**
     * Retrieves all PagoPa recorded timeout data with pagination support.
     *
     * @param pageable the pagination information including page number, size, and sorting details.
     * @return a {@link ResponseEntity} containing a list of {@link PagoPaRecordedTimeoutDTO} objects and pagination headers.
     */
    @GetMapping("/pago-pa/recorded-timeout")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.PAGOPA_RECORDED_TIMEOUT_LIST + "\")")
    public ResponseEntity<List<PagoPaRecordedTimeoutDTO>> getAllPagoPaRecordedTimeout(
        @Parameter(description = "Pageable", required = true) @ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get all pagoPA recorded timeout");
        Page<PagoPaRecordedTimeoutDTO> page = pagoPaRecordedTimeoutService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
