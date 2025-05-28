package com.nexigroup.pagopa.cruscotto.job.kpi.a2;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.OutcomeStatus;
import com.nexigroup.pagopa.cruscotto.job.config.JobConstant;
import com.nexigroup.pagopa.cruscotto.service.InstanceModuleService;
import com.nexigroup.pagopa.cruscotto.service.InstanceService;
import com.nexigroup.pagopa.cruscotto.service.KpiA2AnalyticDataService;
import com.nexigroup.pagopa.cruscotto.service.KpiA2DetailResultService;
import com.nexigroup.pagopa.cruscotto.service.KpiA2ResultService;
import com.nexigroup.pagopa.cruscotto.service.KpiConfigurationService;
import com.nexigroup.pagopa.cruscotto.service.PagoPaTaxonomyAggregatePositionService;
import com.nexigroup.pagopa.cruscotto.service.TaxonomyService;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.InstanceModuleDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiA2AnalyticDataDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiA2DetailResultDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiA2ResultDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiConfigurationDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.PagoPaTaxonomyAggregatePositionDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
@DisallowConcurrentExecution
public class KpiA2Job extends QuartzJobBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(KpiA2Job.class);

    private final InstanceService instanceService;

    private final InstanceModuleService instanceModuleService;

    private final ApplicationProperties applicationProperties;

    private final PagoPaTaxonomyAggregatePositionService pagoPaTaxonomyAggregatePositionService;

    private final KpiConfigurationService kpiConfigurationService;

    private final KpiA2AnalyticDataService kpiA2AnalyticDataService;

    private final KpiA2DetailResultService kpiA2DetailResultService;

    private final KpiA2ResultService kpiA2ResultService;

    private final TaxonomyService taxonomyService;

    private final Scheduler scheduler;

    @Override
    protected void executeInternal(@NotNull JobExecutionContext context) {
        LOGGER.info("Start calculate kpi A.2");

        try {
        	
			if (!applicationProperties.getJob().getKpiA2Job().isEnabled()) {
				LOGGER.info("Job calculate kpi A.2 disabled. Exit...");
				return;
			}
			
            List<InstanceDTO> instanceDTOS = instanceService.findInstanceToCalculate(
                ModuleCode.A2,
                applicationProperties.getJob().getKpiA2Job().getLimit()
            );

            if (instanceDTOS.isEmpty()) {
                LOGGER.info("No instance to calculate A.2. Exit....");
            } else {
                KpiConfigurationDTO kpiConfigurationDTO = kpiConfigurationService
                    .findKpiConfigurationByCode(ModuleCode.A2)
                    .orElseThrow(() -> new NullPointerException("KPI A.2 Configuration not found"));

                LOGGER.info("Kpi configuration {}", kpiConfigurationDTO);

                // Estrazione corretta perchè il job LoadTaxonomyJob cancella ogni volta tutti i record e li ricrea
                Set<String> taxonomyTakingsIdentifierSet = new HashSet<>(taxonomyService.getAllUpdatedTakingsIdentifiers());

                if (CollectionUtils.isEmpty(taxonomyTakingsIdentifierSet)) {
                    LOGGER.warn("Taxonomy table data not updated as of today, the kpi A.2 cannot be calculated. Exit...");
                } else {
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
                                .orElseThrow(() -> new NullPointerException("KPI A2 InstanceModule not found"));

                            LOGGER.info("Deletion phase for any previous processing in error");

                            int kpiA2AnalyticRecordsDataDeleted = kpiA2AnalyticDataService.deleteAllByInstanceModule(
                                instanceModuleDTO.getId()
                            );
                            LOGGER.info("{} kpiA2AnalyticData records deleted", kpiA2AnalyticRecordsDataDeleted);

                            int kpiA2DetailResultDeleted = kpiA2DetailResultService.deleteAllByInstanceModule(instanceModuleDTO.getId());
                            LOGGER.info("{} kpiA2DetailResult records deleted", kpiA2DetailResultDeleted);

                            int kpiA2ResultDeleted = kpiA2ResultService.deleteAllByInstanceModule(instanceModuleDTO.getId());
                            LOGGER.info("{} kpiA2ResultDeleted records deleted", kpiA2ResultDeleted);

                            KpiA2ResultDTO kpiA2ResultDTO = new KpiA2ResultDTO();
                            kpiA2ResultDTO.setInstanceId(instanceDTO.getId());
                            kpiA2ResultDTO.setInstanceModuleId(instanceModuleDTO.getId());
                            kpiA2ResultDTO.setAnalysisDate(LocalDate.now());
                            kpiA2ResultDTO.setTolerance(kpiConfigurationDTO.getTolerance());
                            kpiA2ResultDTO.setOutcome(OutcomeStatus.STANDBY);

                            kpiA2ResultDTO = kpiA2ResultService.save(kpiA2ResultDTO);

                            AtomicReference<Long> totPaymentsPeriod = new AtomicReference<>(0L);
                            AtomicReference<Long> totIncorrectPaymentsPeriod = new AtomicReference<>(0L);
                            List<KpiA2AnalyticDataDTO> kpiA2AnalyticDataDTOS = new ArrayList<>();
                            Map<String, Boolean> transferCategoryMap = new HashMap<>();

                            instanceDTO
                                .getAnalysisPeriodStartDate()
                                .datesUntil(instanceDTO.getAnalysisPeriodEndDate().plusDays(1))
                                .forEach(date -> {
                                    LOGGER.info("Date {}", date);

                                    List<PagoPaTaxonomyAggregatePositionDTO> pagoPaTaxonomyAggregatePositionDTOS =
                                        pagoPaTaxonomyAggregatePositionService.findAllRecordIntoDayForPartner(
                                            instanceDTO.getPartnerFiscalCode(),
                                            date
                                        );

                                    long sumPaymentsDaily = 0;
                                    long sumIncorrectPaymentsDaily = 0;

                                    for (PagoPaTaxonomyAggregatePositionDTO pagoPaTaxonomyAggregatePositionDTO : pagoPaTaxonomyAggregatePositionDTOS) {
                                        sumPaymentsDaily = sumPaymentsDaily + pagoPaTaxonomyAggregatePositionDTO.getTotal();

                                        if (
                                            !isCorrectPayment(
                                                pagoPaTaxonomyAggregatePositionDTO.getTransferCategory(),
                                                taxonomyTakingsIdentifierSet,
                                                transferCategoryMap
                                            )
                                        ) {
                                            sumIncorrectPaymentsDaily =
                                                sumIncorrectPaymentsDaily + pagoPaTaxonomyAggregatePositionDTO.getTotal();
                                        }
                                    }

                                    totPaymentsPeriod.set(totPaymentsPeriod.get() + sumPaymentsDaily);
                                    totIncorrectPaymentsPeriod.set(totIncorrectPaymentsPeriod.get() + sumIncorrectPaymentsDaily);

                                    KpiA2AnalyticDataDTO kpiA2AnalyticDataDTO = new KpiA2AnalyticDataDTO();
                                    kpiA2AnalyticDataDTO.setInstanceId(instanceDTO.getId());
                                    kpiA2AnalyticDataDTO.setInstanceModuleId(instanceModuleDTO.getId());
                                    kpiA2AnalyticDataDTO.setAnalysisDate(LocalDate.now());
                                    kpiA2AnalyticDataDTO.setEvaluationDate(date);
                                    kpiA2AnalyticDataDTO.setTotPayments(sumPaymentsDaily);
                                    kpiA2AnalyticDataDTO.setTotIncorrectPayments(sumIncorrectPaymentsDaily);

                                    kpiA2AnalyticDataDTOS.add(kpiA2AnalyticDataDTO);
                                });

                            Long totPaymentsPeriodValue = totPaymentsPeriod.get();
                            double errorPercentagePeriod = totPaymentsPeriodValue.compareTo(0L) > 0
                                ? (double) (totIncorrectPaymentsPeriod.get() * 100) / totPaymentsPeriodValue
                                : 0.0;

                            KpiA2DetailResultDTO kpiA2DetailResultDTO = new KpiA2DetailResultDTO();
                            kpiA2DetailResultDTO.setInstanceId(instanceDTO.getId());
                            kpiA2DetailResultDTO.setInstanceModuleId(instanceModuleDTO.getId());
                            kpiA2DetailResultDTO.setAnalysisDate(LocalDate.now());
                            kpiA2DetailResultDTO.setEvaluationStartDate(instanceDTO.getAnalysisPeriodStartDate());
                            kpiA2DetailResultDTO.setEvaluationEndDate(instanceDTO.getAnalysisPeriodEndDate());
                            kpiA2DetailResultDTO.setTotPayments(totPaymentsPeriod.get());
                            kpiA2DetailResultDTO.setTotIncorrectPayments(totIncorrectPaymentsPeriod.get());
                            kpiA2DetailResultDTO.setErrorPercentage(roundToNDecimalPlaces(errorPercentagePeriod));
                            kpiA2DetailResultDTO.setKpiA2ResultId(kpiA2ResultDTO.getId());

                            OutcomeStatus outcomeStatus = OutcomeStatus.OK;

                            if (errorPercentagePeriod > kpiConfigurationDTO.getTolerance()) {
                                outcomeStatus = OutcomeStatus.KO;
                            }

                            kpiA2DetailResultDTO.setOutcome(outcomeStatus);

                            kpiA2DetailResultService.save(kpiA2DetailResultDTO);

                            kpiA2AnalyticDataDTOS.forEach(kpiA2AnalyticData -> {
                                kpiA2AnalyticData.setKpiA2DetailResultId(kpiA2DetailResultDTO.getId());
                            });

                            kpiA2AnalyticDataService.saveAll(kpiA2AnalyticDataDTOS);

                            LOGGER.info("Final outcome {}", outcomeStatus);
                            kpiA2ResultService.updateKpiA2ResultOutcome(kpiA2ResultDTO.getId(), outcomeStatus);
                            instanceModuleService.updateAutomaticOutcome(instanceModuleDTO.getId(), outcomeStatus);

                            // Trigger
                            JobDetail job = scheduler.getJobDetail(JobKey.jobKey(JobConstant.CALCULATE_STATE_INSTANCE_JOB, "DEFAULT"));

                            Trigger trigger = TriggerBuilder.newTrigger()
                                .usingJobData("instanceId", instanceDTO.getId())
                                .withSchedule(
                                    SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow().withRepeatCount(0)
                                )
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
            }
        } catch (Exception exception) {
            LOGGER.error("Problem during calculate kpi A.2", exception);
        }

        LOGGER.info("End");
    }

    private static double roundToNDecimalPlaces(double value) {
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(5, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private boolean isCorrectPayment(
        String transferCategory,
        Set<String> taxonomyTakingsIdentifierSet,
        Map<String, Boolean> transferCategoryMap
    ) {
        if (StringUtils.isBlank(transferCategory) || CollectionUtils.isEmpty(taxonomyTakingsIdentifierSet)) {
            return false;
        }

        String taxonomyTakingsIdentifier;
        try {
            if (transferCategory.startsWith("9/")) {
                taxonomyTakingsIdentifier = transferCategory.substring(0, 12);
            } else {
                taxonomyTakingsIdentifier = "9/" + transferCategory.substring(0, 10);
            }
        } catch (Exception e) {
            LOGGER.error("Error in parsing transferCategory {}", transferCategory, e);
            return false;
        }

        Boolean correctPayment = transferCategoryMap.get(taxonomyTakingsIdentifier);
        if (correctPayment == null) {
            correctPayment = taxonomyTakingsIdentifierSet.contains(taxonomyTakingsIdentifier);
            transferCategoryMap.put(taxonomyTakingsIdentifier, correctPayment);
        }

        return correctPayment;
    }
}
