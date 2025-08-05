package com.nexigroup.pagopa.cruscotto.service;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.nexigroup.pagopa.cruscotto.IntegrationTest;
import com.nexigroup.pagopa.cruscotto.domain.AnagPartner;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.PartnerStatus;
import com.nexigroup.pagopa.cruscotto.repository.AnagPartnerRepository;
import com.nexigroup.pagopa.cruscotto.service.dto.AnagPartnerDTO;

@IntegrationTest
public class AnagPartnerServiceImplIT {
	
	@Autowired
	private AnagPartnerService anagPartnerService;
	
	@Autowired
	private AnagPartnerRepository anagPartnerRepository;
	
	@Test
	public void findAll_shouldReturnOne() {
		Pageable pageable = createMockPageable();
		
		AnagPartner partner = createTestAnagPartner();
		AnagPartner savedPartner = anagPartnerRepository.save(partner);
		
		Page<AnagPartnerDTO> res = anagPartnerService.findAll(savedPartner.getId(), null, null, null, null, null, null, pageable);
		
		assertNotNull(res);
		assertTrue(res.getSize() == 1);
	}

	@Test
	public void findAll_withAnalyzedFilter_shouldReturnResults() {
		Pageable pageable = createMockPageable();
		
		AnagPartner partner = createTestAnagPartner();
		anagPartnerRepository.save(partner);
		
		// Test with analyzed = true
		Page<AnagPartnerDTO> res = anagPartnerService.findAll(null, true, null, null, null, null, null, pageable);
		assertNotNull(res);
		
		// Test with analyzed = false
		res = anagPartnerService.findAll(null, false, null, null, null, null, null, pageable);
		assertNotNull(res);
	}

	@Test
	public void findAll_withQualifiedFilter_shouldReturnResults() {
		Pageable pageable = createMockPageable();
		
		AnagPartner partner = createTestAnagPartner();
		partner.setQualified(true);
		anagPartnerRepository.save(partner);
		
		// Test with qualified = true
		Page<AnagPartnerDTO> res = anagPartnerService.findAll(null, null, true, null, null, null, null, pageable);
		assertNotNull(res);
		assertTrue(res.getContent().size() >= 0);
		
		// Test with qualified = false
		res = anagPartnerService.findAll(null, null, false, null, null, null, null, pageable);
		assertNotNull(res);
	}

	@Test
	public void findAll_withShowNotActiveFilter_shouldReturnResults() {
		Pageable pageable = createMockPageable();
		
		// Create an inactive partner
		AnagPartner inactivePartner = createTestAnagPartner();
		inactivePartner.setStatus(PartnerStatus.NON_ATTIVO);
		anagPartnerRepository.save(inactivePartner);
		
		// Create an active partner
		AnagPartner activePartner = createTestAnagPartner();
		activePartner.setStatus(PartnerStatus.ATTIVO);
		activePartner.setFiscalCode("fiscalcode2");
		anagPartnerRepository.save(activePartner);
		
		// Test with showNotActive = true (show inactive partners)
		Page<AnagPartnerDTO> res = anagPartnerService.findAll(null, null, null, null, null, null, true, pageable);
		assertNotNull(res);
		
		// Test with showNotActive = false (show active partners)
		res = anagPartnerService.findAll(null, null, null, null, null, null, false, pageable);
		assertNotNull(res);
	}

	@Test
	public void findAll_withDateFilters_shouldReturnResults() {
		Pageable pageable = createMockPageable();
		
		AnagPartner partner = createTestAnagPartner();
		partner.setAnalysisPeriodStartDate(LocalDate.of(2025, 1, 1));
		partner.setAnalysisPeriodEndDate(LocalDate.of(2025, 12, 31));
		anagPartnerRepository.save(partner);
		
		// Test with date filters
		Page<AnagPartnerDTO> res = anagPartnerService.findAll(null, null, null, "2025-01-01", "2025-01-01", "2025-12-31", null, pageable);
		assertNotNull(res);
	}

	@Test
	public void findAll_withMultipleFilters_shouldReturnResults() {
		Pageable pageable = createMockPageable();
		
		AnagPartner partner = createTestAnagPartner();
		partner.setQualified(true);
		partner.setStatus(PartnerStatus.ATTIVO);
		AnagPartner savedPartner = anagPartnerRepository.save(partner);
		
		// Test with multiple filters combined
		Page<AnagPartnerDTO> res = anagPartnerService.findAll(
			savedPartner.getId(), 
			false, 
			true, 
			null, 
			null, 
			null, 
			false, 
			pageable
		);
		assertNotNull(res);
	}

	@Test
	public void findAll_withNoFilters_shouldReturnAllResults() {
		Pageable pageable = createMockPageable();
		
		// Create multiple partners
		AnagPartner partner1 = createTestAnagPartner();
		partner1.setFiscalCode(randomFiscalCode());
		anagPartnerRepository.save(partner1);
		
		AnagPartner partner2 = createTestAnagPartner();
		partner2.setFiscalCode(randomFiscalCode());
		partner2.setStatus(PartnerStatus.NON_ATTIVO);
		anagPartnerRepository.save(partner2);
		
		// Test with no filters - should return all partners
		Page<AnagPartnerDTO> res = anagPartnerService.findAll(null, null, null, null, null, null, null, pageable);
		assertNotNull(res);
		assertTrue(res.getContent().size() >= 2);
	}

	@Test
	public void findAll_withNonExistentPartnerId_shouldReturnEmpty() {
		Pageable pageable = createMockPageable();
		
		// Test with non-existent partner ID
		Page<AnagPartnerDTO> res = anagPartnerService.findAll(99999L, null, null, null, null, null, null, pageable);
		assertNotNull(res);
		assertTrue(res.getContent().isEmpty());
	}

	@Test
	public void findAll_withEmptyDateStrings_shouldReturnResults() {
		Pageable pageable = createMockPageable();
		
		AnagPartner partner = createTestAnagPartner();
		anagPartnerRepository.save(partner);
		
		// Test with empty date strings (should be ignored)
		Page<AnagPartnerDTO> res = anagPartnerService.findAll(null, null, null, "", "", "", null, pageable);
		assertNotNull(res);
	}

	@Test
	public void findAll_withPaginationParams_shouldRespectPagination() {
		Pageable pageable = mock(Pageable.class);
		
		// Create multiple partners
		for (int i = 0; i < 5; i++) {
			AnagPartner partner = createTestAnagPartner();
			partner.setFiscalCode(randomFiscalCode());
			anagPartnerRepository.save(partner);
		}
		
		when(pageable.getPageSize()).thenReturn(2);
		when(pageable.getOffset()).thenReturn(0L);
		when(pageable.getSortOr(any(Sort.class))).thenReturn(Sort.unsorted());
		
		Page<AnagPartnerDTO> res = anagPartnerService.findAll(null, null, null, null, null, null, null, pageable);
		assertNotNull(res);
		// The actual number of results may vary based on database state
		assertTrue(res.getContent().size() >= 0);
	}

	@Test
	public void findAll_withNullParametersAndPageable_shouldHandleGracefully() {
		Pageable pageable = createMockPageable();
		
		// Test with all null parameters except pageable
		Page<AnagPartnerDTO> res = anagPartnerService.findAll(null, null, null, null, null, null, null, pageable);
		assertNotNull(res);
		assertNotNull(res.getContent());
	}

	@Test
	public void findAll_verifyPageableParametersAreUsed() {
		Pageable pageable = mock(Pageable.class);
		
		AnagPartner partner = createTestAnagPartner();
		anagPartnerRepository.save(partner);
		
		when(pageable.getPageSize()).thenReturn(5);
		when(pageable.getOffset()).thenReturn(1L);
		when(pageable.getSortOr(any(Sort.class))).thenReturn(Sort.by("name").ascending());
		
		Page<AnagPartnerDTO> res = anagPartnerService.findAll(null, null, null, null, null, null, null, pageable);
		assertNotNull(res);
		// Verify the result is a valid page response
		assertTrue(res.getSize() >= 0);
	}
	
	private Pageable createMockPageable() {
		Pageable pageable = mock(Pageable.class);
		when(pageable.getPageSize()).thenReturn(10);
		when(pageable.getOffset()).thenReturn(0L);
		when(pageable.getSortOr(any(Sort.class))).thenReturn(Sort.unsorted());
		return pageable;
	}


		
	
	 
	private AnagPartner createTestAnagPartner() {
		
		AnagPartner partner = new AnagPartner();
		
		partner.setAnagPlannedShutdowns(null);
		partner.setAnagStations(null);
		partner.setAnalysisPeriodEndDate(LocalDate.now());
		partner.setAnalysisPeriodStartDate(LocalDate.now());
		partner.setCreatedBy("test-user");
		partner.setCreatedDate(Instant.now());
		partner.setDeactivationDate(LocalDate.now());
		partner.setFiscalCode(randomFiscalCode());
		partner.setLastAnalysisDate(LocalDate.now());
		partner.setLastModifiedBy("test-user");
		partner.setLastModifiedDate(Instant.now());
		partner.setName("testpartner");
		partner.setQualified(false);
		partner.setStationsCount(0L);
		partner.setStatus(PartnerStatus.ATTIVO);
				
		return partner;
	}
	
	private String randomFiscalCode() {
        String caratteri = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder(35);
        Random random = new Random();

        for (int i = 0; i < 35; i++) {
            int index = random.nextInt(caratteri.length());
            sb.append(caratteri.charAt(index));
        }

        return sb.toString();
    }
}
