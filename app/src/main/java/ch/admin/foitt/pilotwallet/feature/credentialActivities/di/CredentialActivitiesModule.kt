package ch.admin.foitt.pilotwallet.feature.credentialActivities.di

import ch.admin.foitt.pilotwallet.feature.credentialActivities.domain.usecase.GetActivitiesForCredentialFlow
import ch.admin.foitt.pilotwallet.feature.credentialActivities.domain.usecase.GetCredentialActivityDetailFlow
import ch.admin.foitt.pilotwallet.feature.credentialActivities.domain.usecase.implementation.GetActivitiesForCredentialFlowImpl
import ch.admin.foitt.pilotwallet.feature.credentialActivities.domain.usecase.implementation.GetCredentialActivityDetailFlowImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
interface CredentialActivitiesModule {
    @Binds
    fun bindGetCredentialActivityDetailFlow(
        useCase: GetCredentialActivityDetailFlowImpl
    ): GetCredentialActivityDetailFlow

    @Binds
    fun bindGetActivitiesForCredentialFlow(
        useCase: GetActivitiesForCredentialFlowImpl
    ): GetActivitiesForCredentialFlow
}
