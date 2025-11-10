package com.nexigroup.pagopa.cruscotto.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class PagopaApiLogDrilldownTest {

    private PagopaApiLogDrilldown drilldown;
    private Instance instance;
    private InstanceModule instanceModule;
    private AnagStation station;

    @BeforeEach
    void setUp() {
        drilldown = new PagopaApiLogDrilldown();
        instance = new Instance();
        instanceModule = new InstanceModule();
        station = new AnagStation();
    }

    @Test
    void testGettersAndSetters() {
        drilldown.setId(1L);
        drilldown.setInstance(instance);
        drilldown.setInstanceModule(instanceModule);
        drilldown.setStation(station);
        drilldown.setAnalysisDate(LocalDate.of(2025, 10, 30));
        drilldown.setDataDate(LocalDate.of(2025, 10, 29));
        drilldown.setPartnerFiscalCode("PARTNER123");
        drilldown.setStationCode("STATION123");
        drilldown.setFiscalCode("FISCAL123");
        drilldown.setApi("API_TEST");
        drilldown.setTotalRequests(100);
        drilldown.setOkRequests(80);
        drilldown.setKoRequests(20);

        assertThat(drilldown.getId()).isEqualTo(1L);
        assertThat(drilldown.getInstance()).isEqualTo(instance);
        assertThat(drilldown.getInstanceModule()).isEqualTo(instanceModule);
        assertThat(drilldown.getStation()).isEqualTo(station);
        assertThat(drilldown.getAnalysisDate()).isEqualTo(LocalDate.of(2025, 10, 30));
        assertThat(drilldown.getDataDate()).isEqualTo(LocalDate.of(2025, 10, 29));
        assertThat(drilldown.getPartnerFiscalCode()).isEqualTo("PARTNER123");
        assertThat(drilldown.getStationCode()).isEqualTo("STATION123");
        assertThat(drilldown.getFiscalCode()).isEqualTo("FISCAL123");
        assertThat(drilldown.getApi()).isEqualTo("API_TEST");
        assertThat(drilldown.getTotalRequests()).isEqualTo(100);
        assertThat(drilldown.getOkRequests()).isEqualTo(80);
        assertThat(drilldown.getKoRequests()).isEqualTo(20);
    }

    @Test
    void testEqualsAndHashCode() {
        PagopaApiLogDrilldown other = new PagopaApiLogDrilldown();
        drilldown.setId(1L);
        other.setId(1L);

        assertThat(drilldown).isEqualTo(other);
        assertThat(drilldown.hashCode()).isEqualTo(other.hashCode());

        other.setId(2L);
        assertThat(drilldown).isNotEqualTo(other);
    }

    @Test
    void testToStringContainsFields() {
        drilldown.setId(1L);
        drilldown.setAnalysisDate(LocalDate.of(2025, 10, 30));
        drilldown.setDataDate(LocalDate.of(2025, 10, 29));
        drilldown.setPartnerFiscalCode("PARTNER123");
        drilldown.setStationCode("STATION123");
        drilldown.setFiscalCode("FISCAL123");
        drilldown.setApi("API_TEST");
        drilldown.setTotalRequests(100);
        drilldown.setOkRequests(80);
        drilldown.setKoRequests(20);

        String str = drilldown.toString();
        assertThat(str).contains("id=1");
        assertThat(str).contains("analysisDate=2025-10-30");
        assertThat(str).contains("dataDate=2025-10-29");
        assertThat(str).contains("partnerFiscalCode='PARTNER123'");
        assertThat(str).contains("stationCode='STATION123'");
        assertThat(str).contains("fiscalCode='FISCAL123'");
        assertThat(str).contains("api='API_TEST'");
        assertThat(str).contains("totalRequests=100");
        assertThat(str).contains("okRequests=80");
        assertThat(str).contains("koRequests=20");
    }
}
