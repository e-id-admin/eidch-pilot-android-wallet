package ch.admin.foitt.pilotwallet.platform.credential.di

import ch.admin.foitt.pilotwallet.platform.credential.domain.usecase.CheckCredentialIntegrity
import ch.admin.foitt.pilotwallet.platform.credential.domain.usecase.FetchCredential
import ch.admin.foitt.pilotwallet.platform.credential.domain.usecase.GetCredentialPreviewFlow
import ch.admin.foitt.pilotwallet.platform.credential.domain.usecase.GetCredentialPreviewsFlow
import ch.admin.foitt.pilotwallet.platform.credential.domain.usecase.SaveCredential
import ch.admin.foitt.pilotwallet.platform.credential.domain.usecase.VerifyJwt
import ch.admin.foitt.pilotwallet.platform.credential.domain.usecase.VerifyJwtTimestamps
import ch.admin.foitt.pilotwallet.platform.credential.domain.usecase.VerifySdJwt
import ch.admin.foitt.pilotwallet.platform.credential.domain.usecase.implementation.CheckCredentialIntegrityImpl
import ch.admin.foitt.pilotwallet.platform.credential.domain.usecase.implementation.FetchCredentialImpl
import ch.admin.foitt.pilotwallet.platform.credential.domain.usecase.implementation.GetCredentialPreviewFlowImpl
import ch.admin.foitt.pilotwallet.platform.credential.domain.usecase.implementation.GetCredentialPreviewsFlowImpl
import ch.admin.foitt.pilotwallet.platform.credential.domain.usecase.implementation.SaveCredentialImpl
import ch.admin.foitt.pilotwallet.platform.credential.domain.usecase.implementation.VerifyJwtImpl
import ch.admin.foitt.pilotwallet.platform.credential.domain.usecase.implementation.VerifyJwtTimestampsImpl
import ch.admin.foitt.pilotwallet.platform.credential.domain.usecase.implementation.VerifySdJwtImpl
import ch.admin.foitt.pilotwallet.platform.credential.presentation.adapter.GetCredentialCardState
import ch.admin.foitt.pilotwallet.platform.credential.presentation.adapter.GetCredentialStateStack
import ch.admin.foitt.pilotwallet.platform.credential.presentation.adapter.implementation.GetCredentialCardStateImpl
import ch.admin.foitt.pilotwallet.platform.credential.presentation.adapter.implementation.GetCredentialStateStackImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
internal interface CredentialModule {

    @Binds
    fun bindCheckCredentialIntegrity(
        useCase: CheckCredentialIntegrityImpl
    ): CheckCredentialIntegrity

    @Binds
    fun bindFetchCredential(
        useCase: FetchCredentialImpl
    ): FetchCredential

    @Binds
    fun bindGetCredentialPreviewsFlow(
        useCase: GetCredentialPreviewsFlowImpl
    ): GetCredentialPreviewsFlow

    @Binds
    fun bindGetCredentialPreviewFlow(
        useCase: GetCredentialPreviewFlowImpl
    ): GetCredentialPreviewFlow

    @Binds
    fun bindSaveCredential(
        useCase: SaveCredentialImpl
    ): SaveCredential

    @Binds
    fun bindVerifyJwt(
        useCase: VerifyJwtImpl
    ): VerifyJwt

    @Binds
    fun bindVerifyJwtTimestamps(
        useCase: VerifyJwtTimestampsImpl
    ): VerifyJwtTimestamps

    @Binds
    fun bindVerifySdJwt(
        useCase: VerifySdJwtImpl
    ): VerifySdJwt

    @Binds
    fun bindGetCredentialState(
        adapter: GetCredentialCardStateImpl
    ): GetCredentialCardState

    @Binds
    fun bindGetCredentialStateStack(
        adapter: GetCredentialStateStackImpl,
    ): GetCredentialStateStack
}
