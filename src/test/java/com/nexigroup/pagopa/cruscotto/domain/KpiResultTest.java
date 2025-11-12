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
        kpiResult.setModuleCode(ModuleCode.B3);
        kpiResult.setInstanceId(100L);
        kpiResult.setInstanceModuleId(200L);
        kpiResult.setAnalysisDate(LocalDate.of(2025, 1, 1));
        kpiResult.setOutcome(OutcomeStatus.OK);
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
    void testToStringContainsFields() {
        String str = kpiResult.toString();
        // Controllo solo i campi, ignora l'hashcode dinamico
        assertThat(str)
            .contains("id=1")
            .contains("moduleCode=B3")
            .contains("instanceId=100")
            .contains("instanceModuleId=200")
            .contains("analysisDate=2025-01-01")
            .contains("outcome=OK")
            .contains("additionalData={\"key\":\"value\"}");
    }

    @Test
    void testEnumVariations() {
        for (ModuleCode code : ModuleCode.values()) {
            kpiResult.setModuleCode(code);
            assertThat(kpiResult.getModuleCode()).isEqualTo(code);
        }

        for (OutcomeStatus status : OutcomeStatus.values()) {
            kpiResult.setOutcome(status);
            assertThat(kpiResult.getOutcome()).isEqualTo(status);
        }
    }

    @Test
    void testAdditionalDataNullAndEmpty() {
        kpiResult.setAdditionalData(null);
        assertThat(kpiResult.getAdditionalData()).isNull();

        kpiResult.setAdditionalData("");
        assertThat(kpiResult.getAdditionalData()).isEmpty();
    }
}
