package ch.admin.foitt.pilotwallet.feature.login.domain.usecase

import androidx.annotation.CheckResult
import ch.admin.foitt.pilotwallet.platform.navigation.domain.model.NavigationAction
import kotlinx.coroutines.flow.StateFlow

fun interface LockTrigger {
    @CheckResult
    suspend operator fun invoke(): StateFlow<NavigationAction>
}
