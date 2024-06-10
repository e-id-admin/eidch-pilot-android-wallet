package ch.admin.foitt.pilotwallet.platform.composables.di

import android.content.Context
import ch.admin.foitt.pilotwallet.platform.composables.presentation.adapter.GetColor
import ch.admin.foitt.pilotwallet.platform.composables.presentation.adapter.GetDrawable
import ch.admin.foitt.pilotwallet.platform.composables.presentation.adapter.GetDrawableFromData
import ch.admin.foitt.pilotwallet.platform.composables.presentation.adapter.GetDrawableFromUri
import ch.admin.foitt.pilotwallet.platform.composables.presentation.adapter.GetLocalizedDateTime
import ch.admin.foitt.pilotwallet.platform.composables.presentation.adapter.implementation.GetColorImpl
import ch.admin.foitt.pilotwallet.platform.composables.presentation.adapter.implementation.GetDrawableFromDataImpl
import ch.admin.foitt.pilotwallet.platform.composables.presentation.adapter.implementation.GetDrawableFromUriImpl
import ch.admin.foitt.pilotwallet.platform.composables.presentation.adapter.implementation.GetDrawableImpl
import ch.admin.foitt.pilotwallet.platform.composables.presentation.adapter.implementation.GetLocalizedDateTimeImpl
import coil.ImageLoader
import coil.request.CachePolicy
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped

@Module
@InstallIn(ActivityRetainedComponent::class)
class ComposableModule {
    @Provides
    @ActivityRetainedScoped
    fun provideCoilImageLoader(@ApplicationContext appContext: Context): ImageLoader {
        return ImageLoader.Builder(appContext)
            .diskCachePolicy(CachePolicy.DISABLED)
            .build()
    }
}

@Module
@InstallIn(ActivityRetainedComponent::class)
internal interface ComposableBindings {
    @Binds
    fun bindGetColor(
        adapter: GetColorImpl
    ): GetColor

    @Binds
    fun bindGetDrawable(
        adapter: GetDrawableImpl
    ): GetDrawable

    @Binds
    fun bindGetDrawableFromData(
        adapter: GetDrawableFromDataImpl
    ): GetDrawableFromData

    @Binds
    fun bindGetDrawableFromUri(
        adapter: GetDrawableFromUriImpl
    ): GetDrawableFromUri

    @Binds
    fun bindGetLocalizedDateTime(
        adapter: GetLocalizedDateTimeImpl
    ): GetLocalizedDateTime
}
