package com.nexigroup.pagopa.cruscotto.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nexigroup.pagopa.cruscotto.domain.AnagStation;
import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.InstanceModule;
import com.nexigroup.pagopa.cruscotto.domain.PagoPaPaymentReceiptDrilldown;
import com.nexigroup.pagopa.cruscotto.repository.AnagStationRepository;
import com.nexigroup.pagopa.cruscotto.repository.InstanceModuleRepository;
import com.nexigroup.pagopa.cruscotto.repository.InstanceRepository;
import com.nexigroup.pagopa.cruscotto.repository.PagoPaPaymentReceiptDrilldownRepository;
import com.nexigroup.pagopa.cruscotto.service.dto.PagoPaPaymentReceiptDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.PagoPaPaymentReceiptDrilldownDTO;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PagoPaPaymentReceiptDrilldownServiceImplTest {

    @Mock
    private PagoPaPaymentReceiptDrilldownRepository drilldownRepository;

    @Mock
    private InstanceRepository instanceRepository;

    @Mock
    private InstanceModuleRepository instanceModuleRepository;

    @Mock
    private AnagStationRepository stationRepository;

    private PagoPaPaymentReceiptDrilldownServiceImpl drilldownService;

    private Instance instance;
    private InstanceModule instanceModule;
    private AnagStation station;

    @BeforeEach
    void setUp() {
        drilldownService = new PagoPaPaymentReceiptDrilldownServiceImpl(
            drilldownRepository,
            instanceRepository,
            instanceModuleRepository,
            stationRepository
        );

        // Create test entities
        instance = new Instance();
        instance.setId(1L);

        instanceModule = new InstanceModule();
        instanceModule.setId(1L);

        station = new AnagStation();
        station.setId(1L);
        station.setName("Test Station");
    }

    @Test
    void saveQuarterHourAggregatedData_shouldCreateAndSave96Slots() {
        // Given
        Long instanceId = 1L;
        Long instanceModuleId = 1L;
        Long stationId = 1L;
        LocalDate evaluationDate = LocalDate.of(2024, 1, 15);
        LocalDate analysisDate = LocalDate.now();

        when(instanceRepository.findById(instanceId)).thenReturn(Optional.of(instance));
        when(instanceModuleRepository.findById(instanceModuleId)).thenReturn(Optional.of(instanceModule));
        when(stationRepository.findById(stationId)).thenReturn(Optional.of(station));

        // Create sample raw data
        List<PagoPaPaymentReceiptDTO> rawData = createSampleRawData(evaluationDate);

        // When
        drilldownService.saveQuarterHourAggregatedData(
            instanceId,
            instanceModuleId,
            stationId,
            evaluationDate,
            analysisDate,
            rawData
        );

        // Then
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<PagoPaPaymentReceiptDrilldown>> captor = ArgumentCaptor.forClass(List.class);
        verify(drilldownRepository).saveAll(captor.capture());

        List<PagoPaPaymentReceiptDrilldown> savedRecords = captor.getValue();
        assertThat(savedRecords).hasSize(96); // 24 hours * 4 quarters = 96 slots

        // Verify first slot (00:00-00:15)
        PagoPaPaymentReceiptDrilldown firstSlot = savedRecords.get(0);
        assertThat(firstSlot.getInstance()).isEqualTo(instance);
        assertThat(firstSlot.getInstanceModule()).isEqualTo(instanceModule);
        assertThat(firstSlot.getStation()).isEqualTo(station);
        assertThat(firstSlot.getEvaluationDate()).isEqualTo(evaluationDate);
        assertThat(firstSlot.getAnalysisDate()).isEqualTo(analysisDate);
        
        // Check time slot
        Instant expectedStart = evaluationDate.atStartOfDay().atZone(ZoneOffset.systemDefault()).toInstant();
        Instant expectedEnd = evaluationDate.atStartOfDay().plusMinutes(15).atZone(ZoneOffset.systemDefault()).toInstant();
        assertThat(firstSlot.getStartTime()).isEqualTo(expectedStart);
        assertThat(firstSlot.getEndTime()).isEqualTo(expectedEnd);
    }

    @Test
    void getDrilldownData_shouldReturnMappedDTOs() {
        // Given
        Long instanceId = 1L;
        Long stationId = 1L;
        LocalDate evaluationDate = LocalDate.of(2024, 1, 15);

        List<PagoPaPaymentReceiptDrilldown> mockRecords = createSampleDrilldownRecords(evaluationDate);
        when(drilldownRepository.findByInstanceIdAndStationIdAndEvaluationDate(instanceId, stationId, evaluationDate))
            .thenReturn(mockRecords);

        // When
        List<PagoPaPaymentReceiptDrilldownDTO> result = drilldownService.getDrilldownData(instanceId, stationId, evaluationDate);

        // Then
        assertThat(result).hasSize(mockRecords.size());
        
        PagoPaPaymentReceiptDrilldownDTO firstDto = result.get(0);
        PagoPaPaymentReceiptDrilldown firstRecord = mockRecords.get(0);
        
        assertThat(firstDto.getId()).isEqualTo(firstRecord.getId());
        assertThat(firstDto.getInstanceId()).isEqualTo(firstRecord.getInstance().getId());
        assertThat(firstDto.getStationId()).isEqualTo(firstRecord.getStation().getId());
        assertThat(firstDto.getStationName()).isEqualTo(firstRecord.getStation().getName());
        assertThat(firstDto.getTotRes()).isEqualTo(firstRecord.getTotRes());
        assertThat(firstDto.getResOk()).isEqualTo(firstRecord.getResOk());
        assertThat(firstDto.getResKo()).isEqualTo(firstRecord.getResKo());
        assertThat(firstDto.getTimeSlot()).isEqualTo("00:00-00:15");
    }

    @Test
    void deleteAllByInstanceModuleId_shouldCallRepository() {
        // Given
        Long instanceModuleId = 1L;
        int expectedDeletedCount = 96;
        when(drilldownRepository.deleteAllByInstanceModuleId(instanceModuleId)).thenReturn(expectedDeletedCount);

        // When
        int result = drilldownService.deleteAllByInstanceModuleId(instanceModuleId);

        // Then
        assertThat(result).isEqualTo(expectedDeletedCount);
        verify(drilldownRepository).deleteAllByInstanceModuleId(instanceModuleId);
    }

    private List<PagoPaPaymentReceiptDTO> createSampleRawData(LocalDate date) {
        List<PagoPaPaymentReceiptDTO> rawData = new ArrayList<>();
        
        // Create a record for 00:05-00:10 time slot
        PagoPaPaymentReceiptDTO record = new PagoPaPaymentReceiptDTO();
        record.setStartDate(date.atTime(0, 5).atZone(ZoneOffset.systemDefault()).toInstant());
        record.setEndDate(date.atTime(0, 10).atZone(ZoneOffset.systemDefault()).toInstant());
        record.setTotRes(100L);
        record.setResOk(95L);
        record.setResKo(5L);
        rawData.add(record);
        
        return rawData;
    }

    private List<PagoPaPaymentReceiptDrilldown> createSampleDrilldownRecords(LocalDate date) {
        List<PagoPaPaymentReceiptDrilldown> records = new ArrayList<>();
        
        PagoPaPaymentReceiptDrilldown record = new PagoPaPaymentReceiptDrilldown();
        record.setId(1L);
        record.setInstance(instance);
        record.setInstanceModule(instanceModule);
        record.setStation(station);
        record.setAnalysisDate(LocalDate.now());
        record.setEvaluationDate(date);
        record.setStartTime(date.atStartOfDay().atZone(ZoneOffset.systemDefault()).toInstant());
        record.setEndTime(date.atStartOfDay().plusMinutes(15).atZone(ZoneOffset.systemDefault()).toInstant());
        record.setTotRes(100L);
        record.setResOk(95L);
        record.setResKo(5L);
        
        records.add(record);
        return records;
    }
}