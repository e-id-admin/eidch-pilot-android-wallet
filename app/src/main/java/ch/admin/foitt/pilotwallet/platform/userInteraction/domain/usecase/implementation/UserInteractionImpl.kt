package ch.admin.foitt.pilotwallet.platform.userInteraction.domain.usecase.implementation

import ch.admin.foitt.pilotwallet.platform.userInteraction.domain.repository.UserInteractionRepository
import ch.admin.foitt.pilotwallet.platform.userInteraction.domain.usecase.UserInteraction
import javax.inject.Inject

class UserInteractionImpl @Inject constructor(
    private val userInteractionRepository: UserInteractionRepository
) : UserInteraction {
    override fun invoke() = userInteractionRepository.updateLastInteraction()
}
