package com.nexigroup.pagopa.cruscotto.service.util;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.testcontainers.shaded.org.bouncycastle.math.ec.custom.sec.SecT113Field;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.nexigroup.pagopa.cruscotto.service.util.TaxonomyValidationUtils.extractTaxonomyTakingsIdentifier;
import static com.nexigroup.pagopa.cruscotto.service.util.TaxonomyValidationUtils.isCorrectPayment;

public class TaxonomyExtractorTest {

    @ParameterizedTest
    @CsvSource({
        "'9/0103101AP/030020300200099202','9/0103101AP'",
        "'9/0103100TS/','9/0103100TS/'",
        "'0108104AP','0108104AP'",
        "'9/0112102SP/Suolo,-spazi-e-ben','9/0112102SP'",
        "'9/0201102IM/','9/0201102IM/'",
        "'9/123','9/123'",
        "'9/0101107TS/','9/0101107TS/'",
        "'9/0601124SP/','9/0601124SP/'",
        "'9/0102105SA/438','9/0102105SA'",
        "'X/Csdersd/jfdhskjfas','X/Csdersd'",
        "'X/ajsdfhksd','X/ajsdfhksd'"
    })
    void shouldExtractCorrectIdentifier(
        String input,
        String expected) {

        Assert.assertEquals(
            expected,
            extractTaxonomyTakingsIdentifier(input));
    }


    private Set<String> taxonomySet;
    private Map<String, Boolean> cache;

    @BeforeEach
    void setup() {

        taxonomySet = Set.of(
            "9/0103101AP",
            "9/0103100TS/",
            "0108104AP",
            "9/0112102SP",
            "9/0201102IM/",
            "9/123",
            "9/0101107TS/",
            "9/0601124SP/",
            "9/0102105SA",
            "X/Csdersd",
            "X/ajsdfhksd"
        );

        cache = new ConcurrentHashMap<>();
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "9/0103101AP/030020300200099202",
        "9/0103100TS/",
        "0108104AP",
        "9/0112102SP/Suolo,-spazi-e-ben",
        "9/0201102IM/",
        "9/123",
        "9/0101107TS/",
        "9/0601124SP/",
        "9/0102105SA/438",
        "X/Csdersd/jfdhskjfas",
        "X/ajsdfhksd",
        "ajsdfhksd"
    })
    void shouldReturnTrue(String transferCategory) {

        Assert.assertTrue(
            isCorrectPayment(
                transferCategory,
                taxonomySet,
                cache));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "9/9999999AA/123",
        "X/Unknown/123",
        "Y/ajsdfhksd",
        "randomValue",
        ""
    })
    void shouldReturnFalse(String transferCategory) {

        Assert.assertFalse(
            isCorrectPayment(
                transferCategory,
                taxonomySet,
                cache));
    }



    @ParameterizedTest
    @ValueSource(strings = {
        "ajsdfhksd"
    })
    void shouldcontainValue(String transferCategory) {

        Assert.assertTrue(
            isCorrectPayment(
                transferCategory,
                taxonomySet,
                cache));
    }

}
