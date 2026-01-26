package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.*;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.repository.*;
import com.nexigroup.pagopa.cruscotto.service.mapper.KpiB5AnalyticDataMapper;
import com.nexigroup.pagopa.cruscotto.service.mapper.KpiB5DetailResultMapper;
import com.nexigroup.pagopa.cruscotto.service.mapper.KpiB5ResultMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KpiB5ServiceImplTest {

    @InjectMocks
    private KpiB5ServiceImpl kpiB5Service;

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
    private KpiConfigurationRepository kpiConfigurationRepository;
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

    @Test
    void testCalculateKpiB5_OK() {
        Long instanceId = 1L;
        Long instanceModuleId = 2L;
        LocalDate analysisDate = LocalDate.now();

        // Mock Instance and InstanceModule
        Instance instance = new Instance();
        instance.setId(instanceId);
        InstanceModule module = new InstanceModule();
        module.setId(instanceModuleId);
        module.setInstance(instance);
        AnagPartner partner = new AnagPartner();
        partner.setFiscalCode("ABC123");
        module.setInstance(instance);
        instance.setPartner(partner);

        when(instanceModuleRepository.findById(instanceModuleId)).thenReturn(Optional.of(module));
        when(instanceRepository.findById(instanceId)).thenReturn(Optional.of(instance));

        // Mock KPI Configuration
        KpiConfiguration config = new KpiConfiguration();
        config.setEligibilityThreshold(50.0);
        config.setTolerance(10.0);
        when(kpiConfigurationRepository.findByModuleCode(ModuleCode.B5.code)).thenReturn(Optional.of(config));

        // Mock PagopaSpontaneous
        PagopaSpontaneous p1 = new PagopaSpontaneous();
        p1.setCfPartner("ABC123");
        p1.setStation("Station1");
        p1.setSpontaneousPayment(false);

        PagopaSpontaneous p2 = new PagopaSpontaneous();
        p2.setCfPartner("ABC123");
        p2.setStation("Station2");
        p2.setSpontaneousPayment(true);

        when(pagopaSpontaneousRepository.findByCfPartner("ABC123")).thenReturn(List.of(p1, p2));

        // Mock saving of results
        KpiB5Result mainResult = new KpiB5Result();
        mainResult.setInstance(instance);
        mainResult.setInstanceModule(module);
        mainResult.setAnalysisDate(analysisDate);
        mainResult.setThresholdIndex(BigDecimal.valueOf(50));
        mainResult.setToleranceIndex(BigDecimal.valueOf(10));
        mainResult.setOutcome(OutcomeStatus.OK);
        when(kpiB5ResultRepository.save(any())).thenReturn(mainResult);

        KpiB5DetailResult detailResult = new KpiB5DetailResult();
        detailResult.setOutcome(OutcomeStatus.OK);
        when(kpiB5DetailResultRepository.save(any())).thenReturn(detailResult);

        KpiB5AnalyticData analyticData = new KpiB5AnalyticData();
        analyticData.setOutcome(OutcomeStatus.OK);
        when(kpiB5AnalyticDataRepository.save(any())).thenReturn(analyticData);

        OutcomeStatus result = kpiB5Service.calculateKpiB5(instanceId, instanceModuleId, analysisDate);

        assertEquals(OutcomeStatus.OK, result);

        // Verify repositories were called
        verify(kpiB5ResultRepository, atLeastOnce()).save(any());
        verify(kpiB5DetailResultRepository, atLeastOnce()).save(any());
        verify(kpiB5AnalyticDataRepository, atLeastOnce()).save(any());
        verify(spontaneousDrilldownRepository, atLeastOnce()).save(any());
    }

    @Test
    void testCalculateKpiB5_KO_dueToThreshold() {
        Long instanceId = 1L;
        Long instanceModuleId = 2L;
        LocalDate analysisDate = LocalDate.now();

        Instance instance = new Instance();
        instance.setId(instanceId);
        InstanceModule module = new InstanceModule();
        module.setId(instanceModuleId);
        module.setInstance(instance);
        AnagPartner partner = new AnagPartner();
        partner.setFiscalCode("XYZ987");
        module.setInstance(instance);
        instance.setPartner(partner);

        when(instanceModuleRepository.findById(instanceModuleId)).thenReturn(Optional.of(module));
        when(instanceRepository.findById(instanceId)).thenReturn(Optional.of(instance));

        KpiConfiguration config = new KpiConfiguration();
        config.setEligibilityThreshold(10.0);
        config.setTolerance(0.0);
        when(kpiConfigurationRepository.findByModuleCode(ModuleCode.B5.code)).thenReturn(Optional.of(config));

        PagopaSpontaneous p1 = new PagopaSpontaneous();
        p1.setCfPartner("XYZ987");
        p1.setStation("Station1");
        p1.setSpontaneousPayment(false);

        PagopaSpontaneous p2 = new PagopaSpontaneous();
        p2.setCfPartner("XYZ987");
        p2.setStation("Station2");
        p2.setSpontaneousPayment(false);

        when(pagopaSpontaneousRepository.findByCfPartner("XYZ987")).thenReturn(List.of(p1, p2));

        KpiB5Result mainResult = new KpiB5Result();
        mainResult.setOutcome(OutcomeStatus.KO);
        when(kpiB5ResultRepository.save(any())).thenReturn(mainResult);

        OutcomeStatus result = kpiB5Service.calculateKpiB5(instanceId, instanceModuleId, analysisDate);

        assertEquals(OutcomeStatus.KO, result);
    }
}
