package com.nexigroup.pagopa.cruscotto.service.report.excel;

import com.nexigroup.pagopa.cruscotto.domain.PagoPaPaymentReceiptDrilldown;
import com.nexigroup.pagopa.cruscotto.repository.PagoPaPaymentReceiptDrilldownRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class KpiB9AnalyticDrillDownExporter implements DrillDownExcelExporter {

    private final PagoPaPaymentReceiptDrilldownRepository repository;

    private static final DateTimeFormatter DATE_FORMAT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final DateTimeFormatter TIME_FORMAT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.systemDefault());

    @Override
    public String getSheetName() {
        return "PAGOPA_PAYMENT_RECEIPT_DRILLDOWN";
    }

    @Override
    public int getOrder() {
        return 10; // posizionalo correttamente rispetto agli altri sheet
    }

    @Override
    public List<PagoPaPaymentReceiptDrilldown> loadData(String instanceCode) {
        Long instanceId = Long.valueOf(instanceCode);
        return repository.findLatestByInstanceId(instanceId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void writeSheet(Sheet sheet, List<?> data) {

        // ===== HEADER =====
        Row header = sheet.createRow(0);
        String[] columns = {
            "Analysis Date",
            "Evaluation Date",
            "Station ID",
            "Start Time",
            "End Time",
            "Total Responses",
            "OK Responses",
            "KO Responses"
        };

        for (int i = 0; i < columns.length; i++) {
            header.createCell(i).setCellValue(columns[i]);
        }

        if (data == null || data.isEmpty()) {
            sheet.createRow(1).createCell(0).setCellValue("No data found");
            return;
        }

        List<PagoPaPaymentReceiptDrilldown> records =
            (List<PagoPaPaymentReceiptDrilldown>) data;

        // Ordinamento di sicurezza (coerente con repository)
        records.sort(
            Comparator.comparing((PagoPaPaymentReceiptDrilldown d) -> d.getStation().getId())
                .thenComparing(PagoPaPaymentReceiptDrilldown::getStartTime)
        );

        // ===== DATA =====
        int rowIdx = 1;
        for (PagoPaPaymentReceiptDrilldown d : records) {
            Row row = sheet.createRow(rowIdx++);

            row.createCell(0).setCellValue(d.getAnalysisDate().format(DATE_FORMAT));
            row.createCell(1).setCellValue(d.getEvaluationDate().format(DATE_FORMAT));
            row.createCell(2).setCellValue(d.getStation().getId());
            row.createCell(3).setCellValue(TIME_FORMAT.format(d.getStartTime()));
            row.createCell(4).setCellValue(TIME_FORMAT.format(d.getEndTime()));
            row.createCell(5).setCellValue(d.getTotRes());
            row.createCell(6).setCellValue(d.getResOk());
            row.createCell(7).setCellValue(d.getResKo());
        }
    }
}
