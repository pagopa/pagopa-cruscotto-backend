package com.nexigroup.pagopa.cruscotto.service.report.excel;

import com.nexigroup.pagopa.cruscotto.domain.PagoPaPaymentReceiptDrilldown;
import com.nexigroup.pagopa.cruscotto.repository.PagoPaPaymentReceiptDrilldownRepository;
import com.nexigroup.pagopa.cruscotto.service.dto.PagoPaPaymentReceiptDrilldownDTO;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class KpiB9AnalyticDrillDownExporter implements DrillDownExcelExporter<PagoPaPaymentReceiptDrilldownDTO> {

    private final PagoPaPaymentReceiptDrilldownRepository repository;


    private static final DateTimeFormatter TIME_FORMAT =
        DateTimeFormatter.ofPattern("HH:mm")
            .withZone(ZoneId.systemDefault());

    @Override
    public String getSheetName() {
        return "KPI_B9";
    }

    @Override
    public int getOrder() {
        return 10; // posizionalo correttamente rispetto agli altri sheet
    }

    @Override
    public List<PagoPaPaymentReceiptDrilldownDTO> loadData(String instanceCode) {
        Long instanceId = Long.valueOf(instanceCode);
        return repository.findLatestByInstanceId(instanceId)
            .stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    private PagoPaPaymentReceiptDrilldownDTO toDto(PagoPaPaymentReceiptDrilldown record) {
            PagoPaPaymentReceiptDrilldownDTO dto = new PagoPaPaymentReceiptDrilldownDTO();
            dto.setId(record.getId());
            dto.setInstanceId(record.getInstance().getId());
            dto.setInstanceModuleId(record.getInstanceModule().getId());
            dto.setStationId(record.getStation().getId());
            //dto.setStationName(record.getStation().getName());
            dto.setAnalysisDate(record.getAnalysisDate());
            dto.setEvaluationDate(record.getEvaluationDate());
            dto.setStartTime(record.getStartTime());
            dto.setEndTime(record.getEndTime());
            dto.setTotRes(record.getTotRes());
            dto.setResOk(record.getResOk());
            dto.setResKo(record.getResKo());

            // Calculate percentage
            if (record.getTotRes() > 0) {
                dto.setResKoPercentage((double) (record.getResKo() * 100) / record.getTotRes());
            } else {
                dto.setResKoPercentage(0.0);
            }

            // Create time slot string
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime startTime = record.getStartTime().atZone(ZoneId.systemDefault()).toLocalTime();
            LocalTime endTime = record.getEndTime().atZone(ZoneId.systemDefault()).toLocalTime();
            dto.setTimeSlot(startTime.format(timeFormatter) + "-" + endTime.format(timeFormatter));

            return dto;

    }


    @Override
    @SuppressWarnings("unchecked")
    public void writeSheet(Sheet sheet, List<PagoPaPaymentReceiptDrilldownDTO> data) {

        // ===== HEADER =====
        Row header = sheet.createRow(0);
        String[] columns = {
            "Period",
            "From hour",
            "To Hour",
            "Total response",
            "KO response"
        };

        for (int i = 0; i < columns.length; i++) {
            header.createCell(i).setCellValue(columns[i]);
        }

        if (data == null || data.isEmpty()) {
            sheet.createRow(1).createCell(0).setCellValue("No data found");
            return;
        }

        List<PagoPaPaymentReceiptDrilldownDTO> records =
            (List<PagoPaPaymentReceiptDrilldownDTO>) data;

        // Ordinamento di sicurezza (coerente con repository)
        records.sort(
            Comparator.comparing((PagoPaPaymentReceiptDrilldownDTO d) -> d.getStationId())
                .thenComparing(PagoPaPaymentReceiptDrilldownDTO::getStartTime)
        );

        // ===== DATA =====
        int rowIdx = 1;
        for (PagoPaPaymentReceiptDrilldownDTO d : records) {
            Row row = sheet.createRow(rowIdx++);
            int count =0;
            row.createCell(count++).setCellValue(d.getEvaluationDate() != null ? d.getEvaluationDate().format(dateFormatter) : "");
            row.createCell(count++).setCellValue(TIME_FORMAT.format(d.getStartTime()));
            row.createCell(count++).setCellValue(TIME_FORMAT.format(d.getEndTime()));
            row.createCell(count++).setCellValue(d.getTotRes());
            row.createCell(count++).setCellValue(d.getResKo());
        }
    }
}
