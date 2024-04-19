package ch.admin.foitt.pilotwallet.architectureTests

import android.content.SharedPreferences
import androidx.room.RoomDatabase
import ch.admin.foitt.pilotwallet.app.PilotWalletApplication
import com.tngtech.archunit.core.domain.JavaAccess.Predicates.target
import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.domain.properties.HasName.Predicates.name
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.junit.ArchUnitRunner
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noConstructors
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noFields
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noMethods
import com.tngtech.archunit.library.GeneralCodingRules
import com.tngtech.archunit.library.GeneralCodingRules.BE_ANNOTATED_WITH_AN_INJECTION_ANNOTATION
import org.junit.runner.RunWith

@RunWith(ArchUnitRunner::class)
@AnalyzeClasses(
    packages = [ArchitectureTest.packageName],
    importOptions = [
        ArchitectureTest.DoNotIncludeUnitTests::class,
        ArchitectureTest.DoNotIncludeAndroidGeneratedFiles::class
    ]
)
class GeneralCodingTest : ArchitectureTest() {

    @ArchTest
    fun `use cases should be interfaces`(importedClasses: JavaClasses) {
        val rule = classes().that()
            .resideInAPackage("..usecase")
            .and()
            .haveNameNotMatching(".*UseCases$")
            .should()
            .beInterfaces()

        rule.check(importedClasses)
    }

    @ArchTest
    fun `use case implementations should not be interfaces`(importedClasses: JavaClasses) {
        val rule = noClasses().that()
            .resideInAPackage("..usecase.implementation")
            .should()
            .beInterfaces()
            .because("an implementation is concrete")

        rule.check(importedClasses)
    }

    @ArchTest
    fun `should not use collectAsState()`(importedClasses: JavaClasses) {
        val rule = noClasses()
            .should()
            .callMethodWhere(target(name("collectAsState")))
            .because("it's not lifecycle-aware. Use ::collectAsStateWithLifecycle()")

        rule.check(importedClasses)
    }

    @ArchTest
    fun `java_util_Log should not be used`(importedClasses: JavaClasses) {
        val rule = GeneralCodingRules.NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING
            .because("logging is a privacy risk")

        rule.check(importedClasses)
    }

    @ArchTest
    fun `material classes should not be used`(importedClasses: JavaClasses) {
        val rule = noClasses()
            .should()
            .dependOnClassesThat()
            .resideInAPackage("..compose.material..")
            .because("mixing up material components leads to weird issues")
        rule.check(importedClasses)
    }

    @ArchTest
    fun `no field injection should be used`(importedClasses: JavaClasses) {
        val rule = noFields().that()
            .areNotDeclaredIn(PilotWalletApplication::class.java)
            .should(BE_ANNOTATED_WITH_AN_INJECTION_ANNOTATION)
            .because(
                "field injection is considered harmful; use constructor injection or setter injection instead; " +
                    "see https://stackoverflow.com/q/39890849 for detailed explanations"
            )
        rule.check(importedClasses)
    }

    @ArchTest
    fun `non-encrypted SharedPreferences should not be used as method parameter`(importedClasses: JavaClasses) {
        val rule = noMethods()
            .should()
            .haveRawParameterTypes(SharedPreferences::class.java)
            .because("data at rest should be encrypted")
        rule.check(importedClasses)
    }

    @ArchTest
    fun `non-encrypted SharedPreferences should not be used as constructor parameter`(importedClasses: JavaClasses) {
        val rule = noConstructors()
            .should()
            .haveRawParameterTypes(SharedPreferences::class.java)
            .because("data at rest should be encrypted")
        rule.check(importedClasses)
    }

    @ArchTest
    fun `RoomDatabase should not be accessed directly outside its package`(importedClasses: JavaClasses) {
        val rule = classes().that().areAssignableTo(RoomDatabase::class.java)
            .should()
            .onlyBeAccessed()
            .byClassesThat()
            .resideInAPackage("$packageName.platform.database..")
            .because("there are specific use cases for that")
        rule.check(importedClasses)
    }

    @ArchTest
    fun `Base64 operations should not rely on the android specific class`(importedClasses: JavaClasses) {
        val rule = noClasses()
            .should()
            .dependOnClassesThat()
            .haveFullyQualifiedName("android.util.Base64")
            .because("It cannot run in unit tests")
        rule.check(importedClasses)
    }

    @ArchTest
    fun `Base64 operation should use the existing utils`(importedClasses: JavaClasses) {
        val rule = noClasses()
            .that()
            .resideOutsideOfPackage("..platform.utils..")
            .should()
            .dependOnClassesThat()
            .haveFullyQualifiedName("java.util.Base64")
            .orShould()
            .dependOnClassesThat()
            .haveFullyQualifiedName("android.util.Base64")
            .because("Using different encoder and decoders could lead to corrupted data")
        rule.check(importedClasses)
    }

    @ArchTest
    fun `Vanilla kotlin Result class should not be used`(importedClasses: JavaClasses) {
        val rule = noClasses()
            .should()
            .dependOnClassesThat()
            .haveFullyQualifiedName("kotlin.Result")
            .because("This project use the com.github.michaelbull.result library for that")
        rule.check(importedClasses)
    }
}
