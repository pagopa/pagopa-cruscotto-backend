package com.nexigroup.pagopa.cruscotto.service.report.excel;

import com.nexigroup.pagopa.cruscotto.domain.KpiA1AnalyticDrillDown;
import com.nexigroup.pagopa.cruscotto.repository.KpiA1AnalyticDataRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiA1AnalyticDrillDownRepository;

import com.nexigroup.pagopa.cruscotto.service.dto.KpiA1AnalyticDrillDownDTO;
import com.nexigroup.pagopa.cruscotto.service.report.excel.DrillDownExcelExporter;
import com.nexigroup.pagopa.cruscotto.service.report.excel.dto.KpiA1ResultReportExcelDTO;
import com.nexigroup.pagopa.cruscotto.service.report.repository.QueryReportRepository;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Order(1)
@AllArgsConstructor
public class KpiA1AnalyticDrillDownExporter implements DrillDownExcelExporter<KpiA1ResultReportExcelDTO> {

    QueryReportRepository queryReportRepository;


    private static final DateTimeFormatter HOUR_FMT =
        DateTimeFormatter.ofPattern("HH:mm")
            .withZone(ZoneId.systemDefault());



    @Override
    public String getSheetName() {
        return "KPI_A1";
    }

    @Override
    public int getOrder() {
        return 1;
    }

    @Override
    public List<KpiA1ResultReportExcelDTO> loadData(String instanceCode) {

        Long instanceId = Long.valueOf(instanceCode);


        return queryReportRepository.findKpiA1DrilldownForExcel(instanceId);

    }

    @Override
    public void writeSheet(Sheet sheet, List<KpiA1ResultReportExcelDTO> data) {

        int rowIdx = 0;

        // ===== HEADER =====
        Row header = sheet.createRow(rowIdx++);
        header.createCell(0).setCellValue("Period");
        header.createCell(1).setCellValue("From Hour");
        header.createCell(2).setCellValue("To Hour");
        header.createCell(3).setCellValue("Total Requests");
        header.createCell(4).setCellValue("OK Requests");
        header.createCell(5).setCellValue("Request Timeout");

        // ===== NO DATA FOUND =====
        if (data == null || data.isEmpty()) {
            Row row = sheet.createRow(rowIdx);
            row.createCell(0).setCellValue("NO DATA FOUND");
            return;
        }

        @SuppressWarnings("unchecked")
        List<KpiA1ResultReportExcelDTO> rows =
            (List<KpiA1ResultReportExcelDTO>) data;

        // ===== DATA =====
        for (KpiA1ResultReportExcelDTO r : rows) {
            Row row = sheet.createRow(rowIdx++);
            int count=0;

            row.createCell(count++).setCellValue(DrillDownExcelExporter.formatDateFromInstant(r.getFromHour()));
            row.createCell(count++).setCellValue(HOUR_FMT.format(r.getFromHour()));
            row.createCell(count++).setCellValue(HOUR_FMT.format(r.getToHour()));
            row.createCell(count++).setCellValue(r.getTotalRequests());
            row.createCell(count++).setCellValue(r.getOkRequests());
            row.createCell(count++).setCellValue(r.getReqTimeout());
        }
    }

}
