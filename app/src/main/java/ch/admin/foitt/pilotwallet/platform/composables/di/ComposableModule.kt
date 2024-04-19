package ch.admin.foitt.pilotwallet.platform.composables.di

import android.content.Context
import ch.admin.foitt.pilotwallet.platform.composables.presentation.adapter.GetColor
import ch.admin.foitt.pilotwallet.platform.composables.presentation.adapter.GetLocalizedDateTime
import ch.admin.foitt.pilotwallet.platform.composables.presentation.adapter.GetPainter
import ch.admin.foitt.pilotwallet.platform.composables.presentation.adapter.GetPainterFromData
import ch.admin.foitt.pilotwallet.platform.composables.presentation.adapter.GetPainterFromUri
import ch.admin.foitt.pilotwallet.platform.composables.presentation.adapter.implementation.GetColorImpl
import ch.admin.foitt.pilotwallet.platform.composables.presentation.adapter.implementation.GetLocalizedDateTimeImpl
import ch.admin.foitt.pilotwallet.platform.composables.presentation.adapter.implementation.GetPainterFromDataImpl
import ch.admin.foitt.pilotwallet.platform.composables.presentation.adapter.implementation.GetPainterFromUriImpl
import ch.admin.foitt.pilotwallet.platform.composables.presentation.adapter.implementation.GetPainterImpl
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
    fun bindGetPainter(
        adapter: GetPainterImpl,
    ): GetPainter

    @Binds
    fun bindGetPainterFromData(
        adapter: GetPainterFromDataImpl
    ): GetPainterFromData

    @Binds
    fun bindGetPainterFromUri(
        adapter: GetPainterFromUriImpl
    ): GetPainterFromUri

    @Binds
    fun bindGetLocalizedDateTime(
        adapter: GetLocalizedDateTimeImpl
    ): GetLocalizedDateTime
}
