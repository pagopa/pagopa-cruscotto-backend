package com.nexigroup.pagopa.cruscotto.service.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.Set;

/**
 * Utility class for PagoPa taxonomic code validation.
 * Centralizes validation logic used across different parts of the system.
 */
public final class TaxonomyValidationUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaxonomyValidationUtils.class);

    private TaxonomyValidationUtils() {
        // Utility class
    }

    /**
     * Determines if a payment has a correct taxonomic code.
     * 
     * @param transferCategory the transfer category to validate
     * @param taxonomyTakingsIdentifierSet the set of valid takingsIdentifiers from taxonomy
     * @param transferCategoryMap cache to avoid repeated calculations
     * @return true if the taxonomic code is correct, false otherwise
     */
    public static boolean isCorrectPayment(
        String transferCategory,
        Set<String> taxonomyTakingsIdentifierSet,
        Map<String, Boolean> transferCategoryMap
    ) {
        if (StringUtils.isBlank(transferCategory) || CollectionUtils.isEmpty(taxonomyTakingsIdentifierSet)) {
            return false;
        }

        String taxonomyTakingsIdentifier;
        try {
            if (transferCategory.startsWith("9/")) {
                // Already starts with 9/, just ensure trailing slash
                taxonomyTakingsIdentifier = transferCategory.endsWith("/") 
                    ? transferCategory 
                    : transferCategory + "/";
            } else if (transferCategory.startsWith("6/")) {
                // Replace 6/ with 9/, preserving trailing slash
                String withoutPrefix = transferCategory.substring(2);  // Remove "6/"
                String withSlash = withoutPrefix.endsWith("/") 
                    ? withoutPrefix 
                    : withoutPrefix + "/";
                taxonomyTakingsIdentifier = "9/" + withSlash;
            } else {
                // For other cases, prepend 9/ and ensure trailing slash
                String withSlash = transferCategory.endsWith("/") 
                    ? transferCategory 
                    : transferCategory + "/";
                taxonomyTakingsIdentifier = "9/" + withSlash;
            }
        } catch (Exception e) {
            LOGGER.error("Error in parsing transferCategory {}", transferCategory, e);
            return false;
        } 

        // Use cache to avoid repeated lookups
        return transferCategoryMap.computeIfAbsent(taxonomyTakingsIdentifier, taxonomyTakingsIdentifierSet::contains);
    }
}
