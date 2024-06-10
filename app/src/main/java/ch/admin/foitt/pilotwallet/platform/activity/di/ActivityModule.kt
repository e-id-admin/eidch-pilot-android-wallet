package ch.admin.foitt.pilotwallet.platform.activity.di

import ch.admin.foitt.pilotwallet.platform.activity.data.repository.ActivityRepositoryImpl
import ch.admin.foitt.pilotwallet.platform.activity.domain.repository.ActivityRepository
import ch.admin.foitt.pilotwallet.platform.activity.domain.usecase.DeleteActivity
import ch.admin.foitt.pilotwallet.platform.activity.domain.usecase.SaveActivity
import ch.admin.foitt.pilotwallet.platform.activity.domain.usecase.SaveActivityForPresentation
import ch.admin.foitt.pilotwallet.platform.activity.domain.usecase.SaveActivityVerifier
import ch.admin.foitt.pilotwallet.platform.activity.domain.usecase.SaveActivityVerifierCredentialClaimsSnapshot
import ch.admin.foitt.pilotwallet.platform.activity.domain.usecase.implementation.DeleteActivityImpl
import ch.admin.foitt.pilotwallet.platform.activity.domain.usecase.implementation.SaveActivityForPresentationImpl
import ch.admin.foitt.pilotwallet.platform.activity.domain.usecase.implementation.SaveActivityImpl
import ch.admin.foitt.pilotwallet.platform.activity.domain.usecase.implementation.SaveActivityVerifierCredentialClaimsSnapshotImpl
import ch.admin.foitt.pilotwallet.platform.activity.domain.usecase.implementation.SaveActivityVerifierImpl
import ch.admin.foitt.pilotwallet.platform.activity.presentation.adapter.GetActivityListItem
import ch.admin.foitt.pilotwallet.platform.activity.presentation.adapter.implementation.GetActivityListItemImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
interface ActivityModule {
    @Binds
    fun bindActivityRepository(
        repo: ActivityRepositoryImpl
    ): ActivityRepository

    @Binds
    fun bindSaveActivityUseCase(
        useCase: SaveActivityImpl
    ): SaveActivity

    @Binds
    fun bindSaveActivityVerifierUseCase(
        useCase: SaveActivityVerifierImpl
    ): SaveActivityVerifier

    @Binds
    fun bindSaveActivityVerifierCredentialClaimSnapshot(
        useCase: SaveActivityVerifierCredentialClaimsSnapshotImpl
    ): SaveActivityVerifierCredentialClaimsSnapshot

    @Binds
    fun bindSaveActivityForPresentation(
        useCase: SaveActivityForPresentationImpl
    ): SaveActivityForPresentation

    @Binds
    fun bindGetActivityListItem(
        useCase: GetActivityListItemImpl
    ): GetActivityListItem

    @Binds
    fun bindDeleteActivity(
        useCase: DeleteActivityImpl
    ): DeleteActivity
}
