package ch.admin.foitt.pilotwallet.platform.onboardingState.domain

interface SaveOnboardingState {
    suspend operator fun invoke(isCompleted: Boolean)
}
