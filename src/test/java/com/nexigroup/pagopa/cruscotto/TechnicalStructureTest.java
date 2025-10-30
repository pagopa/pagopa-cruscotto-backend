package com.nexigroup.pagopa.cruscotto;

import static com.tngtech.archunit.base.DescribedPredicate.alwaysTrue;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.belongToAnyOf;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

import com.nexigroup.pagopa.cruscotto.config.ApplicationProperties;
import com.nexigroup.pagopa.cruscotto.config.Constants;
import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packagesOf = PagoPaCruscottoBackendApp.class, importOptions = DoNotIncludeTests.class)
class TechnicalStructureTest {

    // prettier-ignore
        @ArchTest
        static final ArchRule respectsTechnicalArchitectureLayers = layeredArchitecture()
            .consideringAllDependencies()
            .layer("Config").definedBy("..config..")
            .layer("Web").definedBy("..web..")
            .layer("Job").definedBy("..job..")
            .layer("Kpi").definedBy("..kpi..")
            .optionalLayer("Service").definedBy("..service..")
            .layer("Security").definedBy("..security..")
            .optionalLayer("Persistence").definedBy("..repository..")
            .layer("Domain").definedBy("..domain..")

            .whereLayer("Config").mayNotBeAccessedByAnyLayer()
            .whereLayer("Web").mayOnlyBeAccessedByLayers("Config")
            .whereLayer("Kpi").mayOnlyBeAccessedByLayers("Service", "Job", "Config")
            .whereLayer("Service").mayOnlyBeAccessedByLayers("Web", "Config", "Job", "Kpi")
            .whereLayer("Security").mayOnlyBeAccessedByLayers("Config", "Service", "Web", "Job")
            .whereLayer("Persistence").mayOnlyBeAccessedByLayers("Service", "Security", "Web", "Config", "Job", "Kpi")
            .whereLayer("Domain").mayOnlyBeAccessedByLayers("Persistence", "Service", "Security", "Web", "Config", "Job", "Kpi")

            .ignoreDependency(belongToAnyOf(PagoPaCruscottoBackendApp.class), alwaysTrue())
            .ignoreDependency(alwaysTrue(), belongToAnyOf(
                Constants.class,
                ApplicationProperties.class
            ));
}
