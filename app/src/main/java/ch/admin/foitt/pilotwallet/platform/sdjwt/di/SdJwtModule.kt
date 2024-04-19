package ch.admin.foitt.pilotwallet.platform.sdjwt.di

import ch.admin.foitt.pilotwallet.platform.sdjwt.domain.usecase.CreateDisclosedSdJwt
import ch.admin.foitt.pilotwallet.platform.sdjwt.domain.usecase.ParseRawSdJwt
import ch.admin.foitt.pilotwallet.platform.sdjwt.domain.usecase.ParseSdJwt
import ch.admin.foitt.pilotwallet.platform.sdjwt.domain.usecase.implementation.CreateDisclosedSdJwtImpl
import ch.admin.foitt.pilotwallet.platform.sdjwt.domain.usecase.implementation.ParseRawSdJwtImpl
import ch.admin.foitt.pilotwallet.platform.sdjwt.domain.usecase.implementation.ParseSdJwtImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
internal interface SdJwtModule {
    @Binds
    fun bindParseSdJwt(
        useCase: ParseSdJwtImpl
    ): ParseSdJwt

    @Binds
    fun bindParseRawSdJwt(
        useCase: ParseRawSdJwtImpl
    ): ParseRawSdJwt

    @Binds
    fun bindCreateDisclosedSdJwt(
        useCase: CreateDisclosedSdJwtImpl
    ): CreateDisclosedSdJwt
}
