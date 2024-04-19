package ch.admin.foitt.pilotwallet.platform.navigation.domain.model

import androidx.annotation.MainThread

fun interface NavigationAction {
    @MainThread
    fun navigate()
}
