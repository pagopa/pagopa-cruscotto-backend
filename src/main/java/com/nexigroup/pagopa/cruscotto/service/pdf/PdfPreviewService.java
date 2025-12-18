package com.nexigroup.pagopa.cruscotto.service.pdf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.nexigroup.pagopa.cruscotto.domain.*;
import com.nexigroup.pagopa.cruscotto.repository.*;
import com.nexigroup.pagopa.cruscotto.service.KpiA1DetailResultService;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiA1DetailResultDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB6DetailResultDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
public class PdfPreviewService {

    private static final Logger log = LoggerFactory.getLogger(PdfPreviewService.class);

    private final PdfGenerationService generationService;
    private final PdfRendererService rendererService;
    private final KpiA1DetailResultRepository kpiA1DetailResultRepository;
    private final KpiA2DetailResultRepository kpiA2DetailResultRepository;

    private final KpiB3DetailResultRepository kpiB3DetailResultRepository;
    private final KpiB4DetailResultRepository kpiB4DetailResultRepository;
    private final KpiB5DetailResultRepository kpiB5DetailResultRepository;

    private final KpiC1DetailResultRepository kpiC1DetailResultRepository;
    private final KpiC2DetailResultRepository kpiC2DetailResultRepository;


    public PdfPreviewService(
        PdfGenerationService generationService,
        PdfRendererService rendererService, KpiA1DetailResultRepository kpiA1DetailResultRepository, KpiA2DetailResultRepository kpiA2DetailResultRepository, KpiB3DetailResultRepository kpiB3DetailResultRepository, KpiB4DetailResultRepository kpiB4DetailResultRepository, KpiB5DetailResultRepository kpiB5DetailResultRepository, KpiC1DetailResultRepository kpiC1DetailResultRepository, KpiC2DetailResultRepository kpiC2DetailResultRepository
    ) {
        this.generationService = generationService;
        this.rendererService = rendererService;
        this.kpiA1DetailResultRepository = kpiA1DetailResultRepository;
        this.kpiA2DetailResultRepository = kpiA2DetailResultRepository;
        this.kpiB3DetailResultRepository = kpiB3DetailResultRepository;
        this.kpiB4DetailResultRepository = kpiB4DetailResultRepository;
        this.kpiB5DetailResultRepository = kpiB5DetailResultRepository;
        this.kpiC1DetailResultRepository = kpiC1DetailResultRepository;
        this.kpiC2DetailResultRepository = kpiC2DetailResultRepository;
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
        Long instanceId = 4060L;
        //
        List<KpiA1DetailResult> a1 = kpiA1DetailResultRepository.findLatestByInstanceId(instanceId);

        List<KpiA2DetailResult> a2 = kpiA2DetailResultRepository.findLatestByInstanceId(instanceId);

        List<KpiB3DetailResult> b3 = kpiB3DetailResultRepository.findLatestByInstanceId(instanceId);

        List<KpiB4DetailResult> b4 = kpiB4DetailResultRepository.findLatestByInstanceId(instanceId);

        List<KpiB5DetailResult> b5 = kpiB5DetailResultRepository.findLatestByInstanceId(instanceId);

        List<KpiC1DetailResult> c1 = kpiC1DetailResultRepository.findLatestByInstanceId(instanceId);

        List<KpiC2DetailResult> c2 = kpiC2DetailResultRepository.findLatestByInstanceId(instanceId);


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
