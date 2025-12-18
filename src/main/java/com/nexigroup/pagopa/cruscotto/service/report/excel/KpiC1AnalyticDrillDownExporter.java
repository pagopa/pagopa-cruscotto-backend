package com.nexigroup.pagopa.cruscotto.service.report.excel;

import com.nexigroup.pagopa.cruscotto.domain.IoDrilldown;
import com.nexigroup.pagopa.cruscotto.repository.IoDrilldownRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class KpiC1AnalyticDrillDownExporter implements DrillDownExcelExporter {

    private final IoDrilldownRepository ioDrilldownRepository;

    private static final DateTimeFormatter DATE_FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public String getSheetName() {
        return "KPI_C1_ANALYTIC_DRILLDOWN";
    }

    @Override
    public int getOrder() {
        return 11; // scegli l’ordine corretto rispetto agli altri sheet
    }

    @Override
    public List<IoDrilldown> loadData(String instanceCode) {
        Long instanceId = Long.valueOf(instanceCode);
        return ioDrilldownRepository.findLatestByInstanceId(instanceId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void writeSheet(Sheet sheet, List<?> data) {

        // ===== HEADER =====
        Row header = sheet.createRow(0);
        String[] columns = {
            "Reference Date",
            "Data",
            "CF Institution",
            "CF Partner",
            "Positions Count",
            "Messages Count",
            "Percentage",
            "Meets Tolerance"
        };

        for (int i = 0; i < columns.length; i++) {
            header.createCell(i).setCellValue(columns[i]);
        }

        if (data == null || data.isEmpty()) {
            sheet.createRow(1).createCell(0).setCellValue("No data found");
            return;
        }

        List<IoDrilldown> records = (List<IoDrilldown>) data;

        // Ordinamento coerente per lettura Excel
        records.sort(
            Comparator.comparing(IoDrilldown::getCfInstitution)
                .thenComparing(IoDrilldown::getDataDate)
        );

        // ===== DATA =====
        int rowIdx = 1;
        for (IoDrilldown d : records) {
            Row row = sheet.createRow(rowIdx++);

            row.createCell(0).setCellValue(
                d.getReferenceDate() != null ? d.getReferenceDate().format(DATE_FORMATTER) : ""
            );
            row.createCell(1).setCellValue(
                d.getDataDate() != null ? d.getDataDate().format(DATE_FORMATTER) : ""
            );
            row.createCell(2).setCellValue(d.getCfInstitution());
            row.createCell(3).setCellValue(
                d.getCfPartner() != null ? d.getCfPartner() : ""
            );
            row.createCell(4).setCellValue(d.getPositionsCount());
            row.createCell(5).setCellValue(d.getMessagesCount());
            row.createCell(6).setCellValue(
                d.getPercentage() != null ? d.getPercentage() : 0.0
            );
            row.createCell(7).setCellValue(
                d.getMeetsTolerance() != null && d.getMeetsTolerance() ? "YES" : "NO"
            );
        }
    }
}
