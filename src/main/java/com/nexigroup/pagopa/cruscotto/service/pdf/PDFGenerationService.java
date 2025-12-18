package com.nexigroup.pagopa.cruscotto.service.pdf;

import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
public class PdfGenerationService {

    private final SpringTemplateEngine templateEngine;

    public PdfGenerationService(SpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    /**
     * Render HTML from a Thymeleaf PDF template using the given locale.
     */
    public String render(String template, Map<String, Object> variables, Locale locale) {
        Context context = new Context(locale != null ? locale : Locale.ITALY);

        if (variables != null) {
            context.setVariables(variables);
        }

        return templateEngine.process(template, context);
    }

    /**
     * Convenience method (default: IT)
     */
    public String render(String template, Map<String, Object> variables) {
        return render(template, variables, Locale.ITALY);
    }
}