package com.nexigroup.pagopa.cruscotto.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PagopaSendTest {

    @Test
    void testConstructorAndGettersSetters() {
        LocalDateTime now = LocalDateTime.now();

        PagopaSend send = new PagopaSend();
        send.setId(1L);
        send.setCfPartner("CFPARTNER");
        send.setCfInstitution("CFINST");
        send.setDate(now);
        send.setPaymentsNumber(10L);
        send.setNotificationNumber(5L);

        assertThat(send.getId()).isEqualTo(1L);
        assertThat(send.getCfPartner()).isEqualTo("CFPARTNER");
        assertThat(send.getCfInstitution()).isEqualTo("CFINST");
        assertThat(send.getDate()).isEqualTo(now);
        assertThat(send.getPaymentsNumber()).isEqualTo(10L);
        assertThat(send.getNotificationNumber()).isEqualTo(5L);
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();

        PagopaSend send = new PagopaSend(
            2L, "CP02", "CI02", now, 20L, 12L
        );

        assertThat(send.getId()).isEqualTo(2L);
        assertThat(send.getCfPartner()).isEqualTo("CP02");
        assertThat(send.getCfInstitution()).isEqualTo("CI02");
        assertThat(send.getDate()).isEqualTo(now);
        assertThat(send.getPaymentsNumber()).isEqualTo(20L);
        assertThat(send.getNotificationNumber()).isEqualTo(12L);
    }

    @Test
    void testEqualsAndHashCode() {
        LocalDateTime now = LocalDateTime.now();

        PagopaSend send1 = new PagopaSend(1L, "CP1", "CI1", now, 10L, 5L);
        PagopaSend send2 = new PagopaSend(1L, "CP1", "CI1", now, 10L, 5L);

        assertThat(send1).isEqualTo(send2);
        assertThat(send1.hashCode()).isEqualTo(send2.hashCode());

        send2.setId(2L);
        assertThat(send1).isNotEqualTo(send2);
    }

    @Test
    void testToString() {
        PagopaSend send = new PagopaSend();
        send.setId(100L);
        send.setCfPartner("PartnerTest");

        String text = send.toString();

        assertThat(text).contains("id=100");
        assertThat(text).contains("cfPartner=PartnerTest");
    }
}
