package ch.admin.foitt.pilotwallet.platform.userInteraction.domain.repository

import ch.admin.foitt.pilotwallet.platform.userInteraction.domain.model.UserInteraction
import kotlinx.coroutines.flow.StateFlow

interface UserInteractionRepository {
    val lastInteraction: StateFlow<UserInteraction>

    fun updateLastInteraction()
}
