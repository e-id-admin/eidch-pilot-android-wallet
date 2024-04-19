package ch.admin.foitt.pilotwallet.platform.login.domain.usecase

import ch.admin.foitt.pilotwallet.platform.navigation.domain.model.NavigationAction

fun interface NavigateToLogin {
    suspend operator fun invoke(): NavigationAction
}
