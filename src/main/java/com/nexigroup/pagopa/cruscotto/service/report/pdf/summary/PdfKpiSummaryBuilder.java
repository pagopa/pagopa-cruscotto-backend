package com.nexigroup.pagopa.cruscotto.service.report.pdf.summary;

import com.nexigroup.pagopa.cruscotto.domain.*;
import com.nexigroup.pagopa.cruscotto.repository.*;
import com.nexigroup.pagopa.cruscotto.service.report.pdf.model.PdfKpiSummaryItem;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class PdfKpiSummaryBuilder {

    private final MessageSource messageSource;

    private final KpiA1DetailResultRepository kpiA1DetailResultRepository;
    private final KpiA2DetailResultRepository kpiA2DetailResultRepository;
    private final KpiB1DetailResultRepository kpiB1DetailResultRepository;
    private final KpiB2DetailResultRepository kpiB2DetailResultRepository;
    private final KpiB3DetailResultRepository kpiB3DetailResultRepository;
    private final KpiB4DetailResultRepository kpiB4DetailResultRepository;
    private final KpiB5DetailResultRepository kpiB5DetailResultRepository;
    private final KpiB8DetailResultRepository kpiB8DetailResultRepository;
    private final KpiB9DetailResultRepository kpiB9DetailResultRepository;
    private final KpiC1DetailResultRepository kpiC1DetailResultRepository;
    private final KpiC2DetailResultRepository kpiC2DetailResultRepository;

    public PdfKpiSummaryBuilder(
            MessageSource messageSource,
            KpiA1DetailResultRepository kpiA1DetailResultRepository,
            KpiA2DetailResultRepository kpiA2DetailResultRepository,
            KpiB1DetailResultRepository kpiB1DetailResultRepository,
            KpiB2DetailResultRepository kpiB2DetailResultRepository,
            KpiB3DetailResultRepository kpiB3DetailResultRepository,
            KpiB4DetailResultRepository kpiB4DetailResultRepository,
            KpiB5DetailResultRepository kpiB5DetailResultRepository,
            KpiB8DetailResultRepository kpiB8DetailResultRepository,
            KpiB9DetailResultRepository kpiB9DetailResultRepository,
            KpiC1DetailResultRepository kpiC1DetailResultRepository,
            KpiC2DetailResultRepository kpiC2DetailResultRepository) {
        this.messageSource = messageSource;
        this.kpiA1DetailResultRepository = kpiA1DetailResultRepository;
        this.kpiA2DetailResultRepository = kpiA2DetailResultRepository;
        this.kpiB1DetailResultRepository = kpiB1DetailResultRepository;
        this.kpiB2DetailResultRepository = kpiB2DetailResultRepository;
        this.kpiB3DetailResultRepository = kpiB3DetailResultRepository;
        this.kpiB4DetailResultRepository = kpiB4DetailResultRepository;
        this.kpiB5DetailResultRepository = kpiB5DetailResultRepository;
        this.kpiB8DetailResultRepository = kpiB8DetailResultRepository;
        this.kpiB9DetailResultRepository = kpiB9DetailResultRepository;
        this.kpiC1DetailResultRepository = kpiC1DetailResultRepository;
        this.kpiC2DetailResultRepository = kpiC2DetailResultRepository;
    }

    public List<PdfKpiSummaryItem> build(Long instanceId, Locale locale) {
        Locale effectiveLocale = (locale != null ? locale : Locale.ITALY);

        List<PdfKpiSummaryItem> out = new ArrayList<>();

        // A.1
        List<KpiA1DetailResult> a1 = kpiA1DetailResultRepository.findLatestByInstanceId(instanceId);
        if (!a1.isEmpty()) {
            out.add(buildFromOutcome("A.1", effectiveLocale, a1.stream().map(KpiA1DetailResult::getOutcome).toList()));
        }

        // A.2
        List<KpiA2DetailResult> a2 = kpiA2DetailResultRepository.findLatestByInstanceId(instanceId);
        if (!a2.isEmpty()) {
            out.add(buildFromOutcome("A.2", effectiveLocale, a2.stream().map(KpiA2DetailResult::getOutcome).toList()));
        }

        // B.1 : TODO: this kpi doesn't have a field called 'outcome'
        List<KpiB1DetailResult> b1 = kpiB1DetailResultRepository.findLatestByInstanceId(instanceId);
        if (!b1.isEmpty()) {
            out.add(buildFromOutcome("B.1", effectiveLocale, b1.stream().map(this::resolveB1Outcome).toList()));
        }

        // B.2
        List<KpiB2DetailResult> b2 = kpiB2DetailResultRepository.findLatestByInstanceId(instanceId);
        if (!b2.isEmpty()) {
            out.add(buildFromOutcome("B.2", effectiveLocale, b2.stream().map(KpiB2DetailResult::getOutcome).toList()));
        }

        // B.3
        List<KpiB3DetailResult> b3 = kpiB3DetailResultRepository.findLatestByInstanceId(instanceId);
        if (!b3.isEmpty()) {
            out.add(buildFromOutcome("B.3", effectiveLocale, b3.stream().map(KpiB3DetailResult::getOutcome).toList()));
        }

        // B.4
        List<KpiB4DetailResult> b4 = kpiB4DetailResultRepository.findLatestByInstanceId(instanceId);
        if (!b4.isEmpty()) {
            out.add(buildFromOutcome("B.4", effectiveLocale, b4.stream().map(KpiB4DetailResult::getOutcome).toList()));
        }

        // B.5
        List<KpiB5DetailResult> b5 = kpiB5DetailResultRepository.findLatestByInstanceId(instanceId);
        if (!b5.isEmpty()) {
            out.add(buildFromOutcome("B.5", effectiveLocale, b5.stream().map(KpiB5DetailResult::getOutcome).toList()));
        }

        // B.8
        List<KpiB8DetailResult> b8 = kpiB8DetailResultRepository.findLatestByInstanceId(instanceId);
        if (!b8.isEmpty()) {
            out.add(buildFromOutcome("B.8", effectiveLocale, b8.stream().map(KpiB8DetailResult::getOutcome).toList()));
        }

        // B.9
        List<KpiB9DetailResult> b9 = kpiB9DetailResultRepository.findLatestByInstanceId(instanceId);
        if (!b9.isEmpty()) {
            out.add(buildFromOutcome("B.9", effectiveLocale, b9.stream().map(KpiB9DetailResult::getOutcome).toList()));
        }

        // C.1
        List<KpiC1DetailResult> c1 = kpiC1DetailResultRepository.findLatestByInstanceId(instanceId);
        if (!c1.isEmpty()) {
            out.add(buildFromOutcome("C.1", effectiveLocale, c1.stream().map(KpiC1DetailResult::getOutcome).toList()));
        }

        // C.2
        List<KpiC2DetailResult> c2 = kpiC2DetailResultRepository.findLatestByInstanceId(instanceId);
        if (!c2.isEmpty()) {
            out.add(buildFromOutcome("C.2", effectiveLocale, c2.stream().map(KpiC2DetailResult::getOutcome).toList()));
        }

        return out;
    }

    private PdfKpiSummaryItem buildFromOutcome(String code, Locale locale, List<?> outcomes) {
        boolean compliant = outcomes.stream().allMatch(this::isCompliantOutcome);

        String descriptionKey = "pdf.kpi." + code + ".shortDescription";
        String fallbackKey = "pdf.kpi." + code + ".description";
        String titleKey = "pdf.kpi." + code + ".title";

        String title = msg(locale, titleKey, null);
        if (title == null || title.isBlank()) {
            title = msg(locale, titleKey, "");
        }

        String description = msg(locale, descriptionKey, null);
        if (description == null || description.isBlank()) {
            description = msg(locale, fallbackKey, "");
        }

        return new PdfKpiSummaryItem(code, title, description, compliant);
    }

    private boolean isCompliantOutcome(Object outcome) {
        if (outcome == null)
            return true;
        String s = outcome.toString().trim().toUpperCase();

        return s.equals("OK")
                || s.equals("KO") == false && s.contains("NON") == false && s.contains("KO") == false
                || s.equals("CONFORME")
                || s.equals("COMPLIANT")
                || s.equals("SUCCESS");
    }

    private String msg(Locale locale, String key, String defaultValue) {
        try {
            return messageSource.getMessage(key, null, locale);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private String resolveB1Outcome(KpiB1DetailResult r) {
        return (
            isCompliantOutcome(r.getInstitutionOutcome()) && isCompliantOutcome(r.getTransactionOutcome())
        ) ? "OK" : "KO";
    }
}