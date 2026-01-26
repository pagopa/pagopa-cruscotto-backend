package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.domain.KpiC1DetailResult;
import com.nexigroup.pagopa.cruscotto.repository.KpiC1DetailResultRepository;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiC1DetailResultDTO;
import com.nexigroup.pagopa.cruscotto.service.mapper.KpiC1DetailResultMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class KpiC1DetailResultServiceTest {

    @Mock
    private KpiC1DetailResultRepository repository;

    @Mock
    private KpiC1DetailResultMapper mapper;

    @InjectMocks
    private KpiC1DetailResultService service;

    private KpiC1DetailResult entity;
    private KpiC1DetailResultDTO dto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        entity = new KpiC1DetailResult();
        dto = new KpiC1DetailResultDTO();
    }

    @Test
    void testSave() {
        when(repository.save(entity)).thenReturn(entity);

        KpiC1DetailResult result = service.save(entity);

        verify(repository).save(entity);
        assertThat(result).isEqualTo(entity);
    }

    @Test
    void testSaveAll() {
        List<KpiC1DetailResult> list = Arrays.asList(entity, entity);
        when(repository.saveAll(list)).thenReturn(list);

        List<KpiC1DetailResult> result = service.saveAll(list);

        verify(repository).saveAll(list);
        assertThat(result).hasSize(2);
    }

    @Test
    void testFindByInstanceAndInstanceModuleAndReferenceDateAndCfInstitution() {
        when(repository.findByInstanceAndInstanceModuleAndReferenceDateAndCfInstitution(1L, 2L, LocalDate.now(), "CF"))
            .thenReturn(Optional.of(entity));

        Optional<KpiC1DetailResult> result = service.findByInstanceAndInstanceModuleAndReferenceDateAndCfInstitution(
            1L, 2L, LocalDate.now(), "CF"
        );

        verify(repository).findByInstanceAndInstanceModuleAndReferenceDateAndCfInstitution(1L, 2L, LocalDate.now(), "CF");
        assertThat(result).isPresent();
    }

    @Test
    void testFindByInstanceIdAndReferenceDate() {
        when(repository.findByInstanceIdAndReferenceDate(1L, LocalDate.now())).thenReturn(List.of(entity));

        List<KpiC1DetailResult> result = service.findByInstanceIdAndReferenceDate(1L, LocalDate.now());

        verify(repository).findByInstanceIdAndReferenceDate(1L, LocalDate.now());
        assertThat(result).hasSize(1);
    }

    @Test
    void testFindByCfInstitution() {
        when(repository.findByCfInstitution("CF")).thenReturn(List.of(entity));

        List<KpiC1DetailResult> result = service.findByCfInstitution("CF");

        verify(repository).findByCfInstitution("CF");
        assertThat(result).hasSize(1);
    }

    @Test
    void testFindByInstanceIdAndReferenceDateBetween() {
        LocalDate start = LocalDate.now().minusDays(1);
        LocalDate end = LocalDate.now();
        when(repository.findByInstanceIdAndReferenceDateBetween(1L, start, end)).thenReturn(List.of(entity));

        List<KpiC1DetailResult> result = service.findByInstanceIdAndReferenceDateBetween(1L, start, end);

        verify(repository).findByInstanceIdAndReferenceDateBetween(1L, start, end);
        assertThat(result).hasSize(1);
    }

    @Test
    void testFindNonCompliantByReferenceDate() {
        LocalDate date = LocalDate.now();
        when(repository.findNonCompliantByReferenceDate(date)).thenReturn(List.of(entity));

        List<KpiC1DetailResult> result = service.findNonCompliantByReferenceDate(date);

        verify(repository).findNonCompliantByReferenceDate(date);
        assertThat(result).hasSize(1);
    }

    @Test
    void testFindCompliantByReferenceDate() {
        LocalDate date = LocalDate.now();
        when(repository.findCompliantByReferenceDate(date)).thenReturn(List.of(entity));

        List<KpiC1DetailResult> result = service.findCompliantByReferenceDate(date);

        verify(repository).findCompliantByReferenceDate(date);
        assertThat(result).hasSize(1);
    }

    @Test
    void testCountCompliantByReferenceDate() {
        LocalDate date = LocalDate.now();
        when(repository.countCompliantByReferenceDate(date)).thenReturn(5L);

        long result = service.countCompliantByReferenceDate(date);

        verify(repository).countCompliantByReferenceDate(date);
        assertThat(result).isEqualTo(5L);
    }

    @Test
    void testCountTotalByReferenceDate() {
        LocalDate date = LocalDate.now();
        when(repository.countTotalByReferenceDate(date)).thenReturn(10L);

        long result = service.countTotalByReferenceDate(date);

        verify(repository).countTotalByReferenceDate(date);
        assertThat(result).isEqualTo(10L);
    }

    @Test
    void testFindBelowThresholdByReferenceDate() {
        LocalDate date = LocalDate.now();
        BigDecimal threshold = BigDecimal.TEN;
        when(repository.findBelowThresholdByReferenceDate(date, threshold)).thenReturn(List.of(entity));

        List<KpiC1DetailResult> result = service.findBelowThresholdByReferenceDate(date, threshold);

        verify(repository).findBelowThresholdByReferenceDate(date, threshold);
        assertThat(result).hasSize(1);
    }

    @Test
    void testDeleteAll() {
        doNothing().when(repository).deleteAll();

        service.deleteAll();

        verify(repository).deleteAll();
    }

    @Test
    void testFindByResultId() {
        when(repository.findByResultId(1L)).thenReturn(List.of(entity));
        when(mapper.toDto(entity)).thenReturn(dto);

        List<KpiC1DetailResultDTO> result = service.findByResultId(1L);

        verify(repository).findByResultId(1L);
        verify(mapper).toDto(entity);
        assertThat(result).contains(dto);
    }

    @Test
    void testDeleteByReferenceDateBefore() {
        LocalDate cutoff = LocalDate.now();
        doNothing().when(repository).deleteByReferenceDateBefore(cutoff);

        service.deleteByReferenceDateBefore(cutoff);

        verify(repository).deleteByReferenceDateBefore(cutoff);
    }
}
