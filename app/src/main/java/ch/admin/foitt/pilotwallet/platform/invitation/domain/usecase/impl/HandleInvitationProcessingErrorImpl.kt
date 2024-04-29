package ch.admin.foitt.pilotwallet.platform.invitation.domain.usecase.impl

import androidx.annotation.CheckResult
import ch.admin.foitt.pilotwallet.platform.invitation.domain.model.InvitationError
import ch.admin.foitt.pilotwallet.platform.invitation.domain.model.ProcessInvitationError
import ch.admin.foitt.pilotwallet.platform.invitation.domain.usecase.HandleInvitationProcessingError
import ch.admin.foitt.pilotwallet.platform.navigation.NavigationManager
import ch.admin.foitt.pilotwallet.platform.navigation.domain.model.NavigationAction
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.InvalidCredentialErrorScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.InvitationFailureScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.NoInternetConnectionScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.PresentationEmptyWalletScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.PresentationNoMatchScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.UnknownIssuerErrorScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.UnknownVerifierErrorScreenDestination
import javax.inject.Inject

class HandleInvitationProcessingErrorImpl @Inject constructor(
    private val navManager: NavigationManager,
) : HandleInvitationProcessingError {
    @CheckResult
    override suspend operator fun invoke(
        failureResult: ProcessInvitationError,
        invitationUri: String,
    ) = NavigationAction {
        when (failureResult) {
            InvitationError.EmptyWallet -> navigateToEmptyWallet()
            InvitationError.InvalidCredentialInvitation -> navigateToInvalidCredential()
            InvitationError.NetworkError -> navigateToNoInternetConnection(invitationUri)
            InvitationError.NoMatchingCredential -> navigateToNoMatchingCredential()
            InvitationError.Unexpected -> navigateToFailureScreen()
            InvitationError.UnknownIssuer -> navigateToUnknownIssuer()
            InvitationError.UnknownVerifier -> navigateToUnknownVerifier()
            InvitationError.InvalidInput -> navigateToFailureScreen()
        }
    }

    private fun navigateToNoInternetConnection(
        invitationUri: String,
    ) = navManager.navigateToAndClearCurrent(
        NoInternetConnectionScreenDestination(invitationUri)
    )

    private fun navigateToUnknownVerifier() = navManager.navigateToAndClearCurrent(
        UnknownVerifierErrorScreenDestination,
    )

    private fun navigateToUnknownIssuer() = navManager.navigateToAndClearCurrent(
        UnknownIssuerErrorScreenDestination,
    )

    private fun navigateToInvalidCredential() = navManager.navigateToAndClearCurrent(
        InvalidCredentialErrorScreenDestination
    )

    private fun navigateToEmptyWallet() = navManager.navigateToAndClearCurrent(
        PresentationEmptyWalletScreenDestination
    )

    private fun navigateToNoMatchingCredential() = navManager.navigateToAndClearCurrent(
        PresentationNoMatchScreenDestination
    )

    private fun navigateToFailureScreen() = navManager.navigateToAndClearCurrent(
        InvitationFailureScreenDestination
    )
}
