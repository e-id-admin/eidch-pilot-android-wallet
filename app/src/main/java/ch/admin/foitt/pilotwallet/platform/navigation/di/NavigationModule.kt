package ch.admin.foitt.pilotwallet.platform.navigation.di

import ch.admin.foitt.pilotwallet.platform.navigation.NavigationManager
import ch.admin.foitt.pilotwallet.platform.navigation.implementation.NavigationManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped

@Module
@InstallIn(ActivityRetainedComponent::class)
interface NavigationModule {
    @Binds
    @ActivityRetainedScoped
    fun provideNavigationManager(
        manager: NavigationManagerImpl,
    ): NavigationManager
}
