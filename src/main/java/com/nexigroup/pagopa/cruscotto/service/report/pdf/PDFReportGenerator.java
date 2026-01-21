package com.nexigroup.pagopa.cruscotto.service.report.pdf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.nexigroup.pagopa.cruscotto.domain.*;
import com.nexigroup.pagopa.cruscotto.repository.*;
import com.nexigroup.pagopa.cruscotto.service.report.pdf.factory.PdfKpiTableFactory;
import com.nexigroup.pagopa.cruscotto.service.report.pdf.model.PdfKpiPage;
import com.nexigroup.pagopa.cruscotto.service.report.pdf.model.PdfKpiSummaryItem;
import com.nexigroup.pagopa.cruscotto.service.report.pdf.model.PdfKpiTableDescriptor;

import com.nexigroup.pagopa.cruscotto.service.report.pdf.page.PdfKpiPageBuilder;
import com.nexigroup.pagopa.cruscotto.service.report.pdf.wrapper.WrapperPdfFiles;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.context.MessageSource;

@Service
public class PDFReportGenerator {

    private static final Logger log = LoggerFactory.getLogger(PDFReportGenerator.class);

    private final PdfGenerationService generationService;
    private final PdfRendererService rendererService;
    private final PdfKpiTableFactory pdfKpiTableFactory;
    private final MessageSource messageSource;
    private final InstanceRepository instanceRepository;

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

    public PDFReportGenerator(
        PdfGenerationService generationService,
        PdfRendererService rendererService,
        PdfKpiTableFactory pdfKpiTableFactory,
        MessageSource messageSource,
        InstanceRepository instanceRepository,
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
        KpiC2DetailResultRepository kpiC2DetailResultRepository
    )
    {
        this.generationService = generationService;
        this.rendererService = rendererService;
        this.pdfKpiTableFactory = pdfKpiTableFactory;
        this.messageSource = messageSource;
        this.instanceRepository = instanceRepository;
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

    public List<WrapperPdfFiles> generatePDF(Locale locale, Long instanceId) throws Exception {

        // Temp WorkDir
        Path workDir = Files.createTempDirectory("pdf-preview-");

        Map<String, Object> baseVars = new HashMap<>();
        Map<String, List<PdfKpiTableDescriptor>> kpiTables = new HashMap<>();
        Instance instance = instanceRepository.findByIdWithPartner(instanceId);
        if(instance==null){
            throw new EntityNotFoundException();
        }

        // Assets
        copy("pdf/css/pdf-base.css", workDir.resolve("pdf-base.css"));
        copy("pdf/css/pdf-summary.css", workDir.resolve("pdf-summary.css"));
        copy("pdf/css/pdf-kpi-detail.css", workDir.resolve("pdf-kpi-detail.css"));
        copy("pdf/img/logo_pagopa.png", workDir.resolve("logo_pagopa.png"));

        Path fontsDir = workDir.resolve("fonts");
        Files.createDirectories(fontsDir);
        copy("pdf/fonts/TitilliumWeb_Regular.ttf", fontsDir.resolve("TitilliumWeb_Regular.ttf"));
        copy("pdf/fonts/TitilliumWeb_Bold.ttf", fontsDir.resolve("TitilliumWeb_Bold.ttf"));

        Locale effectiveLocale = (locale != null ? locale : Locale.ITALY);

        /*
         * =========================
         * KPI SUMMARY
         * =========================
         */

        List<PdfKpiSummaryItem> kpis = new ArrayList<>();


        /* =========================
           KPI A.1 DATA
           ========================= */

        List<KpiA1DetailResult> a1 = kpiA1DetailResultRepository.findLatestByInstanceId(instanceId);
        List<PdfKpiTableDescriptor> kpiA1Tables = List.of();
        if (!a1.isEmpty()) {
            kpis.add(buildFromOutcome("A.1", effectiveLocale, a1.stream().map(KpiA1DetailResult::getOutcome).toList()));
            baseVars.put("kpiA1Table", pdfKpiTableFactory.buildA1(a1, effectiveLocale));
            PdfKpiTableDescriptor rawTable = pdfKpiTableFactory.buildA1(a1, effectiveLocale);
            kpiTables.put("A.1", pdfKpiTableFactory.splitTable(rawTable));

        }

        /* =========================
           KPI A.2 DATA
           ========================= */
        List<KpiA2DetailResult> a2 = kpiA2DetailResultRepository.findLatestByInstanceId(instanceId);
        List<PdfKpiTableDescriptor> kpiA2Tables = List.of();
        if (!a2.isEmpty()) {
            kpis.add(buildFromOutcome("A.2", effectiveLocale, a2.stream().map(KpiA2DetailResult::getOutcome).toList()));
            baseVars.put("kpiA2Table", pdfKpiTableFactory.buildA2(a2, effectiveLocale));
            PdfKpiTableDescriptor rawTable = pdfKpiTableFactory.buildA2(a2, effectiveLocale);
            kpiTables.put("A.2", pdfKpiTableFactory.splitTable(rawTable));
        }




        /* =========================
               KPI B.1 DATA
        ========================= */
        List<KpiB1DetailResult> b1 = kpiB1DetailResultRepository.findLatestByInstanceId(instanceId);
        if (!b1.isEmpty()) {
            kpis.add(buildFromOutcome("B.1", effectiveLocale, b1.stream().map(KpiB1DetailResult::getInstitutionOutcome).toList()));
            baseVars.put("kpiB1Table", pdfKpiTableFactory.buildB1(b1, effectiveLocale));
            PdfKpiTableDescriptor rawTableB1 = pdfKpiTableFactory.buildB1(b1, effectiveLocale);
            kpiTables.put("B.1", pdfKpiTableFactory.splitTable(rawTableB1));
        }

/* =========================
       KPI B.2 DATA
========================= */
        List<KpiB2DetailResult> b2 = kpiB2DetailResultRepository.findLatestByInstanceId(instanceId);
        if (!b2.isEmpty()) {
            kpis.add(buildFromOutcome("B.2", effectiveLocale, b2.stream().map(KpiB2DetailResult::getOutcome).toList()));
            baseVars.put("kpiB2Table", pdfKpiTableFactory.buildB2(b2, effectiveLocale));
            PdfKpiTableDescriptor rawTableB2 = pdfKpiTableFactory.buildB2(b2, effectiveLocale);
            kpiTables.put("B.2", pdfKpiTableFactory.splitTable(rawTableB2));
        }
        /* =========================
           KPI B.3 DATA
           ========================= */
        List<KpiB3DetailResult> b3 = kpiB3DetailResultRepository.findLatestByInstanceId(instanceId);
        if (!b3.isEmpty()) {
            kpis.add(buildFromOutcome("B.3", effectiveLocale, b3.stream().map(KpiB3DetailResult::getOutcome).toList()));
            baseVars.put("kpiB3Table", pdfKpiTableFactory.buildB3(b3, effectiveLocale));
            PdfKpiTableDescriptor rawTable = pdfKpiTableFactory.buildB3(b3, effectiveLocale);
            kpiTables.put("B.3", pdfKpiTableFactory.splitTable(rawTable));

        }
        /* =========================
       KPI B.4 DATA
        ========================= */
        List<KpiB4DetailResult> b4 = kpiB4DetailResultRepository.findLatestByInstanceId(instanceId);
        if (!b4.isEmpty()) {
            kpis.add(buildFromOutcome("B.4", effectiveLocale, b4.stream().map(KpiB4DetailResult::getOutcome).toList()));
            baseVars.put("kpiB4Table", pdfKpiTableFactory.buildB4(b4, effectiveLocale));
            PdfKpiTableDescriptor rawTableB4 = pdfKpiTableFactory.buildB4(b4, effectiveLocale);
            kpiTables.put("B.4", pdfKpiTableFactory.splitTable(rawTableB4));
        }

        /* =========================
               KPI B.5 DATA
        ========================= */
        List<KpiB5DetailResult> b5 = kpiB5DetailResultRepository.findLatestByInstanceId(instanceId);
        if (!b5.isEmpty()) {
            kpis.add(buildFromOutcome("B.5", effectiveLocale, b5.stream().map(KpiB5DetailResult::getOutcome).toList()));
            baseVars.put("kpiB5Table", pdfKpiTableFactory.buildB5(b5, effectiveLocale));
            PdfKpiTableDescriptor rawTableB5 = pdfKpiTableFactory.buildB5(b5, effectiveLocale);
            kpiTables.put("B.5", pdfKpiTableFactory.splitTable(rawTableB5));
        }

        /* =========================
               KPI B.8 DATA
        ========================= */
        List<KpiB8DetailResult> b8 = kpiB8DetailResultRepository.findLatestByInstanceId(instanceId);
        if (!b8.isEmpty()) {
            kpis.add(buildFromOutcome("B.8", effectiveLocale, b8.stream().map(KpiB8DetailResult::getOutcome).toList()));
            baseVars.put("kpiB8Table", pdfKpiTableFactory.buildB8(b8, effectiveLocale));
            PdfKpiTableDescriptor rawTableB8 = pdfKpiTableFactory.buildB8(b8, effectiveLocale);
            kpiTables.put("B.8", pdfKpiTableFactory.splitTable(rawTableB8));
        }

        /* =========================
               KPI B.9 DATA
        ========================= */
        List<KpiB9DetailResult> b9 = kpiB9DetailResultRepository.findLatestByInstanceId(instanceId);
        if (!b9.isEmpty()) {
            kpis.add(buildFromOutcome("B.9", effectiveLocale, b9.stream().map(KpiB9DetailResult::getOutcome).toList()));
            baseVars.put("kpiB9Table", pdfKpiTableFactory.buildB9(b9, effectiveLocale));
            PdfKpiTableDescriptor rawTableB9 = pdfKpiTableFactory.buildB9(b9, effectiveLocale);
            kpiTables.put("B.9", pdfKpiTableFactory.splitTable(rawTableB9));
        }

        /* =========================
               KPI C.1 DATA
        ========================= */
        List<KpiC1DetailResult> c1 = kpiC1DetailResultRepository.findLatestByInstanceId(instanceId);
        if (!c1.isEmpty()) {
            kpis.add(buildFromOutcome("C.1", effectiveLocale, c1.stream().map(KpiC1DetailResult::getOutcome).toList()));
            baseVars.put("kpiC1Table", pdfKpiTableFactory.buildC1(c1, effectiveLocale));
            PdfKpiTableDescriptor rawTableC1 = pdfKpiTableFactory.buildC1(c1, effectiveLocale);
            kpiTables.put("C.1", pdfKpiTableFactory.splitTable(rawTableC1));
        }

    /* =========================
           KPI C.2 DATA
    ========================= */
        List<KpiC2DetailResult> c2 = kpiC2DetailResultRepository.findLatestByInstanceId(instanceId);
        if (!c2.isEmpty()) {
            kpis.add(buildFromOutcome("C.2", effectiveLocale, c2.stream().map(KpiC2DetailResult::getOutcome).toList()));
            baseVars.put("kpiC2Table", pdfKpiTableFactory.buildC2(c2, effectiveLocale));
            PdfKpiTableDescriptor rawTableC2 = pdfKpiTableFactory.buildC2(c2, effectiveLocale);
            kpiTables.put("C.2", pdfKpiTableFactory.splitTable(rawTableC2));
        }

        /* =========================
           TEMPLATE VARS
           ========================= */

        /* =========================
           NEGATIVE KPI PAGES
           ========================= */
        List<PdfKpiSummaryItem> negativeKpis = kpis.stream().filter(k -> !k.isCompliant()).toList();
        List<PdfKpiSummaryItem> positiveKpis = kpis.stream().filter(p -> p.isCompliant()).toList();

        log.info("KPI totali: {}", kpis);
        log.info("KPI negativi: {}", negativeKpis);
        log.info("KPI positivi: {}", positiveKpis);



        PdfKpiPageBuilder pageBuilder = new PdfKpiPageBuilder();
        List<PdfKpiPage> negativeKpiPages =
            pageBuilder.buildPages(negativeKpis, kpiTables);

        List<PdfKpiPage> positiveKpiPages =
            pageBuilder.buildPages(positiveKpis, kpiTables);

        Hibernate.isInitialized(instance.getPartner());
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        baseVars.put("analysisCode", "INST-"+instanceId);
        baseVars.put("partner", instance.getPartner().getFiscalCode() +"-" + instance.getPartner().getName());
        baseVars.put("esito", negativeKpis.isEmpty() ? "CONFORME" : "NON CONFORME");
        baseVars.put("esitoClass", negativeKpis.isEmpty() ? "ok" : "ko");
        baseVars.put("dataAnalisi", instance.getLastAnalysisDate() != null ?
            instance.getLastAnalysisDate().atZone(ZoneId.systemDefault()).toLocalDate().format(df) : "");
        String periodo = "";
        if (instance.getAnalysisPeriodStartDate() != null || instance.getAnalysisPeriodEndDate() != null) {
            String start = instance.getAnalysisPeriodStartDate() != null ? instance.getAnalysisPeriodStartDate().format(df) : "";
            String end = instance.getAnalysisPeriodEndDate() != null ? instance.getAnalysisPeriodEndDate().format(df) : "";
            periodo = "Dal " + start + " al " + end;
        }
        baseVars.put("periodo", periodo);
        baseVars.put("reportName", "Report_INST-"+instanceId);
        baseVars.put("kpis", kpis);
        baseVars.put("negativeKpis", negativeKpis);
        baseVars.put("positiveKpis", positiveKpis);

        ;
        //   baseVars.put("kpiA1DrilldownTables", kpiA1DrilldownTables);
        baseVars.put("negativeKpiPages", negativeKpiPages);
        baseVars.put("positiveKpiPages", positiveKpiPages);

        /* =========================
           PDF OUTPUT
           ========================= */


        List<WrapperPdfFiles> listPdfFiles = new ArrayList<>();

        // 1) Summary
        String htmlSummary = generationService.render("pdf/layouts/summary-only", baseVars, effectiveLocale);
        listPdfFiles.add(WrapperPdfFiles.builder()
                .content(rendererService.renderToBytes(htmlSummary, workDir))
                .name("report-summary.pdf")
                .build());

        // 2) Summary + Negativi


        String htmlNeg = generationService.render("pdf/layouts/summary-plus-negative", baseVars, effectiveLocale);
        listPdfFiles.add(WrapperPdfFiles.builder()
            .content(rendererService.renderToBytes(htmlNeg, workDir))
            .name("report-negative.pdf")
            .build());


        // 3) Full report
        String htmlFull = generationService.render("pdf/layouts/full-report", baseVars, effectiveLocale);
        listPdfFiles.add(WrapperPdfFiles.builder()
            .content(rendererService.renderToBytes(htmlFull, workDir))
            .name("report-complete.pdf")
            .build());


        log.info("Set PDF generato in {}", workDir.toAbsolutePath());
        return listPdfFiles;
    }

    private void copy(String classpath, Path target) throws IOException {
        try (InputStream is = new ClassPathResource(classpath).getInputStream()) {
            Files.copy(is, target);
        }
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
}
