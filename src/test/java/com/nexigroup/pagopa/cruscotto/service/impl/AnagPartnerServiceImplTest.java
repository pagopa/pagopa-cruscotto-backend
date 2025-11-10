package com.nexigroup.pagopa.cruscotto.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import com.nexigroup.pagopa.cruscotto.domain.AnagPartner;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.PartnerStatus;
import com.nexigroup.pagopa.cruscotto.repository.AnagPartnerRepository;
import com.nexigroup.pagopa.cruscotto.service.dto.AnagPartnerDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.PartnerIdentificationDTO;
import com.nexigroup.pagopa.cruscotto.service.mapper.AnagPartnerMapper;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import com.querydsl.jpa.impl.JPAUpdateClause;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AnagPartnerServiceImplTest {

    @Mock
    private QueryBuilder queryBuilder;

    @Mock
    private AnagPartnerRepository anagPartnerRepository;

    @Mock
    private AnagPartnerMapper anagPartnerMapper;

    @InjectMocks
    private AnagPartnerServiceImpl service;

    @BeforeEach
    void setUp() {

        ReflectionTestUtils.setField(service, "batchSize", "100");
    }

    @Test
    void testFindOne() {
        AnagPartner partner = new AnagPartner();
        partner.setId(1L);
        AnagPartnerDTO dto = new AnagPartnerDTO();
        when(anagPartnerRepository.findById(1L)).thenReturn(Optional.of(partner));
        when(anagPartnerMapper.toDto(partner)).thenReturn(dto);

        Optional<AnagPartnerDTO> result = service.findOne(1L);
        assertThat(result).isPresent();
        assertThat(result.orElseThrow()).isEqualTo(dto);
    }

    @Test
    void testFindOne_NotFound() {
        when(anagPartnerRepository.findById(1L)).thenReturn(Optional.empty());
        Optional<AnagPartnerDTO> result = service.findOne(1L);
        assertThat(result).isEmpty();
    }

    @Test
    void testSaveAll() {
        AnagPartnerDTO dto = new AnagPartnerDTO();
        PartnerIdentificationDTO pid = new PartnerIdentificationDTO();
        pid.setFiscalCode("123");
        pid.setName("Partner1");
        dto.setPartnerIdentification(pid);
        dto.setStatus(PartnerStatus.ATTIVO);

        when(anagPartnerRepository.findOne((Example<AnagPartner>) any())).thenReturn(Optional.empty());
        service.saveAll(Collections.singletonList(dto));

        verify(anagPartnerRepository, times(1)).save(any(AnagPartner.class));
    }

    @Test
    void testChangePartnerQualified() {
        JPAUpdateClause updateClause = mock(JPAUpdateClause.class);
        when(queryBuilder.updateQuery(any())).thenReturn(updateClause);
        when(updateClause.set(any(), anyBoolean())).thenReturn(updateClause);
        when(updateClause.where(any())).thenReturn(updateClause);
        when(updateClause.execute()).thenReturn(1L);

        service.changePartnerQualified(1L, true);

        verify(updateClause).execute();
    }

    @Test
    void testUpdateLastAnalysisDate() {
        // Preparo il partner fittizio
        AnagPartner partner = new AnagPartner();
        when(anagPartnerRepository.findById(1L)).thenReturn(Optional.of(partner));

        // Chiamo il metodo da testare
        Instant now = Instant.now();
        service.updateLastAnalysisDate(1L, now);

        // Calcolo la data locale attesa
        LocalDate expectedDate = now.atZone(java.time.ZoneId.systemDefault()).toLocalDate();

        // Verifico che la data sia stata aggiornata correttamente
        assertThat(partner.getLastAnalysisDate()).isEqualTo(expectedDate);

        // Verifico che il partner sia stato salvato
        verify(anagPartnerRepository).save(partner);
    }

    @Test
    void testUpdateAnalysisPeriodDates() {
        AnagPartner partner = new AnagPartner();
        when(anagPartnerRepository.findById(1L)).thenReturn(Optional.of(partner));

        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now().plusDays(1);

        service.updateAnalysisPeriodDates(1L, start, end);

        assertThat(partner.getAnalysisPeriodStartDate()).isEqualTo(start);
        assertThat(partner.getAnalysisPeriodEndDate()).isEqualTo(end);
        verify(anagPartnerRepository).save(partner);
    }

    @Test
    void testUpdateStationsCount() {
        AnagPartner partner = new AnagPartner();
        when(anagPartnerRepository.findById(1L)).thenReturn(Optional.of(partner));

        service.updateStationsCount(1L, 10L);

        assertThat(partner.getStationsCount()).isEqualTo(10L);
        verify(anagPartnerRepository).save(partner);
    }

    @Test
    void testUpdateInstitutionsCount() {
        AnagPartner partner = new AnagPartner();
        when(anagPartnerRepository.findById(1L)).thenReturn(Optional.of(partner));

        service.updateInstitutionsCount(1L, 5L);

        assertThat(partner.getInstitutionsCount()).isEqualTo(5L);
        verify(anagPartnerRepository).save(partner);
    }

    @Test
    void testFindOneByFiscalCode() {
        AnagPartner partner = new AnagPartner();
        AnagPartnerDTO dto = new AnagPartnerDTO();
        when(anagPartnerRepository.findOneByFiscalCode("123")).thenReturn(Optional.of(partner));
        when(anagPartnerMapper.toDto(partner)).thenReturn(dto);

        Optional<AnagPartnerDTO> result = service.findOneByFiscalCode("123");

        assertThat(result).isPresent();
        assertThat(result.orElseThrow()).isEqualTo(dto);
    }

}
