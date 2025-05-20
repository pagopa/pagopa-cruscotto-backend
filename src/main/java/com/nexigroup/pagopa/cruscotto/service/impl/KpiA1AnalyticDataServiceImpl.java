package com.nexigroup.pagopa.cruscotto.service.impl;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nexigroup.pagopa.cruscotto.domain.AnagStation;
import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.InstanceModule;
import com.nexigroup.pagopa.cruscotto.domain.KpiA1AnalyticData;
import com.nexigroup.pagopa.cruscotto.domain.KpiA1DetailResult;
import com.nexigroup.pagopa.cruscotto.repository.AnagStationRepository;
import com.nexigroup.pagopa.cruscotto.repository.InstanceModuleRepository;
import com.nexigroup.pagopa.cruscotto.repository.InstanceRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiA1AnalyticDataRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiA1DetailResultRepository;
import com.nexigroup.pagopa.cruscotto.service.KpiA1AnalyticDataService;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiA1AnalyticDataDTO;
import com.nexigroup.pagopa.cruscotto.service.qdsl.QueryBuilder;

import java.util.List;

/**
 * Service Implementation for managing {@link KpiA1AnalyticData}.
 */
@Service
@Transactional
public class KpiA1AnalyticDataServiceImpl implements KpiA1AnalyticDataService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KpiA1AnalyticDataServiceImpl.class);

    private final AnagStationRepository anagStationRepository;

    private final InstanceRepository instanceRepository;

    private final InstanceModuleRepository instanceModuleRepository;

    private final KpiA1AnalyticDataRepository kpiA1AnalyticDataRepository;

    private final KpiA1DetailResultRepository kpiA1DetailResultRepository;

    private final QueryBuilder queryBuilder;
    

    public KpiA1AnalyticDataServiceImpl(AnagStationRepository anagStationRepository, InstanceRepository instanceRepository,
    									InstanceModuleRepository instanceModuleRepository, KpiA1AnalyticDataRepository kpiA1AnalyticDataRepository,
    									KpiA1DetailResultRepository kpiA1DetailResultRepository, QueryBuilder queryBuilder ) {
        this.anagStationRepository = anagStationRepository;
        this.instanceRepository = instanceRepository;
        this.instanceModuleRepository = instanceModuleRepository;
        this.kpiA1AnalyticDataRepository = kpiA1AnalyticDataRepository;
        this.kpiA1DetailResultRepository = kpiA1DetailResultRepository;
        this.queryBuilder = queryBuilder;
    }

    /**
     * Save all kpiA1AnalyticData.
     *
     * @param kpiA1AnalyticDataDTOS the entities to save.
     */
    @Override
    public void saveAll(List<KpiA1AnalyticDataDTO> kpiA1AnalyticDataDTOS) {
        kpiA1AnalyticDataDTOS.forEach(kpiA1AnalyticDataDTO -> {
            Instance instance = instanceRepository.findById(kpiA1AnalyticDataDTO.getInstanceId())
            									  .orElseThrow(() -> new IllegalArgumentException("Instance not found"));

            InstanceModule instanceModule = instanceModuleRepository.findById(kpiA1AnalyticDataDTO.getInstanceModuleId())
            														.orElseThrow(() -> new IllegalArgumentException("InstanceModule not found"));

            AnagStation station = anagStationRepository.findById(kpiA1AnalyticDataDTO.getStationId())
            										   .orElseThrow(() -> new IllegalArgumentException("Station not found"));

            KpiA1DetailResult kpiA1DetailResult = kpiA1DetailResultRepository.findById(kpiA1AnalyticDataDTO.getKpiA1DetailResultId())
            																 .orElseThrow(() -> new IllegalArgumentException("KpiA1DetailResult not found"));

            KpiA1AnalyticData kpiA1AnalyticData = getkpiA1AnalyticData(kpiA1AnalyticDataDTO, instance, instanceModule, station, kpiA1DetailResult);

            kpiA1AnalyticDataRepository.save(kpiA1AnalyticData);
        });
    }

    private static @NotNull KpiA1AnalyticData getkpiA1AnalyticData(KpiA1AnalyticDataDTO kpiA1AnalyticDataDTO, Instance instance,
    															   InstanceModule instanceModule, AnagStation station, KpiA1DetailResult kpiA1DetailResult) {
        KpiA1AnalyticData kpiA1AnalyticData = new KpiA1AnalyticData();
        kpiA1AnalyticData.setInstance(instance);
        kpiA1AnalyticData.setInstanceModule(instanceModule);
        kpiA1AnalyticData.setAnalysisDate(kpiA1AnalyticDataDTO.getAnalysisDate());
        kpiA1AnalyticData.setStation(station);
        kpiA1AnalyticData.setMethod(kpiA1AnalyticDataDTO.getMethod());
        kpiA1AnalyticData.setEvaluationDate(kpiA1AnalyticDataDTO.getEvaluationDate());
        kpiA1AnalyticData.setTotReq(kpiA1AnalyticDataDTO.getTotReq());
        kpiA1AnalyticData.setReqOk(kpiA1AnalyticDataDTO.getReqOk());
        kpiA1AnalyticData.setReqTimeoutReal(kpiA1AnalyticDataDTO.getReqTimeoutReal());
        kpiA1AnalyticData.setReqTimeoutValid(kpiA1AnalyticDataDTO.getReqTimeoutValid());
        kpiA1AnalyticData.setKpiA1DetailResult(kpiA1DetailResult);

        return kpiA1AnalyticData;
    }

	@Override
	public int deleteAllByInstanceModule(long instanceModuleId) {
		return kpiA1AnalyticDataRepository.deleteAllByInstanceModuleId(instanceModuleId);	
	}
}
