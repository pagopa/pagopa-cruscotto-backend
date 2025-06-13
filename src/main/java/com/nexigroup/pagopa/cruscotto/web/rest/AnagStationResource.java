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

import com.nexigroup.pagopa.cruscotto.domain.AnagStation;
import com.nexigroup.pagopa.cruscotto.security.AuthoritiesConstants;
import com.nexigroup.pagopa.cruscotto.service.AnagStationService;
import com.nexigroup.pagopa.cruscotto.service.dto.AnagStationDTO;
import com.nexigroup.pagopa.cruscotto.service.filter.StationFilter;

import java.util.List;
import java.util.Optional;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link AnagStation}.
 */
@RestController
@RequestMapping("/api")
public class AnagStationResource {

    private final Logger log = LoggerFactory.getLogger(AnagStationResource.class);

    private static final String ENTITY_NAME = "station";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AnagStationService anagStationService;

    public AnagStationResource(AnagStationService anagStationService) {
        this.anagStationService = anagStationService;
    }

    /**
     * {@code GET  /stations} : get all the stations.
     *
     * @param filter the pagination filter.
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of stations in body.
     */
    @GetMapping("/stations")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.STATION_LIST + "\")")
    public ResponseEntity<List<AnagStationDTO>> getAllStations(
        @Parameter(description = "Filter", required = false) @Valid @ParameterObject StationFilter filter,
        @Parameter(description = "Pageable", required = true) @ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Stations");
        Page<AnagStationDTO> page = anagStationService.findAll(filter, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * Retrieves the station for the given ID.
     *
     * @param id the unique identifier of the station to retrieve
     * @return a {@link ResponseEntity} containing the {@link AnagStationDTO}, or an empty {@link ResponseEntity} if not found
     */
    @GetMapping("/stations/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.STATION_DETAIL + "\")")
    public ResponseEntity<AnagStationDTO> getStation(@PathVariable Long id) {
        log.debug("REST request to get station : {}", id);
        Optional<AnagStationDTO> anagStation = anagStationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(anagStation);
    }
}
