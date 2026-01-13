package com.nexigroup.pagopa.cruscotto.service.report.excel;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

@Service
public class ExcelReportGenerator {

    private final List<DrillDownExcelExporter> exporters;

    public ExcelReportGenerator(List<DrillDownExcelExporter> exporters) {
        this.exporters = exporters.stream()
            .sorted(Comparator.comparingInt(DrillDownExcelExporter::getOrder))
            .toList();
    }

    public byte[] generateExcel(String instanceCode) {

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            for (DrillDownExcelExporter exporter : exporters) {
                List<?> data = exporter.loadData(instanceCode);

                //if (data == null || data.isEmpty()) {
                //    continue;
                //}

                Sheet sheet = workbook.createSheet(exporter.getSheetName());
                exporter.writeSheet(sheet, data);
            }

            workbook.write(out);
            return out.toByteArray();

        } catch (IOException e) {
            throw new IllegalStateException("Errore generazione Excel", e);
        }
    }
}

