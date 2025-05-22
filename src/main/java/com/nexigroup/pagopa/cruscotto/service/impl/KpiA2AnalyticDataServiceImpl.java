package com.nexigroup.pagopa.cruscotto.service.impl;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.InstanceModule;
import com.nexigroup.pagopa.cruscotto.domain.KpiA2AnalyticData;
import com.nexigroup.pagopa.cruscotto.domain.KpiA2DetailResult;
import com.nexigroup.pagopa.cruscotto.repository.InstanceModuleRepository;
import com.nexigroup.pagopa.cruscotto.repository.InstanceRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiA2AnalyticDataRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiA2DetailResultRepository;
import com.nexigroup.pagopa.cruscotto.service.KpiA2AnalyticDataService;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiA2AnalyticDataDTO;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link KpiA2AnalyticData}.
 */

@Service
@Transactional
public class KpiA2AnalyticDataServiceImpl implements KpiA2AnalyticDataService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KpiA2AnalyticDataServiceImpl.class);
    

    private final InstanceRepository instanceRepository;

    private final InstanceModuleRepository instanceModuleRepository;

    private final KpiA2AnalyticDataRepository kpiA2AnalyticDataRepository;

    private final KpiA2DetailResultRepository kpiA2DetailResultRepository;

    private final QueryBuilder queryBuilder;
    

    public KpiA2AnalyticDataServiceImpl(InstanceRepository instanceRepository, InstanceModuleRepository instanceModuleRepository,
    									KpiA2AnalyticDataRepository kpiA2AnalyticDataRepository, KpiA2DetailResultRepository kpiA2DetailResultRepository,
    									QueryBuilder queryBuilder ) {
        this.instanceRepository = instanceRepository;
        this.instanceModuleRepository = instanceModuleRepository;
        this.kpiA2AnalyticDataRepository = kpiA2AnalyticDataRepository;
        this.kpiA2DetailResultRepository = kpiA2DetailResultRepository;
        this.queryBuilder = queryBuilder;
    }

    /**
     * Save all kpiA2AnalyticData.
     *
     * @param kpiA2AnalyticDataDTOS the entities to save.
     */
    @Override
    public void saveAll(List<KpiA2AnalyticDataDTO> kpiA2AnalyticDataDTOS) {
        kpiA2AnalyticDataDTOS.forEach(kpiA2AnalyticDataDTO -> {
            Instance instance = instanceRepository.findById(kpiA2AnalyticDataDTO.getInstanceId())
            									  .orElseThrow(() -> new IllegalArgumentException("Instance not found"));

            InstanceModule instanceModule = instanceModuleRepository.findById(kpiA2AnalyticDataDTO.getInstanceModuleId())
            														.orElseThrow(() -> new IllegalArgumentException("InstanceModule not found"));

            KpiA2DetailResult kpiA2DetailResult = kpiA2DetailResultRepository.findById(kpiA2AnalyticDataDTO.getKpiA2DetailResultId())
            																 .orElseThrow(() -> new IllegalArgumentException("KpiA2DetailResult not found"));

            KpiA2AnalyticData kpiA2AnalyticData = getkpiA2AnalyticData(kpiA2AnalyticDataDTO, instance, instanceModule, kpiA2DetailResult);

            kpiA2AnalyticDataRepository.save(kpiA2AnalyticData);
        });
    }

    private static @NotNull KpiA2AnalyticData getkpiA2AnalyticData(KpiA2AnalyticDataDTO kpiA2AnalyticDataDTO, Instance instance,
    															   InstanceModule instanceModule, KpiA2DetailResult kpiA2DetailResult) {
        KpiA2AnalyticData kpiA2AnalyticData = new KpiA2AnalyticData();
        kpiA2AnalyticData.setInstance(instance);
        kpiA2AnalyticData.setInstanceModule(instanceModule);
        kpiA2AnalyticData.setAnalysisDate(kpiA2AnalyticDataDTO.getAnalysisDate());
        kpiA2AnalyticData.setEvaluationDate(kpiA2AnalyticDataDTO.getEvaluationDate());
        kpiA2AnalyticData.setTotPayments(kpiA2AnalyticDataDTO.getTotPayments());
        kpiA2AnalyticData.setTotIncorrectPayments(kpiA2AnalyticDataDTO.getTotIncorrectPayments());
        kpiA2AnalyticData.setKpiA2DetailResult(kpiA2DetailResult);

        return kpiA2AnalyticData;
    }

	@Override
	public int deleteAllByInstanceModule(long instanceModuleId) {
		return kpiA2AnalyticDataRepository.deleteAllByInstanceModuleId(instanceModuleId);	
	}
	
    @Override
    public List<KpiA2AnalyticDataDTO> findByInstanceModuleId(long instanceModuleId) {
        return kpiA2AnalyticDataRepository
            .selectByInstanceModuleId(instanceModuleId)
            .stream()
            .map(KpiA2AnalyticDataServiceImpl::getkpiA2AnalyticDataDTO)
            .collect(Collectors.toList());
    }
    
    private static @NotNull KpiA2AnalyticDataDTO getkpiA2AnalyticDataDTO(KpiA2AnalyticData kpiA2AnalyticData) {
        KpiA2AnalyticDataDTO kpiA2AnalyticDataDTO = new KpiA2AnalyticDataDTO();
        kpiA2AnalyticDataDTO.setId(kpiA2AnalyticData.getId());
        kpiA2AnalyticDataDTO.setInstanceId(kpiA2AnalyticData.getInstance() != null ? kpiA2AnalyticData.getInstance().getId() : null);
        kpiA2AnalyticDataDTO.setInstanceModuleId(
            kpiA2AnalyticData.getInstanceModule() != null ? kpiA2AnalyticData.getInstanceModule().getId() : null
        );
        kpiA2AnalyticDataDTO.setAnalysisDate(kpiA2AnalyticData.getAnalysisDate());
        kpiA2AnalyticDataDTO.setEvaluationDate(kpiA2AnalyticData.getEvaluationDate());
        kpiA2AnalyticDataDTO.setTotPayments(kpiA2AnalyticData.getTotPayments());
        kpiA2AnalyticDataDTO.setTotIncorrectPayments(kpiA2AnalyticData.getTotIncorrectPayments());
        kpiA2AnalyticDataDTO.setKpiA2DetailResultId(
            kpiA2AnalyticData.getKpiA2DetailResult() != null ? kpiA2AnalyticData.getKpiA2DetailResult().getId() : null
        );
        return kpiA2AnalyticDataDTO;
    }    
}
