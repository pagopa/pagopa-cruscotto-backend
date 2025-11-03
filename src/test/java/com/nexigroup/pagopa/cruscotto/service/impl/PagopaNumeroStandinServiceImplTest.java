package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.*;
import com.nexigroup.pagopa.cruscotto.repository.KpiB3AnalyticDataRepository;
import com.nexigroup.pagopa.cruscotto.repository.PagopaNumeroStandinRepository;
import com.nexigroup.pagopa.cruscotto.service.dto.PagopaNumeroStandinDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PagopaNumeroStandinServiceImplTest {

    @Mock
    private KpiB3AnalyticDataRepository kpiB3AnalyticDataRepository;

    @Mock
    private PagopaNumeroStandinRepository pagopaNumeroStandinRepository;

    @InjectMocks
    private PagopaNumeroStandinServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testConvertToDTO_basic() {
        PagopaNumeroStandin entity = new PagopaNumeroStandin();
        entity.setId(1L);
        entity.setStationCode("ST001");
        entity.setIntervalStart(LocalDateTime.now());
        entity.setIntervalEnd(LocalDateTime.now().plusHours(1));
        entity.setStandInCount(5);
        entity.setEventType("TYPE_A");
        entity.setDataDate(LocalDateTime.now());
        entity.setLoadTimestamp(LocalDateTime.now());

        PagopaNumeroStandinDTO dto = service.convertToDTO(entity);

        assertNotNull(dto);
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getStationCode(), dto.getStationCode());
        assertEquals(entity.getStandInCount(), dto.getStandInCount());
        assertEquals(entity.getEventType(), dto.getEventType());
    }

    @Test
    void testConvertToDTO_withAnalyticData() {
        PagopaNumeroStandin entity = new PagopaNumeroStandin();
        entity.setId(1L);
        KpiB3AnalyticData analyticData = new KpiB3AnalyticData();
        AnagStation station = new AnagStation();
        AnagPartner partner = new AnagPartner();
        partner.setId(10L);
        partner.setName("Partner A");
        partner.setFiscalCode("FISCAL123");
        station.setAnagPartner(partner);
        analyticData.setAnagStation(station);

        PagopaNumeroStandinDTO dto = service.convertToDTO(entity, analyticData);

        assertNotNull(dto);
        assertEquals(partner.getId(), dto.getPartnerId());
        assertEquals(partner.getName(), dto.getPartnerName());
        assertEquals(partner.getFiscalCode(), dto.getPartnerFiscalCode());
    }

    @Test
    void testFindByAnalyticDataId_success() {
        KpiB3AnalyticData analyticData = new KpiB3AnalyticData();
        analyticData.setEventId("100");

        PagopaNumeroStandin standin = new PagopaNumeroStandin();
        standin.setId(100L);

        when(kpiB3AnalyticDataRepository.findById(1L)).thenReturn(Optional.of(analyticData));
        when(pagopaNumeroStandinRepository.findById(100L)).thenReturn(Optional.of(standin));

        List<PagopaNumeroStandinDTO> result = service.findByAnalyticDataId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(standin.getId(), result.get(0).getId());
    }

    @Test
    void testFindByAnalyticDataId_noAnalyticData() {
        when(kpiB3AnalyticDataRepository.findById(1L)).thenReturn(Optional.empty());

        List<PagopaNumeroStandinDTO> result = service.findByAnalyticDataId(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindByAnalyticDataId_invalidEventId() {
        KpiB3AnalyticData analyticData = new KpiB3AnalyticData();
        analyticData.setEventId("INVALID");

        when(kpiB3AnalyticDataRepository.findById(1L)).thenReturn(Optional.of(analyticData));

        List<PagopaNumeroStandinDTO> result = service.findByAnalyticDataId(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindByAnalyticDataId_noPagopaStandin() {
        KpiB3AnalyticData analyticData = new KpiB3AnalyticData();
        analyticData.setEventId("100");

        when(kpiB3AnalyticDataRepository.findById(1L)).thenReturn(Optional.of(analyticData));
        when(pagopaNumeroStandinRepository.findById(100L)).thenReturn(Optional.empty());

        List<PagopaNumeroStandinDTO> result = service.findByAnalyticDataId(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testConvertToDTO_entityNull() {
        PagopaNumeroStandinDTO dto = service.convertToDTO((PagopaNumeroStandin) null);
        assertNull(dto);
    }

    @Test
    void testConvertToDTO_entityWithAnalyticDataNulls() {
        PagopaNumeroStandin entity = new PagopaNumeroStandin();
        entity.setId(1L);

        // AnalyticData null
        PagopaNumeroStandinDTO dto1 = service.convertToDTO(entity, null);
        assertNotNull(dto1);
        assertEquals(entity.getId(), dto1.getId());

        // AnagStation null
        KpiB3AnalyticData analyticData1 = new KpiB3AnalyticData();
        analyticData1.setAnagStation(null);
        PagopaNumeroStandinDTO dto2 = service.convertToDTO(entity, analyticData1);
        assertNotNull(dto2);

        // AnagPartner null
        KpiB3AnalyticData analyticData2 = new KpiB3AnalyticData();
        analyticData2.setAnagStation(new AnagStation()); // no partner
        PagopaNumeroStandinDTO dto3 = service.convertToDTO(entity, analyticData2);
        assertNotNull(dto3);
    }

    @Test
    void testFindByAnalyticDataId_eventIdNull() {
        KpiB3AnalyticData analyticData = new KpiB3AnalyticData();
        analyticData.setEventId(null);

        when(kpiB3AnalyticDataRepository.findById(1L)).thenReturn(Optional.of(analyticData));

        List<PagopaNumeroStandinDTO> result = service.findByAnalyticDataId(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testConvertToDTO_entityNullWithAnalyticData() {
        KpiB3AnalyticData analyticData = new KpiB3AnalyticData();
        PagopaNumeroStandinDTO dto = service.convertToDTO(null, analyticData);
        assertNull(dto);
    }
}
