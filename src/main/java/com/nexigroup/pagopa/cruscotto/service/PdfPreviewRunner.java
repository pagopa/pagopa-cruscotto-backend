package com.nexigroup.pagopa.cruscotto.service;

import java.nio.file.Path;
import java.util.Locale;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.nexigroup.pagopa.cruscotto.service.report.pdf.PdfPreviewService;

@Configuration
public class PdfPreviewRunner {

    @Bean
    CommandLineRunner previewPdfRunner(PdfPreviewService pdfPreviewService) {
        return args -> {
            Path pdf = pdfPreviewService.generatePreviewSetPdf(Locale.ITALY);
            System.out.println("PDF generati in: " + pdf.toAbsolutePath());
        };
    }
}
