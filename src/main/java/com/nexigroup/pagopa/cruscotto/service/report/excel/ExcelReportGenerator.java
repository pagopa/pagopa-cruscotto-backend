package com.nexigroup.pagopa.cruscotto.service.report.excel;

import com.nexigroup.pagopa.cruscotto.job.report.ReportGenerationJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

@Service
public class ExcelReportGenerator {

    private static final Logger log = LoggerFactory.getLogger(ExcelReportGenerator.class);

    private final List<DrillDownExcelExporter> exporters;

    public ExcelReportGenerator(List<DrillDownExcelExporter> exporters) {
        this.exporters = exporters.stream()
            .sorted(Comparator.comparingInt(DrillDownExcelExporter::getOrder))
            .toList();
    }

    public byte[] generateExcel(String instanceCode) {

        logMemory("START generateExcel instance=" + instanceCode);

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream(32 * 1024)) {

            logMemory("After workbook creation");

            for (DrillDownExcelExporter exporter : exporters) {
                String sheetName = exporter.getSheetName();
                log.info("Creating sheet {} for instance {}", sheetName, instanceCode);

                logMemory("Before loadData sheet=" + sheetName);
                List<?> data = exporter.loadData(instanceCode);
                logMemory(
                    "After loadData sheet=" + sheetName +
                        " size=" + (data != null ? data.size() : 0)
                );

                if (data == null || data.isEmpty()) {
                    log.info("No data for sheet {}", sheetName);
                    continue;
                }

                Sheet sheet = workbook.createSheet(sheetName);
                logMemory("After createSheet sheet=" + sheetName);

                exporter.writeSheet(sheet, data);
                logMemory("After writeSheet sheet=" + sheetName);

                // rilascio memoria
                data.clear();
                data = null;

                logMemory("After data clear sheet=" + sheetName);
            }

            logMemory("Before workbook.write");
            workbook.write(out);
            logMemory("After workbook.write");

            byte[] result = out.toByteArray();
            logMemory(
                "After toByteArray size=" + (result.length / (1024 * 1024)) + "MB"
            );

            return result;

        } catch (OutOfMemoryError oom) {
            logMemory("OOM in generateExcel");
            log.error("OOM during Excel generation for instance {}", instanceCode, oom);
            throw oom;

        } catch (IOException e) {
            throw new IllegalStateException("Errore generazione Excel", e);
        }
    }


    private void logMemory(String step) {
        Runtime rt = Runtime.getRuntime();
        long used = (rt.totalMemory() - rt.freeMemory()) / (1024 * 1024);
        long free = rt.freeMemory() / (1024 * 1024);
        long total = rt.totalMemory() / (1024 * 1024);
        long max = rt.maxMemory() / (1024 * 1024);

        log.info(
            "[MEMORY][{}] used={}MB free={}MB total={}MB max={}MB",
            step, used, free, total, max
        );
    }
}

