package ch.admin.foitt.pilotwallet.platform.ssi.di

import ch.admin.foitt.pilotwallet.platform.ssi.data.repository.CredentialClaimDisplayRepoImpl
import ch.admin.foitt.pilotwallet.platform.ssi.data.repository.CredentialClaimRepoImpl
import ch.admin.foitt.pilotwallet.platform.ssi.data.repository.CredentialDisplayRepoImpl
import ch.admin.foitt.pilotwallet.platform.ssi.data.repository.CredentialIssuerDisplayRepoImpl
import ch.admin.foitt.pilotwallet.platform.ssi.data.repository.CredentialRawRepoImpl
import ch.admin.foitt.pilotwallet.platform.ssi.data.repository.CredentialRepoImpl
import ch.admin.foitt.pilotwallet.platform.ssi.domain.repository.CredentialClaimDisplayRepo
import ch.admin.foitt.pilotwallet.platform.ssi.domain.repository.CredentialClaimRepo
import ch.admin.foitt.pilotwallet.platform.ssi.domain.repository.CredentialDisplayRepo
import ch.admin.foitt.pilotwallet.platform.ssi.domain.repository.CredentialIssuerDisplayRepo
import ch.admin.foitt.pilotwallet.platform.ssi.domain.repository.CredentialRawRepo
import ch.admin.foitt.pilotwallet.platform.ssi.domain.repository.CredentialRepo
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.DeleteCredential
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.GetAllCredentialBodies
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.GetAllCredentials
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.GetAllLocalizedCredentialDisplaysFlow
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.GetCompatibleCredentials
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.GetCredentialBodiesByCredentialId
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.GetCredentialById
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.GetCredentialClaimData
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.GetCredentialClaimDisplays
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.GetCredentialClaims
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.GetCredentialClaimsData
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.GetCredentialRaw
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.GetLocalizedCredentialDisplayFlow
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.GetLocalizedCredentialIssuerDisplay
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.MapToCredentialClaimData
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.implementation.DeleteCredentialImpl
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.implementation.GetAllCredentialBodiesImpl
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.implementation.GetAllCredentialsImpl
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.implementation.GetAllLocalizedCredentialDisplaysFlowImpl
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.implementation.GetCompatibleCredentialsImpl
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.implementation.GetCredentialBodiesByCredentialIdImpl
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.implementation.GetCredentialByIdImpl
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.implementation.GetCredentialClaimDataImpl
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.implementation.GetCredentialClaimDisplaysImpl
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.implementation.GetCredentialClaimsDataImpl
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.implementation.GetCredentialClaimsImpl
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.implementation.GetCredentialRawImpl
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.implementation.GetLocalizedCredentialDisplayFlowImpl
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.implementation.GetLocalizedCredentialIssuerDisplayImpl
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.implementation.MapToCredentialClaimDataImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
interface SsiModule {
    @Binds
    fun bindGetCompatibleCredentials(
        useCase: GetCompatibleCredentialsImpl
    ): GetCompatibleCredentials

    @Binds
    fun bindGetAllCredentialBodies(
        useCase: GetAllCredentialBodiesImpl
    ): GetAllCredentialBodies

    @Binds
    fun bindCredentialClaimDisplayRepo(
        useCase: CredentialClaimDisplayRepoImpl
    ): CredentialClaimDisplayRepo

    @Binds
    fun bindCredentialClaimRepo(
        useCase: CredentialClaimRepoImpl
    ): CredentialClaimRepo

    @Binds
    fun bindCredentialRepo(
        useCase: CredentialRepoImpl
    ): CredentialRepo

    @Binds
    fun bindCredentialDisplayRepo(
        useCase: CredentialDisplayRepoImpl
    ): CredentialDisplayRepo

    @Binds
    fun bindCredentialIssuerDisplayRepo(
        useCase: CredentialIssuerDisplayRepoImpl
    ): CredentialIssuerDisplayRepo

    @Binds
    fun bindCredentialRawRepo(
        useCase: CredentialRawRepoImpl
    ): CredentialRawRepo

    @Binds
    fun bindGetAllCredentials(
        useCase: GetAllCredentialsImpl
    ): GetAllCredentials

    @Binds
    fun bindGetCredentialBodiesByCredentialId(
        useCase: GetCredentialBodiesByCredentialIdImpl
    ): GetCredentialBodiesByCredentialId

    @Binds
    fun bindGetAllLocalizedCredentialDisplaysFlow(
        useCase: GetAllLocalizedCredentialDisplaysFlowImpl
    ): GetAllLocalizedCredentialDisplaysFlow

    @Binds
    fun bindDeleteCredential(
        useCase: DeleteCredentialImpl
    ): DeleteCredential

    @Binds
    fun bindGetCredentialById(
        useCase: GetCredentialByIdImpl
    ): GetCredentialById

    @Binds
    fun bindGetCredentialClaims(
        useCase: GetCredentialClaimsImpl
    ): GetCredentialClaims

    @Binds
    fun bindGetCredentialClaimDisplays(
        useCase: GetCredentialClaimDisplaysImpl
    ): GetCredentialClaimDisplays

    @Binds
    fun bindGetLocalizedCredentialClaims(
        useCase: GetCredentialClaimsDataImpl
    ): GetCredentialClaimsData

    @Binds
    fun bindGetCredentialClaimData(
        useCase: GetCredentialClaimDataImpl
    ): GetCredentialClaimData

    @Binds
    fun bindMapToCredentialClaimData(
        useCase: MapToCredentialClaimDataImpl
    ): MapToCredentialClaimData

    @Binds
    fun bindGetCredentialRaw(
        useCase: GetCredentialRawImpl
    ): GetCredentialRaw

    @Binds
    fun bindGetLocalizedCredentialDisplayFlow(
        useCase: GetLocalizedCredentialDisplayFlowImpl
    ): GetLocalizedCredentialDisplayFlow

    @Binds
    fun bindGetLocalizedCredentialIssuerDisplay(
        useCase: GetLocalizedCredentialIssuerDisplayImpl
    ): GetLocalizedCredentialIssuerDisplay
}
