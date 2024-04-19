package ch.admin.foitt.pilotwallet.platform.appSetupState.domain.repository

interface OnboardingStateRepository {
    suspend fun saveOnboardingState(isCompleted: Boolean)
    suspend fun getOnboardingState(): Boolean
}
