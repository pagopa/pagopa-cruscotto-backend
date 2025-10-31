package com.nexigroup.pagopa.cruscotto.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.SpontaneousPayments;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class KpiB5AnalyticDrillDownTest {

    private KpiB5AnalyticDrillDown drillDown;

    @BeforeEach
    void setUp() {
        drillDown = new KpiB5AnalyticDrillDown();
    }

    @Test
    void testGettersAndSetters() {
        KpiB5AnalyticData analyticData = new KpiB5AnalyticData();
        drillDown.setId(1L);
        drillDown.setKpiB5AnalyticData(analyticData);
        drillDown.setPartnerId(100L);
        drillDown.setPartnerName("Partner Name");
        drillDown.setPartnerFiscalCode("PFC123");
        drillDown.setStationCode("ST123");
        drillDown.setFiscalCode("FC123");
        drillDown.setSpontaneousPayment(Boolean.TRUE);
        drillDown.setSpontaneousPayments(SpontaneousPayments.ATTIVI);

        assertThat(drillDown.getId()).isEqualTo(1L);
        assertThat(drillDown.getKpiB5AnalyticData()).isEqualTo(analyticData);
        assertThat(drillDown.getPartnerId()).isEqualTo(100L);
        assertThat(drillDown.getPartnerName()).isEqualTo("Partner Name");
        assertThat(drillDown.getPartnerFiscalCode()).isEqualTo("PFC123");
        assertThat(drillDown.getStationCode()).isEqualTo("ST123");
        assertThat(drillDown.getFiscalCode()).isEqualTo("FC123");
        assertThat(drillDown.getSpontaneousPayment()).isTrue();
        assertThat(drillDown.getSpontaneousPayments()).isEqualTo(SpontaneousPayments.ATTIVI);
    }

    @Test
    void testEqualsAndHashCode() {
        KpiB5AnalyticDrillDown other = new KpiB5AnalyticDrillDown();

        drillDown.setId(1L);
        other.setId(1L);
        assertThat(drillDown).isEqualTo(other);
        assertThat(drillDown.hashCode()).isEqualTo(other.hashCode());

        other.setId(2L);
        assertThat(drillDown).isNotEqualTo(other);
    }

    @Test
    void testToStringContainsAllFields() {
        KpiB5AnalyticData analyticData = new KpiB5AnalyticData();
        drillDown.setId(1L);
        drillDown.setKpiB5AnalyticData(analyticData);
        drillDown.setPartnerId(100L);
        drillDown.setPartnerName("Partner Name");
        drillDown.setPartnerFiscalCode("PFC123");
        drillDown.setStationCode("ST123");
        drillDown.setFiscalCode("FC123");
        drillDown.setSpontaneousPayment(Boolean.TRUE);
        drillDown.setSpontaneousPayments(SpontaneousPayments.ATTIVI);

        String toString = drillDown.toString();

        assertThat(toString).contains(
            "id=1",
            "kpiB5AnalyticData=" + analyticData.toString(),
            "partnerId=100",
            "partnerName='Partner Name'",
            "partnerFiscalCode='PFC123'",
            "stationCode='ST123'",
            "fiscalCode='FC123'",
            "spontaneousPayment=true",
            "spontaneousPayments=ATTIVI"
        );
    }
}
