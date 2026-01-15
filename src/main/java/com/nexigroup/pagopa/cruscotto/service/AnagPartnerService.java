package com.nexigroup.pagopa.cruscotto.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nexigroup.pagopa.cruscotto.domain.AnagPartner;
import com.nexigroup.pagopa.cruscotto.service.dto.AnagPartnerDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.PartnerIdentificationDTO;

/**
 * Service Interface for managing {@link AnagPartner}.
 */
public interface AnagPartnerService {
    /**
     * Retrieves a paginated list of AnagPartnerDTO objects based on the provided criteria.
     *
     * @param fiscalCode the fiscal code to filter the partners, can be null or empty to retrieve all.
     * @param nameFilter a string to filter partners by their name can be null or empty to retrieve all.
     * @param pageable an object containing pagination and sorting information.
     * @return a paginated list of AnagPartnerDTO objects matching the given criteria.
     */
    Page<PartnerIdentificationDTO> findAll(String fiscalCode, String nameFilter, Pageable pageable);
    
	Page<AnagPartnerDTO> findAll(Long partnerId, Boolean analyzed, Boolean qualified, String lastAnalysisDate,
			String analysisPeriodStartDate, String analysisPeriodEndDate, Boolean showNotActive, Pageable pageable);

    /**
     * Save all partners.
     *
     * @param partners the entities to save.
     */
    void saveAll(List<AnagPartnerDTO> partners);

    /**
     * Find a single AnagPartner entity based on its unique identifier.
     *
     * @param id the unique identifier of the AnagPartner to retrieve
     * @return an {@link Optional} containing the {@link AnagPartnerDTO} if a matching entity exists, or an empty {@link Optional} if not found
     */
    Optional<AnagPartnerDTO> findOne(Long id);

    /**
     * Updates the qualification status of a partner identified by the given ID.
     *
     * @param id the unique identifier of the partner whose qualification status is to be updated
     * @param qualified the new qualification status to set for the partner; true if the partner is qualified, false otherwise
     */
    void changePartnerQualified(Long id, boolean qualified);

    /**
     * Updates the lastAnalysisDate of a partner.
     *
     * @param partnerId the unique identifier of the partner
     * @param lastAnalysisDate the new last analysis date to set
     */
    void updateLastAnalysisDate(Long partnerId, java.time.Instant lastAnalysisDate);

    /**
     * Updates the analysis period dates of a partner.
     *
     * @param partnerId the unique identifier of the partner
     * @param startDate the new analysis period start date
     * @param endDate the new analysis period end date
     */
    void updateAnalysisPeriodDates(Long partnerId, java.time.LocalDate startDate, java.time.LocalDate endDate);

    /**
     * Updates the stations count of a partner.
     *
     * @param partnerId the unique identifier of the partner
     * @param stationsCount the new stations count to set
     */
    void updateStationsCount(Long partnerId, Long stationsCount);

    /**
     * Updates the institutions count of a partner.
     *
     * @param partnerId the unique identifier of the partner
     * @param institutionsCount the new institutions count to set
     */
    void updateInstitutionsCount(Long partnerId, Long institutionsCount);

    /**
     * Updates the institutions count for all partners based on their associated stations.
     */
    void updateAllPartnersInstitutionsCount();

    /**
     * Find a single AnagPartner entity based on its fiscal code.
     *
     * @param fiscalCode the fiscal code of the AnagPartner to retrieve
     * @return an {@link Optional} containing the {@link AnagPartnerDTO} if a matching entity exists, or an empty {@link Optional} if not found
     */
    Optional<AnagPartnerDTO> findOneByFiscalCode(String fiscalCode);

    /**
     * Loads partners from PagoPA API and saves them to the database.
     * This method fetches data, validates it, and persists valid partners.
     */
    void loadFromPagoPA();
}
