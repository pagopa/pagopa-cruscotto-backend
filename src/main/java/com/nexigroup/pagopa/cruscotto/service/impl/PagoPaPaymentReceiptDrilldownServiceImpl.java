package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.domain.AnagStation;
import com.nexigroup.pagopa.cruscotto.domain.Instance;
import com.nexigroup.pagopa.cruscotto.domain.InstanceModule;
import com.nexigroup.pagopa.cruscotto.domain.PagoPaPaymentReceiptDrilldown;
import com.nexigroup.pagopa.cruscotto.repository.AnagStationRepository;
import com.nexigroup.pagopa.cruscotto.repository.InstanceModuleRepository;
import com.nexigroup.pagopa.cruscotto.repository.InstanceRepository;
import com.nexigroup.pagopa.cruscotto.repository.PagoPaPaymentReceiptDrilldownRepository;
import com.nexigroup.pagopa.cruscotto.service.PagoPaPaymentReceiptDrilldownService;
import com.nexigroup.pagopa.cruscotto.service.dto.PagoPaPaymentReceiptDrilldownDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.PagoPaPaymentReceiptDTO;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link PagoPaPaymentReceiptDrilldown}.
 */
@Service
@Transactional
public class PagoPaPaymentReceiptDrilldownServiceImpl implements PagoPaPaymentReceiptDrilldownService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PagoPaPaymentReceiptDrilldownServiceImpl.class);

    private final PagoPaPaymentReceiptDrilldownRepository drilldownRepository;
    private final InstanceRepository instanceRepository;
    private final InstanceModuleRepository instanceModuleRepository;
    private final AnagStationRepository stationRepository;

    public PagoPaPaymentReceiptDrilldownServiceImpl(
        PagoPaPaymentReceiptDrilldownRepository drilldownRepository,
        InstanceRepository instanceRepository,
        InstanceModuleRepository instanceModuleRepository,
        AnagStationRepository stationRepository
    ) {
        this.drilldownRepository = drilldownRepository;
        this.instanceRepository = instanceRepository;
        this.instanceModuleRepository = instanceModuleRepository;
        this.stationRepository = stationRepository;
    }

    @Override
    public void saveQuarterHourAggregatedData(
        Long instanceId,
        Long instanceModuleId,
        Long stationId,
        LocalDate evaluationDate,
        LocalDate analysisDate,
        List<PagoPaPaymentReceiptDTO> rawData
    ) {
        LOGGER.debug("Saving quarter-hour aggregated data for instance={}, station={}, date={}", 
                     instanceId, stationId, evaluationDate);

        // Load entities
        Instance instance = instanceRepository.findById(instanceId)
            .orElseThrow(() -> new IllegalArgumentException("Instance not found with id: " + instanceId));
        InstanceModule instanceModule = instanceModuleRepository.findById(instanceModuleId)
            .orElseThrow(() -> new IllegalArgumentException("InstanceModule not found with id: " + instanceModuleId));
        AnagStation station = stationRepository.findById(stationId)
            .orElseThrow(() -> new IllegalArgumentException("Station not found with id: " + stationId));

        // Delegate to optimized method
        saveQuarterHourAggregatedDataOptimized(instance, instanceModule, station, evaluationDate, analysisDate, rawData);
    }

    @Override
    public void saveQuarterHourAggregatedDataOptimized(
        Instance instance,
        InstanceModule instanceModule,
        AnagStation station,
        LocalDate evaluationDate,
        LocalDate analysisDate,
        List<PagoPaPaymentReceiptDTO> rawData
    ) {
        LOGGER.debug("Saving quarter-hour aggregated data (optimized) for instance={}, station={}, date={}", 
                     instance.getId(), station.getId(), evaluationDate);

        // Create 96 quarter-hour slots for the day (00:00-00:15, 00:15-00:30, etc.)
        Map<LocalDateTime, long[]> quarterHourSlots = createQuarterHourSlots(evaluationDate);

        // Aggregate raw data into quarter-hour slots
        for (PagoPaPaymentReceiptDTO record : rawData) {
            LocalDateTime recordStart = record.getStartDate().atZone(ZoneOffset.systemDefault()).toLocalDateTime();
            LocalDateTime recordEnd = record.getEndDate().atZone(ZoneOffset.systemDefault()).toLocalDateTime();
            
            // Find the quarter-hour slot this record belongs to
            LocalDateTime slotStart = findQuarterHourSlot(recordStart);
            
            if (quarterHourSlots.containsKey(slotStart)) {
                long[] values = quarterHourSlots.get(slotStart);
                values[0] += record.getTotRes(); // totRes
                values[1] += record.getResOk();  // resOk
                values[2] += record.getResKo();  // resKo
            }
        }

        // Save aggregated data
        List<PagoPaPaymentReceiptDrilldown> drilldownRecords = new ArrayList<>();
        for (Map.Entry<LocalDateTime, long[]> entry : quarterHourSlots.entrySet()) {
            LocalDateTime slotStart = entry.getKey();
            LocalDateTime slotEnd = slotStart.plusMinutes(15);
            long[] values = entry.getValue();

            PagoPaPaymentReceiptDrilldown drilldown = new PagoPaPaymentReceiptDrilldown();
            drilldown.setInstance(instance);
            drilldown.setInstanceModule(instanceModule);
            drilldown.setStation(station);
            drilldown.setAnalysisDate(analysisDate);
            drilldown.setEvaluationDate(evaluationDate);
            drilldown.setStartTime(slotStart.atZone(ZoneOffset.systemDefault()).toInstant());
            drilldown.setEndTime(slotEnd.atZone(ZoneOffset.systemDefault()).toInstant());
            drilldown.setTotRes(values[0]);
            drilldown.setResOk(values[1]);
            drilldown.setResKo(values[2]);

            drilldownRecords.add(drilldown);
        }

        drilldownRepository.saveAll(drilldownRecords);
        LOGGER.info("Saved {} quarter-hour drilldown records for station {} on date {}", 
                    drilldownRecords.size(), station.getName(), evaluationDate);
    }

    @Override
    public void addToBatch(
        List<PagoPaPaymentReceiptDrilldown> batch,
        Instance instance,
        InstanceModule instanceModule,
        AnagStation station,
        LocalDate evaluationDate,
        LocalDate analysisDate,
        List<PagoPaPaymentReceiptDTO> rawData
    ) {
        // Create 96 quarter-hour slots for the day (00:00-00:15, 00:15-00:30, etc.)
        Map<LocalDateTime, long[]> quarterHourSlots = createQuarterHourSlots(evaluationDate);

        // Aggregate raw data into quarter-hour slots
        for (PagoPaPaymentReceiptDTO record : rawData) {
            LocalDateTime recordStart = record.getStartDate().atZone(ZoneOffset.systemDefault()).toLocalDateTime();
            
            // Find the quarter-hour slot this record belongs to
            LocalDateTime slotStart = findQuarterHourSlot(recordStart);
            
            if (quarterHourSlots.containsKey(slotStart)) {
                long[] values = quarterHourSlots.get(slotStart);
                values[0] += record.getTotRes(); // totRes
                values[1] += record.getResOk();  // resOk
                values[2] += record.getResKo();  // resKo
            }
        }

        // Add aggregated data to batch
        for (Map.Entry<LocalDateTime, long[]> entry : quarterHourSlots.entrySet()) {
            LocalDateTime slotStart = entry.getKey();
            LocalDateTime slotEnd = slotStart.plusMinutes(15);
            long[] values = entry.getValue();

            PagoPaPaymentReceiptDrilldown drilldown = new PagoPaPaymentReceiptDrilldown();
            drilldown.setInstance(instance);
            drilldown.setInstanceModule(instanceModule);
            drilldown.setStation(station);
            drilldown.setAnalysisDate(analysisDate);
            drilldown.setEvaluationDate(evaluationDate);
            drilldown.setStartTime(slotStart.atZone(ZoneOffset.systemDefault()).toInstant());
            drilldown.setEndTime(slotEnd.atZone(ZoneOffset.systemDefault()).toInstant());
            drilldown.setTotRes(values[0]);
            drilldown.setResOk(values[1]);
            drilldown.setResKo(values[2]);

            batch.add(drilldown);
        }

        LOGGER.debug("Added {} quarter-hour records to batch for station {} on date {}", 
                     96, station.getName(), evaluationDate);
    }

    @Override
    public int saveBatch(List<PagoPaPaymentReceiptDrilldown> batch) {
        if (batch.isEmpty()) {
            return 0;
        }

        LOGGER.debug("Saving batch of {} drilldown records", batch.size());
        List<PagoPaPaymentReceiptDrilldown> savedRecords = drilldownRepository.saveAll(batch);
        LOGGER.info("Successfully saved batch of {} drilldown records", savedRecords.size());
        
        return savedRecords.size();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PagoPaPaymentReceiptDrilldownDTO> getDrilldownData(
        Long instanceId,
        Long stationId,
        LocalDate evaluationDate
    ) {
        LOGGER.debug("Getting drilldown data for instance={}, station={}, evaluation date={}", 
                     instanceId, stationId, evaluationDate);

        List<PagoPaPaymentReceiptDrilldown> records = drilldownRepository
            .findByInstanceIdAndStationIdAndEvaluationDate(instanceId, stationId, evaluationDate);

        List<PagoPaPaymentReceiptDrilldownDTO> result = new ArrayList<>();
        for (PagoPaPaymentReceiptDrilldown record : records) {
            PagoPaPaymentReceiptDrilldownDTO dto = mapToDTO(record);
            result.add(dto);
        }

        LOGGER.debug("Found {} drilldown records for evaluation date {}", result.size(), evaluationDate);
        return result;
    }

    @Override
    public int deleteAllByInstanceModuleId(Long instanceModuleId) {
        LOGGER.debug("Deleting drilldown data for instance module {}", instanceModuleId);
        int deletedCount = drilldownRepository.deleteAllByInstanceModuleId(instanceModuleId);
        LOGGER.info("Deleted {} drilldown records for instance module {}", deletedCount, instanceModuleId);
        return deletedCount;
    }

    @Override
    public int deleteByInstanceModuleIdAndAnalysisDate(Long instanceModuleId, LocalDate analysisDate) {
        LOGGER.debug("Deleting drilldown data for instance module {} and analysis date {}", instanceModuleId, analysisDate);
        int deletedCount = drilldownRepository.deleteByInstanceModuleIdAndAnalysisDate(instanceModuleId, analysisDate);
        LOGGER.info("Deleted {} drilldown records for instance module {} on analysis date {}", 
                    deletedCount, instanceModuleId, analysisDate);
        return deletedCount;
    }

    /**
     * Create 96 quarter-hour slots for a given date
     */
    private Map<LocalDateTime, long[]> createQuarterHourSlots(LocalDate date) {
        Map<LocalDateTime, long[]> slots = new TreeMap<>();
        LocalDateTime startOfDay = date.atStartOfDay();
        
        for (int i = 0; i < 96; i++) { // 24 hours * 4 quarters = 96 slots
            LocalDateTime slotStart = startOfDay.plusMinutes(i * 15);
            slots.put(slotStart, new long[]{0L, 0L, 0L}); // [totRes, resOk, resKo]
        }
        
        return slots;
    }

    /**
     * Find the quarter-hour slot start time for a given datetime
     */
    private LocalDateTime findQuarterHourSlot(LocalDateTime dateTime) {
        int minute = dateTime.getMinute();
        int quarterHour = (minute / 15) * 15; // Round down to nearest quarter hour
        return dateTime.withMinute(quarterHour).withSecond(0).withNano(0);
    }

    /**
     * Map entity to DTO
     */
    private PagoPaPaymentReceiptDrilldownDTO mapToDTO(PagoPaPaymentReceiptDrilldown record) {
        PagoPaPaymentReceiptDrilldownDTO dto = new PagoPaPaymentReceiptDrilldownDTO();
        dto.setId(record.getId());
        dto.setInstanceId(record.getInstance().getId());
        dto.setInstanceModuleId(record.getInstanceModule().getId());
        dto.setStationId(record.getStation().getId());
        dto.setStationName(record.getStation().getName());
        dto.setAnalysisDate(record.getAnalysisDate());
        dto.setEvaluationDate(record.getEvaluationDate());
        dto.setStartTime(record.getStartTime());
        dto.setEndTime(record.getEndTime());
        dto.setTotRes(record.getTotRes());
        dto.setResOk(record.getResOk());
        dto.setResKo(record.getResKo());
        
        // Calculate percentage
        if (record.getTotRes() > 0) {
            dto.setResKoPercentage((double) (record.getResKo() * 100) / record.getTotRes());
        } else {
            dto.setResKoPercentage(0.0);
        }
        
        // Create time slot string
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime startTime = record.getStartTime().atZone(ZoneOffset.systemDefault()).toLocalTime();
        LocalTime endTime = record.getEndTime().atZone(ZoneOffset.systemDefault()).toLocalTime();
        dto.setTimeSlot(startTime.format(timeFormatter) + "-" + endTime.format(timeFormatter));
        
        return dto;
    }
}