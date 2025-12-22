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

    private final KpiA1DetailResultRepository kpiA1DetailResultRepository;
    private final KpiA1AnalyticDrillDownRepository kpiA1AnalyticDrillDownRepository;
    private final KpiA2DetailResultRepository kpiA2DetailResultRepository;

    private final KpiB3DetailResultRepository kpiB3DetailResultRepository;
    private final KpiB4DetailResultRepository kpiB4DetailResultRepository;
    private final KpiB5DetailResultRepository kpiB5DetailResultRepository;

    private final KpiC1DetailResultRepository kpiC1DetailResultRepository;
    private final KpiC2DetailResultRepository kpiC2DetailResultRepository;

    public PdfPreviewService(
            PdfGenerationService generationService,
            PdfRendererService rendererService,
            PdfKpiTableFactory pdfKpiTableFactory,
            MessageSource messageSource,
            PdfKpiSummaryBuilder pdfKpiSummaryBuilder,
            KpiA1DetailResultRepository kpiA1DetailResultRepository,
            KpiA1AnalyticDrillDownRepository kpiA1AnalyticDrillDownRepository,
            KpiA2DetailResultRepository kpiA2DetailResultRepository,
            KpiB3DetailResultRepository kpiB3DetailResultRepository,
            KpiB4DetailResultRepository kpiB4DetailResultRepository,
            KpiB5DetailResultRepository kpiB5DetailResultRepository,
            KpiC1DetailResultRepository kpiC1DetailResultRepository,
            KpiC2DetailResultRepository kpiC2DetailResultRepository) {
        this.generationService = generationService;
        this.rendererService = rendererService;
        this.pdfKpiTableFactory = pdfKpiTableFactory;
        this.messageSource = messageSource;
        this.pdfKpiSummaryBuilder = pdfKpiSummaryBuilder;
        this.kpiA1DetailResultRepository = kpiA1DetailResultRepository;
        this.kpiA1AnalyticDrillDownRepository = kpiA1AnalyticDrillDownRepository;
        this.kpiA2DetailResultRepository = kpiA2DetailResultRepository;
        this.kpiB3DetailResultRepository = kpiB3DetailResultRepository;
        this.kpiB4DetailResultRepository = kpiB4DetailResultRepository;
        this.kpiB5DetailResultRepository = kpiB5DetailResultRepository;
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
        Long instanceId = 8220L;

        /*
         * =========================
         * KPI SUMMARY
         * =========================
         */

        List<PdfKpiSummaryItem> kpis = pdfKpiSummaryBuilder.build(instanceId, effectiveLocale);

        List<PdfKpiSummaryItem> negativeKpis = kpis.stream().filter(k -> !k.isCompliant()).toList();
        List<PdfKpiSummaryItem> positiveKpis = kpis.stream().filter(PdfKpiSummaryItem::isCompliant).toList();

        log.info("KPI totali: {}", kpis);
        log.info("KPI negativi: {}", negativeKpis);
        log.info("KPI positivi: {}", positiveKpis);

        
        /* =========================
           KPI A.1 DATA
           ========================= */

        List<KpiA1DetailResult> a1 = kpiA1DetailResultRepository.findLatestByInstanceId(instanceId);

        PdfKpiTableDescriptor kpiA1Table = null;

        // PdfKpiTableDescriptor kpiA2Table = null;
        // PdfKpiTableDescriptor kpiB3Table = null;
        // PdfKpiTableDescriptor kpiB4Table = null;
        // PdfKpiTableDescriptor kpiB5Table = null;
        // PdfKpiTableDescriptor kpiC1Table = null;
        // PdfKpiTableDescriptor kpiC2Table = null;

        // Long kpiA1AnalyticDataId = 296600L;
        Long kpiA1AnalyticDataId = 306754L;
        List<KpiA1AnalyticDrillDown> a1Drilldown = kpiA1AnalyticDrillDownRepository.findByKpiA1AnalyticDataIdOrderByFromHourAsc(kpiA1AnalyticDataId);
        List<PdfKpiTableDescriptor> kpiA1DrilldownTables = List.of();

        if (!a1.isEmpty()) {
            kpiA1Table = pdfKpiTableFactory.buildA1(a1, effectiveLocale);
        }
        if (!a1Drilldown.isEmpty()) {
            PdfKpiTableDescriptor rawDrilldown = pdfKpiTableFactory.buildA1Drilldown(a1Drilldown, effectiveLocale);

            kpiA1DrilldownTables = pdfKpiTableFactory.splitTable(rawDrilldown);
        }

        List<PdfKpiPage> negativeKpiPages = new ArrayList<>();

         for (PdfKpiSummaryItem kpi : negativeKpis) {
            // A.1 con drilldown
            if ("A.1".equals(kpi.getCode()) && !kpiA1DrilldownTables.isEmpty()) {
            
                for (int i = 0; i < kpiA1DrilldownTables.size(); i++) {
                    negativeKpiPages.add(
                        new PdfKpiPage(
                            kpi,
                            kpiA1DrilldownTables.get(i),
                            i == 0   // SOLO la prima pagina ha header
                        )
                    );
                }
                continue;
            }
         
            // KPI negativi SENZA tabelle lunghe
            negativeKpiPages.add(
                new PdfKpiPage(kpi, null, true)
            );
         }


        /* =========================
           KPI A.2 DATA
           ========================= */

        List<KpiA2DetailResult> a2 = kpiA2DetailResultRepository.findLatestByInstanceId(instanceId);

        /* =========================
           KPI B.3 DATA
           ========================= */

        List<KpiB3DetailResult> b3 = kpiB3DetailResultRepository.findLatestByInstanceId(instanceId);

        /* =========================
           KPI B.4 DATA
           ========================= */

        List<KpiB4DetailResult> b4 = kpiB4DetailResultRepository.findLatestByInstanceId(instanceId);

        /* =========================
           KPI B.5 DATA
           ========================= */

        List<KpiB5DetailResult> b5 = kpiB5DetailResultRepository.findLatestByInstanceId(instanceId);

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
      //   baseVars.put("kpiA1DrilldownTables", kpiA1DrilldownTables);
        baseVars.put("negativeKpiPages", negativeKpiPages);

        // baseVars.put("kpiA2Table", kpiA2Table);
        // baseVars.put("kpiB3Table", kpiB3Table);
        // baseVars.put("kpiB4Table", kpiB4Table);
        // baseVars.put("kpiB5Table", kpiB5Table);
        // baseVars.put("kpiC1Table", kpiC1Table);
        // baseVars.put("kpiC2Table", kpiC2Table);

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
