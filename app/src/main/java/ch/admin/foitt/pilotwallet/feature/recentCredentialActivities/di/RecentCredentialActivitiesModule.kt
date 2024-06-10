package ch.admin.foitt.pilotwallet.feature.recentCredentialActivities.di

import ch.admin.foitt.pilotwallet.feature.recentCredentialActivities.domain.GetLast3ActivitiesForCredentialFlow
import ch.admin.foitt.pilotwallet.feature.recentCredentialActivities.domain.implementation.GetLast3ActivitiesForCredentialFlowImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
interface RecentCredentialActivitiesModule {
    @Binds
    fun bindGetLast3ActivitiesForCredentialFlow(
        useCase: GetLast3ActivitiesForCredentialFlowImpl
    ): GetLast3ActivitiesForCredentialFlow
}
