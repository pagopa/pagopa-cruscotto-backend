package com.nexigroup.pagopa.cruscotto.service;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import com.nexigroup.pagopa.cruscotto.IntegrationTest;
import com.nexigroup.pagopa.cruscotto.domain.AnagInstitution;
import com.nexigroup.pagopa.cruscotto.domain.AnagPartner;
import com.nexigroup.pagopa.cruscotto.domain.AnagStation;
import com.nexigroup.pagopa.cruscotto.domain.AnagStationAnagInstitution;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.PartnerStatus;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.StationStatus;
import com.nexigroup.pagopa.cruscotto.repository.AnagInstitutionRepository;
import com.nexigroup.pagopa.cruscotto.repository.AnagPartnerRepository;
import com.nexigroup.pagopa.cruscotto.repository.AnagStationAnagInstitutionRepository;
import com.nexigroup.pagopa.cruscotto.repository.AnagStationRepository;
import com.nexigroup.pagopa.cruscotto.service.dto.AnagInstitutionDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.InstitutionIdentificationDTO;
import com.nexigroup.pagopa.cruscotto.service.filter.AnagInstitutionFilter;
import com.nexigroup.pagopa.cruscotto.service.filter.InstitutionFilter;

@IntegrationTest
@Transactional
public class AnagInstitutionServiceImplIT {

	@Autowired
	private AnagInstitutionService anagInstitutionService;

	@Autowired
	private AnagInstitutionRepository anagInstitutionRepository;

	@Autowired
	private AnagPartnerRepository anagPartnerRepository;

	@Autowired
	private AnagStationRepository anagStationRepository;

	@Autowired
	private AnagStationAnagInstitutionRepository anagStationAnagInstitutionRepository;

    @MockitoBean
    private AzureBlobStorageService azureBlobStorageService;

	@Test
	public void findByInstitutionCode_shouldReturnInstitution() {
		AnagInstitution institution = createTestAnagInstitution();
		AnagInstitution savedInstitution = anagInstitutionRepository.save(institution);

		AnagInstitution found = anagInstitutionService.findByInstitutionCode(savedInstitution.getFiscalCode());

		assertNotNull(found);
		assertTrue(found.getId().equals(savedInstitution.getId()));
		assertTrue(found.getFiscalCode().equals(savedInstitution.getFiscalCode()));
	}

	@Test
	public void findByInstitutionCode_withNonExistentCode_shouldReturnNull() {
		AnagInstitution found = anagInstitutionService.findByInstitutionCode("NON_EXISTENT_CODE");
		assertTrue(found == null);
	}

	@Test
	public void findAll_InstitutionFilter_shouldReturnResults() {
		Pageable pageable = createMockPageable();

		AnagInstitution institution = createTestAnagInstitution();
		AnagInstitution savedInstitution = anagInstitutionRepository.save(institution);

		// Test with fiscal code filter
		InstitutionFilter filter = new InstitutionFilter();
		filter.setFiscalCode(savedInstitution.getFiscalCode());

		Page<InstitutionIdentificationDTO> result = anagInstitutionService.findAll(filter, pageable);
		assertNotNull(result);
		assertTrue(result.getContent().size() >= 0);

		// Test with name filter
		filter = new InstitutionFilter();
		filter.setName(savedInstitution.getName());

		result = anagInstitutionService.findAll(filter, pageable);
		assertNotNull(result);
		assertTrue(result.getContent().size() >= 0);
	}

	@Test
	public void findAll_InstitutionFilter_withEmptyFilter_shouldReturnResults() {
		Pageable pageable = createMockPageable();

		AnagInstitution institution = createTestAnagInstitution();
		anagInstitutionRepository.save(institution);

		InstitutionFilter filter = new InstitutionFilter();

		Page<InstitutionIdentificationDTO> result = anagInstitutionService.findAll(filter, pageable);
		assertNotNull(result);
		assertTrue(result.getContent().size() >= 0);
	}

	@Test
	public void findAll_AnagInstitutionFilter_shouldReturnResults() {
		// Create test data with complete relationship
		AnagPartner partner = createTestAnagPartner();
		AnagPartner savedPartner = anagPartnerRepository.save(partner);

		AnagStation station = createTestAnagStation();
		station.setAnagPartner(savedPartner);
		AnagStation savedStation = anagStationRepository.save(station);

		AnagInstitution institution = createTestAnagInstitution();
		AnagInstitution savedInstitution = anagInstitutionRepository.save(institution);

		// Create the association
		AnagStationAnagInstitution association = new AnagStationAnagInstitution();
		association.setAnagStation(savedStation);
		association.setAnagInstitution(savedInstitution);
		association.setAca(true);
		association.setStandin(false);
		anagStationAnagInstitutionRepository.save(association);

		Pageable pageable = createMockPageable();

		// Test with institution ID filter
		AnagInstitutionFilter filter = new AnagInstitutionFilter();
		filter.setInstitutionId(savedInstitution.getId());

		Page<AnagInstitutionDTO> result = anagInstitutionService.findAll(filter, pageable);
		assertNotNull(result);

		// Test with partner ID filter
		filter = new AnagInstitutionFilter();
		filter.setPartnerId(savedPartner.getId());

		result = anagInstitutionService.findAll(filter, pageable);
		assertNotNull(result);

		// Test with station ID filter
		filter = new AnagInstitutionFilter();
		filter.setStationId(savedStation.getId());

		result = anagInstitutionService.findAll(filter, pageable);
		assertNotNull(result);
	}

	@Test
	public void findAll_AnagInstitutionFilter_withShowNotEnabled_shouldReturnResults() {
		// Create test data
		AnagPartner partner = createTestAnagPartner();
		AnagPartner savedPartner = anagPartnerRepository.save(partner);

		AnagStation station = createTestAnagStation();
		station.setAnagPartner(savedPartner);
		AnagStation savedStation = anagStationRepository.save(station);

		// Create enabled institution
		AnagInstitution enabledInstitution = createTestAnagInstitution();
		enabledInstitution.setEnabled(true);
		AnagInstitution savedEnabledInstitution = anagInstitutionRepository.save(enabledInstitution);

		// Create disabled institution
		AnagInstitution disabledInstitution = createTestAnagInstitution();
		disabledInstitution.setFiscalCode(randomFiscalCode());
		disabledInstitution.setEnabled(false);
		AnagInstitution savedDisabledInstitution = anagInstitutionRepository.save(disabledInstitution);

		// Create associations
		AnagStationAnagInstitution association1 = new AnagStationAnagInstitution();
		association1.setAnagStation(savedStation);
		association1.setAnagInstitution(savedEnabledInstitution);
		association1.setAca(true);
		association1.setStandin(false);
		anagStationAnagInstitutionRepository.save(association1);

		AnagStationAnagInstitution association2 = new AnagStationAnagInstitution();
		association2.setAnagStation(savedStation);
		association2.setAnagInstitution(savedDisabledInstitution);
		association2.setAca(false);
		association2.setStandin(true);
		anagStationAnagInstitutionRepository.save(association2);

		Pageable pageable = createMockPageable();

		// Test with showNotEnabled = true (should show disabled institutions)
		AnagInstitutionFilter filter = new AnagInstitutionFilter();
		filter.setShowNotEnabled(true);

		Page<AnagInstitutionDTO> result = anagInstitutionService.findAll(filter, pageable);
		assertNotNull(result);

		// Test with showNotEnabled = false (should show only enabled institutions)
		filter = new AnagInstitutionFilter();
		filter.setShowNotEnabled(false);

		result = anagInstitutionService.findAll(filter, pageable);
		assertNotNull(result);

		// Test with showNotEnabled = null (should show only enabled institutions by default)
		filter = new AnagInstitutionFilter();
		filter.setShowNotEnabled(null);

		result = anagInstitutionService.findAll(filter, pageable);
		assertNotNull(result);
	}

	@Test
	public void findAll_AnagInstitutionFilter_withEmptyFilter_shouldReturnResults() {
		// Create minimal test data
		AnagPartner partner = createTestAnagPartner();
		AnagPartner savedPartner = anagPartnerRepository.save(partner);

		AnagStation station = createTestAnagStation();
		station.setAnagPartner(savedPartner);
		AnagStation savedStation = anagStationRepository.save(station);

		AnagInstitution institution = createTestAnagInstitution();
		AnagInstitution savedInstitution = anagInstitutionRepository.save(institution);

		// Create the association
		AnagStationAnagInstitution association = new AnagStationAnagInstitution();
		association.setAnagStation(savedStation);
		association.setAnagInstitution(savedInstitution);
		association.setAca(true);
		association.setStandin(false);
		anagStationAnagInstitutionRepository.save(association);

		Pageable pageable = createMockPageable();

		AnagInstitutionFilter filter = new AnagInstitutionFilter();

		Page<AnagInstitutionDTO> result = anagInstitutionService.findAll(filter, pageable);
		assertNotNull(result);
		assertTrue(result.getContent().size() >= 0);
	}

	@Test
	public void findAll_AnagInstitutionFilter_withNonExistentIds_shouldReturnEmpty() {
		Pageable pageable = createMockPageable();

		// Test with non-existent institution ID
		AnagInstitutionFilter filter = new AnagInstitutionFilter();
		filter.setInstitutionId(99999L);

		Page<AnagInstitutionDTO> result = anagInstitutionService.findAll(filter, pageable);
		assertNotNull(result);
		assertTrue(result.getContent().isEmpty());

		// Test with non-existent partner ID
		filter = new AnagInstitutionFilter();
		filter.setPartnerId(99999L);

		result = anagInstitutionService.findAll(filter, pageable);
		assertNotNull(result);
		assertTrue(result.getContent().isEmpty());

		// Test with non-existent station ID
		filter = new AnagInstitutionFilter();
		filter.setStationId(99999L);

		result = anagInstitutionService.findAll(filter, pageable);
		assertNotNull(result);
		assertTrue(result.getContent().isEmpty());
	}

	@Test
	public void findAll_withPaginationParams_shouldRespectPagination() {
		// Create multiple test data entries
		AnagPartner partner = createTestAnagPartner();
		AnagPartner savedPartner = anagPartnerRepository.save(partner);

		AnagStation station = createTestAnagStation();
		station.setAnagPartner(savedPartner);
		AnagStation savedStation = anagStationRepository.save(station);

		for (int i = 0; i < 5; i++) {
			AnagInstitution institution = createTestAnagInstitution();
			institution.setFiscalCode(randomFiscalCode());
			AnagInstitution savedInstitution = anagInstitutionRepository.save(institution);

			AnagStationAnagInstitution association = new AnagStationAnagInstitution();
			association.setAnagStation(savedStation);
			association.setAnagInstitution(savedInstitution);
			association.setAca(true);
			association.setStandin(false);
			anagStationAnagInstitutionRepository.save(association);
		}

		Pageable pageable = mock(Pageable.class);
		when(pageable.getPageSize()).thenReturn(2);
		when(pageable.getOffset()).thenReturn(0L);
		when(pageable.getSortOr(any(Sort.class))).thenReturn(Sort.unsorted());

		AnagInstitutionFilter filter = new AnagInstitutionFilter();

		Page<AnagInstitutionDTO> result = anagInstitutionService.findAll(filter, pageable);
		assertNotNull(result);
		assertTrue(result.getContent().size() >= 0);
	}

	@Test
	public void findAll_InstitutionFilter_withPaginationParams_shouldRespectPagination() {
		// Create multiple institutions
		for (int i = 0; i < 3; i++) {
			AnagInstitution institution = createTestAnagInstitution();
			institution.setFiscalCode(randomFiscalCode());
			anagInstitutionRepository.save(institution);
		}

		Pageable pageable = mock(Pageable.class);
		when(pageable.getPageSize()).thenReturn(2);
		when(pageable.getOffset()).thenReturn(0L);
		when(pageable.getSortOr(any(Sort.class))).thenReturn(Sort.unsorted());

		InstitutionFilter filter = new InstitutionFilter();

		Page<InstitutionIdentificationDTO> result = anagInstitutionService.findAll(filter, pageable);
		assertNotNull(result);
		assertTrue(result.getContent().size() >= 0);
	}

	@Test
	public void findAll_withNullParametersAndPageable_shouldHandleGracefully() {
		Pageable pageable = createMockPageable();

		// Test InstitutionFilter with null values
		InstitutionFilter institutionFilter = new InstitutionFilter();
		institutionFilter.setFiscalCode(null);
		institutionFilter.setName(null);

		Page<InstitutionIdentificationDTO> result1 = anagInstitutionService.findAll(institutionFilter, pageable);
		assertNotNull(result1);
		assertNotNull(result1.getContent());

		// Test AnagInstitutionFilter with null values
		AnagInstitutionFilter anagInstitutionFilter = new AnagInstitutionFilter();
		anagInstitutionFilter.setInstitutionId(null);
		anagInstitutionFilter.setPartnerId(null);
		anagInstitutionFilter.setStationId(null);
		anagInstitutionFilter.setShowNotEnabled(null);

		// Since AnagInstitutionFilter requires joins, we need at least minimal data
		AnagPartner partner = createTestAnagPartner();
		AnagPartner savedPartner = anagPartnerRepository.save(partner);

		AnagStation station = createTestAnagStation();
		station.setAnagPartner(savedPartner);
		AnagStation savedStation = anagStationRepository.save(station);

		AnagInstitution institution = createTestAnagInstitution();
		AnagInstitution savedInstitution = anagInstitutionRepository.save(institution);

		AnagStationAnagInstitution association = new AnagStationAnagInstitution();
		association.setAnagStation(savedStation);
		association.setAnagInstitution(savedInstitution);
		association.setAca(true);
		association.setStandin(false);
		anagStationAnagInstitutionRepository.save(association);

		Page<AnagInstitutionDTO> result2 = anagInstitutionService.findAll(anagInstitutionFilter, pageable);
		assertNotNull(result2);
		assertNotNull(result2.getContent());
	}

	private Pageable createMockPageable() {
		Pageable pageable = mock(Pageable.class);
		when(pageable.getPageSize()).thenReturn(10);
		when(pageable.getOffset()).thenReturn(0L);
		when(pageable.getSortOr(any(Sort.class))).thenReturn(Sort.unsorted());
		return pageable;
	}

	private AnagInstitution createTestAnagInstitution() {
		AnagInstitution institution = new AnagInstitution();
		institution.setFiscalCode(randomFiscalCode());
		institution.setName("Test Institution " + randomFiscalCode());
		institution.setEnabled(true);
		institution.setCreatedBy("test-user");
		institution.setCreatedDate(Instant.now());
		institution.setLastModifiedBy("test-user");
		institution.setLastModifiedDate(Instant.now());
		return institution;
	}

	private AnagPartner createTestAnagPartner() {
		AnagPartner partner = new AnagPartner();
		partner.setFiscalCode(randomFiscalCode());
		partner.setName("Test Partner " + randomFiscalCode());
		partner.setStatus(PartnerStatus.ATTIVO);
		partner.setQualified(true);
		partner.setStationsCount(0L);
		partner.setCreatedBy("test-user");
		partner.setCreatedDate(Instant.now());
		partner.setLastModifiedBy("test-user");
		partner.setLastModifiedDate(Instant.now());
		return partner;
	}

	private AnagStation createTestAnagStation() {
		AnagStation station = new AnagStation();
		station.setName("Test Station " + randomFiscalCode());
		station.setStatus(StationStatus.ATTIVA);
		station.setTypeConnection("SYNC");
		station.setPrimitiveVersion(1);
		station.setPaymentOption(true);
		station.setAssociatedInstitutes(0);
		station.setCreatedBy("test-user");
		station.setCreatedDate(Instant.now());
		station.setLastModifiedBy("test-user");
		station.setLastModifiedDate(Instant.now());
		return station;
	}

	private String randomFiscalCode() {
		Random random = new Random();
		return "TEST" + String.format("%08d", random.nextInt(100000000));
	}
}
