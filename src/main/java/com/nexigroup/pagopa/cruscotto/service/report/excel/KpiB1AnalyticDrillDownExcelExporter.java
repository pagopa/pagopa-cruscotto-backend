package com.nexigroup.pagopa.cruscotto.service.report.excel;

import com.nexigroup.pagopa.cruscotto.domain.KpiB1AnalyticDrillDown;
import com.nexigroup.pagopa.cruscotto.repository.KpiB1AnalyticDataRepository;
import com.nexigroup.pagopa.cruscotto.repository.KpiB1AnalyticDrillDownRepository;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB1AnalyticDrillDownDTO;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Order(3) // ordine nello sheet finale
public class KpiB1AnalyticDrillDownExcelExporter
    implements DrillDownExcelExporter {

    private final KpiB1AnalyticDataRepository analyticDataRepository;
    private final KpiB1AnalyticDrillDownRepository drillDownRepository;

    private static final DateTimeFormatter DATE_FMT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public KpiB1AnalyticDrillDownExcelExporter(
        KpiB1AnalyticDataRepository analyticDataRepository,
        KpiB1AnalyticDrillDownRepository drillDownRepository
    ) {
        this.analyticDataRepository = analyticDataRepository;
        this.drillDownRepository = drillDownRepository;
    }

    @Override
    public String getSheetName() {
        return "KPI_B1";
    }

    @Override
    public int getOrder() {
        return 3;
    }

    @Override
    public List<KpiB1AnalyticDrillDownDTO> loadData(String instanceCode) {

        Long instanceId = Long.valueOf(instanceCode);

        List<Long> analyticDataIds =
            analyticDataRepository
                .findLatestAnalyticDataIdsByInstanceId(instanceId);

        if (analyticDataIds == null || analyticDataIds.isEmpty()) {
            return List.of();
        }

        return drillDownRepository
            .findByKpiB1AnalyticDataIds(analyticDataIds)
            .stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    private KpiB1AnalyticDrillDownDTO toDto(KpiB1AnalyticDrillDown entity) {

        KpiB1AnalyticDrillDownDTO dto = new KpiB1AnalyticDrillDownDTO();
        dto.setId(entity.getId());
        dto.setDataDate(entity.getDataDate());
        dto.setPartnerFiscalCode(entity.getPartnerFiscalCode());
        dto.setInstitutionFiscalCode(entity.getInstitutionFiscalCode());
        dto.setTransactionCount(entity.getTransactionCount());
        dto.setStationCode(entity.getStationCode());
        dto.setKpiB1AnalyticDataId(entity.getKpiB1AnalyticData().getId());

        return dto;

    }

    @Override
    public void writeSheet(Sheet sheet, List<?> data) {

        int rowIdx = 0;

        // ===== HEADER =====
        Row header = sheet.createRow(rowIdx++);
        header.createCell(0).setCellValue("Data Date");
        header.createCell(1).setCellValue("Partner Fiscal Code");
        header.createCell(2).setCellValue("Institution Fiscal Code");
        header.createCell(3).setCellValue("Transaction Count");
        header.createCell(4).setCellValue("Station Code");

        // ===== NO DATA =====
        if (data == null || data.isEmpty()) {
            Row row = sheet.createRow(rowIdx);
            row.createCell(0).setCellValue("NO DATA FOUND");
            return;
        }

        @SuppressWarnings("unchecked")
        List<KpiB1AnalyticDrillDownDTO> rows =
            (List<KpiB1AnalyticDrillDownDTO>) data;

        // ===== DATA =====
        for (KpiB1AnalyticDrillDownDTO r : rows) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(
                r.getDataDate() != null ? r.getDataDate().format(DATE_FMT) : ""
            );
            row.createCell(1).setCellValue(r.getPartnerFiscalCode());
            row.createCell(2).setCellValue(r.getInstitutionFiscalCode());
            row.createCell(3).setCellValue(r.getTransactionCount());
            row.createCell(4).setCellValue(
                r.getStationCode() != null ? r.getStationCode() : ""
            );
        }

        // Autosize colonne
        for (int i = 0; i <= 4; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}
