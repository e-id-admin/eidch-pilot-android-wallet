package ch.admin.foitt.openid4vc.architectureTests

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.junit.ArchUnitRunner
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
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
    fun `domain has no dependencies to data or presentation`(importedClasses: JavaClasses) {
        val rule = noClasses().that()
            .resideInAPackage("..domain..")
            .should()
            .accessClassesThat()
            .resideInAnyPackage("..data..", "..presentation..")
            .because("domain should not depend on data or presentation")

        rule.check(importedClasses)
    }

    @ArchTest
    fun `domain_repository should only contain interfaces`(importedClasses: JavaClasses) {
        val rule = classes().that()
            .resideInAPackage("..domain.repository")
            .should()
            .beInterfaces()
            .because("implementations should be in data package")

        rule.allowEmptyShould(true).check(importedClasses)
    }

    @ArchTest
    fun `a use case contains exactly one public method named invoke`(importedClasses: JavaClasses) {
        val rule = classes().that()
            .resideInAPackage("..domain.usecase.implementation")
            .and()
            .areNotInnerClasses()
            .should(containSinglePublicMethodNamed("invoke"))

        rule.allowEmptyShould(true).check(importedClasses)
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
