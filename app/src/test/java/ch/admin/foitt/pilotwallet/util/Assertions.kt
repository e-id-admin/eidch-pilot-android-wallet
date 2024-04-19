package ch.admin.foitt.pilotwallet.util

import org.junit.Assert.fail

fun assertTrue(condition: Boolean, message: () -> String) {
    if (!condition) {
        fail(message())
    }
}
