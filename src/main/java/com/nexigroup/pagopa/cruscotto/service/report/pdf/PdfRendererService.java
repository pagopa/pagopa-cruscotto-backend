package com.nexigroup.pagopa.cruscotto.service.report.pdf;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.lowagie.text.pdf.BaseFont;

@Service
public class PdfRendererService {

    public byte[] renderToBytes(String html, Path workDir) throws Exception {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();

            // BASE URI → CSS / IMG
            renderer.setDocumentFromString(html, workDir.toUri().toString());

            // Fonts
            addFonts(renderer, workDir);

            renderer.layout();
            renderer.createPDF(os);

            return os.toByteArray();
        }
    }

    private void addFonts(ITextRenderer renderer, Path workDir) throws Exception {
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
    }
}
