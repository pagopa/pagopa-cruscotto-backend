package com.nexigroup.pagopa.cruscotto.web.rest;


import com.nexigroup.pagopa.cruscotto.service.report.excel.ExcelReportGenerator;
import com.nexigroup.pagopa.cruscotto.service.report.pdf.PDFReportGenerator;
import com.nexigroup.pagopa.cruscotto.service.report.pdf.wrapper.WrapperPdfFiles;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/api/kpi-analytic-drilldown")
@RequiredArgsConstructor
public class KpiAnalyticDrillDownReportResource {

    private final ExcelReportGenerator excelReportGenerator;
    private final PDFReportGenerator PDFReportGenerator;

    /**
     * Export Excel DrillDown per Instance
     */
    @GetMapping("/export/{instanceId}")
    public ResponseEntity<byte[]> exportDrillDownExcel(
        @PathVariable Long instanceId
    ) {

        byte[] excel = excelReportGenerator.generateExcel(
            String.valueOf(instanceId)
        );

        return ResponseEntity.ok()
            .header(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=kpi-analytic-drilldown-" + instanceId + ".xlsx"
            )
            .contentType(
                MediaType.parseMediaType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                )
            )
            .body(excel);
    }

    @GetMapping("/exportPDF")
    public void exportPdfZip(
        @RequestParam("instanceId") Long instanceId,
        @RequestParam(value = "locale", required = false) Locale locale,
        HttpServletResponse response
    ) throws Exception {

        // Genera tutti i PDF come byte[]
        List<WrapperPdfFiles> pdfFiles = PDFReportGenerator.generatePreviewSetPdf(locale, instanceId);

        // Imposta header HTTP per download
        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=report_instance_" + instanceId + ".zip");

        // Scrive i PDF nello ZIP
        try (ZipOutputStream zos = new ZipOutputStream(response.getOutputStream())) {
            for (WrapperPdfFiles pdf : pdfFiles) {
                ZipEntry entry = new ZipEntry(pdf.getName());
                zos.putNextEntry(entry);
                zos.write(pdf.getContent());
                zos.closeEntry();
            }
            zos.finish();
        }
    }
}

