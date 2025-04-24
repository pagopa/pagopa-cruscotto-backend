package com.nexigroup.pagopa.cruscotto.web.rest;

import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.service.InstanceService;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceDTO;
import com.nexigroup.pagopa.cruscotto.service.filter.InstanceFilter;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;
import java.util.List;

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

//    /**
//     * {@code POST  /auth-functions} : Create a new authFunction.
//     *
//     * @param authFunctionDTO the authFunctionDTO to create.
//     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new authFunctionDTO, or with status {@code 400 (Bad Request)} if the authFunction has already an ID.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PostMapping("/auth-functions")
//    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.GTW_CREATE_FUNCTION + "\")")
//    public ResponseEntity<AuthFunctionDTO> createAuthFunction(@Valid @RequestBody AuthFunctionDTO authFunctionDTO)
//        throws URISyntaxException {
//        log.debug("REST request to save AuthFunction : {}", authFunctionDTO);
//        if (authFunctionDTO.getId() != null) {
//            throw new BadRequestAlertException("A new authFunction cannot already have an ID", ENTITY_NAME, "idexists");
//        }
//        AuthFunctionDTO result = authFunctionService.save(authFunctionDTO);
//        return ResponseEntity.created(new URI("/api/auth-functions/" + result.getId()))
//            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
//            .body(result);
//    }

//    /**
//     * {@code PUT  /auth-functions} : Updates an existing authFunction.
//     *
//     * @param authFunctionDTO the authFunctionDTO to update.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated authFunctionDTO,
//     * or with status {@code 400 (Bad Request)} if the authFunctionDTO is not valid,
//     * or with status {@code 500 (Internal Server Error)} if the authFunctionDTO couldn't be updated.
//     * @throws URISyntaxException if the Location URI syntax is incorrect.
//     */
//    @PutMapping("/auth-functions")
//    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.GTW_UPDATE_FUNCTION + "\")")
//    public ResponseEntity<AuthFunctionDTO> updateAuthFunction(@Valid @RequestBody AuthFunctionDTO authFunctionDTO)
//        throws URISyntaxException {
//        log.debug("REST request to update AuthFunction : {}", authFunctionDTO);
//        if (authFunctionDTO.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        AuthFunctionDTO result = authFunctionService.update(authFunctionDTO).orElseGet(() -> null);
//        return ResponseEntity.ok()
//            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, authFunctionDTO.getId().toString()))
//            .body(result);
//    }

    /**
     * {@code GET  /instances} : get all the instances.
     *
     * @param pageable the pagination information.
     * @param filter the filter which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of instances in body.
     */
    @GetMapping("/instances")
//    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.GTW_LIST_FUNCTION + "\")")
    public ResponseEntity<List<InstanceDTO>> getAllInstances(@Valid InstanceFilter filter, Pageable pageable) {
        log.debug("REST request to get Instances by filter: {}", filter);
        Page<InstanceDTO> page = instanceService.findAll(filter, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

//    /**
//     * {@code GET  /auth-functions/:id} : get the "id" authFunction.
//     *
//     * @param id the id of the authFunctionDTO to retrieve.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the authFunctionDTO, or with status {@code 404 (Not Found)}.
//     */
//    @GetMapping("/auth-functions/{id}")
//    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.GTW_DETAIL_FUNCTION + "\")")
//    public ResponseEntity<AuthFunctionDTO> getAuthFunction(@PathVariable Long id) {
//        log.debug("REST request to get AuthFunction : {}", id);
//        Optional<AuthFunctionDTO> authFunctionDTO = authFunctionService.findOne(id);
//        return ResponseUtil.wrapOrNotFound(authFunctionDTO);
//    }
//
//    /**
//     * {@code GET  /auth-functions/detail/:id} : get the "id" authFunction.
//     *
//     * @param id the id of the authFunctionDTO to retrieve.
//     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the authFunctionDTO, or with status {@code 404 (Not Found)}.
//     */
//    @GetMapping("/auth-functions/detail/{id}")
//    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.GTW_DETAIL_FUNCTION + "\")")
//    public ResponseEntity<AuthFunctionDTO> getAuthFunctionWithPermission(@PathVariable Long id) {
//        log.debug("REST request to get AuthGroup : {}", id);
//        Optional<AuthFunctionDTO> authFunctionDTO = authFunctionService.findOneWithEagerRelationships(id);
//        return ResponseUtil.wrapOrNotFound(authFunctionDTO);
//    }
//
//    /**
//     * {@code DELETE  /auth-functions/:id} : delete the "id" authFunction.
//     *
//     * @param id the id of the authFunctionDTO to delete.
//     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
//     */
//    @DeleteMapping("/auth-functions/{id}")
//    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.GTW_DELETE_FUNCTION + "\")")
//    public ResponseEntity<Void> deleteAuthFunction(@PathVariable Long id) {
//        log.debug("REST request to delete AuthFunction : {}", id);
//        authFunctionService.delete(id);
//        return ResponseEntity.noContent()
//            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
//            .build();
//    }
//
//    @GetMapping("/auth-functions/auth-group/{idGroup}")
//    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.GTW_ELENCO_FUNZIONI_ASSOCIATI_A_GRUPPO + "\")")
//    public ResponseEntity<List<AuthFunctionDTO>> getAllAuthFunctions(@PathVariable Long idGroup, Pageable pageable) {
//        Page<AuthFunctionDTO> page = authFunctionService.listAllFunctionSelected(idGroup, pageable);
//        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
//        return ResponseEntity.ok().headers(headers).body(page.getContent());
//    }
//
//    @GetMapping("/auth-functions/auth-group/{idGroup}/associabili")
//    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.GTW_ELENCO_FUNZIONI_ASSOCIABILI_A_GRUPPO + "\")")
//    public ResponseEntity<List<AuthFunctionDTO>> getAllAuthFunctionsAssociabili(
//        @PathVariable Long idGroup,
//        @RequestParam Optional<String> nameFilter,
//        Pageable pageable
//    ) {
//        Page<AuthFunctionDTO> page = authFunctionService.listAllFunctionAssociabili(idGroup, nameFilter, pageable);
//        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
//        return ResponseEntity.ok().headers(headers).body(page.getContent());
//    }
//
//    @PostMapping("/auth-functions/{idFunction}/associa-permessi")
//    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.GTW_UPDATE_FUNCTION_ASSOCIA_PERMESSO + "\")")
//    public ResponseEntity<Void> aggiungiAssociazioneFunzione(
//        @PathVariable Long idFunction,
//        @Valid @RequestBody AuthPermissionDTO[] permessi
//    ) throws URISyntaxException {
//        log.debug("REST request to save permission da associare alla funzione");
//
//        if (permessi == null || permessi.length == 0) throw new BadRequestAlertException(
//            "Selezionare almeno un permesso da associare alla funzione",
//            "authFunction.associaPermessi.permessiDaAssociare.notEmpty"
//        );
//
//        //verifico che il permesso selezionato non sia già stato associato
//
//        List<AuthPermissionDTO> permessiDaAssociare = Arrays.asList(permessi);
//
//        List<Long> permessiAssociati = authPermissionService.listAllPermissionSelected(idFunction);
//
//        Boolean check = permessiAssociati
//            .stream()
//            .anyMatch(permessiDaAssociare.stream().map(AuthPermissionDTO::getId).collect(Collectors.toSet())::contains);
//
//        if (check) {
//            throw new BadRequestAlertException(
//                "Permesso già associata alla funzione",
//                "authFunction.associaPermessi.permessoAlreadyAssociated"
//            );
//        }
//
//        // Associa Funzioni
//        authFunctionService.associaPermesso(idFunction, permessi);
//
//        return ResponseEntity.created(new URI("api/auth-functions/" + idFunction.toString() + "/associa-permessi"))
//            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, "authFunction.message.associaPermessi", ""))
//            .body(null);
//    }
//
//    @GetMapping("/auth-functions/{idFunction}/rimuovi-permesso/{idPermesso}")
//    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.GTW_UPDATE_FUNCTION_RIMUOVI_PERMESSO + "\")")
//    public ResponseEntity<Void> rimuoviAssociazioneFunzione(@PathVariable Long idFunction, @PathVariable Long idPermesso)
//        throws URISyntaxException {
//        log.debug("Rimuovi associazione permesso (id) {} alla funzione (id) {}", idFunction, idPermesso);
//
//        // Dissocia funzione
//        authFunctionService.rimuoviAssociazionePermesso(idFunction, idPermesso);
//
//        return ResponseEntity.created(new URI("api/auth-functions/" + idFunction.toString() + "/rimuovi-permessi/" + idPermesso.toString()))
//            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, "authFunction.message.dissociaPermesso", ""))
//            .body(null);
//    }
}
