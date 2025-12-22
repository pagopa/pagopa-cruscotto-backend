package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.service.dto.ExcelFile;
import com.nexigroup.pagopa.cruscotto.service.dto.ReportGenerationContext;

import java.util.List;

// Packager provides ZIP operations
public interface ReportPackagingService {
    byte[] createReportPackage(ReportGenerationContext context,
                               byte[] pdfContent,
                               ExcelFile excelFiles);
}
