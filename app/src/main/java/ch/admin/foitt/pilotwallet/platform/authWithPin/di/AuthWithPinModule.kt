package ch.admin.foitt.pilotwallet.platform.authWithPin.di

import ch.admin.foitt.pilotwallet.platform.authWithPin.domain.usecase.AuthWithPin
import ch.admin.foitt.pilotwallet.platform.authWithPin.domain.usecase.implementation.AuthWithPinImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
class AuthWithPinModule

@Module
@InstallIn(ActivityRetainedComponent::class)
internal interface AuthWithPinBindingsModule {
    @Binds
    fun bindAuthWithPin(
        useCase: AuthWithPinImpl,
    ): AuthWithPin
}
