package com.nexigroup.pagopa.cruscotto.service.impl;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.nexigroup.pagopa.cruscotto.domain.QrtzLogTriggerExecuted;
import com.nexigroup.pagopa.cruscotto.repository.QrtzLogTriggerExecutedRepository;
import com.nexigroup.pagopa.cruscotto.service.dto.QrtzLogTriggerExecutedDTO;
import com.nexigroup.pagopa.cruscotto.service.mapper.QrtzLogTriggerExecutedMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("QrtzLogTriggerExecutedServiceImpl Tests")
class QrtzLogTriggerExecutedServiceImplTest {

    @Mock
    private QrtzLogTriggerExecutedRepository repository;

    @Mock
    private QrtzLogTriggerExecutedMapper mapper;

    @InjectMocks
    private QrtzLogTriggerExecutedServiceImpl service;

    private QrtzLogTriggerExecuted entity;
    private QrtzLogTriggerExecutedDTO dto;

    @BeforeEach
    void setUp() {

        entity = new QrtzLogTriggerExecuted();
        entity.setId(1L);
        entity.setState("PENDING");

        dto = new QrtzLogTriggerExecutedDTO();
        dto.setId(1L);
        dto.setState("PENDING");
    }

    @Test
    void testSave() {
        when(mapper.toEntity(dto)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toDto(entity)).thenReturn(dto);

        QrtzLogTriggerExecutedDTO result = service.save(dto);

        assertNotNull(result);
        assertEquals(dto.getId(), result.getId());

        verify(repository, times(1)).save(entity);
        verify(mapper, times(1)).toDto(entity);
    }

    @Test
    void testJobToBeExecuted() {
        Instant now = Instant.now();
        when(repository.findFirstByFireInstanceId("123")).thenReturn(Optional.of(entity));

        service.jobToBeExecuted("123", now);

        assertEquals(now, entity.getInitFiredTime());
        assertEquals("EXECUTING", entity.getState());
        verify(repository).save(entity);
    }

    @Test
    void testJobToBeExecuted_NotFound() {
        when(repository.findFirstByFireInstanceId("123")).thenReturn(Optional.empty());

        Instant now = Instant.now();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> service.jobToBeExecuted("123", now));

        assertTrue(exception.getMessage().contains("Quartz log trigger executed not found"));
    }

    @Test
    void testJobWasExecuted_Success() {
        Instant now = Instant.now();
        when(repository.findFirstByFireInstanceId("123")).thenReturn(Optional.of(entity));

        service.jobWasExecuted("123", now, null);

        assertEquals(now, entity.getEndFiredTime());
        assertEquals("COMPLETED", entity.getState());
        assertNull(entity.getMessageException());
        verify(repository).save(entity);
    }

    @Test
    void testJobWasExecuted_Error() {
        Instant now = Instant.now();
        byte[] exceptionBytes = "Error".getBytes();
        when(repository.findFirstByFireInstanceId("123")).thenReturn(Optional.of(entity));

        service.jobWasExecuted("123", now, exceptionBytes);

        assertEquals(now, entity.getEndFiredTime());
        assertEquals("ERROR", entity.getState());
        assertArrayEquals(exceptionBytes, entity.getMessageException());
        verify(repository).save(entity);
    }

    @Test
    void testFindByScheduledTimeBefore() {
        Instant now = Instant.now();
        QrtzLogTriggerExecuted e2 = new QrtzLogTriggerExecuted();
        e2.setId(2L);

        when(repository.findByScheduledTimeBefore(now)).thenReturn(Arrays.asList(entity, e2));

        List<Long> result = service.findByScheduledTimeBefore(now);

        assertEquals(2, result.size());
        assertTrue(result.contains(1L));
        assertTrue(result.contains(2L));
    }

    @Test
    void testDeleteById() {
        service.deleteById(1L);
        verify(repository).deleteById(1L);
    }
}
