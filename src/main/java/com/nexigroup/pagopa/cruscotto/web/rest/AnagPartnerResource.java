package com.nexigroup.pagopa.cruscotto.web.rest;

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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.nexigroup.pagopa.cruscotto.domain.AnagPartner;
import com.nexigroup.pagopa.cruscotto.security.AuthoritiesConstants;
import com.nexigroup.pagopa.cruscotto.service.AnagPartnerService;
import com.nexigroup.pagopa.cruscotto.service.dto.AnagPartnerDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.AnagPartnerFilterDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.PartnerIdentificationDTO;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link AnagPartner}.
 */
@RestController
@RequestMapping("/api")
public class AnagPartnerResource {

    private final Logger log = LoggerFactory.getLogger(AnagPartnerResource.class);

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AnagPartnerService anagPartnerService;
    
    public AnagPartnerResource(AnagPartnerService anagPartnerService) {
        this.anagPartnerService = anagPartnerService;
    }

    /**
     * {@code GET  /partners} : get all the partners.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of partners in body.
     */
    @GetMapping("/partners")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.PARTNER_LIST + "\")")
    public ResponseEntity<List<PartnerIdentificationDTO>> getAllPartners(
        @RequestParam("name") Optional<String> nameFilter,
        @RequestParam("fiscalCode") Optional<String> fiscalCode,
        @RequestParam("showNotActive") Optional<Boolean> showNotActive,
        @Parameter(description = "Pageable", required = true) @ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Partners");
        Page<PartnerIdentificationDTO> page = anagPartnerService.findAll(fiscalCode.orElse(null), nameFilter.orElse(null), showNotActive.orElse(false), pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * Retrieves the partner for the given ID.
     *
     * @param id the unique identifier of the partner to retrieve
     * @return a {@link ResponseEntity} containing the {@link AnagPartnerDTO}, or an empty {@link ResponseEntity} if not found
     */
    @GetMapping("/partners/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.PARTNER_DETAIL + "\")")
    public ResponseEntity<AnagPartnerDTO> getPartner(@PathVariable Long id) {
        log.debug("REST request to get Partner : {}", id);
        Optional<AnagPartnerDTO> partner = anagPartnerService.findOne(id);
        return ResponseUtil.wrapOrNotFound(partner);
    }
    
    @GetMapping("/anag-partners")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.PARTNER_LIST + "\")")
    public ResponseEntity<List<AnagPartnerDTO>> getAnagPartners(
    		@Valid @ModelAttribute AnagPartnerFilterDTO filter,
        @Parameter(description = "Pageable", required = true) @ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Partners");
        //TODO validation
        Page<AnagPartnerDTO> page = anagPartnerService.findAll(filter.getPartnerId(), filter.getAnalyzed(), filter.getQualified(), filter.getLastAnalysisDate(), filter.getAnalysisPeriodStartDate(), filter.getAnalysisPeriodEndDate(), filter.getShowNotActive(), pageable);
//        Page<AnagPartnerDTO> page = anagPartnerService.findAll(partnerId.orElse(null), analyzed.orElse(null), qualified.orElse(null), lastAnalysisDate.orElse(null), analysisPeriodStartDate.orElse(null), analysisPeriodEndDate.orElse(null), showNotActive.orElse(null), pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
    
}
