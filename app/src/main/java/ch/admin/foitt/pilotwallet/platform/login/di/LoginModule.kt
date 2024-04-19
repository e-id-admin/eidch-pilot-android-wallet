package ch.admin.foitt.pilotwallet.platform.login.di

import ch.admin.foitt.pilotwallet.platform.login.domain.usecase.AfterLoginWork
import ch.admin.foitt.pilotwallet.platform.login.domain.usecase.BiometricLoginEnabled
import ch.admin.foitt.pilotwallet.platform.login.domain.usecase.CanUseBiometricsForLogin
import ch.admin.foitt.pilotwallet.platform.login.domain.usecase.LoginWithBiometrics
import ch.admin.foitt.pilotwallet.platform.login.domain.usecase.LoginWithPin
import ch.admin.foitt.pilotwallet.platform.login.domain.usecase.NavigateToLogin
import ch.admin.foitt.pilotwallet.platform.login.domain.usecase.implementation.AfterLoginWorkImpl
import ch.admin.foitt.pilotwallet.platform.login.domain.usecase.implementation.BiometricLoginEnabledImpl
import ch.admin.foitt.pilotwallet.platform.login.domain.usecase.implementation.CanUseBiometricsForLoginImpl
import ch.admin.foitt.pilotwallet.platform.login.domain.usecase.implementation.LoginWithBiometricsImpl
import ch.admin.foitt.pilotwallet.platform.login.domain.usecase.implementation.LoginWithPinImpl
import ch.admin.foitt.pilotwallet.platform.login.domain.usecase.implementation.NavigateToLoginImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
class LoginModule

@Module
@InstallIn(ActivityRetainedComponent::class)
internal interface LoginBindingsModule {
    @Binds
    fun bindLoginWithPin(
        useCase: LoginWithPinImpl,
    ): LoginWithPin

    @Binds
    fun bindBiometricLoginEnabled(
        useCase: BiometricLoginEnabledImpl,
    ): BiometricLoginEnabled

    @Binds
    fun bindLoginWithBiometric(
        useCase: LoginWithBiometricsImpl
    ): LoginWithBiometrics

    @Binds
    fun bindCanUseBiometricsForLogin(
        useCase: CanUseBiometricsForLoginImpl
    ): CanUseBiometricsForLogin

    @Binds
    fun bindAfterLoginWork(
        useCase: AfterLoginWorkImpl
    ): AfterLoginWork

    @Binds
    fun bindNavigateToLogin(
        useCase: NavigateToLoginImpl
    ): NavigateToLogin
}
