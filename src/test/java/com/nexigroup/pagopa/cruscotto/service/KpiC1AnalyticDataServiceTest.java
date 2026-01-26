package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.InstanceModule;
import com.nexigroup.pagopa.cruscotto.domain.KpiC1AnalyticData;
import com.nexigroup.pagopa.cruscotto.repository.KpiC1AnalyticDataRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiC1DetailResultRepository;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiC1AnalyticDataDTO;
import com.nexigroup.pagopa.cruscotto.service.mapper.KpiC1AnalyticDataMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class KpiC1AnalyticDataServiceTest {

    @Mock
    private KpiC1AnalyticDataRepository kpiC1AnalyticDataRepository;

    @Mock
    private KpiC1AnalyticDataMapper kpiC1AnalyticDataMapper;

    @Mock
    private KpiC1DetailResultRepository kpiC1DetailResultRepository;

    @InjectMocks
    private KpiC1AnalyticDataService service;

    private KpiC1AnalyticData sampleData;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        sampleData = new KpiC1AnalyticData();
        sampleData.setId(1L);
        sampleData.setData(LocalDate.of(2025, 12, 1));
        sampleData.setMessageNumber(100L);
        sampleData.setPositionNumber(50L);
        sampleData.setInstitutionCount(10);
    }

    @Test
    void testSave() {
        when(kpiC1AnalyticDataRepository.save(sampleData)).thenReturn(sampleData);

        KpiC1AnalyticData result = service.save(sampleData);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(kpiC1AnalyticDataRepository, times(1)).save(sampleData);
    }

    @Test
    void testSaveAll() {
        List<KpiC1AnalyticData> list = List.of(sampleData);
        when(kpiC1AnalyticDataRepository.saveAll(list)).thenReturn(list);

        List<KpiC1AnalyticData> result = service.saveAll(list);

        assertEquals(1, result.size());
        verify(kpiC1AnalyticDataRepository, times(1)).saveAll(list);
    }

    @Test
    void testFindByInstanceIdAndReferenceDate() {
        List<KpiC1AnalyticData> list = List.of(sampleData);
        when(kpiC1AnalyticDataRepository.findByInstanceIdAndReferenceDate(1L, sampleData.getData()))
            .thenReturn(list);

        List<KpiC1AnalyticData> result = service.findByInstanceIdAndReferenceDate(1L, sampleData.getData());

        assertEquals(1, result.size());
        verify(kpiC1AnalyticDataRepository, times(1))
            .findByInstanceIdAndReferenceDate(1L, sampleData.getData());
    }

    @Test
    void testFindByData() {
        List<KpiC1AnalyticData> list = List.of(sampleData);
        when(kpiC1AnalyticDataRepository.findByData(sampleData.getData())).thenReturn(list);

        List<KpiC1AnalyticData> result = service.findByData(sampleData.getData());

        assertEquals(1, result.size());
        verify(kpiC1AnalyticDataRepository, times(1)).findByData(sampleData.getData());
    }

    @Test
    void testDeleteAll() {
        doNothing().when(kpiC1AnalyticDataRepository).deleteAll();

        service.deleteAll();

        verify(kpiC1AnalyticDataRepository, times(1)).deleteAll();
    }

    @Test
    void testDeleteByReferenceDateBefore() {
        LocalDate cutoff = LocalDate.of(2025, 1, 1);
        doNothing().when(kpiC1AnalyticDataRepository).deleteByReferenceDateBefore(cutoff);

        service.deleteByReferenceDateBefore(cutoff);

        verify(kpiC1AnalyticDataRepository, times(1)).deleteByReferenceDateBefore(cutoff);
    }

    @Test
    void testFindByDetailResultIdEmpty() {
        when(kpiC1AnalyticDataRepository.findByDetailResultId(1L)).thenReturn(Collections.emptyList());

        List<KpiC1AnalyticDataDTO> result = service.findByDetailResultId(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    void testFindByDetailResultIdWithData() {
        // Prepare sample data
        KpiC1AnalyticData data = sampleData;
        data.setReferenceDate(LocalDate.of(2025, 12, 2));

        // Mock the instance
        Instance instance = new Instance();
        instance.setId(1L);
        data.setInstance(instance);

        // Mock the instance module
        InstanceModule instanceModule = new InstanceModule();
        instanceModule.setId(1L); // Set an ID for testing
        data.setInstanceModule(instanceModule);

        // Mock repository to return analytic data
        when(kpiC1AnalyticDataRepository.findByDetailResultId(1L))
            .thenReturn(List.of(data));

        // Mock the detail result for the period and threshold
        var mockDetailResult = mock(com.nexigroup.pagopa.cruscotto.domain.KpiC1DetailResult.class);
        when(mockDetailResult.getEvaluationStartDate()).thenReturn(LocalDate.of(2025, 12, 1));
        when(mockDetailResult.getEvaluationEndDate()).thenReturn(LocalDate.of(2025, 12, 31));
        when(mockDetailResult.getConfiguredThreshold()).thenReturn(BigDecimal.valueOf(90.0));

        when(kpiC1DetailResultRepository.findById(1L))
            .thenReturn(Optional.of(mockDetailResult));

        // Execute service method
        List<KpiC1AnalyticDataDTO> result = service.findByDetailResultId(1L);

        // Verify results
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(1L, result.get(0).getInstanceId());
        assertEquals(1L, result.get(0).getInstanceModuleId());
        assertEquals(50L, result.get(0).getPositionsCount());
        assertEquals(100L, result.get(0).getMessagesCount());
    }

}
