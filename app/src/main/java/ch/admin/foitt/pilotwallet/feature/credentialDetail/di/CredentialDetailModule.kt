package ch.admin.foitt.pilotwallet.feature.credentialDetail.di

import ch.admin.foitt.pilotwallet.feature.credentialDetail.domain.usecase.GetPoliceQrCode
import ch.admin.foitt.pilotwallet.feature.credentialDetail.domain.usecase.implementation.GetPoliceQrCodeImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
interface CredentialDetailModule {
    @Binds
    fun bindGetPoliceQrCode(
        useCase: GetPoliceQrCodeImpl
    ): GetPoliceQrCode
}
