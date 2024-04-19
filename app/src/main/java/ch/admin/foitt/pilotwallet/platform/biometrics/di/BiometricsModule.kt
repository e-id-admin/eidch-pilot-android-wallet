package ch.admin.foitt.pilotwallet.platform.biometrics.di

import ch.admin.foitt.pilotwallet.platform.biometrics.domain.usecase.EnableBiometrics
import ch.admin.foitt.pilotwallet.platform.biometrics.domain.usecase.GetBiometricsCipher
import ch.admin.foitt.pilotwallet.platform.biometrics.domain.usecase.ResetBiometrics
import ch.admin.foitt.pilotwallet.platform.biometrics.domain.usecase.implementation.EnableBiometricsImpl
import ch.admin.foitt.pilotwallet.platform.biometrics.domain.usecase.implementation.GetBiometricsCipherImpl
import ch.admin.foitt.pilotwallet.platform.biometrics.domain.usecase.implementation.ResetBiometricsImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
class AuthenticationModule

@Module
@InstallIn(ActivityRetainedComponent::class)
interface AuthenticationBindingsModule {
    @Binds
    fun bindEnableBiometricsUseCase(
        useCase: EnableBiometricsImpl
    ): EnableBiometrics

    @Binds
    fun bindResetBiometricsUseCase(
        useCase: ResetBiometricsImpl
    ): ResetBiometrics

    @Binds
    fun bindGetBiometricsCipher(
        useCase: GetBiometricsCipherImpl
    ): GetBiometricsCipher
}
