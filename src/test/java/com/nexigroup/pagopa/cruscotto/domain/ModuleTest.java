package com.nexigroup.pagopa.cruscotto.domain;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.AnalysisType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ModuleTest {

    @Test
    void equalsVerifier() {
        Module m1 = new Module();
        m1.setId(1L);

        Module m2 = new Module();
        m2.setId(1L);

        assertThat(m1).isEqualTo(m2);
        assertThat(m1.hashCode()).isEqualTo(m2.hashCode());

        m2.setId(2L);
        assertThat(m1).isNotEqualTo(m2);

        m1.setId(null);
        assertThat(m1).isNotEqualTo(m2);
    }

    @Test
    void gettersAndSettersShouldWork() {
        Module module = new Module();

        module.setId(10L);
        module.setCode("MOD-1");
        module.setName("Module Name");
        module.setDescription("Module Description");
        module.setAnalysisType(AnalysisType.AUTOMATICA);
        module.setAllowManualOutcome(true);
        module.setStatus(ModuleStatus.ATTIVO);

        module.setConfigExcludePlannedShutdown(true);
        module.setConfigExcludeUnplannedShutdown(true);
        module.setConfigEligibilityThreshold(true);
        module.setConfigTolerance(true);
        module.setConfigAverageTimeLimit(true);
        module.setConfigEvaluationType(true);
        module.setConfigInstitutionCount(true);
        module.setConfigTransactionCount(true);
        module.setConfigInstitutionTolerance(true);
        module.setConfigTransactionTolerance(true);
        module.setConfigNotificationTolerance(true);
        module.setDeleted(true);

        assertThat(module.getId()).isEqualTo(10L);
        assertThat(module.getCode()).isEqualTo("MOD-1");
        assertThat(module.getName()).isEqualTo("Module Name");
        assertThat(module.getDescription()).isEqualTo("Module Description");
        assertThat(module.getAnalysisType()).isEqualTo(AnalysisType.AUTOMATICA);
        assertThat(module.isAllowManualOutcome()).isTrue();
        assertThat(module.getStatus()).isEqualTo(ModuleStatus.ATTIVO);

        assertThat(module.getConfigExcludePlannedShutdown()).isTrue();
        assertThat(module.getConfigExcludeUnplannedShutdown()).isTrue();
        assertThat(module.getConfigEligibilityThreshold()).isTrue();
        assertThat(module.getConfigTolerance()).isTrue();
        assertThat(module.getConfigAverageTimeLimit()).isTrue();
        assertThat(module.getConfigEvaluationType()).isTrue();
        assertThat(module.getConfigInstitutionCount()).isTrue();
        assertThat(module.getConfigTransactionCount()).isTrue();
        assertThat(module.getConfigInstitutionTolerance()).isTrue();
        assertThat(module.getConfigTransactionTolerance()).isTrue();
        assertThat(module.getConfigNotificationTolerance()).isTrue();
        assertThat(module.isDeleted()).isTrue();
    }
}
