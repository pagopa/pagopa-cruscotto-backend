package com.nexigroup.pagopa.cruscotto.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nexigroup.pagopa.cruscotto.service.ReportPackagingService;
import com.nexigroup.pagopa.cruscotto.service.dto.ExcelFile;
import com.nexigroup.pagopa.cruscotto.service.dto.ReportGenerationContext;
import com.nexigroup.pagopa.cruscotto.service.report.excel.DrillDownExcelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@Slf4j
public class ReportPackagingServiceImpl implements ReportPackagingService {

    private static final String PDF_NAME = "report.pdf";
    private static final String EXCEL_FOLDER = "excel/";
    private static final String METADATA_FILE = "metadata.json";

    private final ObjectMapper objectMapper;

    public ReportPackagingServiceImpl(DrillDownExcelService drillDownExcelService) {
        objectMapper = new ObjectMapper();
        // registriamo il modulo per Java 8 Date/Time
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public byte[] createReportPackage(ReportGenerationContext context, byte[] pdfContent, ExcelFile excelFile) {
        String baseFolder = "report_" + context.getReportId() + "/";

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zip = new ZipOutputStream(baos)) {

            // 1️⃣ PDF
            addEntry(zip, baseFolder + PDF_NAME, pdfContent);

            // 2️⃣ Excel file singolo
            if (excelFile != null) {
                addEntry(zip, baseFolder + EXCEL_FOLDER + excelFile.getFileName(), excelFile.getContent());
            }

            // 3️⃣ Metadata (JSON)
            byte[] metadataBytes = objectMapper
                .writerWithDefaultPrettyPrinter()
                .writeValueAsBytes(context);

            addEntry(zip, baseFolder + METADATA_FILE, metadataBytes);

            zip.finish();
            return baos.toByteArray();

        } catch (IOException e) {
            log.error("Error creating report ZIP for reportId={}", context.getReportId(), e);
            throw new RuntimeException("Failed to create report package", e);
        }
    }

    private void addEntry(ZipOutputStream zip, String name, byte[] content) throws IOException {
        ZipEntry entry = new ZipEntry(name);
        zip.putNextEntry(entry);
        zip.write(content);
        zip.closeEntry();
    }

}
