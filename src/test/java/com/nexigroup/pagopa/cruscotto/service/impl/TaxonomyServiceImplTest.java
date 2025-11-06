package com.nexigroup.pagopa.cruscotto.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.nexigroup.pagopa.cruscotto.domain.Taxonomy;
import com.nexigroup.pagopa.cruscotto.repository.TaxonomyRepository;
import com.nexigroup.pagopa.cruscotto.service.dto.TaxonomyDTO;
import com.nexigroup.pagopa.cruscotto.service.filter.TaxonomyFilter;
import com.nexigroup.pagopa.cruscotto.service.mapper.TaxonomyMapper;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.jpa.impl.JPAQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("TaxonomyServiceImpl Tests")
class TaxonomyServiceImplTest {

    @Mock
    private TaxonomyRepository taxonomyRepository;

    @Mock
    private TaxonomyMapper taxonomyMapper;

    @Mock
    private QueryBuilder queryBuilder;

    @InjectMocks
    private TaxonomyServiceImpl taxonomyService;

    @Test
    void testSave() {
        TaxonomyDTO dto = new TaxonomyDTO();
        dto.setTakingsIdentifier("TID123");
        dto.setValidityStartDate(LocalDate.now());
        dto.setValidityEndDate(LocalDate.now().plusDays(1));

        Taxonomy entity = new Taxonomy();
        when(taxonomyRepository.save(any(Taxonomy.class))).thenReturn(entity);
        when(taxonomyMapper.toDto(entity)).thenReturn(dto);

        TaxonomyDTO result = taxonomyService.save(dto);

        assertThat(result).isEqualTo(dto);
        verify(taxonomyRepository, times(1)).save(any(Taxonomy.class));
        verify(taxonomyMapper, times(1)).toDto(entity);
    }

    @Test
    void testSaveAll() {
        TaxonomyDTO dto1 = new TaxonomyDTO();
        TaxonomyDTO dto2 = new TaxonomyDTO();
        List<TaxonomyDTO> dtos = Arrays.asList(dto1, dto2);

        List<Taxonomy> entities = Arrays.asList(new Taxonomy(), new Taxonomy());
        when(taxonomyMapper.toEntity(dtos)).thenReturn(entities);

        taxonomyService.saveAll(dtos);

        verify(taxonomyRepository, times(1)).saveAll(entities);
    }

    @Test
    void testCountAll() {
        when(taxonomyRepository.count()).thenReturn(5L);

        Long count = taxonomyService.countAll();

        assertThat(count).isEqualTo(5L);
        verify(taxonomyRepository, times(1)).count();
    }

    @Test
    void testDeleteAll() {
        doNothing().when(taxonomyRepository).delete();

        taxonomyService.deleteAll();

        verify(taxonomyRepository, times(1)).delete();
    }

    @Test
    void testGetAllUpdatedTakingsIdentifiers() {
        List<String> identifiers = Arrays.asList("TID1", "TID2");
        when(taxonomyRepository.findAllUpdatedTakingsIdentifiers()).thenReturn(identifiers);

        List<String> result = taxonomyService.getAllUpdatedTakingsIdentifiers();

        assertThat(result).isEqualTo(identifiers);
        verify(taxonomyRepository, times(1)).findAllUpdatedTakingsIdentifiers();
    }

    @Test
    void testFindOne() {
        Taxonomy entity = new Taxonomy();
        TaxonomyDTO dto = new TaxonomyDTO();
        when(taxonomyRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(taxonomyMapper.toDto(entity)).thenReturn(dto);

        Optional<TaxonomyDTO> result = taxonomyService.findOne(1L);

        assertThat(result).isPresent().contains(dto);
        verify(taxonomyRepository, times(1)).findById(1L);
        verify(taxonomyMapper, times(1)).toDto(entity);
    }

    @Test
    void testFindAll() {
        // 1️⃣ Create a filter with a value to cover the "not null" branch
        TaxonomyFilter filter = new TaxonomyFilter();
        filter.setTakingsIdentifier("TID");
        Taxonomy tax1 = new Taxonomy(); // or use a mock
        Taxonomy tax2 = new Taxonomy(); // or use a mock

        // 2️⃣ Create a pageable with both ascending and descending orders to cover both branches
        Sort sort = Sort.by(
            Sort.Order.asc("name"),   // ascending
            Sort.Order.desc("id")     // descending
        );
        Pageable pageable = PageRequest.of(0, 10, sort);

        // 3️⃣ Mock JPAQuery
        JPAQuery<Taxonomy> mockQuery = mock(JPAQuery.class);
        Mockito.<JPAQuery<Taxonomy>>when(queryBuilder.<Taxonomy>createQuery()).thenReturn(mockQuery);
        when(mockQuery.from((EntityPath<?>) any())).thenReturn(mockQuery);
        when(mockQuery.where(any(BooleanBuilder.class))).thenReturn(mockQuery);
        lenient().when(mockQuery.fetch()).thenReturn(Arrays.asList(tax1, tax2));

        @SuppressWarnings("unchecked")
        JPAQuery<TaxonomyDTO> mockQueryDto = mock(JPAQuery.class);
        Mockito.<JPAQuery<TaxonomyDTO>>when(mockQuery.<TaxonomyDTO>select((Expression<TaxonomyDTO>) any()))
            .thenReturn(mockQueryDto);

        when(mockQueryDto.offset(anyLong())).thenReturn(mockQueryDto);
        when(mockQueryDto.limit(anyLong())).thenReturn(mockQueryDto);
        when(mockQueryDto.fetch()).thenReturn(List.of(new TaxonomyDTO(), new TaxonomyDTO()));

        // 4️⃣ Execute the method
        Page<TaxonomyDTO> result = taxonomyService.findAll(filter, pageable);

        // 5️⃣ Assertions
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).hasSize(2);
    }

}
