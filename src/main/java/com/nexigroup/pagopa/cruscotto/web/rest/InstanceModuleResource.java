package com.nexigroup.pagopa.cruscotto.web.rest;

import com.nexigroup.pagopa.cruscotto.domain.InstanceModule;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.service.InstanceModuleService;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceModuleDTO;
import com.nexigroup.pagopa.cruscotto.web.rest.errors.BadRequestAlertException;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;

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
     * {@code GET  /instance-modules/:instanceId/:moduleId} : Get details of a specific InstanceModule.
     *
     * @param instanceId the ID of the instance to which the module belongs.
     * @param moduleId the ID of the module.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and body containing the InstanceModuleDTO,
     * or with status {@code 404 (Not Found)} if the entity does not exist.
     */
    @GetMapping("/instance-modules/{instanceId}/{moduleId}")
    public ResponseEntity<InstanceModuleDTO> getInstanceModule(@PathVariable Long instanceId, @PathVariable Long moduleId) {
        log.debug("REST request to get InstanceModule : instanceId={}, moduleId={}", instanceId, moduleId);
        Optional<InstanceModuleDTO> instanceModuleDTO = instanceModuleService.findOne(instanceId, moduleId);
        return instanceModuleDTO
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new BadRequestAlertException("InstanceModule not found", ENTITY_NAME, "notfound"));
    }

    /**
     * {@code PUT  /instance-modules/automatic-outcome/:id} : Update the automatic outcome of a specific InstanceModule.
     *
     * @param id the ID of the InstanceModule to update.
     * @param automaticOutcome the new automatic outcome to set.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)}.
     */
    @PutMapping("/instance-modules/automatic-outcome/{id}")
    public ResponseEntity<Void> updateAutomaticOutcome(
        @PathVariable Long id,
        @Parameter(description = "Automatic outcome status", required = true) @RequestParam OutcomeStatus automaticOutcome
    ) {
        log.debug("REST request to update automatic outcome of InstanceModule with id: {}", id);
        instanceModuleService.updateAutomaticOutcome(id, automaticOutcome);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code GET  /instance-modules} : Get a list of all instance modules.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} containing the list of InstanceModuleDTO.
     */
    @GetMapping("/instance-modules")
    public ResponseEntity<List<InstanceModuleDTO>> getAllInstanceModules() {
        log.debug("REST request to get all InstanceModules");
        List<InstanceModuleDTO> instanceModules = instanceModuleService.findAll();
        return ResponseEntity.ok().body(instanceModules);
    }

    /**
     * {@code DELETE  /instance-modules/:id} : Delete an InstanceModule by ID.
     *
     * @param id the ID of the InstanceModule to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (No Content)}.
     */
    @DeleteMapping("/instance-modules/{id}")
    public ResponseEntity<Void> deleteInstanceModule(@PathVariable Long id) {
        log.debug("REST request to delete InstanceModule with id: {}", id);
        instanceModuleService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
