package ch.admin.foitt.pilotwallet.platform.passphrasePeppering.di

import ch.admin.foitt.pilotwallet.platform.passphrasePeppering.data.repository.PepperIvRepositoryImpl
import ch.admin.foitt.pilotwallet.platform.passphrasePeppering.domain.model.PassphrasePepperKeyConfig
import ch.admin.foitt.pilotwallet.platform.passphrasePeppering.domain.repository.PepperIvRepository
import ch.admin.foitt.pilotwallet.platform.passphrasePeppering.domain.usecase.PepperPassphrase
import ch.admin.foitt.pilotwallet.platform.passphrasePeppering.domain.usecase.implementation.PepperPassphraseImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped

@Module
@InstallIn(ActivityRetainedComponent::class)
internal class PassphrasePepperingModule {
    @Provides
    @ActivityRetainedScoped
    fun providePassphrasePepperConfig(): PassphrasePepperKeyConfig = PassphrasePepperKeyConfig()
}

@Module
@InstallIn(ActivityRetainedComponent::class)
internal interface PassphrasePepperingBindings {

    @Binds
    @ActivityRetainedScoped
    fun bindPepperIvRepository(
        repo: PepperIvRepositoryImpl
    ): PepperIvRepository

    @Binds
    fun bindPepperPassphrase(
        useCase: PepperPassphraseImpl
    ): PepperPassphrase
}
