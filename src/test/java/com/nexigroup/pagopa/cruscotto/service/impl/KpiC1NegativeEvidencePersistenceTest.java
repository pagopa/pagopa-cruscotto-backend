package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.repository.PagopaIORepository;
import com.nexigroup.pagopa.cruscotto.service.*;
import com.nexigroup.pagopa.cruscotto.service.dto.PagoPaIODTO;
import com.nexigroup.pagopa.cruscotto.service.dto.PartnerIdentificationDTO;
import com.nexigroup.pagopa.cruscotto.service.filter.InstitutionFilter;
import com.nexigroup.pagopa.cruscotto.service.mapper.PagopaIOMapper;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Test that verifies negative evidences are persisted via IoDrilldownService when below threshold.
 */
class KpiC1NegativeEvidencePersistenceTest {

    private KpiC1DataServiceImpl service;
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
        service = new KpiC1DataServiceImpl(pagopaIORepository, pagopaIOMapper, kpiC1ResultService,
            kpiC1DetailResultService, kpiC1AnalyticDataService, anagPartnerService, anagInstitutionService, ioDrilldownService);
    }

    private PagoPaIODTO dto(String ente, LocalDate date, int pos, int msg, String cfPartner) {
        PagoPaIODTO d = new PagoPaIODTO();
        d.setEnte(ente);
        d.setData(date);
        d.setNumeroPosizioni(pos);
        d.setNumeroMessaggi(msg);
        d.setCfPartner(cfPartner);
        return d;
    }

    @Test
    @DisplayName("Negative evidences are captured when percentage below required threshold")
    void testNegativeEvidenceSnapshot() {
        // Configuration: tolerance=5 -> requiredMessagePercentage = 95
        var instance = new com.nexigroup.pagopa.cruscotto.service.dto.InstanceDTO();
        instance.setId(1L);
        instance.setPartnerFiscalCode("PARTNER_CF");
        instance.setAnalysisPeriodStartDate(LocalDate.of(2025,1,1));
        instance.setAnalysisPeriodEndDate(LocalDate.of(2025,1,2));

        var module = new com.nexigroup.pagopa.cruscotto.service.dto.InstanceModuleDTO();
        module.setId(2L);

        var conf = new com.nexigroup.pagopa.cruscotto.service.dto.KpiConfigurationDTO();
        conf.setTolerance(5.0); // required = 95%
        conf.setEligibilityThreshold(50.0);
        conf.setEvaluationType(com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType.MESE);

        // ENTE_LOW: 100 positions, 60 messages -> 60% < 95% -> negative evidence
        // ENTE_OK: 100 positions, 100 messages -> 100% OK
        List<PagoPaIODTO> ioData = List.of(
            dto("ENTE_LOW", LocalDate.of(2025,1,1), 100, 60, "PARTNER_CF"),
            dto("ENTE_OK", LocalDate.of(2025,1,1), 100, 100, "PARTNER_CF")
        );

        // Simulate repository first access path
        when(pagopaIORepository.existsByCfPartner("PARTNER_CF")).thenReturn(true);
        when(pagopaIORepository.findByCfPartnerAndDateRange(eq("PARTNER_CF"), any(), any()))
            .thenReturn(ioData.stream().map(d -> new com.nexigroup.pagopa.cruscotto.domain.PagopaIO(d.getCfPartner(), d.getEnte(), d.getData(),
                d.getNumeroPosizioni().longValue(), d.getNumeroMessaggi().longValue())).toList());
    when(pagopaIOMapper.toDto(Mockito.any(com.nexigroup.pagopa.cruscotto.domain.PagopaIO.class))).thenAnswer(inv -> {
            com.nexigroup.pagopa.cruscotto.domain.PagopaIO src = inv.getArgument(0);
            PagoPaIODTO out = new PagoPaIODTO();
            out.setCfPartner(src.getCfPartner());
            out.setEnte(src.getCfInstitution());
            out.setData(src.getDate());
            out.setNumeroPosizioni(src.getPositionNumber().intValue());
            out.setNumeroMessaggi(src.getMessageNumber().intValue());
            return out;
        });

        // Stub partner + institutions
        var partnerDto = new com.nexigroup.pagopa.cruscotto.service.dto.AnagPartnerDTO();
    var partnerId = new PartnerIdentificationDTO();
        partnerId.setId(10L);
        partnerDto.setPartnerIdentification(partnerId);
        when(anagPartnerService.findOneByFiscalCode("PARTNER_CF")).thenReturn(java.util.Optional.of(partnerDto));
        // Avoid ambiguity between overloaded findAll methods by specifying the filter class explicitly
        when(anagInstitutionService.findAll(any(InstitutionFilter.class), any()))
            .thenReturn(new org.springframework.data.domain.PageImpl<>(List.of()));

        // Capture negative evidences saved
    ArgumentCaptor<List<com.nexigroup.pagopa.cruscotto.domain.IoDrilldown>> captor = ArgumentCaptor.forClass((Class) List.class);
        when(ioDrilldownService.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

    OutcomeStatus outcome = service.executeKpiC1Calculation(instance, module, conf, LocalDate.of(2025,1,3));
    // Con nuova logica: outcome OK perché percentuale enti compliant (1/2 = 50%) soddisfa institutionTolerance=50% anche se esistono evidenze negative
    assertThat(outcome).isEqualTo(OutcomeStatus.OK);

    verify(ioDrilldownService, atLeastOnce()).saveAll(any());
    List<com.nexigroup.pagopa.cruscotto.domain.IoDrilldown> saved = captured.get();
        // Nuova logica: giornata con almeno un KO => tutte le righe del giorno in drilldown (2)
        assertThat(saved).hasSize(2);
        // Verifica presenza ente KO e ente OK
        assertThat(saved.stream().anyMatch(d -> d.getCfInstitution().equals("ENTE_LOW") && Boolean.FALSE.equals(d.getMeetsTolerance()))).isTrue();
        assertThat(saved.stream().anyMatch(d -> d.getCfInstitution().equals("ENTE_OK") && Boolean.TRUE.equals(d.getMeetsTolerance()))).isTrue();
        // Percentuale ente KO < soglia
        assertThat(saved.stream().filter(d -> d.getCfInstitution().equals("ENTE_LOW")).findFirst().orElseThrow().getPercentage()).isLessThan(95.0);
    }
}
