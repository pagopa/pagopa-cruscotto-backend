package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.KpiA2AnalyticIncorrectTaxonomyData;
import com.nexigroup.pagopa.cruscotto.repository.KpiA2AnalyticIncorrectTaxonomyDataRepository;
import com.nexigroup.pagopa.cruscotto.service.KpiA2AnalyticIncorrectTaxonomyDataService;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiA2AnalyticIncorrectTaxonomyDataDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KpiA2AnalyticIncorrectTaxonomyDataServiceImpl implements KpiA2AnalyticIncorrectTaxonomyDataService {
    private final KpiA2AnalyticIncorrectTaxonomyDataRepository repository;

    @Override
    public void saveAll(List<KpiA2AnalyticIncorrectTaxonomyDataDTO> incorrectTaxonomyDataList) {

        List<KpiA2AnalyticIncorrectTaxonomyData> entities = incorrectTaxonomyDataList.stream()
            .map(dto -> {
                KpiA2AnalyticIncorrectTaxonomyData entity = new KpiA2AnalyticIncorrectTaxonomyData();
                entity.setKpiA2AnalyticDataId(dto.getKpiA2AnalyticDataId());
                entity.setTransferCategory(dto.getTransferCategory());
                entity.setTotPayments(dto.getTotPayments());
                entity.setTotIncorrectPayments(dto.getTotIncorrectPayments());
                entity.setFromHour(dto.getFromHour());
                entity.setEndHour(dto.getEndHour());
                return entity;
            })
            .collect(Collectors.toList());
        repository.saveAll(entities);
    }

    @Override
    public List<KpiA2AnalyticIncorrectTaxonomyDataDTO> findByKpiA2AnalyticDataId(Long analyticDataId) {
        return repository.findByKpiA2AnalyticDataIdOrderByFromHourAsc(analyticDataId).stream()
            .map(entity -> {
                KpiA2AnalyticIncorrectTaxonomyDataDTO dto = new KpiA2AnalyticIncorrectTaxonomyDataDTO();
                dto.setId(entity.getId());
                dto.setKpiA2AnalyticDataId(entity.getKpiA2AnalyticDataId());
                dto.setTransferCategory(entity.getTransferCategory());
                dto.setTotPayments(entity.getTotPayments());
                dto.setTotIncorrectPayments(entity.getTotIncorrectPayments());
                dto.setFromHour(entity.getFromHour());
                dto.setEndHour(entity.getEndHour());
                return dto;
            })
            .collect(Collectors.toList());
    }
}
