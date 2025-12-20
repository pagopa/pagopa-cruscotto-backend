package com.nexigroup.pagopa.cruscotto.service.report.pdf;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.lowagie.text.pdf.BaseFont;

@Service
public class PdfRendererService {

    public Path renderToFile(String html, Path workDir, String outputFileName) throws Exception {

        Path pdfFile = workDir.resolve(outputFileName);

        try (OutputStream os = Files.newOutputStream(pdfFile)) {

            ITextRenderer renderer = new ITextRenderer();

            // BASE URI → CSS / IMG
            renderer.setDocumentFromString(html, workDir.toUri().toString());

            // Fonts
            renderer.getFontResolver().addFont(
                workDir.resolve("fonts/TitilliumWeb_Regular.ttf").toString(),
                BaseFont.IDENTITY_H,
                BaseFont.EMBEDDED
            );

            renderer.getFontResolver().addFont(
                workDir.resolve("fonts/TitilliumWeb_Bold.ttf").toString(),
                BaseFont.IDENTITY_H,
                BaseFont.EMBEDDED
            );

            renderer.layout();
            renderer.createPDF(os);
        }

        return pdfFile;
    }
}
