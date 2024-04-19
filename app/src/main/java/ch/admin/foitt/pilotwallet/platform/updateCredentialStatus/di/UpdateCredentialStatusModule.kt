package ch.admin.foitt.pilotwallet.platform.updateCredentialStatus.di

import ch.admin.foitt.pilotwallet.platform.updateCredentialStatus.domain.usecase.GetAndRefreshCredentialValidity
import ch.admin.foitt.pilotwallet.platform.updateCredentialStatus.domain.usecase.UpdateAllCredentialStatuses
import ch.admin.foitt.pilotwallet.platform.updateCredentialStatus.domain.usecase.UpdateCredentialStatus
import ch.admin.foitt.pilotwallet.platform.updateCredentialStatus.domain.usecase.implementation.GetAndRefreshCredentialValidityImpl
import ch.admin.foitt.pilotwallet.platform.updateCredentialStatus.domain.usecase.implementation.UpdateAllCredentialStatusesImpl
import ch.admin.foitt.pilotwallet.platform.updateCredentialStatus.domain.usecase.implementation.UpdateCredentialStatusImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import kotlinx.serialization.json.Json
import javax.inject.Named

@Module
@InstallIn(ActivityRetainedComponent::class)
class UpdateCredentialStatusModule {
    @Provides
    @Named("jsonSerializerIgnoreKeys")
    fun provideJsonSerializer(): Json {
        return Json {
            ignoreUnknownKeys = true
        }
    }
}

@Module
@InstallIn(ActivityRetainedComponent::class)
interface UpdateCredentialStatusBindingModule {
    @Binds
    fun bindUpdateAllCredentialStatuses(
        useCase: UpdateAllCredentialStatusesImpl
    ): UpdateAllCredentialStatuses

    @Binds
    fun bindUpdateCredentialStatus(
        useCase: UpdateCredentialStatusImpl
    ): UpdateCredentialStatus

    @Binds
    fun bindGetAndRefreshCredentialValidity(
        useCase: GetAndRefreshCredentialValidityImpl
    ): GetAndRefreshCredentialValidity
}
