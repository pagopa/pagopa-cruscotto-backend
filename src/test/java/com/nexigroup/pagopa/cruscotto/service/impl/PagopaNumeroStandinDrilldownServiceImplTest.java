package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.*;
import com.nexigroup.pagopa.cruscotto.repository.PagopaNumeroStandinDrilldownRepository;
import com.nexigroup.pagopa.cruscotto.service.dto.PagopaNumeroStandinDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagopaNumeroStandinDrilldownServiceImplTest {

    @Mock
    private PagopaNumeroStandinDrilldownRepository repository;

    private PagopaNumeroStandinDrilldownServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new PagopaNumeroStandinDrilldownServiceImpl(repository);
    }

    @Test
    void testSaveStandInSnapshot() {
        Instance instance = new Instance();
        InstanceModule module = new InstanceModule();
        AnagStation station = new AnagStation();
        KpiB3AnalyticData kpi = new KpiB3AnalyticData();
        kpi.setId(1L);
        LocalDate date = LocalDate.now();

        PagopaNumeroStandin standin1 = new PagopaNumeroStandin();
        standin1.setId(100L);
        standin1.setStationCode("ST1");

        PagopaNumeroStandin standin2 = new PagopaNumeroStandin();
        standin2.setId(101L);
        standin2.setStationCode("ST2");

        List<PagopaNumeroStandin> standinData = Arrays.asList(standin1, standin2);

        when(repository.saveAll(anyList())).thenReturn(Arrays.asList(new PagopaNumeroStandinDrilldown(), new PagopaNumeroStandinDrilldown()));

        service.saveStandInSnapshot(instance, module, station, kpi, date, standinData);

        ArgumentCaptor<List<PagopaNumeroStandinDrilldown>> captor = ArgumentCaptor.forClass(List.class);
        verify(repository, times(1)).saveAll(captor.capture());

        List<PagopaNumeroStandinDrilldown> saved = captor.getValue();
        assertEquals(2, saved.size());
        assertEquals(instance, saved.get(0).getInstance());
        assertEquals("ST1", saved.get(0).getStationCode());
    }

    @Test
    void testAddToBatch() {
        List<PagopaNumeroStandinDrilldown> batch = new java.util.ArrayList<>();
        PagopaNumeroStandin standin = new PagopaNumeroStandin();
        standin.setId(1L);
        standin.setStationCode("S1");

        service.addToBatch(batch, new Instance(), new InstanceModule(), new AnagStation(), new KpiB3AnalyticData(), LocalDate.now(), List.of(standin));

        assertEquals(1, batch.size());
        assertEquals("S1", batch.get(0).getStationCode());
        assertEquals(1L, batch.get(0).getOriginalStandinId());
    }

    @Test
    void testSaveBatch() {
        PagopaNumeroStandinDrilldown drilldown = new PagopaNumeroStandinDrilldown();
        when(repository.saveAll(anyList())).thenReturn(List.of(drilldown));

        int savedCount = service.saveBatch(List.of(drilldown));

        assertEquals(1, savedCount);
        verify(repository, times(1)).saveAll(anyList());
    }

    @Test
    void testFindByAnalyticDataId() {
        PagopaNumeroStandinDrilldown entity = new PagopaNumeroStandinDrilldown();
        entity.setId(1L);
        when(repository.findByKpiB3AnalyticDataId(1L)).thenReturn(List.of(entity));

        List<PagopaNumeroStandinDTO> result = service.findByAnalyticDataId(1L);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void testConvertToDTO_withPartner() {
        PagopaNumeroStandinDrilldown entity = new PagopaNumeroStandinDrilldown();

        AnagPartner partner = new AnagPartner();
        partner.setId(10L);
        partner.setName("PartnerName");
        partner.setFiscalCode("FISCAL123");

        AnagStation station = new AnagStation();
        station.setAnagPartner(partner);

        entity.setStation(station);

        PagopaNumeroStandinDTO dto = service.convertToDTO(entity);

        assertEquals(10L, dto.getPartnerId());
        assertEquals("PartnerName", dto.getPartnerName());
        assertEquals("FISCAL123", dto.getPartnerFiscalCode());
    }

    @Test
    void testDeleteAllByInstanceModuleId() {
        when(repository.deleteAllByInstanceModuleId(5L)).thenReturn(3);

        int deleted = service.deleteAllByInstanceModuleId(5L);

        assertEquals(3, deleted);
        verify(repository, times(1)).deleteAllByInstanceModuleId(5L);
    }

    @Test
    void testDeleteByInstanceModuleIdAndAnalysisDate() {
        LocalDate date = LocalDate.now();
        when(repository.deleteByInstanceModuleIdAndAnalysisDate(2L, date)).thenReturn(2);

        int deleted = service.deleteByInstanceModuleIdAndAnalysisDate(2L, date);

        assertEquals(2, deleted);
        verify(repository, times(1)).deleteByInstanceModuleIdAndAnalysisDate(2L, date);
    }
}
