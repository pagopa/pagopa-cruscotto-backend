package com.nexigroup.pagopa.cruscotto.service.report.excel;

import com.nexigroup.pagopa.cruscotto.domain.KpiB2AnalyticDrillDown;
import com.nexigroup.pagopa.cruscotto.repository.KpiB2AnalyticDataRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiB2AnalyticDrillDownRepository;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB2AnalyticDrillDownDTO;
import com.nexigroup.pagopa.cruscotto.service.report.excel.dto.KpiB2ResultReportExcelDTO;
import com.nexigroup.pagopa.cruscotto.service.report.repository.QueryReportRepository;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Order(4) // ordine dopo B1
@AllArgsConstructor
public class KpiB2AnalyticDrillDownExcelExporter
    implements DrillDownExcelExporter<KpiB2ResultReportExcelDTO> {


    private static final DateTimeFormatter HOUR_FMT =
        DateTimeFormatter.ofPattern("HH:mm")
            .withZone(ZoneId.systemDefault());

    QueryReportRepository queryReportRepository;

    @Override
    public String getSheetName() {
        return "KPI_B2";
    }

    @Override
    public int getOrder() {
        return 4;
    }

    @Override
    public List<KpiB2ResultReportExcelDTO> loadData(String instanceCode) {
        Long instanceId = Long.valueOf(instanceCode);
        return queryReportRepository.findKpiB2DrilldownForExcel(instanceId);
    }



    @Override
    public void writeSheet(Sheet sheet, List<KpiB2ResultReportExcelDTO> data) {

        int rowIdx = 0;

        // ===== HEADER =====
        Row header = sheet.createRow(rowIdx++);
        header.createCell(0).setCellValue("Period");
        header.createCell(1).setCellValue("From Hour");
        header.createCell(2).setCellValue("To Hour");
        header.createCell(3).setCellValue("Total Requests");
        header.createCell(4).setCellValue("OK Requests");
        header.createCell(5).setCellValue("Average Time (ms)");

        // ===== NO DATA =====
        if (data == null || data.isEmpty()) {
            Row row = sheet.createRow(rowIdx);
            row.createCell(0).setCellValue("NO DATA FOUND");
            return;
        }

        @SuppressWarnings("unchecked")
        List<KpiB2ResultReportExcelDTO> rows =
            (List<KpiB2ResultReportExcelDTO>) data;

        // ===== DATA =====
        for (KpiB2ResultReportExcelDTO r : rows) {
            Row row = sheet.createRow(rowIdx++);
            int count =0;
            row.createCell(count++).setCellValue(DrillDownExcelExporter.formatDateFromInstant(r.getFromHour()));
            row.createCell(count++).setCellValue(r.getFromHour() != null ? HOUR_FMT.format(r.getFromHour()) : "");
            row.createCell(count++).setCellValue(r.getEndHour() != null ? HOUR_FMT.format(r.getEndHour()) : "");
            row.createCell(count++).setCellValue(r.getTotalRequests());
            row.createCell(count++).setCellValue(r.getOkRequests());
            row.createCell(count++).setCellValue(r.getAverageTimeMs() != null ? r.getAverageTimeMs() : 0d);
        }

        // Autosize colonne
        for (int i = 0; i <= 4; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}
