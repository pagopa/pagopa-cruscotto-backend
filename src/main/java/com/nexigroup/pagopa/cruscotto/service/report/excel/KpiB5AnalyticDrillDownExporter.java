package com.nexigroup.pagopa.cruscotto.service.report.excel;

import com.nexigroup.pagopa.cruscotto.domain.KpiB5AnalyticDrillDown;
import com.nexigroup.pagopa.cruscotto.repository.KpiB5AnalyticDrillDownRepository;
import com.nexigroup.pagopa.cruscotto.service.dto.PagopaSpontaneiDTO;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class KpiB5AnalyticDrillDownExporter implements DrillDownExcelExporter {

    private final KpiB5AnalyticDrillDownRepository repository;

    public KpiB5AnalyticDrillDownExporter(
        KpiB5AnalyticDrillDownRepository repository
    ) {
        this.repository = repository;
    }

    @Override
    public String getSheetName() {
        return "KPI_B5";
    }

    @Override
    public int getOrder() {
        return 7;
    }

    @Override
    public List<PagopaSpontaneiDTO> loadData(String instanceCode) {

        // instanceCode = instanceId (come da tuoi esempi precedenti)
        Long instanceId = Long.valueOf(instanceCode);

        List<PagopaSpontaneiDTO> result =
            repository.findLatestByInstanceId(instanceId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());


        if (result == null || result.isEmpty()) {
            return List.of();
        }

        // richiesto: excel del primo record trovato
        return List.of(result.get(0));
    }

    private PagopaSpontaneiDTO toDto(KpiB5AnalyticDrillDown drillDown) {
        PagopaSpontaneiDTO dto = new PagopaSpontaneiDTO();
        dto.setId(drillDown.getId());
        dto.setKpiB5AnalyticDataId(drillDown.getKpiB5AnalyticData().getId());
        dto.setPartnerId(drillDown.getPartnerId());
        dto.setPartnerName(drillDown.getPartnerName());
        dto.setPartnerFiscalCode(drillDown.getPartnerFiscalCode());
        dto.setStationCode(drillDown.getStationCode());
        dto.setFiscalCode(drillDown.getFiscalCode());

        // Use the enum's getValue() method to get the string representation
        dto.setSpontaneousPayments(drillDown.getSpontaneousPayments().getValue());

        return dto;
    }

    @Override
    public void writeSheet(Sheet sheet, List<?> data) {

        @SuppressWarnings("unchecked")        int rowIdx = 0;
        if (data == null || data.isEmpty()) {
            Row row = sheet.createRow(rowIdx);
            row.createCell(0).setCellValue("NO DATA FOUND");
            return;
        }

        List<PagopaSpontaneiDTO> rows =
            (List<PagopaSpontaneiDTO>) data;


        // ===== HEADER =====
        Row header = sheet.createRow(rowIdx++);

        header.createCell(0).setCellValue("Partner Fiscal Code");
        header.createCell(1).setCellValue("Station Code");
        header.createCell(2).setCellValue("Fiscal Code");
        header.createCell(3).setCellValue("Spontaneous Payments");

        // ===== DATA (1 record) =====
        for (PagopaSpontaneiDTO r : rows) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(r.getPartnerFiscalCode());
            row.createCell(1).setCellValue(r.getStationCode());
            row.createCell(2).setCellValue(r.getFiscalCode());
            row.createCell(3).setCellValue(r.getSpontaneousPayments() );
        }
    }
}
