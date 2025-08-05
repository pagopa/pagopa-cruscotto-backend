package com.nexigroup.pagopa.cruscotto.web.rest;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.nexigroup.pagopa.cruscotto.service.AnagInstitutionService;
import com.nexigroup.pagopa.cruscotto.service.dto.AnagInstitutionDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.InstitutionIdentificationDTO;
import com.nexigroup.pagopa.cruscotto.service.filter.AnagInstitutionFilter;
import com.nexigroup.pagopa.cruscotto.service.filter.InstitutionFilter;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import tech.jhipster.web.util.PaginationUtil;

@RestController
@RequestMapping("/api")
public class InstitutionResource {
	
	private final Logger log = LoggerFactory.getLogger(InstitutionResource.class);
	
	@Autowired
	private AnagInstitutionService institutionService; 
	
	@GetMapping("/institutions")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.INSTITUTION_LIST + "\")")
    public ResponseEntity<List<InstitutionIdentificationDTO>> getInstitutionsLookup(
    		@Parameter(description = "Filter", required = false) @Valid @ParameterObject InstitutionFilter filter,
	        @Parameter(description = "Pageable", required = true) @ParameterObject Pageable pageable
    		) {
        
		log.debug("REST request to get Stations");
		Page<InstitutionIdentificationDTO> page = institutionService.findAll(filter, pageable);
		HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
	
	@GetMapping("/anag-institutions")
	@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.INSTITUTION_LIST + "\")")
	public ResponseEntity<List<AnagInstitutionDTO>> getAllInstitutions(
			@Parameter(description = "Filter", required = false) @Valid @ParameterObject AnagInstitutionFilter filter,
	        @Parameter(description = "Pageable", required = true) @ParameterObject Pageable pageable) {
		
		Page<AnagInstitutionDTO> page = institutionService.findAll(filter, pageable);
		HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
	}

}
