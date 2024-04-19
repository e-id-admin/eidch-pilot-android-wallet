package ch.admin.foitt.pilotwallet.platform.deeplink.domain.usecase

import ch.admin.foitt.pilotwallet.platform.navigation.domain.model.NavigationAction

interface AfterLoginNavigation {
    suspend operator fun invoke(): NavigationAction
}
