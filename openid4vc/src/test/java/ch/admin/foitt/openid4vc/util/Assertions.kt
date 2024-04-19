package ch.admin.foitt.openid4vc.util

import org.junit.Assert.fail

fun assertTrue(condition: Boolean, message: () -> String) {
    if (!condition) {
        fail(message())
    }
}
