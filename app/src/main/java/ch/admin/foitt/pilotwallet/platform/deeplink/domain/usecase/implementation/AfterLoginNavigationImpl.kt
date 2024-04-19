package ch.admin.foitt.pilotwallet.platform.deeplink.domain.usecase.implementation

import ch.admin.foitt.pilotwallet.platform.deeplink.domain.repository.DeepLinkIntentRepository
import ch.admin.foitt.pilotwallet.platform.deeplink.domain.usecase.AfterLoginNavigation
import ch.admin.foitt.pilotwallet.platform.invitation.domain.usecase.ProcessInvitation
import ch.admin.foitt.pilotwallet.platform.navigation.NavigationManager
import ch.admin.foitt.pilotwallet.platform.navigation.domain.model.NavigationAction
import ch.admin.foitt.pilotwallet.platform.scaffold.extension.navigateUpOrToRoot
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.InvitationFailureScreenDestination
import com.github.michaelbull.result.getOrElse
import javax.inject.Inject

class AfterLoginNavigationImpl @Inject constructor(
    private val navManager: NavigationManager,
    private val deepLinkIntentRepository: DeepLinkIntentRepository,
    private val processInvitation: ProcessInvitation
) : AfterLoginNavigation {
    override suspend fun invoke(): NavigationAction {
        val deepLink = deepLinkIntentRepository.deepLink

        return if (deepLink == null) {
            handleStandardNavigation()
        } else {
            deepLinkIntentRepository.deepLink = null
            processInvitation(deepLink).getOrElse {
                navigateToFailureScreen()
            }
        }
    }

    private fun handleStandardNavigation() = NavigationAction {
        navManager.navigateUpOrToRoot()
    }

    private fun navigateToFailureScreen() =
        NavigationAction {
            navManager.navigateToAndClearCurrent(
                direction = InvitationFailureScreenDestination
            )
        }
}
