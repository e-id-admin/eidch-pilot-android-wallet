package ch.admin.foitt.pilotwallet.platform.passphraseHashing.di

import ch.admin.foitt.pilotwallet.platform.passphraseHashing.data.repository.SaltRepositoryImpl
import ch.admin.foitt.pilotwallet.platform.passphraseHashing.domain.repository.SaltRepository
import ch.admin.foitt.pilotwallet.platform.passphraseHashing.domain.usecase.HashPassphrase
import ch.admin.foitt.pilotwallet.platform.passphraseHashing.domain.usecase.implementation.HashPassphraseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface PassphraseHashingModule {
    @Binds
    fun bindPinRepository(
        repo: SaltRepositoryImpl
    ): SaltRepository

    @Binds
    fun bindHashPassphrase(
        useCase: HashPassphraseImpl,
    ): HashPassphrase
}
