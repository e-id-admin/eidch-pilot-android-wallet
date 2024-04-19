package ch.admin.foitt.pilotwallet.feature.login.di

import ch.admin.foitt.pilotwallet.feature.login.data.repository.DeviceLockoutStartRepository
import ch.admin.foitt.pilotwallet.feature.login.data.repository.LoginAttemptsRepository
import ch.admin.foitt.pilotwallet.feature.login.domain.repository.AttemptsRepository
import ch.admin.foitt.pilotwallet.feature.login.domain.repository.LockoutStartRepository
import ch.admin.foitt.pilotwallet.feature.login.domain.usecase.GetLockoutDuration
import ch.admin.foitt.pilotwallet.feature.login.domain.usecase.IncreaseFailedLoginAttemptsCounter
import ch.admin.foitt.pilotwallet.feature.login.domain.usecase.IsDevicePinSet
import ch.admin.foitt.pilotwallet.feature.login.domain.usecase.LockTrigger
import ch.admin.foitt.pilotwallet.feature.login.domain.usecase.ResetLockout
import ch.admin.foitt.pilotwallet.feature.login.domain.usecase.implementation.GetLockoutDurationImpl
import ch.admin.foitt.pilotwallet.feature.login.domain.usecase.implementation.IncreaseFailedLoginAttemptsCounterImpl
import ch.admin.foitt.pilotwallet.feature.login.domain.usecase.implementation.IsDevicePinSetImpl
import ch.admin.foitt.pilotwallet.feature.login.domain.usecase.implementation.LockTriggerImpl
import ch.admin.foitt.pilotwallet.feature.login.domain.usecase.implementation.ResetLockoutImpl
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
    fun bindUptimeRepository(
        repo: DeviceLockoutStartRepository
    ): LockoutStartRepository

    @Binds
    fun bindAttemptsRepository(
        repo: LoginAttemptsRepository
    ): AttemptsRepository

    @Binds
    fun bindResetLoginLock(
        useCase: ResetLockoutImpl
    ): ResetLockout

    @Binds
    fun bindGetRemainingLockDuration(
        useCase: GetLockoutDurationImpl
    ): GetLockoutDuration

    @Binds
    fun bindHandleFailedLoginAttempt(
        useCase: IncreaseFailedLoginAttemptsCounterImpl
    ): IncreaseFailedLoginAttemptsCounter

    @Binds
    fun bindLockTriggerUseCase(
        useCase: LockTriggerImpl
    ): LockTrigger

    @Binds
    fun bindIsDevicePinSet(
        useCase: IsDevicePinSetImpl
    ): IsDevicePinSet
}
