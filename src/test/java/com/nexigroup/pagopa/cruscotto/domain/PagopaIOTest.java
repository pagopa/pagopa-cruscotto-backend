package com.nexigroup.pagopa.cruscotto.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link PagopaIO} entity.
 */
class PagopaIOTest {

    @Test
    void testGetPercentualeMessaggi() {
        // Test normal case: 50 messages out of 100 positions = 50%
        PagopaIO pagopaIO = new PagopaIO();
        pagopaIO.setPositionNumber(100L);
        pagopaIO.setMessageNumber(50L);
        
        assertThat(pagopaIO.getPercentualeMessaggi()).isEqualTo(50.0);
    }

    @Test
    void testGetPercentualeMessaggi_ZeroPositions() {
        // Test edge case: 0 positions but some messages = 100%
        PagopaIO pagopaIO = new PagopaIO();
        pagopaIO.setPositionNumber(0L);
        pagopaIO.setMessageNumber(10L);
        
        assertThat(pagopaIO.getPercentualeMessaggi()).isEqualTo(100.0);
    }

    @Test
    void testGetPercentualeMessaggi_ZeroPositionsZeroMessages() {
        // Test edge case: 0 positions and 0 messages = 0%
        PagopaIO pagopaIO = new PagopaIO();
        pagopaIO.setPositionNumber(0L);
        pagopaIO.setMessageNumber(0L);
        
        assertThat(pagopaIO.getPercentualeMessaggi()).isEqualTo(0.0);
    }

    @Test
    void testGetPercentualeMessaggi_NullPositions() {
        // Test edge case: null positions with messages = 100%
        PagopaIO pagopaIO = new PagopaIO();
        pagopaIO.setPositionNumber(null);
        pagopaIO.setMessageNumber(10L);
        
        assertThat(pagopaIO.getPercentualeMessaggi()).isEqualTo(100.0);
    }

    @Test
    void testGetPercentualeMessaggi_NullMessages() {
        // Test edge case: null messages = 0%
        PagopaIO pagopaIO = new PagopaIO();
        pagopaIO.setPositionNumber(100L);
        pagopaIO.setMessageNumber(null);
        
        assertThat(pagopaIO.getPercentualeMessaggi()).isEqualTo(0.0);
    }

    @Test
    void testGetPercentualeMessaggi_MoreMessagesThanPositions() {
        // Test case: more messages than positions = >100%
        PagopaIO pagopaIO = new PagopaIO();
        pagopaIO.setPositionNumber(50L);
        pagopaIO.setMessageNumber(75L);
        
        assertThat(pagopaIO.getPercentualeMessaggi()).isEqualTo(150.0);
    }

    @Test
    void testMeetsToleranceThreshold_AboveThreshold() {
        // Test: 95% meets 90% threshold
        PagopaIO pagopaIO = new PagopaIO();
        pagopaIO.setPositionNumber(100L);
        pagopaIO.setMessageNumber(95L);
        
        assertThat(pagopaIO.meetsToleranceThreshold(90.0)).isTrue();
    }

    @Test
    void testMeetsToleranceThreshold_ExactThreshold() {
        // Test: 90% meets 90% threshold exactly
        PagopaIO pagopaIO = new PagopaIO();
        pagopaIO.setPositionNumber(100L);
        pagopaIO.setMessageNumber(90L);
        
        assertThat(pagopaIO.meetsToleranceThreshold(90.0)).isTrue();
    }

    @Test
    void testMeetsToleranceThreshold_BelowThreshold() {
        // Test: 85% does not meet 90% threshold
        PagopaIO pagopaIO = new PagopaIO();
        pagopaIO.setPositionNumber(100L);
        pagopaIO.setMessageNumber(85L);
        
        assertThat(pagopaIO.meetsToleranceThreshold(90.0)).isFalse();
    }

    @Test
    void testConstructorAndGetters() {
        // Test parameterized constructor
        String cfPartner = "TSTPRT01";
        String cfInstitution = "TST001";
        LocalDate date = LocalDate.of(2024, 10, 1);
        Long positionNumber = 100L;
        Long messageNumber = 85L;

        PagopaIO pagopaIO = new PagopaIO(cfPartner, cfInstitution, date, positionNumber, messageNumber);
        
        assertThat(pagopaIO.getCfPartner()).isEqualTo(cfPartner);
        assertThat(pagopaIO.getCfInstitution()).isEqualTo(cfInstitution);
        assertThat(pagopaIO.getDate()).isEqualTo(date);
        assertThat(pagopaIO.getPositionNumber()).isEqualTo(positionNumber);
        assertThat(pagopaIO.getMessageNumber()).isEqualTo(messageNumber);
        assertThat(pagopaIO.getPercentualeMessaggi()).isEqualTo(85.0);
    }

    @Test
    void testToString() {
        // Test toString includes percentage
        PagopaIO pagopaIO = new PagopaIO();
        pagopaIO.setId(1L);
        pagopaIO.setCfPartner("TSTPRT01");
        pagopaIO.setCfInstitution("TST001");
        pagopaIO.setDate(LocalDate.of(2024, 10, 1));
        pagopaIO.setPositionNumber(100L);
        pagopaIO.setMessageNumber(85L);
        
        String toString = pagopaIO.toString();
        
        assertThat(toString).contains("id=1");
        assertThat(toString).contains("cfPartner='TSTPRT01'");
        assertThat(toString).contains("cfInstitution='TST001'");
        assertThat(toString).contains("positionNumber=100");
        assertThat(toString).contains("messageNumber=85");
        assertThat(toString).contains("percentuale=85.00%");
    }

    @Test
    void testEqualsAndHashCode() {
        PagopaIO pagopaIO1 = new PagopaIO();
        pagopaIO1.setId(1L);
        
        PagopaIO pagopaIO2 = new PagopaIO();
        pagopaIO2.setId(1L);
        
        PagopaIO pagopaIO3 = new PagopaIO();
        pagopaIO3.setId(2L);
        
        // Test equals
        assertThat(pagopaIO1).isEqualTo(pagopaIO2);
        assertThat(pagopaIO1).isNotEqualTo(pagopaIO3);
        assertThat(pagopaIO1).isNotEqualTo(null);
        assertThat(pagopaIO1).isNotEqualTo("not a PagopaIO");
        
        // Test hashCode consistency
        assertThat(pagopaIO1.hashCode()).isEqualTo(pagopaIO2.hashCode());
    }
}