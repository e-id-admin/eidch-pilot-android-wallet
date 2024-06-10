package ch.admin.foitt.pilotwallet.feature.deeplink

import ch.admin.foitt.openid4vc.domain.model.presentationRequest.PresentationDefinition
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.PresentationRequest
import ch.admin.foitt.pilotwallet.feature.onboarding.navigation.OnboardingNavGraph
import ch.admin.foitt.pilotwallet.platform.deeplink.domain.repository.DeepLinkIntentRepository
import ch.admin.foitt.pilotwallet.platform.deeplink.domain.usecase.HandleDeeplink
import ch.admin.foitt.pilotwallet.platform.deeplink.domain.usecase.implementation.HandleDeeplinkImpl
import ch.admin.foitt.pilotwallet.platform.invitation.domain.model.InvitationError
import ch.admin.foitt.pilotwallet.platform.invitation.domain.model.ProcessInvitationError
import ch.admin.foitt.pilotwallet.platform.invitation.domain.model.ProcessInvitationResult
import ch.admin.foitt.pilotwallet.platform.invitation.domain.usecase.ProcessInvitation
import ch.admin.foitt.pilotwallet.platform.navArgs.domain.model.CredentialOfferNavArg
import ch.admin.foitt.pilotwallet.platform.navigation.NavigationManager
import ch.admin.foitt.pilotwallet.platform.scaffold.extension.navigateUpOrToRoot
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.CompatibleCredential
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.CredentialOfferScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.HomeScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.InvalidCredentialErrorScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.InvitationFailureScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.NoInternetConnectionScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.UnknownIssuerErrorScreenDestination
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.ramcosta.composedestinations.spec.Direction
import io.mockk.MockKAnnotations
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.runs
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class HandleDeeplinkTest {
    @MockK
    private lateinit var mockNavigationManager: NavigationManager

    @MockK
    private lateinit var mockDeepLinkIntentRepository: DeepLinkIntentRepository

    @MockK
    private lateinit var mockProcessInvitation: ProcessInvitation

    private lateinit var handleDeeplinkUseCase: HandleDeeplink

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        coEvery { mockProcessInvitation(invitationUri = any()) } returns Ok(mockCredentialOfferResult)
        coEvery { mockDeepLinkIntentRepository.get() } returns someDeepLink
        coEvery { mockNavigationManager.navigateToAndPopUpTo(any(), any()) } just runs
        coEvery { mockNavigationManager.navigateToAndClearCurrent(any()) } just runs
        coEvery { mockNavigationManager.navigateUpOrToRoot() } just runs
        coEvery { mockDeepLinkIntentRepository.reset() } just runs

        handleDeeplinkUseCase = HandleDeeplinkImpl(
            navManager = mockNavigationManager,
            deepLinkIntentRepository = mockDeepLinkIntentRepository,
            processInvitation = mockProcessInvitation,
        )
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `Coming from onboarding, on no intent, navigates to home screen and pop onboarding`() = runTest {
        coEvery { mockDeepLinkIntentRepository.get() } returns null

        handleDeeplinkUseCase(true).navigate()

        coVerifyOrder {
            mockDeepLinkIntentRepository.get()
            mockNavigationManager.navigateToAndPopUpTo(
                direction = HomeScreenDestination,
                route = OnboardingNavGraph.NAME,
            )
        }
    }

    @Test
    fun `Not being in onboarding, on no intent, navigates up or to root`() = runTest {
        coEvery { mockDeepLinkIntentRepository.get() } returns null

        handleDeeplinkUseCase(false).navigate()

        coVerifyOrder {
            mockDeepLinkIntentRepository.get()
            mockNavigationManager.navigateUpOrToRoot()
        }
    }

    @Test
    fun `On deeplink handling, reset the repository to null`() = runTest {
        handleDeeplinkUseCase(false).navigate()

        coVerifyOrder {
            mockDeepLinkIntentRepository.get()
            mockDeepLinkIntentRepository.reset()
        }
    }

    @Test
    fun `On deeplink handling, in onboarding, pop the onboarding stack in all success cases`() = runTest {
        mockSuccesses.forEach { success ->
            coEvery { mockProcessInvitation(someDeepLink) } returns Ok(success)
            handleDeeplinkUseCase(true).navigate()

            coVerify(exactly = 1) {
                mockNavigationManager.navigateToAndPopUpTo(any(), route = OnboardingNavGraph.NAME)
            }
            clearMocks(mockNavigationManager, answers = false)
        }
    }

    @Test
    fun `On deeplink handling, not in onboarding, navigate and pop current screen in all success cases`() = runTest {
        mockSuccesses.forEach { success ->
            coEvery { mockProcessInvitation(someDeepLink) } returns Ok(success)
            handleDeeplinkUseCase(false).navigate()

            coVerify(exactly = 1) {
                mockNavigationManager.navigateToAndClearCurrent(any())
            }
            clearMocks(mockNavigationManager, answers = false)
        }
    }

    @Test
    fun `On deeplink handling, in onboarding, pop the onboarding stack in all failure cases`() = runTest {
        mockFailures.forEach { failure ->
            coEvery { mockProcessInvitation(someDeepLink) } returns Err(failure)
            handleDeeplinkUseCase(true).navigate()

            coVerify(exactly = 1) {
                mockNavigationManager.navigateToAndPopUpTo(any(), route = OnboardingNavGraph.NAME)
            }
            clearMocks(mockNavigationManager, answers = false)
        }
    }

    @Test
    fun `On deeplink handling, not in onboarding, navigate and pop current screen in all failure cases`() = runTest {
        mockFailures.forEach { failure ->
            coEvery { mockProcessInvitation(someDeepLink) } returns Err(failure)
            handleDeeplinkUseCase(false).navigate()

            coVerify(exactly = 1) {
                mockNavigationManager.navigateToAndClearCurrent(any())
            }
            clearMocks(mockNavigationManager, answers = false)
        }
    }

    @Test
    fun `On credential offer, navigates to credential offer screen`() = runTest {
        coEvery { mockProcessInvitation(someDeepLink) } returns Ok(mockCredentialOfferResult)
        handleDeeplinkUseCase(false).navigate()

        coVerify(exactly = 1) {
            mockNavigationManager.navigateToAndClearCurrent(any())
            mockNavigationManager.navigateToAndClearCurrent(
                direction = CredentialOfferScreenDestination(
                    CredentialOfferNavArg(
                        mockCredentialOfferResult.credentialId
                    )
                ),
            )
        }
    }

    @Test
    fun `On presentation request, navigate to an error`() = runTest {
        coEvery { mockProcessInvitation(someDeepLink) } returns Ok(mockPresentationRequestResult)
        handleDeeplinkUseCase(false).navigate()

        coVerify(exactly = 1) {
            mockNavigationManager.navigateToAndClearCurrent(any())
            mockNavigationManager.navigateToAndClearCurrent(
                direction = InvalidCredentialErrorScreenDestination,
            )
        }
    }

    @Test
    fun `On presentation request with multiple credentials, navigate to an error`() = runTest {
        coEvery { mockProcessInvitation(someDeepLink) } returns Ok(mockPresentationRequestListResult)
        handleDeeplinkUseCase(false).navigate()

        coVerify(exactly = 1) {
            mockNavigationManager.navigateToAndClearCurrent(any())
            mockNavigationManager.navigateToAndClearCurrent(
                direction = InvalidCredentialErrorScreenDestination,
            )
        }
    }

    @Test
    fun `On deeplink processing failure, navigate to the defined error screen`() = runTest {
        val definedErrorDestinations: Map<ProcessInvitationError, Direction> = mapOf(
            InvitationError.EmptyWallet to InvitationFailureScreenDestination,
            InvitationError.InvalidCredentialInvitation to InvalidCredentialErrorScreenDestination,
            InvitationError.InvalidInput to InvalidCredentialErrorScreenDestination,
            InvitationError.NetworkError to NoInternetConnectionScreenDestination(someDeepLink),
            InvitationError.NoMatchingCredential to InvitationFailureScreenDestination,
            InvitationError.Unexpected to InvitationFailureScreenDestination,
            InvitationError.UnknownIssuer to UnknownIssuerErrorScreenDestination,
            InvitationError.UnknownVerifier to InvitationFailureScreenDestination,
        )

        definedErrorDestinations.forEach { (error, destination) ->
            coEvery { mockProcessInvitation(someDeepLink) } returns Err(error)
            handleDeeplinkUseCase(false).navigate()

            coVerify(exactly = 1) {
                mockNavigationManager.navigateToAndClearCurrent(any())
                mockNavigationManager.navigateToAndClearCurrent(
                    direction = destination,
                )
            }
            clearMocks(mockNavigationManager, answers = false)
        }
    }

    companion object {
        private const val someDeepLink = "openid-credential-offer://credential_offer=..."
        private val mockCredentialOfferResult = ProcessInvitationResult.CredentialOffer(0L)

        private val mockPresentationRequest = PresentationRequest(
            nonce = "iusto",
            presentationDefinition = PresentationDefinition(
                id = "diam",
                inputDescriptors = listOf()
            ),
            responseUri = "tincidunt",
            responseMode = "suscipit",
            state = null,
            clientMetaData = null
        )

        private val mockCompatibleCredential = CompatibleCredential(
            credentialId = mockCredentialOfferResult.credentialId,
            requestedFields = listOf(),
        )

        private val mockPresentationRequestResult = ProcessInvitationResult.PresentationRequest(
            mockCompatibleCredential,
            mockPresentationRequest,
        )

        private val mockPresentationRequestListResult = ProcessInvitationResult.PresentationRequestCredentialList(
            listOf(mockCompatibleCredential),
            mockPresentationRequest,
        )

        private val mockSuccesses: List<ProcessInvitationResult> = listOf(
            ProcessInvitationResult.CredentialOffer(0L),
            ProcessInvitationResult.PresentationRequest(CompatibleCredential(0L, listOf()), mockPresentationRequest),
            ProcessInvitationResult.PresentationRequestCredentialList(listOf(), mockPresentationRequest),
        )

        private val mockFailures: List<ProcessInvitationError> = listOf(
            InvitationError.EmptyWallet,
            InvitationError.InvalidCredentialInvitation,
            InvitationError.InvalidInput,
            InvitationError.NetworkError,
            InvitationError.NoMatchingCredential,
            InvitationError.Unexpected,
            InvitationError.UnknownIssuer,
            InvitationError.UnknownVerifier,
        )
    }
}
