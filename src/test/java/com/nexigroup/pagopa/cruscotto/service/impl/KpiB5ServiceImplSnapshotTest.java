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

        // When - Test with "ALL" filter to get all records
        List<PagopaSpontaneiDTO> result = kpiB5Service.findDrillDownByAnalyticDataId(analyticDataId, "ALL");

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());

        // Find the ATTIVI record
        PagopaSpontaneiDTO dtoAttivi = result.stream()
            .filter(dto -> "ATTIVI".equals(dto.getSpontaneousPayments()))
            .findFirst()
            .orElseThrow(() -> new AssertionError("ATTIVI record not found"));
        
        assertEquals(101L, dtoAttivi.getId());
        assertEquals(analyticDataId, dtoAttivi.getKpiB5AnalyticDataId());
        assertEquals(1L, dtoAttivi.getPartnerId());
        assertEquals("12345678901", dtoAttivi.getPartnerFiscalCode());
        assertEquals("STATION_001", dtoAttivi.getStationCode());
        assertEquals("STATION_001", dtoAttivi.getFiscalCode());
        assertEquals(SpontaneousPayments.ATTIVI.getValue(), dtoAttivi.getSpontaneousPayments());

        // Find the NON_ATTIVI record
        PagopaSpontaneiDTO dtoNonAttivi = result.stream()
            .filter(dto -> "NON ATTIVI".equals(dto.getSpontaneousPayments()))
            .findFirst()
            .orElseThrow(() -> new AssertionError("NON ATTIVI record not found"));
        
        assertEquals(102L, dtoNonAttivi.getId());
        assertEquals(analyticDataId, dtoNonAttivi.getKpiB5AnalyticDataId());
        assertEquals(2L, dtoNonAttivi.getPartnerId());
        assertEquals("10987654321", dtoNonAttivi.getPartnerFiscalCode());
        assertEquals("STATION_002", dtoNonAttivi.getStationCode());
        assertEquals("STATION_002", dtoNonAttivi.getFiscalCode());
        assertEquals(SpontaneousPayments.NON_ATTIVI.getValue(), dtoNonAttivi.getSpontaneousPayments());
    }

    @Test
    void testFindDrillDownByAnalyticDataId_DefaultFilter_ReturnsOnlyNonAttivi() {
        // Given - Setup snapshot data with both ATTIVI and NON_ATTIVI
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
        snapshot1.setSpontaneousPayment(true);  // ATTIVI

        SpontaneousDrilldown snapshot2 = new SpontaneousDrilldown();
        snapshot2.setId(102L);
        snapshot2.setInstance(testInstance);
        snapshot2.setInstanceModule(testInstanceModule);
        snapshot2.setKpiB5AnalyticData(testAnalyticData);
        snapshot2.setPartnerId(2L);
        snapshot2.setPartnerFiscalCode("10987654321");
        snapshot2.setStationCode("STATION_002");
        snapshot2.setFiscalCode("STATION_002");
        snapshot2.setSpontaneousPayment(false); // NON_ATTIVI

        List<SpontaneousDrilldown> snapshots = Arrays.asList(snapshot1, snapshot2);

        when(spontaneousDrilldownRepository.findByKpiB5AnalyticDataId(analyticDataId))
            .thenReturn(snapshots);

        // When - Test with null filter (should default to NON_ATTIVI only)
        List<PagopaSpontaneiDTO> result = kpiB5Service.findDrillDownByAnalyticDataId(analyticDataId, null);

        // Then - Should return only NON_ATTIVI record
        assertNotNull(result);
        assertEquals(1, result.size());

        PagopaSpontaneiDTO dto = result.get(0);
        assertEquals(102L, dto.getId());
        assertEquals(analyticDataId, dto.getKpiB5AnalyticDataId());
        assertEquals(2L, dto.getPartnerId());
        assertEquals("10987654321", dto.getPartnerFiscalCode());
        assertEquals("STATION_002", dto.getStationCode());
        assertEquals("STATION_002", dto.getFiscalCode());
        assertEquals(SpontaneousPayments.NON_ATTIVI.getValue(), dto.getSpontaneousPayments());
    }

    @Test
    void testFindDrillDownByAnalyticDataId_EmptyResult() {
        // Given
        Long analyticDataId = 999L;
        when(spontaneousDrilldownRepository.findByKpiB5AnalyticDataId(analyticDataId))
            .thenReturn(Arrays.asList());

        // When
        List<PagopaSpontaneiDTO> result = kpiB5Service.findDrillDownByAnalyticDataId(analyticDataId, null);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}