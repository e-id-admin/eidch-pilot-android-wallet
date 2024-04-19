package ch.admin.foitt.pilotwallet.platform.deeplink.di

import ch.admin.foitt.pilotwallet.platform.deeplink.data.DeepLinkIntentRepositoryImpl
import ch.admin.foitt.pilotwallet.platform.deeplink.domain.repository.DeepLinkIntentRepository
import ch.admin.foitt.pilotwallet.platform.deeplink.domain.usecase.AfterLoginNavigation
import ch.admin.foitt.pilotwallet.platform.deeplink.domain.usecase.SetDeepLinkIntent
import ch.admin.foitt.pilotwallet.platform.deeplink.domain.usecase.implementation.AfterLoginNavigationImpl
import ch.admin.foitt.pilotwallet.platform.deeplink.domain.usecase.implementation.SetDeepLinkIntentImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped

@Module
@InstallIn(ActivityRetainedComponent::class)
class DeepLinkModule

@Module
@InstallIn(ActivityRetainedComponent::class)
internal interface DeepLinkBindingsModule {
    @Binds
    @ActivityRetainedScoped
    fun provideIntentRepository(
        repo: DeepLinkIntentRepositoryImpl
    ): DeepLinkIntentRepository

    @Binds
    fun bindAfterLoginNavigation(
        useCase: AfterLoginNavigationImpl
    ): AfterLoginNavigation

    @Binds
    fun bindSetDeepLinkIntent(
        useCase: SetDeepLinkIntentImpl
    ): SetDeepLinkIntent
}
