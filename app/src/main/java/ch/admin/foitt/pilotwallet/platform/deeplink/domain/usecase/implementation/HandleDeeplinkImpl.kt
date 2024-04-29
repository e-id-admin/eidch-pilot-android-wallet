package ch.admin.foitt.pilotwallet.platform.deeplink.domain.usecase.implementation

import androidx.annotation.CheckResult
import ch.admin.foitt.pilotwallet.feature.onboarding.navigation.OnboardingNavGraph
import ch.admin.foitt.pilotwallet.platform.deeplink.domain.repository.DeepLinkIntentRepository
import ch.admin.foitt.pilotwallet.platform.deeplink.domain.usecase.HandleDeeplink
import ch.admin.foitt.pilotwallet.platform.invitation.domain.model.InvitationError
import ch.admin.foitt.pilotwallet.platform.invitation.domain.model.ProcessInvitationError
import ch.admin.foitt.pilotwallet.platform.invitation.domain.model.ProcessInvitationResult
import ch.admin.foitt.pilotwallet.platform.invitation.domain.usecase.ProcessInvitation
import ch.admin.foitt.pilotwallet.platform.navArgs.domain.model.CredentialOfferNavArg
import ch.admin.foitt.pilotwallet.platform.navigation.NavigationManager
import ch.admin.foitt.pilotwallet.platform.navigation.domain.model.NavigationAction
import ch.admin.foitt.pilotwallet.platform.scaffold.extension.navigateUpOrToRoot
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.CredentialOfferScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.HomeScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.InvalidCredentialErrorScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.InvitationFailureScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.NoInternetConnectionScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.UnknownIssuerErrorScreenDestination
import com.github.michaelbull.result.mapBoth
import com.ramcosta.composedestinations.spec.Direction
import timber.log.Timber
import javax.inject.Inject

class HandleDeeplinkImpl @Inject constructor(
    private val navManager: NavigationManager,
    private val deepLinkIntentRepository: DeepLinkIntentRepository,
    private val processInvitation: ProcessInvitation,
) : HandleDeeplink {

    @CheckResult
    override suspend operator fun invoke(fromOnboarding: Boolean): NavigationAction {
        val deepLink = deepLinkIntentRepository.get()
        Timber.d("Deeplink read: $deepLink")

        return if (deepLink == null) {
            if (fromOnboarding) {
                navigateTo(
                    direction = HomeScreenDestination,
                    fromOnboarding = true
                )
            } else {
                NavigationAction {
                    navManager.navigateUpOrToRoot()
                }
            }
        } else {
            deepLinkIntentRepository.reset()

            val nextDirection = processInvitation(deepLink)
                .mapBoth(
                    success = { invitation ->
                        handleSuccess(invitation)
                    },
                    failure = { invitationError ->
                        handleFailure(invitationError, deepLink)
                    },
                )

            navigateTo(
                direction = nextDirection,
                fromOnboarding = fromOnboarding,
            )
        }
    }

    private fun handleSuccess(invitation: ProcessInvitationResult): Direction = when (invitation) {
        is ProcessInvitationResult.CredentialOffer -> credentialOfferDirection(invitation)
        is ProcessInvitationResult.PresentationRequest,
        is ProcessInvitationResult.PresentationRequestCredentialList -> {
            // We do no support presentation deeplinks. So it is redirected to an error.
            InvalidCredentialErrorScreenDestination
        }
    }

    private fun handleFailure(invitationError: ProcessInvitationError, deepLink: String): Direction = when (invitationError) {
        InvitationError.UnknownIssuer -> UnknownIssuerErrorScreenDestination
        InvitationError.InvalidCredentialInvitation,
        InvitationError.InvalidInput -> InvalidCredentialErrorScreenDestination
        InvitationError.NetworkError -> NoInternetConnectionScreenDestination(deepLink)
        InvitationError.UnknownVerifier,
        InvitationError.EmptyWallet,
        InvitationError.NoMatchingCredential,
        InvitationError.Unexpected -> InvitationFailureScreenDestination
    }

    private fun credentialOfferDirection(
        credentialOffer: ProcessInvitationResult.CredentialOffer,
    ) = CredentialOfferScreenDestination(
        CredentialOfferNavArg(
            credentialOffer.credentialId
        )
    )

    private fun navigateTo(direction: Direction, fromOnboarding: Boolean) = NavigationAction {
        if (fromOnboarding) {
            navManager.navigateToAndPopUpTo(
                direction = direction,
                route = OnboardingNavGraph.NAME,
            )
        } else {
            navManager.navigateToAndClearCurrent(
                direction = direction,
            )
        }
    }
}
