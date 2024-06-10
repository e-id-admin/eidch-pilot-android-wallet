package ch.admin.foitt.pilotwallet.feature.sessionTimeout.domain

import androidx.annotation.CheckResult
import ch.admin.foitt.pilotwallet.platform.userInteraction.domain.model.UserInteraction
import kotlinx.coroutines.flow.StateFlow

interface UserInteractionFlow {
    @CheckResult
    suspend operator fun invoke(): StateFlow<UserInteraction>
}
