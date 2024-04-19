package ch.admin.foitt.pilotwallet.platform.onboardingState.domain.implementation

import ch.admin.foitt.pilotwallet.platform.appSetupState.domain.repository.OnboardingStateRepository
import ch.admin.foitt.pilotwallet.platform.onboardingState.domain.SaveOnboardingState
import javax.inject.Inject

class SaveOnboardingStateImpl @Inject constructor(
    private val repository: OnboardingStateRepository,
) : SaveOnboardingState {

    override suspend fun invoke(isCompleted: Boolean) {
        repository.saveOnboardingState(isCompleted = isCompleted)
    }
}
