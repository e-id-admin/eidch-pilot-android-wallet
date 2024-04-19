package ch.admin.foitt.pilotwallet.feature.home.di

import ch.admin.foitt.pilotwallet.feature.home.data.repository.HomeIntroductionRepositoryImpl
import ch.admin.foitt.pilotwallet.feature.home.domain.repository.GetHomeIntroductionIsDone
import ch.admin.foitt.pilotwallet.feature.home.domain.repository.HomeIntroductionRepository
import ch.admin.foitt.pilotwallet.feature.home.domain.repository.SaveHomeIntroductionIsDone
import ch.admin.foitt.pilotwallet.feature.home.domain.repository.implementation.GetHomeIntroductionIsDoneImpl
import ch.admin.foitt.pilotwallet.feature.home.domain.repository.implementation.SaveHomeIntroductionIsDoneImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
interface HomeModule {
    @Binds
    fun bindHomeIntroductionRepository(
        repo: HomeIntroductionRepositoryImpl
    ): HomeIntroductionRepository

    @Binds
    fun bindGetHomeIntroductionIsDone(
        useCase: GetHomeIntroductionIsDoneImpl
    ): GetHomeIntroductionIsDone

    @Binds
    fun bindSaveHomeIntroductionIsDone(
        useCase: SaveHomeIntroductionIsDoneImpl
    ): SaveHomeIntroductionIsDone
}
