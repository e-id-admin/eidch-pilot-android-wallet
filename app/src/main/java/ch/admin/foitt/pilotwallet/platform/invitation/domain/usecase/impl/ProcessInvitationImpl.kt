package ch.admin.foitt.pilotwallet.platform.invitation.domain.usecase.impl

import ch.admin.foitt.openid4vc.domain.model.Invitation
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.CredentialOffer
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.PresentationRequest
import ch.admin.foitt.pilotwallet.platform.credential.domain.model.CredentialOfferError
import ch.admin.foitt.pilotwallet.platform.credential.domain.usecase.FetchCredential
import ch.admin.foitt.pilotwallet.platform.invitation.domain.model.InvitationError
import ch.admin.foitt.pilotwallet.platform.invitation.domain.model.ProcessInvitationError
import ch.admin.foitt.pilotwallet.platform.invitation.domain.usecase.ProcessInvitation
import ch.admin.foitt.pilotwallet.platform.invitation.domain.usecase.ValidateInvitation
import ch.admin.foitt.pilotwallet.platform.navArgs.domain.model.CredentialOfferNavArg
import ch.admin.foitt.pilotwallet.platform.navArgs.domain.model.PresentationCredentialListNavArg
import ch.admin.foitt.pilotwallet.platform.navArgs.domain.model.PresentationRequestNavArg
import ch.admin.foitt.pilotwallet.platform.navigation.NavigationManager
import ch.admin.foitt.pilotwallet.platform.navigation.domain.model.NavigationAction
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.CompatibleCredential
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.GetAllCredentials
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.GetCompatibleCredentials
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.CredentialOfferScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.InvalidCredentialErrorScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.InvitationFailureScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.NoInternetConnectionScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.PresentationCredentialListScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.PresentationEmptyWalletScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.PresentationNoMatchScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.PresentationRequestScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.UnknownIssuerErrorScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.UnknownVerifierErrorScreenDestination
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.getOrElse
import com.github.michaelbull.result.mapBoth
import timber.log.Timber
import javax.inject.Inject

internal class ProcessInvitationImpl @Inject constructor(
    private val navManager: NavigationManager,
    private val validateInvitation: ValidateInvitation,
    private val fetchCredential: FetchCredential,
    private val getCompatibleCredentials: GetCompatibleCredentials,
    private val getAllCredentials: GetAllCredentials,
) : ProcessInvitation {
    override suspend fun invoke(invitationUri: String): Result<NavigationAction, ProcessInvitationError> {
        return validateInvitation(invitationUri)
            .mapBoth(
                success = { invitation ->
                    Timber.d("Found valid invitation with uri: $invitationUri")
                    Ok(processValidInvitation(invitation, invitationUri))
                },
                failure = { validateInvitationError ->
                    Timber.w("No valid invitation found in $invitationUri, $validateInvitationError")
                    when (validateInvitationError) {
                        is InvitationError.InvalidUri,
                        is InvitationError.UnknownSchema,
                        InvitationError.InvalidInvitation -> Err(InvitationError.InvalidInvitation)
                        InvitationError.NetworkError -> Ok(navigateToNoInternetConnection(invitationUri))
                        InvitationError.UnknownVerifier -> Ok(navigateToUnknownVerifier())
                        is InvitationError.UnsupportedGrantType -> Ok(navigateToInvalidCredentialErrorScreen())
                        is InvitationError.DeserializationFailed,
                        InvitationError.NoCredentialsFound -> Ok(navigateToFailureScreen())
                    }
                },
            )
    }

    private fun navigateToNoInternetConnection(invitationUri: String) =
        NavigationAction {
            navManager.navigateToAndClearCurrent(
                direction = NoInternetConnectionScreenDestination(invitationUri = invitationUri)
            )
        }

    private fun navigateToUnknownVerifier() = NavigationAction {
        navManager.navigateToAndClearCurrent(
            direction = UnknownVerifierErrorScreenDestination,
        )
    }

    private suspend fun processValidInvitation(invitation: Invitation, invitationUri: String): NavigationAction {
        return when (invitation) {
            is CredentialOffer -> processCredentialOffer(credentialOffer = invitation, invitationUri = invitationUri)
            is PresentationRequest -> processPresentationRequest(invitation)
            else -> navigateToFailureScreen()
        }
    }

    private suspend fun processCredentialOffer(credentialOffer: CredentialOffer, invitationUri: String): NavigationAction =
        fetchCredential(credentialOffer).mapBoth(
            success = { credentialId -> navigateToCredentialOffer(credentialId) },
            failure = { error ->
                when (error) {
                    CredentialOfferError.CertificateNotPinned -> navigateToUnknownIssuerErrorScreen()
                    CredentialOfferError.IntegrityCheckFailed,
                    CredentialOfferError.InvalidGrant,
                    CredentialOfferError.UnsupportedGrantType,
                    is CredentialOfferError.MissingMandatoryField,
                    CredentialOfferError.InvalidCredentialOffer,
                    CredentialOfferError.UnsupportedCredentialFormat,
                    CredentialOfferError.UnsupportedCredentialType,
                    CredentialOfferError.UnsupportedProofType,
                    CredentialOfferError.UnsupportedCryptographicSuite,
                    CredentialOfferError.CredentialParsingError -> navigateToInvalidCredentialErrorScreen()
                    CredentialOfferError.NetworkError -> navigateToNoInternetConnection(invitationUri = invitationUri)
                    CredentialOfferError.DatabaseError,
                    is CredentialOfferError.Unexpected -> navigateToFailureScreen()
                }
            },
        )

    private fun navigateToCredentialOffer(credentialId: Long) =
        NavigationAction {
            navManager.navigateToAndClearCurrent(
                direction = CredentialOfferScreenDestination(
                    navArgs = CredentialOfferNavArg(credentialId)
                )
            )
        }

    private fun navigateToUnknownIssuerErrorScreen() =
        NavigationAction {
            navManager.navigateToAndClearCurrent(UnknownIssuerErrorScreenDestination)
        }

    private fun navigateToInvalidCredentialErrorScreen() =
        NavigationAction {
            navManager.navigateToAndClearCurrent(InvalidCredentialErrorScreenDestination)
        }

    private suspend fun processPresentationRequest(presentationRequest: PresentationRequest): NavigationAction = coroutineBinding {
        val credentials = getAllCredentials().bind()
        if (credentials.isEmpty()) {
            navigateToEmptyWallet()
        } else {
            val compatibleCredentials = getCompatibleCredentials(
                inputDescriptors = presentationRequest.presentationDefinition.inputDescriptors
            ).bind()
            processCompatibleCredentials(compatibleCredentials, presentationRequest)
        }
    }.getOrElse {
        navigateToFailureScreen()
    }

    private fun processCompatibleCredentials(
        credentials: List<CompatibleCredential>,
        presentationRequest: PresentationRequest,
    ) =
        when {
            credentials.isEmpty() -> navigateToNoMatchingCredential()
            credentials.size == 1 -> navigateToPresentationRequest(credentials.first(), presentationRequest)
            else -> navigateToPresentationCredentialList(compatibleCredentials = credentials, presentationRequest = presentationRequest)
        }

    private fun navigateToEmptyWallet() = NavigationAction {
        navManager.navigateToAndClearCurrent(
            direction = PresentationEmptyWalletScreenDestination
        )
    }

    private fun navigateToNoMatchingCredential() = NavigationAction {
        navManager.navigateToAndClearCurrent(
            direction = PresentationNoMatchScreenDestination
        )
    }

    private fun navigateToPresentationRequest(
        compatibleCredential: CompatibleCredential,
        presentationRequest: PresentationRequest,
    ) = NavigationAction {
        navManager.navigateToAndClearCurrent(
            direction = PresentationRequestScreenDestination(
                navArgs = PresentationRequestNavArg(
                    compatibleCredential,
                    presentationRequest,
                )
            )
        )
    }

    private fun navigateToPresentationCredentialList(
        compatibleCredentials: List<CompatibleCredential>,
        presentationRequest: PresentationRequest,
    ) = NavigationAction {
        navManager.navigateToAndClearCurrent(
            direction = PresentationCredentialListScreenDestination(
                navArgs = PresentationCredentialListNavArg(
                    compatibleCredentials.toTypedArray(),
                    presentationRequest,
                )
            )
        )
    }

    private fun navigateToFailureScreen() =
        NavigationAction {
            navManager.navigateToAndClearCurrent(
                direction = InvitationFailureScreenDestination
            )
        }
}
