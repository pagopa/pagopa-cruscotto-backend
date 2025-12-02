package com.nexigroup.pagopa.cruscotto.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class KpiA2AnalyticIncorrectTaxonomyDataTest {

    @Test
    void testGetterAndSetter() {
        KpiA2AnalyticIncorrectTaxonomyData data = new KpiA2AnalyticIncorrectTaxonomyData();

        Long id = 1L;
        Long analyticDataId = 100L;
        String transferCategory = "CAT01";
        Long total = 500L;
        Instant fromHour = Instant.parse("2025-10-30T10:00:00Z");
        Instant endHour = Instant.parse("2025-10-30T12:00:00Z");

        data.setId(id);
        data.setKpiA2AnalyticDataId(analyticDataId);
        data.setTransferCategory(transferCategory);
        data.setCoTotalIncorrectPayments(0L);
        data.setTotPayments(total);
        data.setFromHour(fromHour);
        data.setEndHour(endHour);

        assertThat(data.getId()).isEqualTo(id);
        assertThat(data.getKpiA2AnalyticDataId()).isEqualTo(analyticDataId);
        assertThat(data.getTransferCategory()).isEqualTo(transferCategory);
        assertThat(data.getCoTotalIncorrectPayments()).isEqualTo(0L);
        assertThat(data.getTotPayments()).isEqualTo(total);
        assertThat(data.getFromHour()).isEqualTo(fromHour);
        assertThat(data.getEndHour()).isEqualTo(endHour);
    }

    @Test
    void testEqualsAndHashCode() {
        KpiA2AnalyticIncorrectTaxonomyData d1 = new KpiA2AnalyticIncorrectTaxonomyData();
        d1.setId(1L);
        d1.setKpiA2AnalyticDataId(100L);
        d1.setTransferCategory("CAT01");
        d1.setCoTotalIncorrectPayments(0L);
        d1.setTotPayments(500L);

        KpiA2AnalyticIncorrectTaxonomyData d2 = new KpiA2AnalyticIncorrectTaxonomyData();
        d2.setId(1L);
        d2.setKpiA2AnalyticDataId(100L);
        d2.setTransferCategory("CAT01");
        d2.setCoTotalIncorrectPayments(0L);
        d2.setTotPayments(500L);

        assertThat(d1).isEqualTo(d2);
        assertThat(d1.hashCode()).isEqualTo(d2.hashCode());
    }

    @Test
    void testToString() {
        KpiA2AnalyticIncorrectTaxonomyData data = new KpiA2AnalyticIncorrectTaxonomyData();
        data.setId(1L);
        data.setTransferCategory("CAT01");
        data.setTotPayments(200L);

        String toString = data.toString();
        assertThat(toString).contains("CAT01");
        assertThat(toString).contains("200");
        assertThat(toString).contains("id=1");
    }

    @Test
    void testNotEqualsDifferentId() {
        KpiA2AnalyticIncorrectTaxonomyData d1 = new KpiA2AnalyticIncorrectTaxonomyData();
        d1.setId(1L);
        KpiA2AnalyticIncorrectTaxonomyData d2 = new KpiA2AnalyticIncorrectTaxonomyData();
        d2.setId(2L);

        assertThat(d1).isNotEqualTo(d2);
    }
}
