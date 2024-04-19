package ch.admin.foitt.pilotwallet.platform.scaffold.di

import ch.admin.foitt.pilotwallet.platform.scaffold.data.repository.ErrorDialogRepositoryImpl
import ch.admin.foitt.pilotwallet.platform.scaffold.data.repository.TopBarStateRepositoryImpl
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.repository.ErrorDialogRepository
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.repository.TopBarStateRepository
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.usecase.SetErrorDialogState
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.usecase.SetTopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.usecase.implementation.SetErrorDialogStateImpl
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.usecase.implementation.SetTopBarStateImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped

@Module
@InstallIn(ActivityRetainedComponent::class)
internal interface ScaffoldModule {
    @Binds
    @ActivityRetainedScoped
    fun bindTopBarRepository(
        repository: TopBarStateRepositoryImpl
    ): TopBarStateRepository

    @Binds
    fun bindSetTopBar(
        useCase: SetTopBarStateImpl
    ): SetTopBarState

    @Binds
    @ActivityRetainedScoped
    fun bindErrorDialogRepository(
        repository: ErrorDialogRepositoryImpl
    ): ErrorDialogRepository

    @Binds
    fun bindSetErrorDialogState(
        useCase: SetErrorDialogStateImpl
    ): SetErrorDialogState
}
