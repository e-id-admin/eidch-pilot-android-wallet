package ch.admin.foitt.pilotwallet.platform.preview

import androidx.compose.runtime.Composable

fun interface ComposableWrapper<T> {
    @Composable
    fun value(): T
}
