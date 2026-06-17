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
        Map<String, Boolean> transferCategoryMap) {

        if (StringUtils.isBlank(transferCategory)
            || CollectionUtils.isEmpty(taxonomyTakingsIdentifierSet)) {
            return false;
        }

        try {
            String taxonomyTakingsIdentifier =
                extractTaxonomyTakingsIdentifier(transferCategory);

            return transferCategoryMap.computeIfAbsent(
                taxonomyTakingsIdentifier,
                key -> taxonomyTakingsIdentifierSet.stream()
                    .anyMatch(value -> value.contains(key))
            );

        } catch (Exception e) {
            LOGGER.error(
                "Error in parsing transferCategory {}",
                transferCategory,
                e);

            return false;
        }
    }

    public static String extractTaxonomyTakingsIdentifier(String transferCategory) {

        if (StringUtils.isBlank(transferCategory)) {
            return null;
        }

        String value = transferCategory.trim();

        int firstSlash = value.indexOf('/');

        if (firstSlash < 0) {
            return value;
        }

        int secondSlash = value.indexOf('/', firstSlash + 1);

        if (secondSlash < 0) {
            return value;
        }

        // se termina proprio con il secondo slash
        if (secondSlash == value.length() - 1) {
            return value;
        }

        return value.substring(0, secondSlash);
    }


}
