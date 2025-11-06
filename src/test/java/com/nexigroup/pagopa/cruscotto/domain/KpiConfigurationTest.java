package com.nexigroup.pagopa.cruscotto.domain;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class KpiConfigurationTest {

    @Test
    void testGettersAndSetters() {
        KpiConfiguration kpi = new KpiConfiguration();
        Module module = new Module();

        kpi.setId(1L);
        kpi.setModule(module);
        kpi.setExcludePlannedShutdown(true);
        kpi.setExcludeUnplannedShutdown(false);
        kpi.setEligibilityThreshold(0.8);
        kpi.setTolerance(0.1);
        kpi.setAverageTimeLimit(30.0);
        kpi.setEvaluationType(EvaluationType.MESE); // replace with actual enum value
        kpi.setInstitutionCount(5);
        kpi.setTransactionCount(100);
        kpi.setInstitutionTolerance(BigDecimal.valueOf(1.5));
        kpi.setTransactionTolerance(BigDecimal.valueOf(2.5));

        assertThat(kpi.getId()).isEqualTo(1L);
        assertThat(kpi.getModule()).isEqualTo(module);
        assertThat(kpi.getExcludePlannedShutdown()).isTrue();
        assertThat(kpi.getExcludeUnplannedShutdown()).isFalse();
        assertThat(kpi.getEligibilityThreshold()).isEqualTo(0.8);
        assertThat(kpi.getTolerance()).isEqualTo(0.1);
        assertThat(kpi.getAverageTimeLimit()).isEqualTo(30.0);
        assertThat(kpi.getEvaluationType()).isEqualTo(EvaluationType.MESE);
        assertThat(kpi.getInstitutionCount()).isEqualTo(5);
        assertThat(kpi.getTransactionCount()).isEqualTo(100);
        assertThat(kpi.getInstitutionTolerance()).isEqualByComparingTo(BigDecimal.valueOf(1.5));
        assertThat(kpi.getTransactionTolerance()).isEqualByComparingTo(BigDecimal.valueOf(2.5));
    }

    @Test
    void testEqualsAndHashCode() {
        KpiConfiguration kpi1 = new KpiConfiguration();
        KpiConfiguration kpi2 = new KpiConfiguration();

        kpi1.setId(1L);
        kpi2.setId(1L);

        assertThat(kpi1).isEqualTo(kpi2);
        assertThat(kpi1.hashCode()).isEqualTo(kpi2.hashCode());

        kpi2.setId(2L);
        assertThat(kpi1).isNotEqualTo(kpi2);
        assertThat(kpi1.hashCode()).isNotEqualTo(kpi2.hashCode());
    }

    @Test
    void testToString() {
        KpiConfiguration kpi = new KpiConfiguration();
        kpi.setId(1L);
        kpi.setExcludePlannedShutdown(true);
        kpi.setExcludeUnplannedShutdown(false);

        String str = kpi.toString();
        assertThat(str).contains("id=1");
        assertThat(str).contains("excludePlannedShutdown=true");
        assertThat(str).contains("excludeUnplannedShutdown=false");
    }
}
