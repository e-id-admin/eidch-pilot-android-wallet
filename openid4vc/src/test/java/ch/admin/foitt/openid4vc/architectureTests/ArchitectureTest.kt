package ch.admin.foitt.openid4vc.architectureTests

import com.tngtech.archunit.core.domain.JavaClass
import com.tngtech.archunit.core.domain.JavaModifier
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.core.importer.Location
import com.tngtech.archunit.lang.ArchCondition
import com.tngtech.archunit.lang.ConditionEvents
import com.tngtech.archunit.lang.SimpleConditionEvent
import java.util.regex.Pattern

open class ArchitectureTest {

    companion object {
        internal const val packageName = "ch.admin.foitt.openid4vc"
    }

    internal class DoNotIncludeUnitTests : ImportOption {
        private val pattern = Pattern.compile(".*/[a-zA-Z]+UnitTest/.*")

        override fun includes(location: Location) = location.matches(pattern).not()
    }

    internal class DoNotIncludeAndroidGeneratedFiles : ImportOption {
        private val pattern = Pattern.compile(".*\\$.*|.*_Factory.+|.*_AssistedFactory.+|.*_HiltModule.+")

        override fun includes(location: Location) = location.matches(pattern).not()
    }

    internal fun containSinglePublicMethodNamed(name: String): ArchCondition<JavaClass> {
        return object : ArchCondition<JavaClass>("have exactly one public method named '$name'") {
            override fun check(item: JavaClass?, events: ConditionEvents?) {
                if (item == null || events == null) {
                    return
                }
                val publicMethods = item.methods.filter { method ->
                    method.modifiers.contains(JavaModifier.PUBLIC)
                }.filterNot { method ->
                    method.modifiers.contains(JavaModifier.SYNTHETIC)
                }

                var satisfied = false
                var message = item.name + " contains " + publicMethods.size + " public method"
                if (publicMethods.size == 1) {
                    val methodName = publicMethods.first().name
                    satisfied = methodName.startsWith(name)
                    message += " named '" + methodName + "' " + publicMethods.first().sourceCodeLocation
                } else {
                    message += "s $publicMethods in ${item.sourceCodeLocation}"
                }

                events.add(SimpleConditionEvent(item, satisfied, message))
            }
        }
    }
}
