package com.nexigroup.pagopa.cruscotto.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class PagoPaPaymentReceiptTest {

    @Test
    void testGettersAndSetters() {
        PagoPaPaymentReceipt receipt = new PagoPaPaymentReceipt();

        receipt.setId(1L);
        receipt.setCfPartner("CF12345");
        receipt.setStation("STATION1");
        Instant now = Instant.now();
        receipt.setStartDate(now);
        receipt.setEndDate(now.plusSeconds(3600));
        receipt.setTotRes(100L);
        receipt.setResOk(80L);
        receipt.setResKo(20L);

        assertThat(receipt.getId()).isEqualTo(1L);
        assertThat(receipt.getCfPartner()).isEqualTo("CF12345");
        assertThat(receipt.getStation()).isEqualTo("STATION1");
        assertThat(receipt.getStartDate()).isEqualTo(now);
        assertThat(receipt.getEndDate()).isEqualTo(now.plusSeconds(3600));
        assertThat(receipt.getTotRes()).isEqualTo(100L);
        assertThat(receipt.getResOk()).isEqualTo(80L);
        assertThat(receipt.getResKo()).isEqualTo(20L);
    }

    @Test
    void testEqualsAndHashCode() {
        Instant now = Instant.now();
        PagoPaPaymentReceipt receipt1 = new PagoPaPaymentReceipt();
        receipt1.setCfPartner("CF1");
        receipt1.setStation("ST1");
        receipt1.setStartDate(now);
        receipt1.setEndDate(now.plusSeconds(100));

        PagoPaPaymentReceipt receipt2 = new PagoPaPaymentReceipt();
        receipt2.setCfPartner("CF1");
        receipt2.setStation("ST1");
        receipt2.setStartDate(now);
        receipt2.setEndDate(now.plusSeconds(100));

        // Equals
        assertThat(receipt1).
            isEqualTo(receipt2)
            .hasSameHashCodeAs(receipt2);

        // Different object
        receipt2.setStation("ST2");
        assertThat(receipt1).isNotEqualTo(receipt2);
    }

    @Test
    void testToString() {
        PagoPaPaymentReceipt receipt = new PagoPaPaymentReceipt();
        receipt.setId(1L);
        receipt.setCfPartner("CF1");
        receipt.setStation("ST1");
        Instant now = Instant.now();
        receipt.setStartDate(now);
        receipt.setEndDate(now.plusSeconds(100));
        receipt.setTotRes(10L);
        receipt.setResOk(8L);
        receipt.setResKo(2L);

        String toString = receipt.toString();

        assertThat(toString)
            .contains("PagoPaPaymentReceipt")
            .contains("CF1")
            .contains("ST1")
            .contains("10")
            .contains("8")
            .contains("2");
    }
}
