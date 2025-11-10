package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.PagopaTransaction;
import com.nexigroup.pagopa.cruscotto.service.dto.PagopaTransactionDTO;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PagopaTransazioniServiceImplTest {

    @Mock
    private QueryBuilder queryBuilder;

    @BeforeEach
    void setUp() {
        PagopaTransazioniServiceImpl service = new PagopaTransazioniServiceImpl(null, queryBuilder);
    }

    @Test
    void testConvertToDTOAndEntity() {
        PagopaTransaction entity = new PagopaTransaction();
        entity.setId(1L);
        entity.setCfPartner("P1");
        entity.setCfInstitution("I1");
        entity.setDate(LocalDate.now());
        entity.setStation("S1");
        entity.setTransactionTotal(50);

        PagopaTransactionDTO dto = PagopaTransazioniServiceImpl.convertToDTO(entity);

        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getCfPartner(), dto.getCfPartner());

        PagopaTransaction convertedEntity = PagopaTransazioniServiceImpl.convertToEntity(dto);
        assertEquals(dto.getId(), convertedEntity.getId());
        assertEquals(dto.getCfInstitution(), convertedEntity.getCfInstitution());
    }
}
