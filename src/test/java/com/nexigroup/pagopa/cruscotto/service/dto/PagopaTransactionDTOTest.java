package com.nexigroup.pagopa.cruscotto.service.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class PagopaTransactionDTOTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        PagopaTransactionDTO dto = new PagopaTransactionDTO();
        dto.setId(1L);
        dto.setCfPartner("partner1");
        dto.setCfInstitution("institution1");
        dto.setDate(LocalDate.of(2025, 10, 31));
        dto.setStation("station1");
        dto.setTransactionTotal(100);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getCfPartner()).isEqualTo("partner1");
        assertThat(dto.getCfInstitution()).isEqualTo("institution1");
        assertThat(dto.getDate()).isEqualTo(LocalDate.of(2025, 10, 31));
        assertThat(dto.getStation()).isEqualTo("station1");
        assertThat(dto.getTransactionTotal()).isEqualTo(100);
    }

    @Test
    void testAllArgsConstructor() {
        LocalDate date = LocalDate.of(2025, 10, 31);
        PagopaTransactionDTO dto = new PagopaTransactionDTO("partner1", "institution1", date, "station1", 100);

        assertThat(dto.getCfPartner()).isEqualTo("partner1");
        assertThat(dto.getCfInstitution()).isEqualTo("institution1");
        assertThat(dto.getDate()).isEqualTo(date);
        assertThat(dto.getStation()).isEqualTo("station1");
        assertThat(dto.getTransactionTotal()).isEqualTo(100);
    }

    @Test
    void testEqualsAndHashCode() {
        LocalDate date = LocalDate.of(2025, 10, 31);
        PagopaTransactionDTO dto1 = new PagopaTransactionDTO("partner1", "institution1", date, "station1", 100);
        PagopaTransactionDTO dto2 = new PagopaTransactionDTO("partner1", "institution1", date, "station1", 100);

        dto1.setId(1L);
        dto2.setId(1L);

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());

        dto2.setId(2L);
        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void testToStringContainsFields() {
        LocalDate date = LocalDate.of(2025, 10, 31);
        PagopaTransactionDTO dto = new PagopaTransactionDTO("partner1", "institution1", date, "station1", 100);
        dto.setId(1L);

        String str = dto.toString();
        assertThat(str).contains("id=1");
        assertThat(str).contains("cfPartner='partner1'");
        assertThat(str).contains("cfInstitution='institution1'");
        assertThat(str).contains("date=2025-10-31");
        assertThat(str).contains("station='station1'");
        assertThat(str).contains("transactionTotal=100");
    }
}
