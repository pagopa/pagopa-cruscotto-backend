package com.nexigroup.pagopa.cruscotto.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.InstanceModule;
import com.nexigroup.pagopa.cruscotto.domain.KpiB5AnalyticData;
import com.nexigroup.pagopa.cruscotto.domain.SpontaneousDrilldown;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.SpontaneousPayments;
import com.nexigroup.pagopa.cruscotto.repository.InstanceRepository;
import com.nexigroup.pagopa.cruscotto.repository.InstanceModuleRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiB5AnalyticDataRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiB5AnalyticDrillDownRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiB5DetailResultRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiB5ResultRepository;
import com.nexigroup.pagopa.cruscotto.repository.PagopaSpontaneousRepository;
import com.nexigroup.pagopa.cruscotto.repository.SpontaneousDrilldownRepository;
import com.nexigroup.pagopa.cruscotto.service.dto.PagopaSpontaneiDTO;
import com.nexigroup.pagopa.cruscotto.service.mapper.KpiB5AnalyticDataMapper;
import com.nexigroup.pagopa.cruscotto.service.mapper.KpiB5DetailResultMapper;
import com.nexigroup.pagopa.cruscotto.service.mapper.KpiB5ResultMapper;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test for KPI B5 Service Implementation focusing on snapshot functionality.
 */
@ExtendWith(MockitoExtension.class)
class KpiB5ServiceImplSnapshotTest {

    @Mock
    private KpiB5ResultRepository kpiB5ResultRepository;

    @Mock
    private KpiB5DetailResultRepository kpiB5DetailResultRepository;

    @Mock
    private KpiB5AnalyticDataRepository kpiB5AnalyticDataRepository;

    @Mock
    private KpiB5AnalyticDrillDownRepository kpiB5AnalyticDrillDownRepository;

    @Mock
    private PagopaSpontaneousRepository pagopaSpontaneousRepository;

    @Mock
    private SpontaneousDrilldownRepository spontaneousDrilldownRepository;

    @Mock
    private InstanceRepository instanceRepository;

    @Mock
    private InstanceModuleRepository instanceModuleRepository;

    @Mock
    private KpiB5ResultMapper kpiB5ResultMapper;

    @Mock
    private KpiB5DetailResultMapper kpiB5DetailResultMapper;

    @Mock
    private KpiB5AnalyticDataMapper kpiB5AnalyticDataMapper;

    @InjectMocks
    private KpiB5ServiceImpl kpiB5Service;

    private Instance testInstance;
    private InstanceModule testInstanceModule;
    private KpiB5AnalyticData testAnalyticData;

    @BeforeEach
    void setUp() {
        // Setup test data
        testInstance = new Instance();
        testInstance.setId(1L);

        testInstanceModule = new InstanceModule();
        testInstanceModule.setId(100L);
        testInstanceModule.setInstance(testInstance);

        // Create test partners with different spontaneous payment configurations
        // (These are just for reference in test setup - not used in snapshot tests)

        // Setup analytic data mock
        testAnalyticData = new KpiB5AnalyticData();
        testAnalyticData.setId(50L);
        testAnalyticData.setInstance(testInstance);
        testAnalyticData.setInstanceModule(testInstanceModule);
        testAnalyticData.setAnalysisDate(LocalDate.now());
    }

    @Test
    void testFindDrillDownByAnalyticDataId_ReturnsSnapshotData() {
        // Given - Setup snapshot data
        Long analyticDataId = 50L;

        SpontaneousDrilldown snapshot1 = new SpontaneousDrilldown();
        snapshot1.setId(101L);
        snapshot1.setInstance(testInstance);
        snapshot1.setInstanceModule(testInstanceModule);
        snapshot1.setKpiB5AnalyticData(testAnalyticData);
        snapshot1.setPartnerId(1L);
        snapshot1.setPartnerFiscalCode("12345678901");
        snapshot1.setStationCode("STATION_001");
        snapshot1.setFiscalCode("STATION_001");
        snapshot1.setSpontaneousPayment(true);

        SpontaneousDrilldown snapshot2 = new SpontaneousDrilldown();
        snapshot2.setId(102L);
        snapshot2.setInstance(testInstance);
        snapshot2.setInstanceModule(testInstanceModule);
        snapshot2.setKpiB5AnalyticData(testAnalyticData);
        snapshot2.setPartnerId(2L);
        snapshot2.setPartnerFiscalCode("10987654321");
        snapshot2.setStationCode("STATION_002");
        snapshot2.setFiscalCode("STATION_002");
        snapshot2.setSpontaneousPayment(false);

        List<SpontaneousDrilldown> snapshots = Arrays.asList(snapshot1, snapshot2);

        when(spontaneousDrilldownRepository.findByKpiB5AnalyticDataId(analyticDataId))
            .thenReturn(snapshots);

        // When
        List<PagopaSpontaneiDTO> result = kpiB5Service.findDrillDownByAnalyticDataId(analyticDataId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());

        PagopaSpontaneiDTO dto1 = result.get(0);
        assertEquals(101L, dto1.getId());
        assertEquals(analyticDataId, dto1.getKpiB5AnalyticDataId());
        assertEquals(1L, dto1.getPartnerId());
        assertEquals("12345678901", dto1.getPartnerFiscalCode());
        assertEquals("STATION_001", dto1.getStationCode());
        assertEquals("STATION_001", dto1.getFiscalCode());
        assertEquals(SpontaneousPayments.ATTIVI.getValue(), dto1.getSpontaneousPayments());

        PagopaSpontaneiDTO dto2 = result.get(1);
        assertEquals(102L, dto2.getId());
        assertEquals(analyticDataId, dto2.getKpiB5AnalyticDataId());
        assertEquals(2L, dto2.getPartnerId());
        assertEquals("10987654321", dto2.getPartnerFiscalCode());
        assertEquals("STATION_002", dto2.getStationCode());
        assertEquals("STATION_002", dto2.getFiscalCode());
        assertEquals(SpontaneousPayments.NON_ATTIVI.getValue(), dto2.getSpontaneousPayments());
    }

    @Test
    void testFindDrillDownByAnalyticDataId_EmptyResult() {
        // Given
        Long analyticDataId = 999L;
        when(spontaneousDrilldownRepository.findByKpiB5AnalyticDataId(analyticDataId))
            .thenReturn(Arrays.asList());

        // When
        List<PagopaSpontaneiDTO> result = kpiB5Service.findDrillDownByAnalyticDataId(analyticDataId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}