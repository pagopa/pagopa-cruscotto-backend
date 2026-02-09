package com.nexigroup.pagopa.cruscotto.service.report.excel;

import com.nexigroup.pagopa.cruscotto.domain.KpiB1AnalyticDrillDown;
import com.nexigroup.pagopa.cruscotto.repository.KpiB1AnalyticDataRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiB1AnalyticDrillDownRepository;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB1AnalyticDrillDownDTO;
import com.nexigroup.pagopa.cruscotto.service.report.excel.dto.KpiB1ResultReportExcelDTO;
import com.nexigroup.pagopa.cruscotto.service.report.repository.QueryReportRepository;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Order(3) // ordine nello sheet finale
@AllArgsConstructor
public class KpiB1AnalyticDrillDownExcelExporter
    implements DrillDownExcelExporter<KpiB1AnalyticDrillDownDTO> {

    QueryReportRepository queryReportRepository;

    private static final DateTimeFormatter DATE_FMT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd");



    @Override
    public String getSheetName() {
        return "KPI_B1";
    }

    @Override
    public int getOrder() {
        return 3;
    }

    @Override
    public List<KpiB1ResultReportExcelDTO> loadData(String instanceCode) {

        Long instanceId = Long.valueOf(instanceCode);
        return queryReportRepository.findKpiB1DrilldownForExcel(instanceId);
    }


    @Override
    public void writeSheet(Sheet sheet, List<KpiB1AnalyticDrillDownDTO> data) {

        int rowIdx = 0;

        // ===== HEADER =====
        Row header = sheet.createRow(rowIdx++);
        header.createCell(0).setCellValue("Date");
        header.createCell(1).setCellValue("Station Code");
        header.createCell(2).setCellValue("Institution Fiscal Code");
        header.createCell(3).setCellValue("Transaction Count");

        // ===== NO DATA =====
        if (data == null || data.isEmpty()) {
            Row row = sheet.createRow(rowIdx);
            row.createCell(0).setCellValue("NO DATA FOUND");
            return;
        }

        @SuppressWarnings("unchecked")
        List<KpiB1ResultReportExcelDTO> rows =
            (List<KpiB1ResultReportExcelDTO>) data;

        // ===== DATA =====
        for (KpiB1ResultReportExcelDTO r : rows) {
            Row row = sheet.createRow(rowIdx++);
            int count=0;
            row.createCell(count++).setCellValue(r.getDataDate() != null ? r.getDataDate().format(dateFormatter) : "");
            row.createCell(count++).setCellValue(r.getStationCode());
            row.createCell(count++).setCellValue(r.getInstitutionFiscalCode());
            row.createCell(count++).setCellValue(r.getTransactionCount());

        }

        // Autosize colonne
        for (int i = 0; i <= 4; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}
