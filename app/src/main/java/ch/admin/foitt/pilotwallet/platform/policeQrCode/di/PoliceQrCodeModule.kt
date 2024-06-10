package ch.admin.foitt.pilotwallet.platform.policeQrCode.di

import ch.admin.foitt.pilotwallet.platform.policeQrCode.usecase.GetPoliceQrCode
import ch.admin.foitt.pilotwallet.platform.policeQrCode.usecase.implementation.GetPoliceQrCodeImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
interface PoliceQrCodeModule {
    @Binds
    fun bindGetPoliceQrCode(
        useCase: GetPoliceQrCodeImpl
    ): GetPoliceQrCode
}
