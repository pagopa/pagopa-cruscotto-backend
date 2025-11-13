package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.repository.PagopaIORepository;
import com.nexigroup.pagopa.cruscotto.service.*;
import com.nexigroup.pagopa.cruscotto.service.dto.PagoPaIODTO;
import com.nexigroup.pagopa.cruscotto.service.mapper.PagopaIOMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link KpiC1DataServiceImpl} focused on the compliance aggregation logic.
 *
 * These tests validate that:
 * 1. Aggregated percentage per entity uses SUM(messages)/SUM(positions) over the period.
 * 2. Entity compliance requires percentage >= requiredMessagePercentage (100 - tolerance margin already precomputed upstream).
 * 3. Total compliance compares percentage of compliant entities vs entityThreshold.
 */
class KpiC1DataServiceImplTest {

    private KpiC1DataServiceImpl service;

    // Dependencies (mocked as they are not used by the tested methods)
    private PagopaIORepository pagopaIORepository;
    private PagopaIOMapper pagopaIOMapper;
    private KpiC1ResultService kpiC1ResultService;
    private KpiC1DetailResultService kpiC1DetailResultService;
    private KpiC1AnalyticDataService kpiC1AnalyticDataService;
    private AnagPartnerService anagPartnerService;
    private AnagInstitutionService anagInstitutionService;
    private IoDrilldownService ioDrilldownService;

    @BeforeEach
    void setUp() {
        pagopaIORepository = Mockito.mock(PagopaIORepository.class);
        pagopaIOMapper = Mockito.mock(PagopaIOMapper.class);
        kpiC1ResultService = Mockito.mock(KpiC1ResultService.class);
        kpiC1DetailResultService = Mockito.mock(KpiC1DetailResultService.class);
        kpiC1AnalyticDataService = Mockito.mock(KpiC1AnalyticDataService.class);
        anagPartnerService = Mockito.mock(AnagPartnerService.class);
        anagInstitutionService = Mockito.mock(AnagInstitutionService.class);
        ioDrilldownService = Mockito.mock(IoDrilldownService.class);
        KpiC1DataService kpiC1DataService = Mockito.mock(KpiC1DataService.class);
        service = new KpiC1DataServiceImpl(
            pagopaIORepository,
            pagopaIOMapper,
            kpiC1ResultService,
            kpiC1DetailResultService,
            kpiC1AnalyticDataService,
            anagPartnerService,
            anagInstitutionService,
            ioDrilldownService,
            kpiC1DataService
        );
    }

    private PagoPaIODTO dto(String ente, LocalDate date, int pos, int msg) {
        PagoPaIODTO dto = new PagoPaIODTO();
        dto.setEnte(ente);
        dto.setData(date);
        dto.setNumeroPosizioni(pos);
        dto.setNumeroMessaggi(msg);
        return dto;
    }

    @Test
    @DisplayName("Aggregated compliance: one entity 100%, one entity 75% with required=100 -> only first compliant")
    void testCalculateEntityComplianceAggregation() {
        // Required message percentage after tolerance application (e.g. tolerance=0 => required = 100)
        double requiredMessagePercentage = 100.0;

        // ENTE_A: two days (50/50 + 50/50) -> 100/100 = 100% (compliant)
        // ENTE_B: two days (120/90 + 80/60) -> total positions 200, messages 150 => 75% (not compliant)
        List<PagoPaIODTO> data = List.of(
            dto("ENTE_A", LocalDate.of(2025,1,1), 50, 50),
            dto("ENTE_A", LocalDate.of(2025,1,2), 50, 50),
            dto("ENTE_B", LocalDate.of(2025,1,1), 120, 90),
            dto("ENTE_B", LocalDate.of(2025,1,2), 80, 60)
        );

        Map<String, Boolean> compliance = service.calculateEntityCompliance(data, requiredMessagePercentage);

        assertThat(compliance).hasSize(2);
        assertThat(compliance.get("ENTE_A")).isTrue();
        assertThat(compliance.get("ENTE_B")).isFalse();
    }

    @Test
    @DisplayName("Total compliance true when compliant entities percentage meets entity threshold")
    void testCalculateTotalComplianceTrue() {
        double requiredMessagePercentage = 100.0; // already tolerance-adjusted
        double entityThreshold = 50.0; // need >=50% compliant entities

        List<PagoPaIODTO> data = List.of(
            // ENTE_A compliant 100%
            dto("ENTE_A", LocalDate.of(2025,1,1), 40, 40),
            // ENTE_B not compliant 50/40 -> 80% (<100 required)
            dto("ENTE_B", LocalDate.of(2025,1,1), 50, 40)
        );

        boolean total = service.calculateTotalCompliance(data, entityThreshold, requiredMessagePercentage);
        assertThat(total).as("1 of 2 entities compliant => 50% >= 50 threshold").isTrue();
    }

    @Test
    @DisplayName("Total compliance false when compliant entities percentage below entity threshold")
    void testCalculateTotalComplianceFalse() {
        double requiredMessagePercentage = 100.0;
        double entityThreshold = 60.0; // need >=60% compliant entities

        List<PagoPaIODTO> data = List.of(
            dto("ENTE_A", LocalDate.of(2025,1,1), 40, 40), // compliant
            dto("ENTE_B", LocalDate.of(2025,1,1), 50, 40)  // not compliant
        );

        boolean total = service.calculateTotalCompliance(data, entityThreshold, requiredMessagePercentage);
        assertThat(total).as("1 of 2 entities compliant => 50% < 60 threshold").isFalse();
    }

    @Test
    @DisplayName("Edge cases: zero positions with zero messages => 0%, zero positions with messages => 100%")
    void testEdgeCasesZeroPositionsLogic() {
        double requiredMessagePercentage = 100.0;

        List<PagoPaIODTO> data = List.of(
            dto("ENTE_ZERO", LocalDate.of(2025,1,1), 0, 0),      // 0% -> not compliant
            dto("ENTE_MSG", LocalDate.of(2025,1,1), 0, 5)       // 100% -> compliant
        );

        Map<String, Boolean> compliance = service.calculateEntityCompliance(data, requiredMessagePercentage);
        assertThat(compliance.get("ENTE_ZERO")).isFalse();
        assertThat(compliance.get("ENTE_MSG")).isTrue();
    }
}
