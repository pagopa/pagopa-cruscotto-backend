package com.nexigroup.pagopa.cruscotto.web.rest;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nexigroup.pagopa.cruscotto.domain.AuthUser;
import com.nexigroup.pagopa.cruscotto.domain.InstanceModule;
import com.nexigroup.pagopa.cruscotto.security.AuthoritiesConstants;
import com.nexigroup.pagopa.cruscotto.service.CalculateStateInstanceService;
import com.nexigroup.pagopa.cruscotto.service.InstanceModuleService;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceModuleDTO;
import com.nexigroup.pagopa.cruscotto.service.util.UserUtils;

/**
 * REST controller for managing {@link InstanceModule}.
 */
@RestController
@RequestMapping("/api")
public class InstanceModuleResource {

    private final Logger log = LoggerFactory.getLogger(InstanceModuleResource.class);


    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final InstanceModuleService instanceModuleService;
    private final UserUtils userUtils;
    private final CalculateStateInstanceService calculateStateInstanceService;
    public InstanceModuleResource(InstanceModuleService instanceModuleService, UserUtils userUtils, CalculateStateInstanceService calculateStateInstanceService) {
        this.instanceModuleService = instanceModuleService;
        this.userUtils = userUtils;
        this.calculateStateInstanceService = calculateStateInstanceService;
    }

    /**
     * {@code GET  /instance-modules/:id} : Ottieni i dettagli di un InstanceModule
     * inclusi i dati dell'utente associato.
     *
     * @param id l'ID dell'oggetto InstanceModule da trovare.
     * @return il {@link ResponseEntity} con status {@code 200 (OK)} e corpo i dettagli di InstanceModule.
     */
    @GetMapping("/instance-modules/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.INSTANCE_MODULE_DETAIL + "\")")
    public ResponseEntity<InstanceModuleDTO> getInstanceModuleById(@PathVariable Long id) {
        log.debug("REST request per ottenere InstanceModule con id : {}", id);
        Optional<InstanceModuleDTO> instanceModuleDTO = instanceModuleService.findInstanceModuleDTOById(id);
        return instanceModuleDTO.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    /*
    @PatchMapping ("/instance-modules/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.INSTANCE_MODULE_DETAIL + "\")") //TODO USE INSTANCE_MODULE_MODIFICATION
    public ResponseEntity<Void> updateInstanceModule(@PathVariable Long id, @RequestBody InstanceModuleUpdateStatusDTO body) {
    	instanceModuleService.updateStatusAndAllowManualOutcome(id, body.getStatus(), body.getAllowManualOutcome());
    	return ResponseEntity.ok().build();
    	
    }*/
    
    /**
     * {@code PATCH /instance-modules/:id} : Updates an existing InstanceModule.
     *
     * @param id the ID of the InstanceModule to update
     * @param body the InstanceModuleDTO containing the fields to update
     * @return the {@link ResponseEntity} with status {@code 200 (OK)}
     */
    @PatchMapping ("/instance-modules")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.INSTANCE_MODULE_MODIFICATION + "\")")
    public ResponseEntity<InstanceModuleDTO> updateInstanceModule(@RequestBody InstanceModuleDTO body) {
    log.debug("REST request to update InstanceModule with id: {}", body.getId());

    // Get current authenticated user information
    AuthUser currentUser = userUtils.getLoggedUser();
    log.debug("User '{}' (ID: {}, Name: {} {}) is updating InstanceModule with id: {}", 
        currentUser.getLogin(), 
        currentUser.getId(),
        currentUser.getFirstName(), 
        currentUser.getLastName(), 
        body.getId());

    InstanceModuleDTO result = calculateStateInstanceService.updateModuleAndInstanceState(body, currentUser);

    log.info("InstanceModule with id: {} successfully updated by user: {}", body.getId(), currentUser.getLogin());
    return ResponseEntity.ok(result);
    }
}
