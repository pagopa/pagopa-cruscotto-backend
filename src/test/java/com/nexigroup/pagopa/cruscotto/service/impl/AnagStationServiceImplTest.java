package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.*;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.StationStatus;
import com.nexigroup.pagopa.cruscotto.repository.*;
import com.nexigroup.pagopa.cruscotto.service.dto.AnagStationDTO;
import com.nexigroup.pagopa.cruscotto.service.filter.AnagStationFilter;
import com.nexigroup.pagopa.cruscotto.service.filter.StationFilter;
import com.nexigroup.pagopa.cruscotto.service.mapper.AnagStationMapper;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import com.querydsl.core.types.*;
import com.querydsl.jpa.impl.JPAQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.*;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("AnagStationServiceImpl Tests")
class AnagStationServiceImplTest {

    @Mock
    private AnagStationRepository anagStationRepository;

    @Mock
    private AnagPartnerRepository anagPartnerRepository;

    @Mock
    private AnagStationAnagInstitutionRepository anagStationAnagInstitutionRepository;

    @Mock
    private AnagStationMapper anagStationMapper;

    @InjectMocks
    private AnagStationServiceImpl anagStationService;

    @Mock
    private QueryBuilder queryBuilder;

    @Mock
    private JPAQuery jpaQuery; // Raw type per evitare problemi generici

    @BeforeEach
    void setUp() throws Exception {
        // Set batchSize tramite reflection
        java.lang.reflect.Field batchSizeField = AnagStationServiceImpl.class.getDeclaredField("batchSize");
        batchSizeField.setAccessible(true);
        batchSizeField.set(anagStationService, "100");
    }

    @Test
    void testFindOneByName_ReturnsStation() {
        AnagStation station = new AnagStation();
        station.setName("Station1");
        when(anagStationRepository.findOneByName("Station1")).thenReturn(Optional.of(station));

        Optional<AnagStation> result = anagStationService.findOneByName("Station1");

        assertThat(result).isPresent();
        assertThat(result.orElseThrow().getName()).isEqualTo("Station1");
    }

    @Test
    void testSaveAll_SavesStationsInBatch() {
        AnagStationDTO dto = new AnagStationDTO();
        dto.setName("Station1");
        dto.setPartnerFiscalCode("FISCAL123");
        dto.setStatus(StationStatus.NON_ATTIVA);

        AnagPartner partner = new AnagPartner();
        partner.setFiscalCode("FISCAL123");

        when(anagPartnerRepository.findOne((Example<AnagPartner>) any())).thenReturn(Optional.of(partner));
        when(anagStationRepository.findOne((Example<AnagStation>) any())).thenReturn(Optional.empty());
        when(anagStationRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        anagStationService.saveAll(List.of(dto));

        verify(anagStationRepository, atLeastOnce()).save(any(AnagStation.class));
    }

    @Test
    void testFindOne_ReturnsDto() {
        AnagStation station = new AnagStation();
        station.setId(1L);

        AnagStationDTO dto = new AnagStationDTO();
        dto.setId(1L);

        when(anagStationRepository.findById(1L)).thenReturn(Optional.of(station));
        when(anagStationMapper.toDto(station)).thenReturn(dto);

        Optional<AnagStationDTO> result = anagStationService.findOne(1L);

        assertThat(result).isPresent();
        assertThat(result.orElseThrow().getId()).isEqualTo(1L);
    }

    @Test
    void testUpdateAllStationsAssociatedInstitutionsCount_UpdatesCounts() {
        // Array di oggetti per simulare i risultati della query
        Object[] count1 = {1L, 5L};
        Object[] count2 = {2L, 3L};

        // Crea una lista di Object[] usando Arrays.<Object[]>asList
        when(anagStationAnagInstitutionRepository.countInstitutionsByStation())
            .thenReturn(Arrays.<Object[]>asList(count1, count2));

        // Esegui il metodo
        anagStationService.updateAllStationsAssociatedInstitutionsCount();

        // Verifica che il repository sia stato chiamato con i valori corretti
        verify(anagStationRepository).updateAssociatedInstitutesCount(1L, 5);
        verify(anagStationRepository).updateAssociatedInstitutesCount(2L, 3);
    }

    @Test
    void testFindIdByNameOrCreate_ReturnsExistingId() {
        mockQueryBuilderChain();
        when(jpaQuery.fetchOne()).thenReturn(99L);

        long result = anagStationService.findIdByNameOrCreate("Station1", 1L);

        assertThat(result).isEqualTo(99L);
        verify(jpaQuery).fetchOne();
    }

    @Test
    void testFindIdByNameOrCreate_CreatesNewStation() {
        mockQueryBuilderChain();
        when(jpaQuery.fetchOne()).thenReturn(null);

        AnagPartner partner = new AnagPartner();
        partner.setId(1L);
        when(anagPartnerRepository.findOne((Example<AnagPartner>) any())).thenReturn(Optional.of(partner));

        AnagStation savedStation = new AnagStation();
        savedStation.setId(123L);
        when(anagStationRepository.save(any())).thenReturn(savedStation);

        long result = anagStationService.findIdByNameOrCreate("NewStation", 1L);

        assertThat(result).isEqualTo(123L);
        verify(anagStationRepository).save(any());
    }

    @Test
    void testFindAll_WithStationFilter() {
        StationFilter filter = new StationFilter();
        filter.setPartnerId("1");

        mockQueryBuilderChain();
        lenient().when(jpaQuery.fetch()).thenReturn(List.of(new Object()));
        when(jpaQuery.fetch()).thenReturn(List.of(new AnagStationDTO()));

        Pageable pageable = PageRequest.of(0, 10);
        Page<AnagStationDTO> page = anagStationService.findAll(filter, pageable);

        assertThat(page).isNotNull();
        assertThat(page.getTotalElements()).isEqualTo(1);
    }

    @Test
    void testFindAll_WithAnagStationFilter() {
        AnagStationFilter filter = new AnagStationFilter();
        filter.setPartnerId(1L);
        filter.setShowNotActive(false);

        mockQueryBuilderChain();
        lenient().when(jpaQuery.fetch()).thenReturn(List.of(new Object()));
        when(jpaQuery.fetch()).thenReturn(List.of(new AnagStationDTO()));

        Pageable pageable = PageRequest.of(0, 10);
        Page<AnagStationDTO> page = anagStationService.findAll(filter, pageable);

        assertThat(page).isNotNull();
        assertThat(page.getTotalElements()).isEqualTo(1);
    }

    // --- helper method per mock chain QueryDSL ---
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void mockQueryBuilderChain() {
        when(queryBuilder.createQuery()).thenReturn(jpaQuery);
        when(jpaQuery.from(any(EntityPath.class))).thenReturn(jpaQuery);
        when(jpaQuery.leftJoin(any(EntityPath.class))).thenReturn(jpaQuery);
        when(jpaQuery.on(any(Predicate.class))).thenReturn(jpaQuery);
        when(jpaQuery.where(any(Predicate.class))).thenReturn(jpaQuery);
        when(jpaQuery.select(any(Expression.class))).thenReturn(jpaQuery);
        when(jpaQuery.offset(anyLong())).thenReturn(jpaQuery);
        when(jpaQuery.limit(anyLong())).thenReturn(jpaQuery);
        when(jpaQuery.orderBy(any(OrderSpecifier.class))).thenReturn(jpaQuery);
    }

    @Test
    void testFindIdByNameOrCreate_PartnerNotFound_ThrowsException() {
        mockQueryBuilderChain();
        when(jpaQuery.fetchOne()).thenReturn(null);
        when(anagPartnerRepository.findOne((Example<AnagPartner>) any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> anagStationService.findIdByNameOrCreate("StationX", 1L))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("Partner not found");
    }

    @Test
    void testUpdateAllStationsAssociatedInstitutionsCount_ErrorHandling() {
        Object[] count = {1L, 2L};
        when(anagStationAnagInstitutionRepository.countInstitutionsByStation())
            .thenReturn(Arrays.<Object[]>asList(count));

        doThrow(new RuntimeException("DB error"))
            .when(anagStationRepository).updateAssociatedInstitutesCount(anyLong(), anyInt());

        anagStationService.updateAllStationsAssociatedInstitutionsCount();

        verify(anagStationRepository).updateAssociatedInstitutesCount(1L, 2);
    }

    @Test
    void testFindAll_WithStationId() {
        AnagStationFilter filter = new AnagStationFilter();
        filter.setPartnerId(1L);
        filter.setStationId(123L); // abilita il ramo
        filter.setShowNotActive(false);

        mockQueryBuilderChain();
        when(jpaQuery.fetch()).thenReturn(List.of(new AnagStationDTO()));

        Pageable pageable = PageRequest.of(0, 10);
        Page<AnagStationDTO> page = anagStationService.findAll(filter, pageable);

        assertThat(page).isNotNull();
        assertThat(page.getContent()).hasSize(1);
    }
}
