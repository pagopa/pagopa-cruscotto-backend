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
            // Ensure transferCategory ends with "/"
            if (!transferCategory.endsWith("/")) {
                transferCategory = transferCategory + "/";
            }

            // Extract takingsIdentifier according to PagoPa logic
            if (transferCategory.startsWith("9/")) {
                taxonomyTakingsIdentifier = transferCategory.substring(0, 12);
            } else {
                taxonomyTakingsIdentifier = "9/" + transferCategory.substring(0, 10);
            }
        } catch (Exception e) {
            LOGGER.error("Error in parsing transferCategory {}", transferCategory, e);
            return false;
        }

        // Use cache to avoid repeated lookups
        return transferCategoryMap.computeIfAbsent(taxonomyTakingsIdentifier, taxonomyTakingsIdentifierSet::contains);
    }
}
