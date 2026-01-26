package com.nexigroup.pagopa.cruscotto.service.report.excel;

import com.nexigroup.pagopa.cruscotto.domain.IoDrilldown;
import com.nexigroup.pagopa.cruscotto.repository.IoDrilldownRepository;
import com.nexigroup.pagopa.cruscotto.service.dto.IoDrilldownDTO;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class KpiC1AnalyticDrillDownExporter implements DrillDownExcelExporter {

    private final IoDrilldownRepository ioDrilldownRepository;

    private static final DateTimeFormatter DATE_FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public String getSheetName() {
        return "KPI_C1";
    }

    @Override
    public int getOrder() {
        return 11; // scegli l’ordine corretto rispetto agli altri sheet
    }

    @Override
    public List<IoDrilldownDTO> loadData(String instanceCode) {
        Long instanceId = Long.valueOf(instanceCode);
        return ioDrilldownRepository.findLatestByInstanceId(instanceId).stream()
            .map(this::toDto)
            .collect(Collectors.toList());

    }

    IoDrilldownDTO toDto(IoDrilldown entity) {
        if (entity == null) return null;
        IoDrilldownDTO dto = new IoDrilldownDTO();
        dto.setId(entity.getId());
        dto.setAnalyticDataId(entity.getKpiC1AnalyticData() != null ? entity.getKpiC1AnalyticData().getId() : null);
        dto.setInstanceId(entity.getInstance() != null ? entity.getInstance().getId() : null);
        dto.setInstanceModuleId(entity.getInstanceModule() != null ? entity.getInstanceModule().getId() : null);
        dto.setReferenceDate(entity.getReferenceDate());
        dto.setDataDate(entity.getDataDate());
        dto.setCfInstitution(entity.getCfInstitution());
        dto.setCfPartner(entity.getCfPartner());
        dto.setPositionsCount(entity.getPositionsCount());
        dto.setMessagesCount(entity.getMessagesCount());
        dto.setPercentage(entity.getPercentage());
        dto.setMeetsTolerance(entity.getMeetsTolerance());
        return dto;
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

        List<IoDrilldownDTO> records = (List<IoDrilldownDTO>) data;

        // Ordinamento coerente per lettura Excel
        records.sort(
            Comparator.comparing(IoDrilldownDTO::getCfInstitution)
                .thenComparing(IoDrilldownDTO::getDataDate)
        );

        // ===== DATA =====
        int rowIdx = 1;
        for (IoDrilldownDTO d : records) {
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
