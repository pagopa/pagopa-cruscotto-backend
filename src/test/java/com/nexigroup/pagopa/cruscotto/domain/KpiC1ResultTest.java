package com.nexigroup.pagopa.cruscotto.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Unit tests for {@link KpiC1Result} entity.
 */
class KpiC1ResultTest {

    @Test
    void testBasicEntityProperties() {
        KpiC1Result result = new KpiC1Result();
        
        // Test setters and getters
        result.setCompliantInstitutions(75L);
        result.setTotalInstitutions(100L);
        result.setSentMessages(800L);
        result.setTotalPositions(1000L);
        result.setCompliancePercentage(new BigDecimal("75.00"));
        result.setGlobalSendingPercentage(new BigDecimal("80.00"));
        result.setOutcome(OutcomeStatus.OK);
        
        assertThat(result.getCompliantInstitutions()).isEqualTo(75L);
        assertThat(result.getTotalInstitutions()).isEqualTo(100L);
        assertThat(result.getSentMessages()).isEqualTo(800L);
        assertThat(result.getTotalPositions()).isEqualTo(1000L);
        assertThat(result.getCompliancePercentage()).isEqualTo(new BigDecimal("75.00"));
        assertThat(result.getGlobalSendingPercentage()).isEqualTo(new BigDecimal("80.00"));
        assertThat(result.getOutcome()).isEqualTo(OutcomeStatus.OK);
    }

    @Test
    void testUpdateTotalsMethod() {
        KpiC1Result result = new KpiC1Result();
        
        // Test updateTotals method (Long compliantInstitutions, Long totalInstitutions, Long totalPositions, Long sentMessages)
        result.updateTotals(75L, 100L, 1000L, 800L);
        
        assertThat(result.getCompliantInstitutions()).isEqualTo(75L);
        assertThat(result.getTotalInstitutions()).isEqualTo(100L);
        assertThat(result.getSentMessages()).isEqualTo(800L);
        assertThat(result.getTotalPositions()).isEqualTo(1000L);
        // Check that percentages are calculated automatically
        assertThat(result.getCompliancePercentage()).isEqualTo(new BigDecimal("75.00"));
        assertThat(result.getGlobalSendingPercentage()).isEqualTo(new BigDecimal("80.00"));
    }

    @Test
    void testConstructorWithParameters() {
        Instance instance = new Instance();
        instance.setId(1L);
        
        InstanceModule instanceModule = new InstanceModule();
        instanceModule.setId(2L);
        
        LocalDate referenceDate = LocalDate.of(2023, 10, 1);
        OutcomeStatus outcome = OutcomeStatus.KO;
        Boolean compliant = false;
        BigDecimal configuredThreshold = new BigDecimal("70.00");
        
        KpiC1Result result = new KpiC1Result(instance, instanceModule, referenceDate, outcome, compliant, configuredThreshold);
        
        assertThat(result.getInstance()).isEqualTo(instance);
        assertThat(result.getInstanceModule()).isEqualTo(instanceModule);
        assertThat(result.getReferenceDate()).isEqualTo(referenceDate);
        assertThat(result.getOutcome()).isEqualTo(outcome);
        assertThat(result.getCompliant()).isEqualTo(compliant);
        assertThat(result.getConfiguredThreshold()).isEqualTo(configuredThreshold);
    }
}