package com.nexigroup.pagopa.cruscotto.service.report.pdf.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexigroup.pagopa.cruscotto.domain.KpiB5DetailResult;
import com.nexigroup.pagopa.cruscotto.domain.KpiDetailResult;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB6AdditionalDataDTO;
import com.nexigroup.pagopa.cruscotto.service.report.pdf.model.PdfTableColumn;
import org.springframework.context.MessageSource;

import java.time.format.DateTimeFormatter;
import java.util.*;

public class KpiB6PdfTableConfig extends CommonConfig {

    private KpiB6PdfTableConfig() {}
    public static List<PdfTableColumn> columns(
        MessageSource messageSource,
        Locale locale
    ) {
        return List.of(
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.period"), "evaluationType"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.from"), "evaluationStartDate"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.to"), "evaluationEndDate"),

            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.activeStations"), "activeStations"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.stationsWithPaymentOptions"), "stationsWithPaymentOptions"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.difference"), "difference"),
            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.percentageDifference"), "percentageDifference"),


            new PdfTableColumn(msg(messageSource, locale, "pdf.kpi.table.outcome"), "outcome")
        );
    }

    public static List<Map<String, Object>> mapB6Rows(List<KpiDetailResult> entities,
                                                      ObjectMapper objectMapper) {

        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        return entities.stream()
            .sorted(Comparator.comparing(KpiDetailResult::getAnalysisDate))
            .map(kpiDetail -> {

                KpiB6AdditionalDataDTO e =  readAdditionalData(kpiDetail.getAdditionalData(),objectMapper);
                Map<String, Object> row = new HashMap<>();

                row.put("evaluationType", e.getEvaluationType() != null ? e.getEvaluationType().name() : "");
                row.put("evaluationStartDate",
                    e.getEvaluationStartDate() != null ? e.getEvaluationStartDate().format(df) : "");
                row.put("evaluationEndDate", e.getEvaluationEndDate() != null ? e.getEvaluationEndDate().format(df) : "");

                row.put("activeStations", e.getActiveStations());
                row.put("stationsWithPaymentOptions", e.getStationsWithPaymentOptions());
                row.put("difference", e.getDifference());
                row.put("percentageDifference",
                    e.getPercentageDifference() != null
                        ? PERC_FMT.format(e.getPercentageDifference())+ "%" :   PERCENTAGE_0);
                row.put("outcome", kpiDetail.getOutcome() != null ? kpiDetail.getOutcome().name() : "");
                return row;


            })
            .toList();
    }
    public static KpiB6AdditionalDataDTO readAdditionalData(String json,ObjectMapper objectMapper) {
        try {
            return objectMapper.readValue(json, KpiB6AdditionalDataDTO.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Error deserializing B6 additionalData", e);
        }
    }
    private static String msg(MessageSource ms, Locale l, String key) {
        return ms.getMessage(key, null, l);
    }
}
