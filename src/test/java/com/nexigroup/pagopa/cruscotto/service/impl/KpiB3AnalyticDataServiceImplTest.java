package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.*;
import com.nexigroup.pagopa.cruscotto.repository.*;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB3AnalyticDataDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KpiB3AnalyticDataServiceImplTest {

    @Mock
    private InstanceRepository instanceRepository;
    @Mock
    private InstanceModuleRepository instanceModuleRepository;
    @Mock
    private KpiB3AnalyticDataRepository kpiB3AnalyticDataRepository;
    @Mock
    private KpiB3DetailResultRepository kpiB3DetailResultRepository;
    @Mock
    private AnagStationRepository anagStationRepository;

    @InjectMocks
    private KpiB3AnalyticDataServiceImpl service;

    private Instance instance;
    private InstanceModule instanceModule;
    private KpiB3DetailResult detailResult;
    private AnagStation station;
    private KpiB3AnalyticDataDTO dto;

    @BeforeEach
    void setUp() {
        instance = new Instance();
        instance.setId(1L);
        instance.setAnalysisPeriodStartDate(LocalDate.of(2025, 1, 1));
        instance.setAnalysisPeriodEndDate(LocalDate.of(2025, 1, 31));

        instanceModule = new InstanceModule();
        instanceModule.setId(2L);

        detailResult = new KpiB3DetailResult();
        detailResult.setId(3L);
        detailResult.setAnalysisDate(LocalDate.of(2025, 1, 31));

        station = new AnagStation();
        station.setId(4L);
        station.setName("Station123");

        dto = new KpiB3AnalyticDataDTO();
        dto.setInstanceId(instance.getId());
        dto.setInstanceModuleId(instanceModule.getId());
        dto.setKpiB3DetailResultId(detailResult.getId());
        dto.setAnagStationId(station.getId());
        dto.setEventId("EVT1");
        dto.setEventType("TYPE1");
        dto.setEventTimestamp(LocalDateTime.of(2025, 1, 15, 12, 0));
        dto.setStandInCount(5);
    }

    @Test
    void save_ShouldPersistAndReturnDTO() {
        when(instanceRepository.findById(instance.getId())).thenReturn(Optional.of(instance));
        when(instanceModuleRepository.findById(instanceModule.getId())).thenReturn(Optional.of(instanceModule));
        when(kpiB3DetailResultRepository.findById(detailResult.getId())).thenReturn(Optional.of(detailResult));
        when(anagStationRepository.findById(station.getId())).thenReturn(Optional.of(station));

        KpiB3AnalyticData savedEntity = new KpiB3AnalyticData();
        savedEntity.setId(100L);
        when(kpiB3AnalyticDataRepository.save(any(KpiB3AnalyticData.class))).thenReturn(savedEntity);

        KpiB3AnalyticDataDTO result = service.save(dto);

        assertThat(result.getId()).isEqualTo(100L);
        verify(kpiB3AnalyticDataRepository).save(any(KpiB3AnalyticData.class));
    }

    @Test
    void saveAll_ShouldPersistAllDTOs() {
        when(instanceRepository.findById(instance.getId())).thenReturn(Optional.of(instance));
        when(instanceModuleRepository.findById(instanceModule.getId())).thenReturn(Optional.of(instanceModule));
        when(kpiB3DetailResultRepository.findById(detailResult.getId())).thenReturn(Optional.of(detailResult));
        when(anagStationRepository.findById(station.getId())).thenReturn(Optional.of(station));

        service.saveAll(Arrays.asList(dto, dto));

        verify(kpiB3AnalyticDataRepository, times(1)).saveAll(anyList());
    }

    @Test
    void deleteAllByInstanceModule_ShouldCallRepository() {
        when(kpiB3AnalyticDataRepository.deleteAllByInstanceModuleId(2L)).thenReturn(3);

        int deletedCount = service.deleteAllByInstanceModule(2L);

        assertThat(deletedCount).isEqualTo(3);
        verify(kpiB3AnalyticDataRepository).deleteAllByInstanceModuleId(2L);
    }

    @Test
    void findByDetailResultId_ShouldReturnDTOList() {
        KpiB3AnalyticData entity = new KpiB3AnalyticData();
        entity.setId(10L);
        entity.setInstance(instance);
        entity.setInstanceModule(instanceModule);
        entity.setAnagStation(station);
        entity.setKpiB3DetailResult(detailResult);
        entity.setEventId("EVT1");
        entity.setEventType("TYPE1");
        entity.setEventTimestamp(LocalDateTime.of(2025, 1, 15, 12, 0));
        entity.setStandInCount(5);

        when(kpiB3AnalyticDataRepository.findAllByDetailResultIdOrderByEventTimestampDesc(3L))
            .thenReturn(List.of(entity));

        List<KpiB3AnalyticDataDTO> result = service.findByDetailResultId(3L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(10L);
        assertThat(result.get(0).getAnalysisDate()).isEqualTo(detailResult.getAnalysisDate());
        assertThat(result.get(0).getAnalysisPeriod()).isEqualTo("01/01/2025 - 31/01/2025");
        assertThat(result.get(0).getStationFiscalCode()).isEqualTo("Station123");
    }

    @Test
    void save_ShouldThrowException_WhenInstanceNotFound() {
        when(instanceRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.save(dto));
    }

    @Test
    void save_ShouldThrowException_WhenInstanceModuleNotFound() {
        when(instanceRepository.findById(instance.getId())).thenReturn(Optional.of(instance));
        when(instanceModuleRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.save(dto));
    }

    @Test
    void save_ShouldThrowException_WhenDetailResultNotFound() {
        when(instanceRepository.findById(instance.getId())).thenReturn(Optional.of(instance));
        when(instanceModuleRepository.findById(instanceModule.getId())).thenReturn(Optional.of(instanceModule));
        when(kpiB3DetailResultRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.save(dto));
    }

    @Test
    void save_ShouldThrowException_WhenStationNotFound() {
        when(instanceRepository.findById(instance.getId())).thenReturn(Optional.of(instance));
        when(instanceModuleRepository.findById(instanceModule.getId())).thenReturn(Optional.of(instanceModule));
        when(kpiB3DetailResultRepository.findById(detailResult.getId())).thenReturn(Optional.of(detailResult));
        when(anagStationRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.save(dto));
    }
}
