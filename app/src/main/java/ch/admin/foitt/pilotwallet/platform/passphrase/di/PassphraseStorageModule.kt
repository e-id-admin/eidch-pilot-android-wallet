package ch.admin.foitt.pilotwallet.platform.passphrase.di

import ch.admin.foitt.pilotwallet.platform.passphrase.data.repository.PassphraseRepositoryImpl
import ch.admin.foitt.pilotwallet.platform.passphrase.domain.model.PassphraseStorageKeyConfig
import ch.admin.foitt.pilotwallet.platform.passphrase.domain.repository.PassphraseRepository
import ch.admin.foitt.pilotwallet.platform.passphrase.domain.usecase.DeleteSecretKey
import ch.admin.foitt.pilotwallet.platform.passphrase.domain.usecase.EncryptAndSavePassphrase
import ch.admin.foitt.pilotwallet.platform.passphrase.domain.usecase.GetPassphraseWasDeleted
import ch.admin.foitt.pilotwallet.platform.passphrase.domain.usecase.InitializePassphrase
import ch.admin.foitt.pilotwallet.platform.passphrase.domain.usecase.LoadAndDecryptPassphrase
import ch.admin.foitt.pilotwallet.platform.passphrase.domain.usecase.SavePassphraseWasDeleted
import ch.admin.foitt.pilotwallet.platform.passphrase.domain.usecase.implementation.DeleteSecretKeyImpl
import ch.admin.foitt.pilotwallet.platform.passphrase.domain.usecase.implementation.EncryptAndSavePassphraseImpl
import ch.admin.foitt.pilotwallet.platform.passphrase.domain.usecase.implementation.GetPassphraseWasDeletedImpl
import ch.admin.foitt.pilotwallet.platform.passphrase.domain.usecase.implementation.InitializePassphraseImpl
import ch.admin.foitt.pilotwallet.platform.passphrase.domain.usecase.implementation.LoadAndDecryptPassphraseImpl
import ch.admin.foitt.pilotwallet.platform.passphrase.domain.usecase.implementation.SavePassphraseWasDeletedImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped

@Module
@InstallIn(ActivityRetainedComponent::class)
internal class PassphraseStorageModule {
    @Provides
    @ActivityRetainedScoped
    fun providePassphraseStorageConfig(): PassphraseStorageKeyConfig = PassphraseStorageKeyConfig()
}

@Module
@InstallIn(ActivityRetainedComponent::class)
internal interface PassphraseStorageBindings {

    @Binds
    @ActivityRetainedScoped
    fun bindPassphraseRepository(
        repo: PassphraseRepositoryImpl
    ): PassphraseRepository

    @Binds
    fun bindEncryptAndSavePassphrase(
        useCase: EncryptAndSavePassphraseImpl
    ): EncryptAndSavePassphrase

    @Binds
    fun bindLoadAndDecryptPassphrase(
        useCase: LoadAndDecryptPassphraseImpl
    ): LoadAndDecryptPassphrase

    @Binds
    fun bindInitializePassphrase(useCase: InitializePassphraseImpl): InitializePassphrase

    @Binds
    fun bindDeleteSecretKey(
        useCase: DeleteSecretKeyImpl
    ): DeleteSecretKey

    @Binds
    fun bindSavePassphraseDidChange(useCase: SavePassphraseWasDeletedImpl): SavePassphraseWasDeleted

    @Binds
    fun bindGetPassphraseDidChange(useCase: GetPassphraseWasDeletedImpl): GetPassphraseWasDeleted
}
