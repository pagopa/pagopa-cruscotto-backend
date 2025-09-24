package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.KpiB2AnalyticDrillDown;
import com.nexigroup.pagopa.cruscotto.repository.KpiB2AnalyticDrillDownRepository;
import com.nexigroup.pagopa.cruscotto.service.KpiB2AnalyticDrillDownService;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB2AnalyticDrillDownDTO;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class KpiB2AnalyticDrillDownServiceImpl implements KpiB2AnalyticDrillDownService {
    private final KpiB2AnalyticDrillDownRepository repository;

    @Override
    public void saveAll(List<KpiB2AnalyticDrillDownDTO> drillDowns) {
        
        List<KpiB2AnalyticDrillDown> entities = drillDowns.stream()
            .map(dto -> {
                KpiB2AnalyticDrillDown entity = new KpiB2AnalyticDrillDown();
                entity.setKpiB2AnalyticDataId(dto.getKpiB2AnalyticDataId());
                entity.setTotalRequests(dto.getTotalRequests());
                entity.setOkRequests(dto.getOkRequests());
                entity.setAverageTimeMs(dto.getAverageTimeMs());
                entity.setFromHour(dto.getFromHour());
                entity.setEndHour(dto.getEndHour());
                return entity;
            })
            .collect(Collectors.toList());
        repository.saveAll(entities);
    }

    @Override
    public List<KpiB2AnalyticDrillDownDTO> findByKpiB2AnalyticDataId(Long kpiB2AnalyticDataId) {
        return repository.findByKpiB2AnalyticDataIdOrderByFromHourAsc(kpiB2AnalyticDataId).stream()
            .map(entity -> {
                KpiB2AnalyticDrillDownDTO dto = new KpiB2AnalyticDrillDownDTO();
                dto.setId(entity.getId());
                dto.setKpiB2AnalyticDataId(entity.getKpiB2AnalyticDataId());
                dto.setTotalRequests(entity.getTotalRequests());
                dto.setOkRequests(entity.getOkRequests());
                dto.setAverageTimeMs(entity.getAverageTimeMs());
                dto.setFromHour(entity.getFromHour());
                dto.setEndHour(entity.getEndHour());
                return dto;
            })
            .collect(Collectors.toList());
    }

    @Override
    public void deleteByKpiB2AnalyticDataIds(List<Long> analyticDataIds) {
        if (analyticDataIds == null || analyticDataIds.isEmpty()) return;
        repository.deleteByKpiB2AnalyticDataIdIn(analyticDataIds);
    }
}
