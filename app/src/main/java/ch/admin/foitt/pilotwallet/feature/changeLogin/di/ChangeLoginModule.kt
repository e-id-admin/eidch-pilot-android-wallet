package ch.admin.foitt.pilotwallet.feature.changeLogin.di

import ch.admin.foitt.pilotwallet.feature.changeLogin.domain.usecase.ChangePassphrase
import ch.admin.foitt.pilotwallet.feature.changeLogin.domain.usecase.implementation.ChangePassphraseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
interface ChangeLoginModule {
    @Binds
    fun bindChangePassphrase(useCase: ChangePassphraseImpl): ChangePassphrase
}
