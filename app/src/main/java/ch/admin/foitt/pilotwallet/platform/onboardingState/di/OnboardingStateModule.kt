package ch.admin.foitt.pilotwallet.platform.onboardingState.di

import ch.admin.foitt.pilotwallet.platform.onboardingState.domain.SaveOnboardingState
import ch.admin.foitt.pilotwallet.platform.onboardingState.domain.implementation.SaveOnboardingStateImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
interface OnboardingStateModule {
    @Binds
    fun bindSaveOnboardingState(useCase: SaveOnboardingStateImpl): SaveOnboardingState
}
