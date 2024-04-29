package ch.admin.foitt.pilotwallet.platform.deeplink.domain.usecase

import androidx.annotation.CheckResult
import ch.admin.foitt.pilotwallet.platform.navigation.domain.model.NavigationAction

fun interface HandleDeeplink {
    @CheckResult
    suspend operator fun invoke(fromOnboarding: Boolean): NavigationAction
}
