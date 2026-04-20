package com.nexigroup.pagopa.cruscotto.service;

import com.nexigroup.pagopa.cruscotto.service.dto.ExcelFile;
import com.nexigroup.pagopa.cruscotto.service.dto.ReportGenerationContext;
import com.nexigroup.pagopa.cruscotto.service.report.pdf.wrapper.WrapperPdfFiles;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

// Packager provides ZIP operations
public interface ReportPackagingService {
    byte[] createReportPackage(ReportGenerationContext context, List<WrapperPdfFiles> pdfFiles, ExcelFile excelFile);
    void writeReportPackage(ReportGenerationContext context, List<WrapperPdfFiles> pdfFiles, ExcelFile excelFile, OutputStream target) throws IOException;
}
