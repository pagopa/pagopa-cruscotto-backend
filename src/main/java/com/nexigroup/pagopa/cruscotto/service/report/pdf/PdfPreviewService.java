package com.nexigroup.pagopa.cruscotto.service.report.pdf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.nexigroup.pagopa.cruscotto.domain.*;
import com.nexigroup.pagopa.cruscotto.repository.*;
import com.nexigroup.pagopa.cruscotto.service.KpiA1DetailResultService;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiA1DetailResultDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiA1AnalyticDrillDownDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB6DetailResultDTO;
import com.nexigroup.pagopa.cruscotto.service.report.pdf.factory.PdfKpiTableFactory;
import com.nexigroup.pagopa.cruscotto.service.report.pdf.model.PdfKpiPage;
import com.nexigroup.pagopa.cruscotto.service.report.pdf.model.PdfKpiSummaryItem;
import com.nexigroup.pagopa.cruscotto.service.report.pdf.model.PdfKpiTableDescriptor;
import com.nexigroup.pagopa.cruscotto.service.report.pdf.summary.PdfKpiSummaryBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.context.MessageSource;

@Service
public class PdfPreviewService {

    private static final Logger log = LoggerFactory.getLogger(PdfPreviewService.class);

    private final PdfGenerationService generationService;
    private final PdfRendererService rendererService;
    private final PdfKpiTableFactory pdfKpiTableFactory;
    private final MessageSource messageSource;
    private final PdfKpiSummaryBuilder pdfKpiSummaryBuilder;
    private final InstanceRepository instanceRepository;

    private final KpiA1DetailResultRepository kpiA1DetailResultRepository;
    // private final KpiA1AnalyticDrillDownRepository kpiA1AnalyticDrillDownRepository;
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

    public PdfPreviewService(
            PdfGenerationService generationService,
            PdfRendererService rendererService,
            PdfKpiTableFactory pdfKpiTableFactory,
            MessageSource messageSource,
            InstanceRepository instanceRepository,
            PdfKpiSummaryBuilder pdfKpiSummaryBuilder,
            KpiA1DetailResultRepository kpiA1DetailResultRepository,
            // KpiA1AnalyticDrillDownRepository kpiA1AnalyticDrillDownRepository,
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
        this.pdfKpiSummaryBuilder = pdfKpiSummaryBuilder;
        this.kpiA1DetailResultRepository = kpiA1DetailResultRepository;
        // this.kpiA1AnalyticDrillDownRepository = kpiA1AnalyticDrillDownRepository;
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

    public Path generatePreviewSetPdf(Locale locale) throws Exception {

        // Temp WorkDir
        Path workDir = Files.createTempDirectory("pdf-preview-");

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

        // Long instanceId = 4060L;
        Long instanceId = 8040L;

        /*
         * =========================
         * KPI SUMMARY
         * =========================
         */

        List<PdfKpiSummaryItem> kpis = pdfKpiSummaryBuilder.build(instanceId, effectiveLocale);

        List<PdfKpiSummaryItem> negativeKpis = kpis.stream().filter(k -> !k.isCompliant()).toList();
        List<PdfKpiSummaryItem> positiveKpis = kpis.stream().filter(p -> p.isCompliant()).toList();

        log.info("KPI totali: {}", kpis);
        log.info("KPI negativi: {}", negativeKpis);
        log.info("KPI positivi: {}", positiveKpis);

        
        /* =========================
           KPI A.1 DATA
           ========================= */

        List<KpiA1DetailResult> a1 = kpiA1DetailResultRepository.findLatestByInstanceId(instanceId);

        PdfKpiTableDescriptor kpiA1Table = null;

        // List<KpiA1AnalyticDrillDown> a1Drilldown = kpiA1AnalyticDrillDownRepository.findByKpiA1AnalyticDataIdOrderByFromHourAsc(kpiA1AnalyticDataId);
        // List<PdfKpiTableDescriptor> kpiA1DrilldownTables = List.of();
        List<PdfKpiTableDescriptor> kpiA1Tables = List.of();

        if (!a1.isEmpty()) {
            kpiA1Table = pdfKpiTableFactory.buildA1(a1, effectiveLocale);
            PdfKpiTableDescriptor rawTable = pdfKpiTableFactory.buildA1(a1, effectiveLocale);

            kpiA1Tables = pdfKpiTableFactory.splitTable(rawTable);
        }
        // if (!a1Drilldown.isEmpty()) {
        //     PdfKpiTableDescriptor rawDrilldown = pdfKpiTableFactory.buildA1Drilldown(a1Drilldown, effectiveLocale);

        //     kpiA1DrilldownTables = pdfKpiTableFactory.splitTable(rawDrilldown);
        // }

        /* =========================
           KPI A.2 DATA
           ========================= */
        PdfKpiTableDescriptor kpiA2Table = null;

        List<KpiA2DetailResult> a2 = kpiA2DetailResultRepository.findLatestByInstanceId(instanceId);

        List<PdfKpiTableDescriptor> kpiA2Tables = List.of();

        if (!a2.isEmpty()) {
            kpiA2Table = pdfKpiTableFactory.buildA2(a2, effectiveLocale);
            PdfKpiTableDescriptor rawTable = pdfKpiTableFactory.buildA2(a2, effectiveLocale);

            kpiA2Tables = pdfKpiTableFactory.splitTable(rawTable);
        }

        /* =========================
           KPI B.3 DATA
           ========================= */
        PdfKpiTableDescriptor kpiB3Table = null;

        List<KpiB3DetailResult> b3 = kpiB3DetailResultRepository.findLatestByInstanceId(instanceId);

        List<PdfKpiTableDescriptor> kpiB3Tables = List.of();

        if (!b3.isEmpty()) {
            kpiB3Table = pdfKpiTableFactory.buildB3(b3, effectiveLocale);
            PdfKpiTableDescriptor rawTable = pdfKpiTableFactory.buildB3(b3, effectiveLocale);

            kpiB3Tables = pdfKpiTableFactory.splitTable(rawTable);
        }

        /* =========================
           NEGATIVE KPI PAGES
           ========================= */
        List<PdfKpiPage> negativeKpiPages = new ArrayList<>();

         for (PdfKpiSummaryItem kpi : negativeKpis) {
            // A.1
            if ("A.1".equals(kpi.getCode()) && !kpiA1Tables.isEmpty()) {
            
                for (int i = 0; i < kpiA1Tables.size(); i++) {
                    negativeKpiPages.add(
                        new PdfKpiPage(
                            kpi,
                            kpiA1Tables.get(i),
                            i == 0, // firstPage
                            i == 0 // pageBreakBefore only on the firstPage
                        )
                    );
                }
                continue;
            }
            // A.2
            if ("A.2".equals(kpi.getCode()) && !kpiA2Tables.isEmpty()) {
            
                for (int i = 0; i < kpiA2Tables.size(); i++) {
                    negativeKpiPages.add(
                        new PdfKpiPage(
                            kpi,
                            kpiA2Tables.get(i),
                            i == 0, // firstPage
                            i == 0 // pageBreakBefore only on the firstPage
                        )
                    );
                }
                continue;
            }
            // B.3
            if ("B.3".equals(kpi.getCode()) && !kpiB3Tables.isEmpty()) {
            
                for (int i = 0; i < kpiB3Tables.size(); i++) {
                    negativeKpiPages.add(
                        new PdfKpiPage(
                            kpi,
                            kpiB3Tables.get(i),
                            i == 0, // firstPage
                            i == 0 // pageBreakBefore only on the firstPage
                        )
                    );
                }
                continue;
            }
         
            // KPI negativi SENZA tabelle lunghe
            negativeKpiPages.add(
                new PdfKpiPage(kpi, null, true, true)
            );
        }

        /* =========================
           POSITIVE KPI PAGES
           ========================= */
        List<PdfKpiPage> positiveKpiPages = new ArrayList<>();

        for (PdfKpiSummaryItem kpi : positiveKpis) {
            // A.1
            if ("A.1".equals(kpi.getCode()) && !kpiA1Tables.isEmpty()) {
            
                for (int i = 0; i < kpiA1Tables.size(); i++) {
                    positiveKpiPages.add(
                        new PdfKpiPage(
                            kpi,
                            kpiA1Tables.get(i),
                            i == 0, // firstPage
                            i == 0 // pageBreakBefore only on the firstPage
                        )
                    );
                }
                continue;
            }
            // A.2
            if ("A.2".equals(kpi.getCode()) && !kpiA2Tables.isEmpty()) {
            
                for (int i = 0; i < kpiA2Tables.size(); i++) {
                    positiveKpiPages.add(
                        new PdfKpiPage(
                            kpi,
                            kpiA2Tables.get(i),
                            i == 0, // firstPage
                            i == 0 // pageBreakBefore only on the firstPage
                        )
                    );
                }
                continue;
            }
            // B.3
            if ("B.3".equals(kpi.getCode())) {
                if (!kpiB3Tables.isEmpty()) {
                for (int i = 0; i < kpiB3Tables.size(); i++) {
                    positiveKpiPages.add(
                        new PdfKpiPage(
                            kpi,
                            kpiB3Tables.get(i),
                            i == 0, // firstPage
                            i == 0 // pageBreakBefore only on the firstPage
                        )
                    );
                }
            } else {
                positiveKpiPages.add(new PdfKpiPage(kpi, null, true, true));
            }
                continue;
            }
            // KPI positivi SENZA tabelle lunghe
            positiveKpiPages.add(
                new PdfKpiPage(kpi, null, true, true)
            );
        }

        /* =========================
           KPI B.1 DATA
           ========================= */

        List<KpiB1DetailResult> b1 = kpiB1DetailResultRepository.findLatestByInstanceId(instanceId);

        /* =========================
           KPI B.2 DATA
           ========================= */

        List<KpiB2DetailResult> b2 = kpiB2DetailResultRepository.findLatestByInstanceId(instanceId);

        /* =========================
           KPI B.4 DATA
           ========================= */

        List<KpiB4DetailResult> b4 = kpiB4DetailResultRepository.findLatestByInstanceId(instanceId);

        /* =========================
           KPI B.5 DATA
           ========================= */

        List<KpiB5DetailResult> b5 = kpiB5DetailResultRepository.findLatestByInstanceId(instanceId);

        /* =========================
           KPI B.8 DATA
           ========================= */

        List<KpiB8DetailResult> b8 = kpiB8DetailResultRepository.findLatestByInstanceId(instanceId);

        /* =========================
           KPI B.9 DATA
           ========================= */

        List<KpiB9DetailResult> b9 = kpiB9DetailResultRepository.findLatestByInstanceId(instanceId);

        /* =========================
           KPI C.1 DATA
           ========================= */

        List<KpiC1DetailResult> c1 = kpiC1DetailResultRepository.findLatestByInstanceId(instanceId);

        /* =========================
           KPI C.2 DATA
           ========================= */

        List<KpiC2DetailResult> c2 = kpiC2DetailResultRepository.findLatestByInstanceId(instanceId);

        /* =========================
           TEMPLATE VARS
           ========================= */

        Map<String, Object> baseVars = new HashMap<>();

        baseVars.put("analysisCode", "INST-06188330150-20251008-171040700");
        baseVars.put("partner", "06188330150 - MAGGIOLI S.P.A.");
        baseVars.put("esito", negativeKpis.isEmpty() ? "CONFORME" : "NON CONFORME");
        baseVars.put("esitoClass", negativeKpis.isEmpty() ? "ok" : "ko");
        baseVars.put("dataAnalisi", "08/10/2025");
        baseVars.put("periodo", "Dal 01/01/2025 al 30/06/2025");
        baseVars.put("reportName", "Report_INST-06188330150");
        baseVars.put("kpis", kpis);
        baseVars.put("negativeKpis", negativeKpis);
        baseVars.put("positiveKpis", positiveKpis);

        baseVars.put("kpiA1Table", kpiA1Table);
        baseVars.put("kpiA2Table", kpiA2Table);
        baseVars.put("kpiB3Table", kpiB3Table);
      //   baseVars.put("kpiA1DrilldownTables", kpiA1DrilldownTables);
        baseVars.put("negativeKpiPages", negativeKpiPages);
        baseVars.put("positiveKpiPages", positiveKpiPages);

        /* =========================
           PDF OUTPUT
           ========================= */ 

        // 1) Summary
        String htmlSummary = generationService.render("pdf/layouts/summary-only", baseVars, effectiveLocale);
        rendererService.renderToFile(htmlSummary, workDir, "report-summary.pdf");

        // 2) Summary + Negativi
        String htmlNeg = generationService.render("pdf/layouts/summary-plus-negative", baseVars, effectiveLocale);
        rendererService.renderToFile(htmlNeg, workDir, "report-negative.pdf");

        // 3) Full report
        String htmlFull = generationService.render("pdf/layouts/full-report", baseVars, effectiveLocale);
        rendererService.renderToFile(htmlFull, workDir, "report-complete.pdf");

        log.info("Set PDF generato in {}", workDir.toAbsolutePath());
        return workDir;
    }

    private void copy(String classpath, Path target) throws IOException {
        try (InputStream is = new ClassPathResource(classpath).getInputStream()) {
            Files.copy(is, target);
        }
    }
}
