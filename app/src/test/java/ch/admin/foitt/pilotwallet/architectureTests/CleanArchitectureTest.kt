package ch.admin.foitt.pilotwallet.architectureTests

import androidx.room.Dao
import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.junit.ArchUnitRunner
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices
import org.junit.runner.RunWith

@RunWith(ArchUnitRunner::class)
@AnalyzeClasses(
    packages = [ArchitectureTest.packageName],
    importOptions = [
        ArchitectureTest.DoNotIncludeUnitTests::class,
        ArchitectureTest.DoNotIncludeAndroidGeneratedFiles::class,
    ]
)
class CleanArchitectureTest : ArchitectureTest() {

    @ArchTest
    fun `feature packages should not depend on other feature packages`(importedClasses: JavaClasses) {
        val rule = slices()
            .matching("$packageName.feature.(*)..")
            .should()
            .notDependOnEachOther()
            .because("each feature package should be independent from other feature packages")

        rule.check(importedClasses)
    }

    @ArchTest
    fun `feature packages should be free of cycles`(importedClasses: JavaClasses) {
        val rule = slices()
            .matching("$packageName.feature.(*)..")
            .should()
            .beFreeOfCycles()

        rule.check(importedClasses)
    }

    @ArchTest
    fun `platform packages should be free of cycles`(importedClasses: JavaClasses) {
        val rule = slices()
            .matching("$packageName.platform.(*)..")
            .should()
            .beFreeOfCycles()

        rule.check(importedClasses)
    }

    @ArchTest
    fun `feature packages should not depend on app package`(importedClasses: JavaClasses) {
        val rule = classes().that()
            .resideInAPackage("$packageName.feature..")
            .should()
            .onlyDependOnClassesThat()
            .resideOutsideOfPackage("$packageName.app..")
            .because("feature packages should not depend on app package")

        rule.check(importedClasses)
    }

    @ArchTest
    fun `platform packages should not depend on feature packages`(importedClasses: JavaClasses) {
        val rule = classes().that()
            .resideInAPackage("$packageName.platform..")
            .should()
            .onlyDependOnClassesThat()
            .resideOutsideOfPackage("$packageName.feature..")
            .because("platform packages should not depend on feature packages")

        rule.check(importedClasses)
    }

    @ArchTest
    fun `platform packages should not depend on core package`(importedClasses: JavaClasses) {
        val rule = classes().that()
            .resideInAPackage("$packageName.platform..")
            .should()
            .onlyDependOnClassesThat()
            .resideOutsideOfPackage("$packageName.app..")
            .because("platform packages should not depend on app package")

        rule.check(importedClasses)
    }

    @ArchTest
    fun `domain has no dependencies to data or presentation`(importedClasses: JavaClasses) {
        val rule = noClasses().that()
            .resideInAPackage("..feature..domain..")
            .should()
            .accessClassesThat()
            .resideInAnyPackage("..feature..data..", "..feature..presentation..")
            .because("domain should not depend on data or presentation")

        rule.check(importedClasses)
    }

    @ArchTest
    fun `presentation does not depend on data`(importedClasses: JavaClasses) {
        val rule = noClasses().that()
            .resideInAPackage("..feature..presentation..")
            .should()
            .accessClassesThat()
            .resideInAPackage("..feature..data..")
            .because("presentation should not depend on data")

        rule.check(importedClasses)
    }

    @ArchTest
    fun `only app and destinations can depend on feature packages`(importedClasses: JavaClasses) {
        val rule = noClasses().that()
            .resideOutsideOfPackages(
                "$packageName.app..",
                "$packageName.feature..",
                "ch.admin.foitt.pilotwallet.destinations.."
            )
            .should()
            .dependOnClassesThat()
            .resideInAPackage("$packageName.feature..")

        rule.check(importedClasses)
    }

    @ArchTest
    fun `domain_repository should only contain interfaces`(importedClasses: JavaClasses) {
        val rule = classes().that()
            .resideInAPackage("..domain.repository")
            .should()
            .beInterfaces()
            .because("implementations should be in data package")

        rule.check(importedClasses)
    }

    @ArchTest
    fun `a use case contains exactly one public method named invoke`(importedClasses: JavaClasses) {
        val rule = classes().that()
            .resideInAPackage("..domain.usecase.implementation")
            .and()
            .areNotInnerClasses()
            .should(containSinglePublicMethodNamed("invoke"))

        rule.check(importedClasses)
    }

    @ArchTest
    fun `@Dao annotated class names should end in Dao`(importedClasses: JavaClasses) {
        val rule = classes().that()
            .areAnnotatedWith(Dao::class.java)
            .should()
            .haveNameMatching(".*Dao")

        rule.check(importedClasses)
    }

    @ArchTest
    fun `DAOs should only be in data layer`(importedClasses: JavaClasses) {
        val rule = noClasses().that()
            .resideOutsideOfPackage("..data..")
            .should()
            .haveNameMatching(".*Dao")

        rule.check(importedClasses)
    }

    @ArchTest
    fun `a use case implementation should not be an interface`(importedClasses: JavaClasses) {
        val rule = classes().that()
            .resideInAPackage("..domain.usecase.implementation")
            .and()
            .areNotInnerClasses()
            .should()
            .notBeInterfaces()

        rule.check(importedClasses)
    }
}
