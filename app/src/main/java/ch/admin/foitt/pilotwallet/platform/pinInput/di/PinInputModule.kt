package ch.admin.foitt.pilotwallet.platform.pinInput.di

import ch.admin.foitt.pilotwallet.platform.pinInput.domain.model.PinConstraints
import ch.admin.foitt.pilotwallet.platform.pinInput.domain.usecase.ValidatePin
import ch.admin.foitt.pilotwallet.platform.pinInput.domain.usecase.implementation.ValidatePinImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class PinInputModule {

    @Provides
    fun providePinConstrains(): PinConstraints = PinConstraints()

    @Provides
    fun provideValidatePin(pinConstraints: PinConstraints): ValidatePin =
        ValidatePinImpl(constraints = pinConstraints)
}
