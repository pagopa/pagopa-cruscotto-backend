package com.nexigroup.pagopa.cruscotto.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nexigroup.pagopa.cruscotto.domain.InstanceModule;
import com.nexigroup.pagopa.cruscotto.security.AuthoritiesConstants;
import com.nexigroup.pagopa.cruscotto.service.InstanceModuleService;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceModuleDTO;

import java.util.Optional;

/**
 * REST controller for managing {@link InstanceModule}.
 */
@RestController
@RequestMapping("/api")
public class InstanceModuleResource {

    private final Logger log = LoggerFactory.getLogger(InstanceModuleResource.class);

    private static final String ENTITY_NAME = "instanceModule";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final InstanceModuleService instanceModuleService;
    

    public InstanceModuleResource(InstanceModuleService instanceModuleService) {
        this.instanceModuleService = instanceModuleService;
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
}
