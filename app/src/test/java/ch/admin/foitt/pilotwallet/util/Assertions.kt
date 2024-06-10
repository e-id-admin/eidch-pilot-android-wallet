package ch.admin.foitt.pilotwallet.util

import org.junit.jupiter.api.Assertions.fail

fun assertTrue(condition: Boolean, message: () -> String) {
    if (!condition) {
        fail<String>(message())
    }
}
