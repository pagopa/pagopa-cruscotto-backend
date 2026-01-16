package com.nexigroup.pagopa.cruscotto.service.impl;

import com.nexigroup.pagopa.cruscotto.service.dto.ExcelFile;
import com.nexigroup.pagopa.cruscotto.service.dto.ReportGenerationContext;
import com.nexigroup.pagopa.cruscotto.service.report.pdf.wrapper.WrapperPdfFiles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class ReportPackagingServiceImplTest {

    private ReportPackagingServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ReportPackagingServiceImpl(null);
    }

    @Test
    void shouldCreateZipWithPdfExcelAndMetadata() throws Exception {
        // given
        ReportGenerationContext context = Mockito.mock(ReportGenerationContext.class);
        when(context.getReportId()).thenReturn(123L);

        WrapperPdfFiles pdf1 = Mockito.mock(WrapperPdfFiles.class);
        when(pdf1.getName()).thenReturn("first.pdf");
        when(pdf1.getContent()).thenReturn("PDF1".getBytes());

        WrapperPdfFiles pdf2 = Mockito.mock(WrapperPdfFiles.class);
        when(pdf2.getName()).thenReturn(null);
        when(pdf2.getContent()).thenReturn("PDF2".getBytes());

        ExcelFile excelFile =
            new ExcelFile("report.xlsx", "EXCEL".getBytes(), "application/vnd.ms-excel");

        // when
        byte[] zipBytes =
            service.createReportPackage(context, List.of(pdf1, pdf2), excelFile);

        // then
        Map<String, byte[]> entries = unzip(zipBytes);

        assertThat(entries).containsKeys(
            "report_123/report.pdffirst.pdf",
            "report_123/report.pdfreport_2.pdf",
            "report_123/excel/report.xlsx",
            "report_123/metadata.json"
        );
    }

    @Test
    void shouldCreateZipWithOnlyMetadata() throws Exception {
        ReportGenerationContext context = Mockito.mock(ReportGenerationContext.class);
        when(context.getReportId()).thenReturn(999L);

        byte[] zipBytes = service.createReportPackage(context, null, null);

        Map<String, byte[]> entries = unzip(zipBytes);

        assertThat(entries).hasSize(1);
        assertThat(entries).containsKey("report_999/metadata.json");
    }

    // ---------- helper ----------
    private Map<String, byte[]> unzip(byte[] zipBytes) throws Exception {
        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipBytes))) {
            ZipEntry entry;
            Map<String, byte[]> result = new java.util.HashMap<>();

            while ((entry = zis.getNextEntry()) != null) {
                result.put(entry.getName(), zis.readAllBytes());
            }
            return result;
        }
    }
}
