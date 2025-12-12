package com.nexigroup.pagopa.cruscotto.web.rest;

import com.nexigroup.pagopa.cruscotto.service.*;
import com.nexigroup.pagopa.cruscotto.service.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class KpiAnalyticDataResourceTest {

    @Mock
    private KpiA1AnalyticDataService kpiA1AnalyticDataService;

    @Mock
    private KpiA2AnalyticDataService kpiA2AnalyticDataService;

    @Mock
    private KpiB1AnalyticDataService kpiB1AnalyticDataService;

    @Mock
    private KpiB2AnalyticDataService kpiB2AnalyticDataService;

    @Mock
    private KpiB9AnalyticDataService kpiB9AnalyticDataService;

    @Mock
    private KpiB3AnalyticDataService kpiB3AnalyticDataService;

    @Mock
    private KpiB4AnalyticDataService kpiB4AnalyticDataService;

    @Mock
    private KpiB5Service kpiB5Service;

    @Mock
    private KpiB8AnalyticDataService kpiB8AnalyticDataService;

    @Mock
    private KpiC1AnalyticDataService kpiC1AnalyticDataService;

    @Mock
    private KpiC2AnalyticDataService kpiC2AnalyticDataService;

    @Mock
    private GenericKpiAnalyticDataService genericKpiAnalyticDataService;

    @InjectMocks
    private KpiAnalyticDataResource resource;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetKpiA1AnalyticDataResults() {
        Long detailResultId = 1L;
        List<KpiA1AnalyticDataDTO> mockData = List.of(new KpiA1AnalyticDataDTO());
        when(kpiA1AnalyticDataService.findByDetailResultId(detailResultId)).thenReturn(mockData);

        ResponseEntity<List<KpiA1AnalyticDataDTO>> response = resource.getKpiA1AnalyticDataResults(detailResultId);

        assertThat(response.getBody()).isEqualTo(mockData);
        verify(kpiA1AnalyticDataService).findByDetailResultId(detailResultId);
    }

    @Test
    void testGetKpiA2AnalyticDataResults() {
        Long detailResultId = 2L;
        List<KpiA2AnalyticDataDTO> mockData = List.of(new KpiA2AnalyticDataDTO());
        when(kpiA2AnalyticDataService.findByDetailResultId(detailResultId)).thenReturn(mockData);

        ResponseEntity<List<KpiA2AnalyticDataDTO>> response = resource.getKpiA2AnalyticDataResults(detailResultId);

        assertThat(response.getBody()).isEqualTo(mockData);
        verify(kpiA2AnalyticDataService).findByDetailResultId(detailResultId);
    }

    @Test
    void testGetKpiB1AnalyticDataResults() {
        Long detailResultId = 3L;
        List<KpiB1AnalyticDataDTO> mockData = List.of(new KpiB1AnalyticDataDTO());
        when(kpiB1AnalyticDataService.findByDetailResultId(detailResultId)).thenReturn(mockData);

        ResponseEntity<List<KpiB1AnalyticDataDTO>> response = resource.getKpiB1AnalyticDataResults(detailResultId);

        assertThat(response.getBody()).isEqualTo(mockData);
        verify(kpiB1AnalyticDataService).findByDetailResultId(detailResultId);
    }

    @Test
    void testGetKpiC1AnalyticDataResults() {
        Long detailResultId = 10L;
        List<KpiC1AnalyticDataDTO> mockData = List.of(new KpiC1AnalyticDataDTO());
        when(kpiC1AnalyticDataService.findByDetailResultId(detailResultId)).thenReturn(mockData);

        ResponseEntity<List<KpiC1AnalyticDataDTO>> response = resource.getKpiC1AnalyticDataResults(detailResultId);

        assertThat(response.getBody()).isEqualTo(mockData);
        verify(kpiC1AnalyticDataService).findByDetailResultId(detailResultId);
    }

}
