package ch.admin.foitt.pilotwallet.platform.invitation.domain.usecase.impl

import ch.admin.foitt.openid4vc.domain.model.Invitation
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.CredentialOffer
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.PresentationRequest
import ch.admin.foitt.pilotwallet.platform.credential.domain.model.FetchCredentialError
import ch.admin.foitt.pilotwallet.platform.credential.domain.usecase.FetchCredential
import ch.admin.foitt.pilotwallet.platform.invitation.domain.model.InvitationError
import ch.admin.foitt.pilotwallet.platform.invitation.domain.model.ProcessInvitationError
import ch.admin.foitt.pilotwallet.platform.invitation.domain.model.ProcessInvitationResult
import ch.admin.foitt.pilotwallet.platform.invitation.domain.model.ValidateInvitationError
import ch.admin.foitt.pilotwallet.platform.invitation.domain.model.toProcessInvitationError
import ch.admin.foitt.pilotwallet.platform.invitation.domain.usecase.ProcessInvitation
import ch.admin.foitt.pilotwallet.platform.invitation.domain.usecase.ValidateInvitation
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.CompatibleCredential
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.SsiError
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.GetAllCredentials
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.GetCompatibleCredentials
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.mapEither
import com.github.michaelbull.result.mapError
import timber.log.Timber
import javax.inject.Inject

internal class ProcessInvitationImpl @Inject constructor(
    private val validateInvitation: ValidateInvitation,
    private val fetchCredential: FetchCredential,
    private val getCompatibleCredentials: GetCompatibleCredentials,
    private val getAllCredentials: GetAllCredentials,
) : ProcessInvitation {
    override suspend fun invoke(
        invitationUri: String,
    ): Result<ProcessInvitationResult, ProcessInvitationError> = coroutineBinding {
        val validationResult: Invitation = validateInvitation(invitationUri)
            .mapError(ValidateInvitationError::toProcessInvitationError)
            .bind()

        Timber.d("Found valid invitation with uri: $invitationUri")
        val processInvitationResult = processValidInvitation(
            invitation = validationResult,
        ).bind()

        processInvitationResult
    }

    private suspend fun processValidInvitation(invitation: Invitation): Result<ProcessInvitationResult, ProcessInvitationError> {
        return when (invitation) {
            is CredentialOffer -> processCredentialOffer(credentialOffer = invitation)
            is PresentationRequest -> processPresentationRequest(invitation)
            else -> Err(InvitationError.Unexpected)
        }
    }

    private suspend fun processCredentialOffer(credentialOffer: CredentialOffer): Result<ProcessInvitationResult, ProcessInvitationError> =
        fetchCredential(credentialOffer).mapEither(
            success = { credentialId ->
                ProcessInvitationResult.CredentialOffer(credentialId)
            },
            failure = FetchCredentialError::toProcessInvitationError,
        )
    private suspend fun processPresentationRequest(
        presentationRequest: PresentationRequest,
    ): Result<ProcessInvitationResult, ProcessInvitationError> = coroutineBinding {
        val credentials = getAllCredentials()
            .mapError { error ->
                when (error) {
                    is SsiError.Unexpected -> InvitationError.Unexpected
                }
            }.bind()
        if (credentials.isEmpty()) {
            Err(InvitationError.EmptyWallet).bind<ProcessInvitationResult>()
        } else {
            val compatibleCredentials = getCompatibleCredentials(
                inputDescriptors = presentationRequest.presentationDefinition.inputDescriptors
            ).mapError {
                InvitationError.Unexpected
            }.bind()

            processCompatibleCredentials(compatibleCredentials, presentationRequest)
                .bind()
        }
    }

    private fun processCompatibleCredentials(
        credentials: List<CompatibleCredential>,
        presentationRequest: PresentationRequest,
    ): Result<ProcessInvitationResult, ProcessInvitationError> = when {
        credentials.isEmpty() -> Err(InvitationError.NoMatchingCredential)
        credentials.size == 1 -> Ok(ProcessInvitationResult.PresentationRequest(credentials.first(), presentationRequest))
        else -> Ok(ProcessInvitationResult.PresentationRequestCredentialList(credentials, presentationRequest))
    }
}
