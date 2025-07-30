package com.nexigroup.pagopa.cruscotto.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.nexigroup.pagopa.cruscotto.IntegrationTest;
import com.nexigroup.pagopa.cruscotto.domain.AnagPartner;
import com.nexigroup.pagopa.cruscotto.domain.AnagStation;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.PartnerStatus;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.StationStatus;
import com.nexigroup.pagopa.cruscotto.repository.AnagPartnerRepository;
import com.nexigroup.pagopa.cruscotto.repository.AnagStationRepository;
import com.nexigroup.pagopa.cruscotto.service.dto.AnagStationDTO;
import com.nexigroup.pagopa.cruscotto.service.filter.AnagStationFilter;
import com.nexigroup.pagopa.cruscotto.service.impl.AnagStationServiceImpl;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for {@link AnagStationServiceImpl#findAll(AnagStationFilter, Pageable)}.
 */
@IntegrationTest
@Transactional
class AnagStationServiceImplIT {

    @Autowired
    private AnagStationService anagStationService;

    @Autowired
    private AnagStationRepository anagStationRepository;

    @Autowired
    private AnagPartnerRepository anagPartnerRepository;

    private AnagPartner testPartner1;
    private AnagPartner testPartner2;
    private AnagStation activeStation1;
    private AnagStation activeStation2;
    private AnagStation inactiveStation1;
    private AnagStation inactiveStation2;

    @BeforeEach
    void setUp() {
        // Clean up any existing data
        anagStationRepository.deleteAll();
        anagPartnerRepository.deleteAll();

        // Create test partners
        testPartner1 = createTestPartner("PARTNER001", "Test Partner 1", PartnerStatus.ATTIVO);
        testPartner2 = createTestPartner("PARTNER002", "Test Partner 2", PartnerStatus.ATTIVO);

        testPartner1 = anagPartnerRepository.save(testPartner1);
        testPartner2 = anagPartnerRepository.save(testPartner2);

        // Create test stations
        activeStation1 = createTestStation("ACTIVE_STATION_1", testPartner1, StationStatus.ATTIVA, 
            LocalDate.now().minusDays(30), null, "SYNC", 1, true, 5);
        
        activeStation2 = createTestStation("ACTIVE_STATION_2", testPartner1, StationStatus.ATTIVA, 
            LocalDate.now().minusDays(15), null, "ASYNC", 2, false, 3);
        
        inactiveStation1 = createTestStation("INACTIVE_STATION_1", testPartner2, StationStatus.NON_ATTIVA, 
            LocalDate.now().minusDays(60), LocalDate.now().minusDays(10), "SYNC", 1, true, 0);
        
        inactiveStation2 = createTestStation("INACTIVE_STATION_2", testPartner2, StationStatus.NON_ATTIVA, 
            LocalDate.now().minusDays(45), LocalDate.now().minusDays(5), "ASYNC", 2, false, 2);

        activeStation1 = anagStationRepository.save(activeStation1);
        activeStation2 = anagStationRepository.save(activeStation2);
        inactiveStation1 = anagStationRepository.save(inactiveStation1);
        inactiveStation2 = anagStationRepository.save(inactiveStation2);
    }

    @AfterEach
    void tearDown() {
        anagStationRepository.deleteAll();
        anagPartnerRepository.deleteAll();
    }

    @Test
    void findAll_WithEmptyFilter_ShouldReturnOnlyActiveStations() {
        // Given
        AnagStationFilter filter = new AnagStationFilter();
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<AnagStationDTO> result = anagStationService.findAll(filter, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getTotalPages()).isEqualTo(1);
        
        List<AnagStationDTO> stations = result.getContent();
        assertThat(stations).allMatch(station -> station.getStatus() == StationStatus.ATTIVA);
        assertThat(stations).extracting(AnagStationDTO::getName)
            .containsExactlyInAnyOrder("ACTIVE_STATION_1", "ACTIVE_STATION_2");
    }

    @Test
    void findAll_WithShowNotActiveTrue_ShouldReturnAllStations() {
        // Given
        AnagStationFilter filter = new AnagStationFilter();
        filter.setShowNotActive(true);
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<AnagStationDTO> result = anagStationService.findAll(filter, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(4);
        assertThat(result.getTotalElements()).isEqualTo(4);
        
        List<AnagStationDTO> stations = result.getContent();
        assertThat(stations).extracting(AnagStationDTO::getName)
            .containsExactlyInAnyOrder("ACTIVE_STATION_1", "ACTIVE_STATION_2", "INACTIVE_STATION_1", "INACTIVE_STATION_2");
    }

    @Test
    void findAll_WithShowNotActiveFalse_ShouldReturnOnlyActiveStations() {
        // Given
        AnagStationFilter filter = new AnagStationFilter();
        filter.setShowNotActive(false);
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<AnagStationDTO> result = anagStationService.findAll(filter, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        
        List<AnagStationDTO> stations = result.getContent();
        assertThat(stations).allMatch(station -> station.getStatus() == StationStatus.ATTIVA);
    }

    @Test
    void findAll_WithPartnerIdFilter_ShouldReturnStationsForSpecificPartner() {
        // Given
        AnagStationFilter filter = new AnagStationFilter();
        filter.setPartnerId(testPartner1.getId());
        filter.setShowNotActive(true); // Show all to test partner filter
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<AnagStationDTO> result = anagStationService.findAll(filter, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        
        List<AnagStationDTO> stations = result.getContent();
        assertThat(stations).allMatch(station -> station.getPartnerId().equals(testPartner1.getId()));
        assertThat(stations).extracting(AnagStationDTO::getName)
            .containsExactlyInAnyOrder("ACTIVE_STATION_1", "ACTIVE_STATION_2");
    }

    @Test
    void findAll_WithStationIdFilter_ShouldReturnSpecificStation() {
        // Given
        AnagStationFilter filter = new AnagStationFilter();
        filter.setStationId(activeStation1.getId());
        filter.setShowNotActive(true);
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<AnagStationDTO> result = anagStationService.findAll(filter, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        
        AnagStationDTO station = result.getContent().get(0);
        assertThat(station.getId()).isEqualTo(activeStation1.getId());
        assertThat(station.getName()).isEqualTo("ACTIVE_STATION_1");
    }

    @Test
    void findAll_WithMultipleFilters_ShouldApplyAllFilters() {
        // Given
        AnagStationFilter filter = new AnagStationFilter();
        filter.setPartnerId(testPartner2.getId());
        filter.setStationId(inactiveStation1.getId());
        filter.setShowNotActive(true);
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<AnagStationDTO> result = anagStationService.findAll(filter, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        
        AnagStationDTO station = result.getContent().get(0);
        assertThat(station.getId()).isEqualTo(inactiveStation1.getId());
        assertThat(station.getName()).isEqualTo("INACTIVE_STATION_1");
        assertThat(station.getPartnerId()).isEqualTo(testPartner2.getId());
        assertThat(station.getStatus()).isEqualTo(StationStatus.NON_ATTIVA);
    }

    @Test
    void findAll_WithPagination_ShouldReturnCorrectPage() {
        // Given
        AnagStationFilter filter = new AnagStationFilter();
        filter.setShowNotActive(true);
        Pageable pageable = PageRequest.of(1, 2); // Second page, 2 items per page

        // When
        Page<AnagStationDTO> result = anagStationService.findAll(filter, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(4);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.getNumber()).isEqualTo(1); // Current page
        assertThat(result.getSize()).isEqualTo(2); // Page size
    }

    @Test
    void findAll_WithSorting_ShouldReturnSortedResults() {
        // Given
        AnagStationFilter filter = new AnagStationFilter();
        filter.setShowNotActive(true);
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "name"));

        // When
        Page<AnagStationDTO> result = anagStationService.findAll(filter, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(4);
        
        List<String> stationNames = result.getContent().stream()
            .map(AnagStationDTO::getName)
            .toList();
        
        // Should be sorted in descending order by name
        assertThat(stationNames).containsExactly("INACTIVE_STATION_2", "INACTIVE_STATION_1", "ACTIVE_STATION_2", "ACTIVE_STATION_1");
    }

    @Test
    void findAll_WithEmptyResult_ShouldReturnEmptyPage() {
        // Given
        AnagStationFilter filter = new AnagStationFilter();
        filter.setPartnerId(999L); // Non-existent partner ID
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<AnagStationDTO> result = anagStationService.findAll(filter, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
        assertThat(result.getTotalPages()).isEqualTo(0);
    }

    @Test
    void findAll_ShouldReturnCompleteStationData() {
        // Given
        AnagStationFilter filter = new AnagStationFilter();
        filter.setStationId(activeStation1.getId());
        filter.setShowNotActive(true);
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<AnagStationDTO> result = anagStationService.findAll(filter, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        
        AnagStationDTO station = result.getContent().get(0);
        
        // Verify all fields are properly mapped
        assertThat(station.getId()).isEqualTo(activeStation1.getId());
        assertThat(station.getName()).isEqualTo("ACTIVE_STATION_1");
        assertThat(station.getActivationDate()).isEqualTo(activeStation1.getActivationDate());
        assertThat(station.getPartnerId()).isEqualTo(testPartner1.getId());
        assertThat(station.getPartnerFiscalCode()).isEqualTo(testPartner1.getFiscalCode());
        assertThat(station.getPartnerName()).isEqualTo(testPartner1.getName());
        assertThat(station.getTypeConnection()).isEqualTo("SYNC");
        assertThat(station.getPrimitiveVersion()).isEqualTo(1);
        assertThat(station.getPaymentOption()).isTrue();
        assertThat(station.getAssociatedInstitutes()).isEqualTo(5);
        assertThat(station.getStatus()).isEqualTo(StationStatus.ATTIVA);
        assertThat(station.getDeactivationDate()).isNull();
        
        // Verify audit fields are included
        assertThat(station.getCreatedBy()).isNotNull();
        assertThat(station.getCreatedDate()).isNotNull();
        assertThat(station.getLastModifiedBy()).isNotNull();
        assertThat(station.getLastModifiedDate()).isNotNull();
    }

    @Test
    void findAll_WithNullFilterValues_ShouldHandleNullsCorrectly() {
        // Given
        AnagStationFilter filter = new AnagStationFilter();
        filter.setPartnerId(null);
        filter.setStationId(null);
        filter.setShowNotActive(null); // This should default to showing only active stations
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<AnagStationDTO> result = anagStationService.findAll(filter, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2); // Only active stations
        assertThat(result.getContent()).allMatch(station -> station.getStatus() == StationStatus.ATTIVA);
    }

    private AnagPartner createTestPartner(String fiscalCode, String name, PartnerStatus status) {
        AnagPartner partner = new AnagPartner();
        partner.setFiscalCode(fiscalCode);
        partner.setName(name);
        partner.setStatus(status);
        partner.setQualified(true);
        return partner;
    }

    private AnagStation createTestStation(String name, AnagPartner partner, StationStatus status, 
            LocalDate activationDate, LocalDate deactivationDate, String typeConnection, 
            Integer primitiveVersion, Boolean paymentOption, Integer associatedInstitutes) {
        AnagStation station = new AnagStation();
        station.setName(name);
        station.setAnagPartner(partner);
        station.setStatus(status);
        station.setActivationDate(activationDate);
        station.setDeactivationDate(deactivationDate);
        station.setTypeConnection(typeConnection);
        station.setPrimitiveVersion(primitiveVersion);
        station.setPaymentOption(paymentOption);
        station.setAssociatedInstitutes(associatedInstitutes);
        return station;
    }
}
