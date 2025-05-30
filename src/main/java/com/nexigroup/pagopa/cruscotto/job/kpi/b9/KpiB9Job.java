package com.nexigroup.pagopa.cruscotto.job.kpi.b9;

import org.apache.commons.lang3.BooleanUtils;
import org.jetbrains.annotations.NotNull;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import com.nexigroup.pagopa.cruscotto.config.ApplicationProperties;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.EvaluationType;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.TypePlanned;
import com.nexigroup.pagopa.cruscotto.job.config.JobConstant;
import com.nexigroup.pagopa.cruscotto.service.AnagPlannedShutdownService;
import com.nexigroup.pagopa.cruscotto.service.AnagStationService;
import com.nexigroup.pagopa.cruscotto.service.InstanceModuleService;
import com.nexigroup.pagopa.cruscotto.service.InstanceService;
import com.nexigroup.pagopa.cruscotto.service.KpiB9AnalyticDataService;
import com.nexigroup.pagopa.cruscotto.service.KpiB9DetailResultService;
import com.nexigroup.pagopa.cruscotto.service.KpiB9ResultService;
import com.nexigroup.pagopa.cruscotto.service.KpiConfigurationService;
import com.nexigroup.pagopa.cruscotto.service.PagoPaPaymentReceiptService;
import com.nexigroup.pagopa.cruscotto.service.dto.AnagPlannedShutdownDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceModuleDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB9AnalyticDataDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB9DetailResultDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB9ResultDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiConfigurationDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.PagoPaPaymentReceiptDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
@DisallowConcurrentExecution
public class KpiB9Job extends QuartzJobBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(KpiB9Job.class);

    private final InstanceService instanceService;

    private final InstanceModuleService instanceModuleService;

    private final ApplicationProperties applicationProperties;

    private final PagoPaPaymentReceiptService pagoPaPaymentReceiptService;

    private final AnagStationService anagStationService;

    private final KpiConfigurationService kpiConfigurationService;

    private final AnagPlannedShutdownService anagPlannedShutdownService;

    private final KpiB9AnalyticDataService kpiB9AnalyticDataService;

    private final KpiB9DetailResultService kpiB9DetailResultService;

    private final KpiB9ResultService kpiB9ResultService;

    private final Scheduler scheduler;

    
    @Override
    protected void executeInternal(@NotNull JobExecutionContext context) {
    	
        LOGGER.info("Start calculate kpi B.9 Job");

        try {        	

			if (!applicationProperties.getJob().getKpiB9Job().isEnabled()) {
				LOGGER.info("Job calculate kpi B.9 disabled. Exit...");
				return;
			}

            List<InstanceDTO> instanceDTOS = instanceService.findInstanceToCalculate(
                ModuleCode.B9,
                applicationProperties.getJob().getKpiB9Job().getLimit()
            );

            if (instanceDTOS.isEmpty()) {
                LOGGER.info("No instance to calculate B.9. Exit....");
            } else {
                KpiConfigurationDTO kpiConfigurationDTO = kpiConfigurationService
                    .findKpiConfigurationByCode(ModuleCode.B9)
                    .orElseThrow(() -> new NullPointerException("KPI B.9 Configuration not found"));

                LOGGER.info("Kpi configuration {}", kpiConfigurationDTO);

                Double eligibilityThreshold = kpiConfigurationDTO.getEligibilityThreshold() != null ? kpiConfigurationDTO.getEligibilityThreshold() : 0.0;
                Double tolerance = kpiConfigurationDTO.getTolerance() != null ? kpiConfigurationDTO.getTolerance() : 0.0;

                instanceDTOS.forEach(instanceDTO -> {
                    try {
                        LOGGER.info(
                            "Start elaboration instance {} for partner {} - {} with period {} - {}",
                            instanceDTO.getInstanceIdentification(),
                            instanceDTO.getPartnerFiscalCode(),
                            instanceDTO.getPartnerName(),
                            instanceDTO.getAnalysisPeriodStartDate(),
                            instanceDTO.getAnalysisPeriodEndDate()
                        );

                        instanceService.updateInstanceStatusInProgress(instanceDTO.getId());

                        InstanceModuleDTO instanceModuleDTO = instanceModuleService
                            .findOne(instanceDTO.getId(), kpiConfigurationDTO.getModuleId())
                            .orElseThrow(() -> new NullPointerException("KPI B9 InstanceModule not found"));

                        LOGGER.info("Deletion phase for any previous processing in error");

                        int kpiB9AnalyticRecordsDataDeleted = kpiB9AnalyticDataService.deleteAllByInstanceModule(instanceModuleDTO.getId());
                        LOGGER.info("{} kpiB9AnalyticData records deleted", kpiB9AnalyticRecordsDataDeleted);

                        int kpiB9DetailResultDeleted = kpiB9DetailResultService.deleteAllByInstanceModule(instanceModuleDTO.getId());
                        LOGGER.info("{} kpiB9DetailResult records deleted", kpiB9DetailResultDeleted);

                        int kpiB9ResultDeleted = kpiB9ResultService.deleteAllByInstanceModule(instanceModuleDTO.getId());
                        LOGGER.info("{} kpiB9ResultDeleted records deleted", kpiB9ResultDeleted);

                        List<String> stations = pagoPaPaymentReceiptService.findAllStationIntoPeriodForPartner(
                            instanceDTO.getPartnerFiscalCode(),
                            instanceDTO.getAnalysisPeriodStartDate(),
                            instanceDTO.getAnalysisPeriodEndDate()
                        );

                        AtomicReference<KpiB9ResultDTO> kpiB9ResultRef = new AtomicReference<>();

                        KpiB9ResultDTO kpiB9ResultDTO = new KpiB9ResultDTO();
                        kpiB9ResultDTO.setInstanceId(instanceDTO.getId());
                        kpiB9ResultDTO.setInstanceModuleId(instanceModuleDTO.getId());
                        kpiB9ResultDTO.setAnalysisDate(LocalDate.now());
                        kpiB9ResultDTO.setExcludePlannedShutdown(BooleanUtils.toBooleanDefaultIfNull(kpiConfigurationDTO.getExcludePlannedShutdown(), false));
                        kpiB9ResultDTO.setExcludeUnplannedShutdown(BooleanUtils.toBooleanDefaultIfNull(kpiConfigurationDTO.getExcludeUnplannedShutdown(), false));
                        kpiB9ResultDTO.setEligibilityThreshold(eligibilityThreshold);
                        kpiB9ResultDTO.setTolerance(tolerance);
                        kpiB9ResultDTO.setEvaluationType(kpiConfigurationDTO.getEvaluationType());
                        kpiB9ResultDTO.setOutcome(!stations.isEmpty() ? OutcomeStatus.STANDBY : OutcomeStatus.OK);

                        kpiB9ResultRef.set(kpiB9ResultService.save(kpiB9ResultDTO));

                        AtomicReference<OutcomeStatus> kpiB9ResultFinalOutcome = new AtomicReference<>(OutcomeStatus.OK);

                        if (stations.isEmpty()) {
                            LOGGER.info("No stations found");
                        } else {
                            stations.forEach((station) -> {
                                LOGGER.info("Station {}", station);

                                long idStation = anagStationService.findIdByNameOrCreate(station, instanceDTO.getPartnerId());

                                List<AnagPlannedShutdownDTO> maintenance = new ArrayList<>();
                                if (BooleanUtils.toBooleanDefaultIfNull(kpiConfigurationDTO.getExcludePlannedShutdown(), false)) {
                                    maintenance.addAll(
                                        anagPlannedShutdownService.findAllByTypePlannedIntoPeriod(
                                            instanceDTO.getPartnerId(),
                                            idStation,
                                            TypePlanned.PROGRAMMATO,
                                            instanceDTO.getAnalysisPeriodStartDate(),
                                            instanceDTO.getAnalysisPeriodEndDate()
                                        )
                                    );
                                }

                                if (BooleanUtils.toBooleanDefaultIfNull(kpiConfigurationDTO.getExcludeUnplannedShutdown(), false)) {
                                    maintenance.addAll(
                                        anagPlannedShutdownService.findAllByTypePlannedIntoPeriod(
                                            instanceDTO.getPartnerId(),
                                            idStation,
                                            TypePlanned.NON_PROGRAMMATO,
                                            instanceDTO.getAnalysisPeriodStartDate(),
                                            instanceDTO.getAnalysisPeriodEndDate()
                                        )
                                    );
                                }

                                LOGGER.info("Found {} rows of maintenance", maintenance.size());

                                AtomicReference<Month> prevMonth = new AtomicReference<>();
                                AtomicReference<Long> totResMonth = new AtomicReference<>();
                                AtomicReference<Long> totResKoMonth = new AtomicReference<>();
                                AtomicReference<Long> totResPeriod = new AtomicReference<>(0L);
                                AtomicReference<Long> totResKoPeriod = new AtomicReference<>(0L);
                                List<KpiB9AnalyticDataDTO> kpiB9AnalyticDataDTOS = new ArrayList<>();
                                List<KpiB9DetailResultDTO> kpiB9DetailResultDTOS = new ArrayList<>();
                                AtomicReference<LocalDate> firstDayOfMonth = new AtomicReference<>();
                                AtomicReference<LocalDate> lastDayOfMonth = new AtomicReference<>();
                                
                                instanceDTO.getAnalysisPeriodStartDate()
                                    	   .datesUntil(instanceDTO.getAnalysisPeriodEndDate().plusDays(1))
                                    	   .forEach(date -> {
                                    		   LOGGER.info("Date {}", date);

                                    		   Month currentMonth = date.getMonth();
                                    		   
                                    		   if (prevMonth.get() == null || prevMonth.get().compareTo(currentMonth) != 0) {
                                               	
                                               		if(prevMonth.get() == null) {
                                               			firstDayOfMonth.set(instanceDTO.getAnalysisPeriodStartDate());                                            		
                                               		} else {
                                               			firstDayOfMonth.set(date.with(TemporalAdjusters.firstDayOfMonth()));
                                               			totResMonth.set(0L);
                                               			totResKoMonth.set(0L);
                                               			kpiB9AnalyticDataDTOS.clear();
                                               		}
                                               	
                                               		if(currentMonth.compareTo(instanceDTO.getAnalysisPeriodEndDate().getMonth()) == 0) {
                                               			lastDayOfMonth.set(instanceDTO.getAnalysisPeriodEndDate());
                                               		} else {
                                               			lastDayOfMonth.set(date.with(TemporalAdjusters.lastDayOfMonth()));
                                               		}
                                    		   }
                                    		   
                                    		   List<PagoPaPaymentReceiptDTO> pagoPaPaymentReceiptDTOS =
                                    				   pagoPaPaymentReceiptService.findAllRecordIntoDayForPartnerAndStation(
                                                           instanceDTO.getPartnerFiscalCode(),
                                                           station,
                                                           date
                                                       );

                                    		   long sumTotResDaily = 0;
                                    		   long sumResOkDaily = 0;
                                    		   long sumRealResKoDaily = 0;
                                    		   long sumValidResKoDaily = 0;                                    		  

                                    		   for (PagoPaPaymentReceiptDTO pagoPaPaymentReceiptDTO : pagoPaPaymentReceiptDTOS) {
                                    			   sumTotResDaily = sumTotResDaily + pagoPaPaymentReceiptDTO.getTotRes();
                                    			   sumResOkDaily = sumResOkDaily + pagoPaPaymentReceiptDTO.getResOk(); 
                                    			   sumRealResKoDaily = sumRealResKoDaily + pagoPaPaymentReceiptDTO.getResKo();                                   			

                                    			   boolean exclude = maintenance.stream()
                                    					   						.map(anagPlannedShutdownDTO -> {
                                    					   							Boolean excludePlanned = isInstantInRangeInclusive(
                                    					   														pagoPaPaymentReceiptDTO.getStartDate(),
                                    					   														anagPlannedShutdownDTO.getShutdownStartDate(),
                                    					   														anagPlannedShutdownDTO.getShutdownEndDate()
                                    					   													 ) &&
                                    					   													 isInstantInRangeInclusive(
                                    					   															 pagoPaPaymentReceiptDTO.getEndDate(),
                                    					   															 anagPlannedShutdownDTO.getShutdownStartDate(),
                                    					   															 anagPlannedShutdownDTO.getShutdownEndDate()
                                    					   													 );
                                    					   							return excludePlanned;
                                    					   						})
                                    					   						.anyMatch(Boolean::booleanValue);
                                    			   if (!exclude) {
                                    				   sumValidResKoDaily = sumValidResKoDaily + pagoPaPaymentReceiptDTO.getResKo();
                                    			   }
                                    		   }

                                    		   totResMonth.set(totResMonth.get() + sumTotResDaily);
                                    		   totResKoMonth.set(totResKoMonth.get() + sumValidResKoDaily);

                                    		   KpiB9AnalyticDataDTO kpiB9AnalyticDataDTO = new KpiB9AnalyticDataDTO();
                                    		   kpiB9AnalyticDataDTO.setInstanceId(instanceDTO.getId());
                                    		   kpiB9AnalyticDataDTO.setInstanceModuleId(instanceModuleDTO.getId());
                                    		   kpiB9AnalyticDataDTO.setAnalysisDate(LocalDate.now());
                                    		   kpiB9AnalyticDataDTO.setStationId(idStation);
                                    		   kpiB9AnalyticDataDTO.setEvaluationDate(date);
                                    		   kpiB9AnalyticDataDTO.setTotRes(sumTotResDaily);
                                    		   kpiB9AnalyticDataDTO.setResOk(sumResOkDaily);
                                    		   kpiB9AnalyticDataDTO.setResKoReal(sumRealResKoDaily);
                                    		   kpiB9AnalyticDataDTO.setResKoValid(sumValidResKoDaily);

                                    		   kpiB9AnalyticDataDTOS.add(kpiB9AnalyticDataDTO);

                                    		   if (date.isEqual(lastDayOfMonth.get())) {
                                    			   totResPeriod.set(totResPeriod.get() + totResMonth.get());
                                    			   totResKoPeriod.set(totResKoPeriod.get() + totResKoMonth.get());

                                    			   Long totResMonthValue = totResMonth.get();
                                    			   double percResKoMonth = totResMonthValue.compareTo(0L) > 0
                                    					   						? (double) (totResKoMonth.get() * 100) / totResMonthValue
                                    					   						: 0.0;

                                    			   KpiB9DetailResultDTO kpiB9DetailResultDTO = new KpiB9DetailResultDTO();
                                    			   kpiB9DetailResultDTO.setInstanceId(instanceDTO.getId());
                                    			   kpiB9DetailResultDTO.setInstanceModuleId(instanceModuleDTO.getId());
                                    			   kpiB9DetailResultDTO.setAnalysisDate(LocalDate.now());
                                    			   kpiB9DetailResultDTO.setStationId(idStation);
                                    			   kpiB9DetailResultDTO.setEvaluationType(EvaluationType.MESE);
                                    			   kpiB9DetailResultDTO.setEvaluationStartDate(firstDayOfMonth.get());
                                    			   kpiB9DetailResultDTO.setEvaluationEndDate(lastDayOfMonth.get());
                                    			   kpiB9DetailResultDTO.setTotRes(totResMonth.get());
                                    			   kpiB9DetailResultDTO.setResKo(totResKoMonth.get());
                                    			   kpiB9DetailResultDTO.setResKoPercentage(roundToNDecimalPlaces(percResKoMonth));
                                    			   kpiB9DetailResultDTO.setKpiB9ResultId(kpiB9ResultRef.get().getId());

                                    			   OutcomeStatus outcomeStatus = OutcomeStatus.OK;

                                    			   if (percResKoMonth > (eligibilityThreshold + tolerance)) {
                                    				   outcomeStatus = OutcomeStatus.KO;
                                    			   }

                                    			   if (kpiConfigurationDTO.getEvaluationType().compareTo(EvaluationType.MESE) == 0 &&
                                    				   outcomeStatus.compareTo(OutcomeStatus.KO) == 0) {
                                    				   kpiB9ResultFinalOutcome.set(OutcomeStatus.KO);
                                    			   }

                                    			   kpiB9DetailResultDTO.setOutcome(outcomeStatus);

                                    			   kpiB9DetailResultDTO = kpiB9DetailResultService.save(kpiB9DetailResultDTO);

                                    			   kpiB9DetailResultDTOS.add(kpiB9DetailResultDTO);

                                    			   KpiB9DetailResultDTO finalKpiB9DetailResultDTO = kpiB9DetailResultDTO;

                                    			   kpiB9AnalyticDataDTOS.forEach(kpiB9AnalyticData -> {
                                    				   kpiB9AnalyticData.setKpiB9DetailResultId(finalKpiB9DetailResultDTO.getId());
                                    			   });

                                    			   kpiB9AnalyticDataService.saveAll(kpiB9AnalyticDataDTOS);
                                    		   }

                                    		   prevMonth.set(currentMonth);
                                    	   });

                                Long totResPeriodValue = totResPeriod.get();
                                double percResKoPeriod = totResPeriodValue.compareTo(0L) > 0
						                     				? (double) (totResKoPeriod.get() * 100) / totResPeriodValue
						                                    : 0.0;

                                KpiB9DetailResultDTO kpiB9DetailResultDTO = new KpiB9DetailResultDTO();
                                kpiB9DetailResultDTO.setInstanceId(instanceDTO.getId());
                                kpiB9DetailResultDTO.setInstanceModuleId(instanceModuleDTO.getId());
                                kpiB9DetailResultDTO.setAnalysisDate(LocalDate.now());
                                kpiB9DetailResultDTO.setStationId(idStation);
                                kpiB9DetailResultDTO.setEvaluationType(EvaluationType.TOTALE);
                                kpiB9DetailResultDTO.setEvaluationStartDate(instanceDTO.getAnalysisPeriodStartDate());
                                kpiB9DetailResultDTO.setEvaluationEndDate(instanceDTO.getAnalysisPeriodEndDate());
                                kpiB9DetailResultDTO.setTotRes(totResPeriod.get());
                                kpiB9DetailResultDTO.setResKo(totResKoPeriod.get());
                                kpiB9DetailResultDTO.setResKoPercentage(roundToNDecimalPlaces(percResKoPeriod));
                                kpiB9DetailResultDTO.setKpiB9ResultId(kpiB9ResultRef.get().getId());

                                OutcomeStatus outcomeStatus = OutcomeStatus.OK;

                                if (percResKoPeriod > (eligibilityThreshold + tolerance)) {
                                    outcomeStatus = OutcomeStatus.KO;
                                }

                                if (kpiConfigurationDTO.getEvaluationType().compareTo(EvaluationType.TOTALE) == 0 &&
                                    outcomeStatus.compareTo(OutcomeStatus.KO) == 0) {
                                    kpiB9ResultFinalOutcome.set(OutcomeStatus.KO);
                                }

                                kpiB9DetailResultDTO.setOutcome(outcomeStatus);

                                kpiB9DetailResultService.save(kpiB9DetailResultDTO);
                            });

                            LOGGER.info("Final outcome {}", kpiB9ResultFinalOutcome.get());
                            kpiB9ResultService.updateKpiB9ResultOutcome(kpiB9ResultRef.get().getId(), kpiB9ResultFinalOutcome.get());
                        }
                        instanceModuleService.updateAutomaticOutcome(instanceModuleDTO.getId(), kpiB9ResultFinalOutcome.get());

                        // Trigger
                        JobDetail job = scheduler.getJobDetail(JobKey.jobKey(JobConstant.CALCULATE_STATE_INSTANCE_JOB, "DEFAULT"));

                        Trigger trigger = TriggerBuilder.newTrigger()
                            .usingJobData("instanceId", instanceDTO.getId())
                            .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow().withRepeatCount(0))
                            .forJob(job)
                            .build();

                        scheduler.scheduleJob(trigger);
                    } catch (Exception e) {
                        LOGGER.error(
                            "Error in elaboration instance {} for partner {} - {} with period {} - {}",
                            instanceDTO.getInstanceIdentification(),
                            instanceDTO.getPartnerFiscalCode(),
                            instanceDTO.getPartnerName(),
                            instanceDTO.getAnalysisPeriodStartDate(),
                            instanceDTO.getAnalysisPeriodEndDate(),
                            e
                        );
                    }
                });
            }
        } catch (Exception exception) {
            LOGGER.error("Problem during calculate kpi B.9", exception);
        }

        LOGGER.info("End");
    }

    private static double roundToNDecimalPlaces(double value) {
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(5, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private boolean isInstantInRangeInclusive(Instant instantToCheck, Instant startInstant, Instant endInstant) {
        return (
            (instantToCheck.atZone(ZoneOffset.systemDefault()).isEqual(startInstant.atZone(ZoneOffset.systemDefault())) ||
                instantToCheck.atZone(ZoneOffset.systemDefault()).isAfter(startInstant.atZone(ZoneOffset.systemDefault()))) &&
            (instantToCheck.atZone(ZoneOffset.systemDefault()).isEqual(endInstant.atZone(ZoneOffset.systemDefault())) ||
                instantToCheck.atZone(ZoneOffset.systemDefault()).isBefore(endInstant.atZone(ZoneOffset.systemDefault())))
        );
    }
}
