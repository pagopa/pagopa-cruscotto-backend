package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.IoDrilldown;
import com.nexigroup.pagopa.cruscotto.domain.KpiC1AnalyticData;
import com.nexigroup.pagopa.cruscotto.domain.KpiC1Result;
import com.nexigroup.pagopa.cruscotto.domain.PagopaIO;
import com.nexigroup.pagopa.cruscotto.repository.PagopaIORepository;
import com.nexigroup.pagopa.cruscotto.service.*;
import com.nexigroup.pagopa.cruscotto.service.dto.PagoPaIODTO;
import com.nexigroup.pagopa.cruscotto.service.dto.PartnerIdentificationDTO;
import com.nexigroup.pagopa.cruscotto.service.mapper.PagopaIOMapper;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

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
    @DisplayName("Negative evidences are captured and all IoDrilldown rows are persisted")
    void testNegativeEvidenceSnapshot() {
        // Configuration: tolerance=95 -> requiredMessagePercentage = 95
        var instance = new com.nexigroup.pagopa.cruscotto.service.dto.InstanceDTO();
        instance.setId(1L);
        instance.setPartnerFiscalCode("PARTNER_CF");
        instance.setAnalysisPeriodStartDate(LocalDate.of(2025,1,1));
        instance.setAnalysisPeriodEndDate(LocalDate.of(2025,1,2));

        var module = new com.nexigroup.pagopa.cruscotto.service.dto.InstanceModuleDTO();
        module.setId(2L);

        var conf = new com.nexigroup.pagopa.cruscotto.service.dto.KpiConfigurationDTO();
        conf.setNotificationTolerance(java.math.BigDecimal.valueOf(95.0));
        conf.setInstitutionTolerance(java.math.BigDecimal.valueOf(50.0));
        conf.setEligibilityThreshold(50.0);
        conf.setEvaluationType(com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType.MESE);

        // Input dati IO
        List<PagoPaIODTO> ioData = List.of(
            dto("ENTE_LOW", LocalDate.of(2025,1,1), 100, 60,  "PARTNER_CF"), // 60% KO
            dto("ENTE_OK",  LocalDate.of(2025,1,1), 100, 100, "PARTNER_CF")  // 100% OK
        );

        when(pagopaIORepository.existsByCfPartner("PARTNER_CF")).thenReturn(true);
        when(pagopaIORepository.findByCfPartnerAndDateRange(eq("PARTNER_CF"), any(), any()))
            .thenReturn(ioData.stream().map(d -> new PagopaIO(
                    d.getCfPartner(), d.getEnte(), d.getData(),
                    d.getNumeroPosizioni().longValue(), d.getNumeroMessaggi().longValue()))
                .toList());

        // =========================
        // FIX: cast esplicito per toDto
        // =========================
        when(pagopaIOMapper.toDto(any(PagopaIO.class))).thenAnswer(inv -> {
            PagopaIO src = inv.getArgument(0, PagopaIO.class);
            PagoPaIODTO out = new PagoPaIODTO();
            out.setCfPartner(src.getCfPartner());
            out.setEnte(src.getCfInstitution());
            out.setData(src.getDate());
            out.setNumeroPosizioni(src.getPositionNumber().intValue());
            out.setNumeroMessaggi(src.getMessageNumber().intValue());
            return out;
        });

        // Partner mock
        var partnerDto = new com.nexigroup.pagopa.cruscotto.service.dto.AnagPartnerDTO();
        var pid = new PartnerIdentificationDTO();
        pid.setId(10L);
        partnerDto.setPartnerIdentification(pid);
        when(anagPartnerService.findOneByFiscalCode("PARTNER_CF"))
            .thenReturn(java.util.Optional.of(partnerDto));

        // =========================
        // FIX: tipizzare findAll
        // =========================
        when(anagInstitutionService.findAll(
            any(com.nexigroup.pagopa.cruscotto.service.filter.AnagInstitutionFilter.class),
            any(org.springframework.data.domain.Pageable.class)))
            .thenReturn(new org.springframework.data.domain.PageImpl<>(List.of()));

        // Analytic data save → ID mock
        when(kpiC1AnalyticDataService.save(any())).thenAnswer(inv -> {
            KpiC1AnalyticData ad = (KpiC1AnalyticData) inv.getArgument(0);
            if (ad.getId() == null) {
                ad.setId(200L);
            }
            return ad;
        });

        when(kpiC1ResultService.findByInstanceAndInstanceModule(anyLong(), anyLong()))
            .thenReturn(java.util.Collections.emptyList());

        when(kpiC1ResultService.save(any())).thenAnswer(inv -> {
            KpiC1Result r = (KpiC1Result) inv.getArgument(0);
            if (r.getId() == null) {
                r.setId(100L);
            }
            return r;
        });

        // ================================================
        // FIX FINALE: cast esplicito per lista IoDrilldown
        // ================================================
        AtomicReference<List<IoDrilldown>> captured = new AtomicReference<>();
        when(ioDrilldownService.saveAll(any())).thenAnswer(inv -> {
            @SuppressWarnings("unchecked")
            List<IoDrilldown> list = (List<IoDrilldown>) inv.getArgument(0);
            captured.set(list);
            return list;
        });

        OutcomeStatus outcome = service.executeKpiC1Calculation(
            instance, module, conf, LocalDate.of(2025,1,3));


    verify(ioDrilldownService, atLeastOnce()).saveAll(any());
    List<com.nexigroup.pagopa.cruscotto.domain.IoDrilldown> saved = captured.get();
        // Logica corrente: solo enti KO in drilldown (1)
        assertThat(saved).hasSize(2);
        // Verifica presenza solo ente KO
        assertThat(saved.stream().anyMatch(d -> d.getCfInstitution().equals("ENTE_LOW") && Boolean.FALSE.equals(d.getMeetsTolerance()))).isTrue();
        // Percentuale ente KO < soglia
        assertThat(saved.stream().filter(d -> d.getCfInstitution().equals("ENTE_LOW")).findFirst().orElseThrow().getPercentage()).isLessThan(95.0);
        assertThat(outcome).isEqualTo(OutcomeStatus.OK);
        verify(ioDrilldownService, atLeastOnce()).saveAll(any());


        assertThat(saved).hasSize(2);

        // ENTE KO
        IoDrilldown ko = saved.stream()
            .filter(d -> Boolean.FALSE.equals(d.getMeetsTolerance()))
            .findFirst()
            .orElseThrow();
        assertThat(ko.getCfInstitution()).isEqualTo("ENTE_LOW");
        assertThat(ko.getPercentage()).isEqualTo(60.0);
        assertThat(ko.getMeetsTolerance()).isFalse();

        // ENTE OK
        IoDrilldown ok = saved.stream()
            .filter(d -> Boolean.TRUE.equals(d.getMeetsTolerance()))
            .findFirst()
            .orElseThrow();
        assertThat(ok.getCfInstitution()).isEqualTo("ENTE_OK");
        assertThat(ok.getPercentage()).isEqualTo(100.0);
        assertThat(ok.getMeetsTolerance()).isTrue();

    }
}

