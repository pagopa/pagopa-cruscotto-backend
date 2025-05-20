package com.nexigroup.pagopa.cruscotto.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.service.InstanceService;
import com.nexigroup.pagopa.cruscotto.service.bean.InstanceRequestBean;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceDTO;
import com.nexigroup.pagopa.cruscotto.service.filter.InstanceFilter;
import com.nexigroup.pagopa.cruscotto.web.rest.errors.BadRequestAlertException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;
 
/**
 * REST controller for managing {@link Instance}.
 */
@RestController
@RequestMapping("/api")
public class InstanceResource {

    private final Logger log = LoggerFactory.getLogger(InstanceResource.class);

    private static final String ENTITY_NAME = "instance";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final InstanceService instanceService;
    

    public InstanceResource(InstanceService instanceService) {
        this.instanceService = instanceService;
    }

    /**
     * {@code POST  /instances} : Create a new instances.
     *
     * @param instanceDTO the instanceDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new instanceDTO, or with status {@code 400 (Bad Request)} if the instance has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/instances")
  //  @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.GTW_ADMIN_INSTANCE + "\")")
    public ResponseEntity<InstanceDTO> createAInstance(@Valid @RequestBody InstanceRequestBean instance) throws URISyntaxException {
        log.debug("REST request to save Instance : {}", instance);
        
        if (instance.getId() != null) {
            throw new BadRequestAlertException("A new instance cannot already have an ID", ENTITY_NAME, "idexists");
        }
        
        InstanceDTO result = instanceService.saveNew(instance);
        
        return ResponseEntity.created(new URI("/api/instances/" + result.getId()))
        					 .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getInstanceIdentification()))
        					 .body(result);
    }

    /**
     * {@code PUT  /instances} : Updates an existing instance.
     *
     * @param instanceDTO the instanceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated instanceDTO,
     * or with status {@code 400 (Bad Request)} if the instanceDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the instanceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/instances")
    //@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.GTW_ADMIN_INSTANCE + "\")")
    public ResponseEntity<InstanceDTO> updateInstance(@Valid @RequestBody InstanceRequestBean instance) throws URISyntaxException {
        log.debug("REST request to update Instance : {}", instance);
        
        if (instance.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        
        InstanceDTO result = instanceService.update(instance);
        
        return ResponseEntity.ok()
        					 .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getInstanceIdentification()))
        					 .body(result);
    }

    /**
     * {@code GET  /instances} : get all the instances.
     *
     * @param pageable the pagination information.
     * @param filter the filter which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of instances in body.
     */
    @GetMapping("/instances")
//    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.GTW_LIST_FUNCTION + "\")")
    public ResponseEntity<List<InstanceDTO>> getAllInstances(
        @Parameter(description = "Filtro", required = false) @Valid @ParameterObject InstanceFilter filter,
        @Parameter(description = "Pageable", required = true) @ParameterObject Pageable pageable) {
        log.debug("REST request to get Instances by filter: {}", filter);
        Page<InstanceDTO> page = instanceService.findAll(filter, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /instances/:id} : get the "id" instance.
     *
     * @param id the id of the instanceDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the instanceDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/instances/{id}")
    //@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.GTW_DETAIL_FUNCTION + "\")")
    public ResponseEntity<InstanceDTO> getInstance(@PathVariable Long id) {
        log.debug("REST request to get Instance : {}", id);
        Optional<InstanceDTO> instance = instanceService.findOne(id);
        return ResponseUtil.wrapOrNotFound(instance);
    }

    /**
     * {@code DELETE  /instances/:id} : delete the "id" instance.
     *
     * @param id the id of the instanceDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/instances/{id}")
    //@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.GTW_DELETE_FUNCTION + "\")")
    public ResponseEntity<Void> deleteInstance(@PathVariable Long id) {
        log.debug("REST request to delete Instance : {}", id);
        InstanceDTO result = instanceService.delete(id);
        return ResponseEntity.noContent()
        					 .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, result.getInstanceIdentification()))
        					 .build();
    }
    
    @PutMapping("/instances/update-status/{id}")
    //@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.GTW_ADMIN_INSTANCE + "\")")
    public ResponseEntity<Void> updateInstanceStatus(@PathVariable Long id) {
        log.debug("REST request to update status od instance {}: ", id);
        
        InstanceDTO result = instanceService.updateStatus(id);
        
        return ResponseEntity.ok()
        					 .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getInstanceIdentification()))
        					 .build();
    }    
}
