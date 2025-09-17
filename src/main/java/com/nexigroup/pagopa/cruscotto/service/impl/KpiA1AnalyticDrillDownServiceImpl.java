package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.service.KpiA1AnalyticDrillDownService;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiA1AnalyticDrillDownDTO;
import com.nexigroup.pagopa.cruscotto.repository.KpiA1AnalyticDrillDownRepository;
import com.nexigroup.pagopa.cruscotto.domain.KpiA1AnalyticDrillDown;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KpiA1AnalyticDrillDownServiceImpl implements KpiA1AnalyticDrillDownService {
    private final KpiA1AnalyticDrillDownRepository repository;

    @Override
    public List<KpiA1AnalyticDrillDownDTO> findByKpiA1AnalyticDataId(Long analyticDataId) {
        return repository.findByKpiA1AnalyticDataId(analyticDataId)
            .stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    @Override
    public void saveAll(List<KpiA1AnalyticDrillDownDTO> drillDowns) {
        List<KpiA1AnalyticDrillDown> entities = drillDowns.stream()
            .map(this::toEntity)
            .collect(Collectors.toList());
        repository.saveAll(entities);
    }

    private KpiA1AnalyticDrillDownDTO toDto(KpiA1AnalyticDrillDown entity) {
        KpiA1AnalyticDrillDownDTO dto = new KpiA1AnalyticDrillDownDTO();
        dto.setId(entity.getId());
        dto.setKpiA1AnalyticDataId(entity.getKpiA1AnalyticDataId());
        dto.setFromHour(entity.getFromHour());
        dto.setToHour(entity.getToHour());
        dto.setTotalRequests(entity.getTotalRequests());
        dto.setOkRequests(entity.getOkRequests());
        dto.setAverageTimeMs(entity.getAverageTimeMs());
        return dto;
    }

    private KpiA1AnalyticDrillDown toEntity(KpiA1AnalyticDrillDownDTO dto) {
        KpiA1AnalyticDrillDown entity = new KpiA1AnalyticDrillDown();
        entity.setId(dto.getId());
        entity.setKpiA1AnalyticDataId(dto.getKpiA1AnalyticDataId());
        entity.setFromHour(dto.getFromHour());
        entity.setToHour(dto.getToHour());
        entity.setTotalRequests(dto.getTotalRequests());
        entity.setOkRequests(dto.getOkRequests());
        entity.setAverageTimeMs(dto.getAverageTimeMs());
        return entity;
    }
}
