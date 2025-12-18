package com.nexigroup.pagopa.cruscotto.service.pdf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
public class PdfPreviewService {

    private static final Logger log = LoggerFactory.getLogger(PdfPreviewService.class);

    private final PdfGenerationService generationService;
    private final PdfRendererService rendererService;

    public PdfPreviewService(
            PdfGenerationService generationService,
            PdfRendererService rendererService
    ) {
        this.generationService = generationService;
        this.rendererService = rendererService;
    }

    public Path generatePreviewSetPdf(Locale locale) throws Exception {

        // Workspace temporaneo
        Path workDir = Files.createTempDirectory("pdf-preview-");

        // Copia assets
        copy("pdf/css/pdf-base.css", workDir.resolve("pdf-base.css"));
        copy("pdf/css/pdf-summary.css", workDir.resolve("pdf-summary.css"));
        copy("pdf/css/pdf-kpi-detail.css", workDir.resolve("pdf-kpi-detail.css"));
        copy("pdf/img/logo_pagopa.png", workDir.resolve("logo_pagopa.png"));

        Path fontsDir = workDir.resolve("fonts");
        Files.createDirectories(fontsDir);
        copy("pdf/fonts/TitilliumWeb_Regular.ttf", fontsDir.resolve("TitilliumWeb_Regular.ttf"));
        copy("pdf/fonts/TitilliumWeb_Bold.ttf", fontsDir.resolve("TitilliumWeb_Bold.ttf"));

        /* =========================
           KPI STUB DATA (FIXATO)
           ========================= */

        List<Map<String, Object>> kpis = List.of(
            Map.of(
                "code", "A.1",
                "description", "Rispetto SLA disponibilità servizio",
                "conforme", Boolean.TRUE,
                "notes", List.of("Nessuna anomalia riscontrata")
            ),
            Map.of(
                "code", "A.2",
                "description", "Tassonomia precisa",
                "conforme", Boolean.FALSE,
                "evidences", List.of(
                    "Mancano tassonomie su 12 posizioni",
                    "Formato non valido su 3 righe"
                )
            ),
            Map.of(
                "code", "B.3",
                "description", "Zero Incident",
                "conforme", Boolean.TRUE,
                "notes", List.of("Zero incident nel periodo")
            )
        );

        // Split KPI
        List<Map<String, Object>> negativeKpis = kpis.stream()
            .filter(k -> Boolean.FALSE.equals(k.get("conforme")))
            .toList();

        List<Map<String, Object>> positiveKpis = kpis.stream()
            .filter(k -> Boolean.TRUE.equals(k.get("conforme")))
            .toList();

        log.info("KPI totali: {}", kpis);
        log.info("KPI negativi: {}", negativeKpis);
        log.info("KPI positivi: {}", positiveKpis);

        Map<String, Object> baseVars = Map.of(
            "analysisCode", "INST-06188330150-20251008-171040700",
            "partner", "06188330150 - MAGGIOLI S.P.A.",
            "esito", negativeKpis.isEmpty() ? "CONFORME" : "NON CONFORME",
            "esitoClass", negativeKpis.isEmpty() ? "ok" : "ko",
            "dataAnalisi", "08/10/2025",
            "periodo", "Dal 01/01/2025 al 30/06/2025",
            "reportName", "Report_INST-06188330150",
            "kpis", kpis,
            "negativeKpis", negativeKpis,
            "positiveKpis", positiveKpis
        );

        Locale effectiveLocale = (locale != null ? locale : Locale.ITALY);

        // 1) Summary
        String htmlSummary =
                generationService.render("pdf/layouts/summary-only", baseVars, effectiveLocale);
        rendererService.renderToFile(htmlSummary, workDir, "report-summary.pdf");

        // 2) Summary + Negativi
        String htmlNeg =
                generationService.render("pdf/layouts/summary-plus-negative", baseVars, effectiveLocale);
        rendererService.renderToFile(htmlNeg, workDir, "report-negative.pdf");

        // 3) Full report
        String htmlFull =
                generationService.render("pdf/layouts/full-report", baseVars, effectiveLocale);
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
