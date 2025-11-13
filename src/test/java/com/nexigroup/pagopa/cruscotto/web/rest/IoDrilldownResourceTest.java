package com.nexigroup.pagopa.cruscotto.web.rest;

import com.nexigroup.pagopa.cruscotto.domain.IoDrilldown;
import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.InstanceModule;
import com.nexigroup.pagopa.cruscotto.domain.KpiC1AnalyticData;
import com.nexigroup.pagopa.cruscotto.service.IoDrilldownService;
import com.nexigroup.pagopa.cruscotto.service.mapper.IoDrilldownMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class IoDrilldownResourceTest {

    private IoDrilldownService service;
    private IoDrilldownMapper mapper;
    private IoDrilldownResource resource;

    @BeforeEach
    void setUp() {
        service = Mockito.mock(IoDrilldownService.class);
        mapper = new IoDrilldownMapper();
        resource = new IoDrilldownResource(service, mapper);
    }

    @Test
    @DisplayName("GET /api/kpi-c1-io-drilldown/{id} returns mapped DTO list when data exists")
    void testGetByAnalyticDataReturnsData() {
        Instance inst = new Instance(); inst.setId(1L);
        InstanceModule mod = new InstanceModule(); mod.setId(2L);
        KpiC1AnalyticData analytic = new KpiC1AnalyticData(); analytic.setId(10L);
        analytic.setInstance(inst); analytic.setInstanceModule(mod);
        analytic.setReferenceDate(LocalDate.now()); analytic.setData(LocalDate.now());
        analytic.setCfInstitution("ENTE_X");

        IoDrilldown row = new IoDrilldown(inst, mod, analytic, LocalDate.now(), LocalDate.now(), "ENTE_X", "PARTNER_CF",
            100L, 60L, 60.0, false);

        Mockito.when(service.findByAnalyticDataId(10L)).thenReturn(List.of(row));

        ResponseEntity<List<com.nexigroup.pagopa.cruscotto.service.dto.IoDrilldownDTO>> response = resource.getByAnalyticData(10L);
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody().get(0).getCfInstitution()).isEqualTo("ENTE_X");
        assertThat(response.getBody().get(0).getPercentage()).isEqualTo(60.0);
    }

    @Test
    @DisplayName("GET returns 404 when no data")
    void testGetByAnalyticDataNotFound() {
        Mockito.when(service.findByAnalyticDataId(99L)).thenReturn(List.of());
        org.springframework.web.server.ResponseStatusException ex = org.junit.jupiter.api.Assertions.assertThrows(
            org.springframework.web.server.ResponseStatusException.class,
            () -> resource.getByAnalyticData(99L)
        );
        assertThat(ex.getStatusCode().value()).isEqualTo(404);
    }
}
