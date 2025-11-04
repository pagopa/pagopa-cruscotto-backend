package com.nexigroup.pagopa.cruscotto.domain;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class KpiResultTest {

    private KpiResult kpiResult;

    @BeforeEach
    void setUp() {
        kpiResult = new KpiResult();
        kpiResult.setId(1L);
        kpiResult.setModuleCode(ModuleCode.B3); // deve corrispondere all'oggetto di test
        kpiResult.setInstanceId(100L);
        kpiResult.setInstanceModuleId(200L);
        kpiResult.setAnalysisDate(LocalDate.of(2025, 1, 1));
        kpiResult.setOutcome(OutcomeStatus.OK); // deve corrispondere all'oggetto di test
        kpiResult.setAdditionalData("{\"key\":\"value\"}");
    }

    @Test
    void testGettersAndSetters() {
        assertThat(kpiResult.getId()).isEqualTo(1L);
        assertThat(kpiResult.getModuleCode()).isEqualTo(ModuleCode.B3);
        assertThat(kpiResult.getInstanceId()).isEqualTo(100L);
        assertThat(kpiResult.getInstanceModuleId()).isEqualTo(200L);
        assertThat(kpiResult.getAnalysisDate()).isEqualTo(LocalDate.of(2025, 1, 1));
        assertThat(kpiResult.getOutcome()).isEqualTo(OutcomeStatus.OK);
        assertThat(kpiResult.getAdditionalData()).isEqualTo("{\"key\":\"value\"}");
    }

    @Test
    void testNotEquals() {
        KpiResult different = new KpiResult();
        different.setId(2L);
        different.setModuleCode(ModuleCode.B3);

        assertThat(kpiResult).isNotEqualTo(different);
    }

    @Test
    void testToString() {
        String result = kpiResult.toString();
        assertThat(result)
            .contains("id=1")
            .contains("moduleCode=B3")
            .contains("instanceId=100")
            .contains("instanceModuleId=200")
            .contains("outcome=OK")
            .contains("additionalData");
    }

    @Test
    void testEntityAnnotations() throws NoSuchFieldException {
        // Assicurati di usare jakarta.persistence
        assertThat(KpiResult.class.isAnnotationPresent(jakarta.persistence.Entity.class)).isTrue();
        assertThat(KpiResult.class.isAnnotationPresent(jakarta.persistence.Table.class)).isTrue();

        assertThat(KpiResult.class.getDeclaredField("id")
            .isAnnotationPresent(jakarta.persistence.Id.class)).isTrue();

        assertThat(KpiResult.class.getDeclaredField("moduleCode")
            .isAnnotationPresent(jakarta.persistence.Enumerated.class)).isTrue();

        assertThat(KpiResult.class.getDeclaredField("analysisDate")
            .isAnnotationPresent(jakarta.persistence.Column.class)).isTrue();
    }
}
