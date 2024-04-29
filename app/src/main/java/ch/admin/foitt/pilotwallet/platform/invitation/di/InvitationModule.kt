package ch.admin.foitt.pilotwallet.platform.invitation.di

import ch.admin.foitt.pilotwallet.platform.invitation.domain.usecase.GetCredentialOfferFromUri
import ch.admin.foitt.pilotwallet.platform.invitation.domain.usecase.GetPresentationRequestFromUri
import ch.admin.foitt.pilotwallet.platform.invitation.domain.usecase.HandleInvitationProcessingError
import ch.admin.foitt.pilotwallet.platform.invitation.domain.usecase.HandleInvitationProcessingSuccess
import ch.admin.foitt.pilotwallet.platform.invitation.domain.usecase.ProcessInvitation
import ch.admin.foitt.pilotwallet.platform.invitation.domain.usecase.ValidateInvitation
import ch.admin.foitt.pilotwallet.platform.invitation.domain.usecase.impl.GetCredentialOfferFromUriImpl
import ch.admin.foitt.pilotwallet.platform.invitation.domain.usecase.impl.GetPresentationRequestFromUriImpl
import ch.admin.foitt.pilotwallet.platform.invitation.domain.usecase.impl.HandleInvitationProcessingErrorImpl
import ch.admin.foitt.pilotwallet.platform.invitation.domain.usecase.impl.HandleInvitationProcessingSuccessImpl
import ch.admin.foitt.pilotwallet.platform.invitation.domain.usecase.impl.ProcessInvitationImpl
import ch.admin.foitt.pilotwallet.platform.invitation.domain.usecase.impl.ValidateInvitationImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
internal interface InvitationModule {
    @Binds
    fun bindProcessInvitation(
        useCase: ProcessInvitationImpl
    ): ProcessInvitation

    @Binds
    fun bindValidateInvitation(
        useCase: ValidateInvitationImpl
    ): ValidateInvitation

    @Binds
    fun bindGetPresentationRequestFromUri(
        useCase: GetPresentationRequestFromUriImpl
    ): GetPresentationRequestFromUri

    @Binds
    fun bindGetCredentialOfferFromUri(
        useCase: GetCredentialOfferFromUriImpl
    ): GetCredentialOfferFromUri

    @Binds
    fun bindHandleInvitationProcessing(
        useCase: HandleInvitationProcessingSuccessImpl
    ): HandleInvitationProcessingSuccess

    @Binds
    fun bindHandleInvitationProcessingError(
        useCase: HandleInvitationProcessingErrorImpl
    ): HandleInvitationProcessingError
}
