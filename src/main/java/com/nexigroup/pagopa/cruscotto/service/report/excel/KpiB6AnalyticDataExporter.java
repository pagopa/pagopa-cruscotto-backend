package com.nexigroup.pagopa.cruscotto.service.report.excel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexigroup.pagopa.cruscotto.domain.KpiAnalyticData;
import com.nexigroup.pagopa.cruscotto.domain.enumeration.ModuleCode;
import com.nexigroup.pagopa.cruscotto.repository.KpiAnalyticDataRepository;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiAnalyticDataDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB6AdditionalAnalyticDataDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.KpiB6AdditionalDetailDataDTO;
import com.nexigroup.pagopa.cruscotto.service.dto.PagopaSpontaneiDTO;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class KpiB6AnalyticDataExporter implements DrillDownExcelExporter {

    private final KpiAnalyticDataRepository repository;
    private final ObjectMapper objectMapper;


    @Override
    public String getSheetName() {
        return "KPI_B6";
    }

    @Override
    public int getOrder() {
        return 7;
    }

    @Override
    public List<KpiAnalyticDataDTO> loadData(String instanceCode) {

        // instanceCode = instanceId (come da tuoi esempi precedenti)
        Long instanceId = Long.valueOf(instanceCode);

        List<KpiAnalyticDataDTO> result =
            repository.findLatestByInstanceIdAndModuleCode(instanceId, ModuleCode.B6)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());


        if (result == null || result.isEmpty()) {
            return List.of();
        }

        // richiesto: excel del primo record trovato
        return result;
    }

    private KpiAnalyticDataDTO toDto(KpiAnalyticData kpiAnalyticData) {

        KpiAnalyticDataDTO kpiAnalyticDataDTO = new KpiAnalyticDataDTO();

        kpiAnalyticDataDTO.setId( kpiAnalyticData.getId() );
        kpiAnalyticDataDTO.setModuleCode( kpiAnalyticData.getModuleCode() );
        kpiAnalyticDataDTO.setInstanceId( kpiAnalyticData.getInstanceId() );
        kpiAnalyticDataDTO.setInstanceModuleId( kpiAnalyticData.getInstanceModuleId() );
        kpiAnalyticDataDTO.setKpiDetailResultId( kpiAnalyticData.getKpiDetailResultId() );
        kpiAnalyticDataDTO.setAnalysisDate( kpiAnalyticData.getAnalysisDate() );
        kpiAnalyticDataDTO.setDataDate( kpiAnalyticData.getDataDate() );
        kpiAnalyticDataDTO.setAnalyticData( kpiAnalyticData.getAnalyticData() );
        kpiAnalyticDataDTO.setCreatedBy( kpiAnalyticData.getCreatedBy() );
        kpiAnalyticDataDTO.setCreatedDate( kpiAnalyticData.getCreatedDate() );
        kpiAnalyticDataDTO.setLastModifiedBy( kpiAnalyticData.getLastModifiedBy() );
        kpiAnalyticDataDTO.setLastModifiedDate( kpiAnalyticData.getLastModifiedDate() );

        return kpiAnalyticDataDTO;
    }

    @Override
    public void writeSheet(Sheet sheet, List<?> data) {
        int rowIdx = 0;
        // ===== HEADER =====

        Row header = sheet.createRow(rowIdx++);
        header.createCell(0).setCellValue("Analysis date");
        header.createCell(1).setCellValue("Station Code");
        header.createCell(2).setCellValue("Optional payment");

        if (data == null || data.isEmpty()) {
            Row row = sheet.createRow(rowIdx);
            row.createCell(0).setCellValue("NO DATA FOUND");
            return;
        }

        List<KpiAnalyticDataDTO> rows =(List<KpiAnalyticDataDTO>) data;




        // ===== DATA (1 record) =====
        for (KpiAnalyticDataDTO r : rows) {
            Row row = sheet.createRow(rowIdx++);
            KpiB6AdditionalAnalyticDataDTO anal =  readAdditionalData(r.getAnalyticData());
            row.createCell(0).setCellValue(
                r.getAnalysisDate() != null ? r.getAnalysisDate().format(dateFormatter) : "");

            row.createCell(1).setCellValue(anal.getStationCode());
            row.createCell(2).setCellValue(anal.getPaymentOptionsEnabled()?"SI":"NO" );
        }
    }

    public  KpiB6AdditionalAnalyticDataDTO readAdditionalData(String json) {
        try {
            return objectMapper.readValue(json, KpiB6AdditionalAnalyticDataDTO.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Error deserializing B6 additionalData", e);
        }
    }
}
