package com.nexigroup.pagopa.cruscotto.service.report.excel;

import org.apache.poi.ss.usermodel.Sheet;

import java.util.List;

public interface DrillDownExcelExporter {

    /** Nome dello sheet */
    String getSheetName();

    /** Ordine di apparizione nello Excel */
    int getOrder();

    /** Recupero dati (input generico: instanceId o codice) */
    List<?> loadData(String instanceCode);

    /** Scrittura sheet */
    void writeSheet(Sheet sheet, List<?> data);
}

