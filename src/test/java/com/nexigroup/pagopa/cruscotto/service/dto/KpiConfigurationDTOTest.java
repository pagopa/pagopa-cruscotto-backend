package com.nexigroup.pagopa.cruscotto.service.dto;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class KpiConfigurationDTOTest {

    @Test
    void testGettersAndSetters() {
        KpiConfigurationDTO dto = new KpiConfigurationDTO();

        dto.setId(1L);
        dto.setModuleId(2L);
        dto.setModuleCode("CODE");
        dto.setModuleName("Module Name");
        dto.setExcludePlannedShutdown(true);
        dto.setExcludeUnplannedShutdown(false);
        dto.setEligibilityThreshold(0.5);
        dto.setTolerance(0.1);
        dto.setAverageTimeLimit(24.0);
        dto.setEvaluationType(EvaluationType.MESE);
        dto.setInstitutionCount(10);
        dto.setTransactionCount(100);
        dto.setInstitutionTolerance(BigDecimal.valueOf(0.2));
        dto.setTransactionTolerance(BigDecimal.valueOf(0.3));
        dto.setConfigExcludePlannedShutdown(true);
        dto.setConfigExcludeUnplannedShutdown(true);
        dto.setConfigEligibilityThreshold(true);
        dto.setConfigTolerance(true);
        dto.setConfigAverageTimeLimit(true);
        dto.setConfigEvaluationType(true);
        dto.setConfigInstitutionCount(true);
        dto.setConfigTransactionCount(true);
        dto.setConfigInstitutionTolerance(true);
        dto.setConfigTransactionTolerance(true);
        dto.setEnabled(true);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getModuleId()).isEqualTo(2L);
        assertThat(dto.getModuleCode()).isEqualTo("CODE");
        assertThat(dto.getModuleName()).isEqualTo("Module Name");
        assertThat(dto.getExcludePlannedShutdown()).isTrue();
        assertThat(dto.getExcludeUnplannedShutdown()).isFalse();
        assertThat(dto.getEligibilityThreshold()).isEqualTo(0.5);
        assertThat(dto.getTolerance()).isEqualTo(0.1);
        assertThat(dto.getAverageTimeLimit()).isEqualTo(24.0);
        assertThat(dto.getEvaluationType()).isEqualTo(EvaluationType.MESE);
        assertThat(dto.getInstitutionCount()).isEqualTo(10);
        assertThat(dto.getTransactionCount()).isEqualTo(100);
        assertThat(dto.getInstitutionTolerance()).isEqualByComparingTo(BigDecimal.valueOf(0.2));
        assertThat(dto.getTransactionTolerance()).isEqualByComparingTo(BigDecimal.valueOf(0.3));
        assertThat(dto.getConfigExcludePlannedShutdown()).isTrue();
        assertThat(dto.getConfigExcludeUnplannedShutdown()).isTrue();
        assertThat(dto.getConfigEligibilityThreshold()).isTrue();
        assertThat(dto.getConfigTolerance()).isTrue();
        assertThat(dto.getConfigAverageTimeLimit()).isTrue();
        assertThat(dto.getConfigEvaluationType()).isTrue();
        assertThat(dto.getConfigInstitutionCount()).isTrue();
        assertThat(dto.getConfigTransactionCount()).isTrue();
        assertThat(dto.getConfigInstitutionTolerance()).isTrue();
        assertThat(dto.getConfigTransactionTolerance()).isTrue();
        assertThat(dto.isEnabled()).isTrue();
    }

    @Test
    void testIsEnabledDefaultsToFalse() {
        KpiConfigurationDTO dto = new KpiConfigurationDTO();
        dto.setEnabled(null);
        assertThat(dto.isEnabled()).isFalse();
    }

    @Test
    void testEqualsAndHashCode() {
        KpiConfigurationDTO dto1 = new KpiConfigurationDTO();
        dto1.setId(1L);

        KpiConfigurationDTO dto2 = new KpiConfigurationDTO();
        dto2.setId(1L);

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    void testToString() {
        KpiConfigurationDTO dto = new KpiConfigurationDTO();
        dto.setId(1L);
        String str = dto.toString();
        assertThat(str).contains("id=1");
    }
}
