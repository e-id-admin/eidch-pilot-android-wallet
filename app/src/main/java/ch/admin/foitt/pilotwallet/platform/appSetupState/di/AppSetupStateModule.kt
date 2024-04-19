package ch.admin.foitt.pilotwallet.platform.appSetupState.di

import ch.admin.foitt.pilotwallet.platform.appSetupState.data.repository.OnboardingStateCompletionRepository
import ch.admin.foitt.pilotwallet.platform.appSetupState.data.repository.UseBiometricLoginRepositoryImpl
import ch.admin.foitt.pilotwallet.platform.appSetupState.domain.repository.OnboardingStateRepository
import ch.admin.foitt.pilotwallet.platform.appSetupState.domain.repository.UseBiometricLoginRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface AppSetupStateModule {

    @Binds
    fun bindEncryptedOnboardingStateRepository(
        repo: OnboardingStateCompletionRepository
    ): OnboardingStateRepository

    @Binds
    fun bindEncryptedUseBiometricLoginStateRepository(
        repo: UseBiometricLoginRepositoryImpl
    ): UseBiometricLoginRepository
}
