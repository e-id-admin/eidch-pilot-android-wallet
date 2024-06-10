package ch.admin.foitt.pilotwallet.platform.invitation

import ch.admin.foitt.pilotwallet.platform.invitation.domain.model.InvitationError
import ch.admin.foitt.pilotwallet.platform.invitation.domain.model.ProcessInvitationError
import ch.admin.foitt.pilotwallet.platform.invitation.domain.usecase.HandleInvitationProcessingError
import ch.admin.foitt.pilotwallet.platform.invitation.domain.usecase.impl.HandleInvitationProcessingErrorImpl
import ch.admin.foitt.pilotwallet.platform.navigation.NavigationManager
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.InvalidCredentialErrorScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.InvitationFailureScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.NoInternetConnectionScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.PresentationEmptyWalletScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.PresentationNoMatchScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.UnknownIssuerErrorScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.UnknownVerifierErrorScreenDestination
import com.ramcosta.composedestinations.spec.Direction
import io.mockk.MockKAnnotations
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.runs
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class HandleInvitationProcessingErrorTest {
    @MockK
    private lateinit var mockNavigationManager: NavigationManager

    private lateinit var handleInvitationProcessingError: HandleInvitationProcessingError

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        coEvery { mockNavigationManager.navigateToAndClearCurrent(any()) } just runs

        handleInvitationProcessingError = HandleInvitationProcessingErrorImpl(
            navManager = mockNavigationManager,
        )
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `execution navigates to the defined error screen`() = runTest {
        definedErrorDestinations.forEach { (error, destination) ->
            handleInvitationProcessingError(
                error,
                SOME_DEEP_LINK,
            ).navigate()

            coVerify(exactly = 1) {
                mockNavigationManager.navigateToAndClearCurrent(direction = any())
                mockNavigationManager.navigateToAndClearCurrent(
                    direction = destination,
                )
            }
            clearMocks(mockNavigationManager, answers = false)
        }
    }

    companion object {
        private const val SOME_DEEP_LINK = "openid-credential-offer://credential_offer=..."

        private val definedErrorDestinations: Map<ProcessInvitationError, Direction> = mapOf(
            InvitationError.EmptyWallet to PresentationEmptyWalletScreenDestination,
            InvitationError.InvalidCredentialInvitation to InvalidCredentialErrorScreenDestination,
            InvitationError.InvalidInput to InvitationFailureScreenDestination,
            InvitationError.NetworkError to NoInternetConnectionScreenDestination(SOME_DEEP_LINK),
            InvitationError.NoMatchingCredential to PresentationNoMatchScreenDestination,
            InvitationError.Unexpected to InvitationFailureScreenDestination,
            InvitationError.UnknownIssuer to UnknownIssuerErrorScreenDestination,
            InvitationError.UnknownVerifier to UnknownVerifierErrorScreenDestination,
        )
    }
}
