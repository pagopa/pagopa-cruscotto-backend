package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.InstanceModule;
import com.nexigroup.pagopa.cruscotto.domain.AnagStation;
import com.nexigroup.pagopa.cruscotto.domain.KpiB3AnalyticData;
import com.nexigroup.pagopa.cruscotto.domain.PagopaNumeroStandin;
import com.nexigroup.pagopa.cruscotto.domain.PagopaNumeroStandinDrilldown;
import com.nexigroup.pagopa.cruscotto.repository.PagopaNumeroStandinDrilldownRepository;
import com.nexigroup.pagopa.cruscotto.service.PagopaNumeroStandinDrilldownService;
import com.nexigroup.pagopa.cruscotto.service.dto.PagopaNumeroStandinDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link com.nexigroup.pagopa.cruscotto.domain.PagopaNumeroStandinDrilldown}.
 * 
 * This service manages historical snapshots of Stand-In data for KPI B.3 drilldown analysis.
 * It ensures that drilldown data remains consistent even if the original Stand-In records change.
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PagopaNumeroStandinDrilldownServiceImpl implements PagopaNumeroStandinDrilldownService {

    private final PagopaNumeroStandinDrilldownRepository repository;

    @Override
    public void saveStandInSnapshot(
            Instance instance,
            InstanceModule instanceModule,
            AnagStation station,
            KpiB3AnalyticData kpiB3AnalyticData,
            LocalDate analysisDate,
            List<PagopaNumeroStandin> standInData) {

        log.debug("Saving Stand-In snapshot for KPI B.3 analytic data ID: {}, records count: {}", 
                 kpiB3AnalyticData.getId(), standInData.size());

        List<PagopaNumeroStandinDrilldown> drilldownRecords = new ArrayList<>();
        addToBatch(drilldownRecords, instance, instanceModule, station, kpiB3AnalyticData, analysisDate, standInData);
        
        int savedCount = saveBatch(drilldownRecords);
        
        log.info("Saved {} Stand-In drilldown records for KPI B.3 analytic data ID: {}", 
                savedCount, kpiB3AnalyticData.getId());
    }

    @Override
    public void addToBatch(
            List<PagopaNumeroStandinDrilldown> batch,
            Instance instance,
            InstanceModule instanceModule,
            AnagStation station,
            KpiB3AnalyticData kpiB3AnalyticData,
            LocalDate analysisDate,
            List<PagopaNumeroStandin> standInData) {

        for (PagopaNumeroStandin standin : standInData) {
            PagopaNumeroStandinDrilldown drilldown = new PagopaNumeroStandinDrilldown();
            
            // Set reference relationships
            drilldown.setInstance(instance);
            drilldown.setInstanceModule(instanceModule);
            drilldown.setStation(station);
            drilldown.setKpiB3AnalyticData(kpiB3AnalyticData);
            
            // Set analysis metadata
            drilldown.setAnalysisDate(analysisDate);
            drilldown.setOriginalStandinId(standin.getId());
            
            // Copy all Stand-In data fields (snapshot at analysis time)
            drilldown.setStationCode(standin.getStationCode());
            drilldown.setIntervalStart(standin.getIntervalStart());
            drilldown.setIntervalEnd(standin.getIntervalEnd());
            drilldown.setStandInCount(standin.getStandInCount());
            drilldown.setEventType(standin.getEventType());
            drilldown.setDataDate(standin.getDataDate());
            drilldown.setDataOraEvento(standin.getDataDate()); // Use dataDate as dataOraEvento for PagopaNumeroStandin
            drilldown.setLoadTimestamp(standin.getLoadTimestamp());
            
            batch.add(drilldown);
        }
    }

    @Override
    public int saveBatch(List<PagopaNumeroStandinDrilldown> batch) {
        if (batch.isEmpty()) {
            return 0;
        }
        
        log.debug("Saving batch of {} drilldown records", batch.size());
        List<PagopaNumeroStandinDrilldown> saved = repository.saveAll(batch);
        return saved.size();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PagopaNumeroStandinDTO> findByAnalyticDataId(Long analyticDataId) {
        log.debug("Finding Stand-In drilldown records for analytic data ID: {}", analyticDataId);
        
        List<PagopaNumeroStandinDrilldown> records = repository.findByKpiB3AnalyticDataId(analyticDataId);
        
        log.debug("Found {} drilldown records for analytic data ID: {}", records.size(), analyticDataId);
        
        return records.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PagopaNumeroStandinDTO convertToDTO(PagopaNumeroStandinDrilldown entity) {
        PagopaNumeroStandinDTO dto = new PagopaNumeroStandinDTO();
        
        // Copy all fields from entity to DTO
        dto.setId(entity.getId());
        dto.setStationCode(entity.getStationCode());
        dto.setIntervalStart(entity.getIntervalStart());
        dto.setIntervalEnd(entity.getIntervalEnd());
        dto.setStandInCount(entity.getStandInCount());
        dto.setEventType(entity.getEventType());
        dto.setDataDate(entity.getDataDate());
        dto.setDataOraEvento(entity.getDataOraEvento());
        dto.setLoadTimestamp(entity.getLoadTimestamp());
        
        // Set partner information from station
        if (entity.getStation() != null && entity.getStation().getAnagPartner() != null) {
            dto.setPartnerId(entity.getStation().getAnagPartner().getId());
            dto.setPartnerName(entity.getStation().getAnagPartner().getName());
            dto.setPartnerFiscalCode(entity.getStation().getAnagPartner().getFiscalCode());
        }
        
        return dto;
    }

    @Override
    public int deleteAllByInstanceModuleId(Long instanceModuleId) {
        log.info("Deleting all Stand-In drilldown records for instance module ID: {}", instanceModuleId);
        return repository.deleteAllByInstanceModuleId(instanceModuleId);
    }

    @Override
    public int deleteByInstanceModuleIdAndAnalysisDate(Long instanceModuleId, LocalDate analysisDate) {
        log.info("Deleting Stand-In drilldown records for instance module ID: {} and analysis date: {}", 
                instanceModuleId, analysisDate);
        return repository.deleteByInstanceModuleIdAndAnalysisDate(instanceModuleId, analysisDate);
    }
}