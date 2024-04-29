package ch.admin.foitt.pilotwallet.platform.invitation

import ch.admin.foitt.openid4vc.domain.model.presentationRequest.PresentationDefinition
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.PresentationRequest
import ch.admin.foitt.pilotwallet.platform.invitation.domain.model.ProcessInvitationResult
import ch.admin.foitt.pilotwallet.platform.invitation.domain.usecase.HandleInvitationProcessingSuccess
import ch.admin.foitt.pilotwallet.platform.invitation.domain.usecase.impl.HandleInvitationProcessingSuccessImpl
import ch.admin.foitt.pilotwallet.platform.navigation.NavigationManager
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.CompatibleCredential
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.CredentialOfferScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.PresentationCredentialListScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.PresentationRequestScreenDestination
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
import org.junit.After
import org.junit.Before
import org.junit.Test

class HandleInvitationProcessingSuccessTest {

    @MockK
    private lateinit var mockNavigationManager: NavigationManager

    private lateinit var handleInvitationProcessingSuccess: HandleInvitationProcessingSuccess

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        coEvery { mockNavigationManager.navigateToAndClearCurrent(any()) } just runs

        handleInvitationProcessingSuccess = HandleInvitationProcessingSuccessImpl(
            navManager = mockNavigationManager,
        )
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `execution navigates to the defined screen`() = runTest {
        definedSuccessDestinations.forEach { (successResult, destination) ->
            handleInvitationProcessingSuccess(successResult).navigate()

            coVerify(exactly = 1) {
                mockNavigationManager.navigateToAndClearCurrent(destination)
                mockNavigationManager.navigateToAndClearCurrent(destination)
            }
            clearMocks(mockNavigationManager, answers = false)
        }
    }

    companion object {
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
        private val mockCredentialOfferResult = ProcessInvitationResult.CredentialOffer(0L)

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

        private val definedSuccessDestinations: Map<ProcessInvitationResult, Direction> = mapOf(
            mockCredentialOfferResult
                to CredentialOfferScreenDestination(mockCredentialOfferResult.credentialId),
            mockPresentationRequestResult
                to PresentationRequestScreenDestination(mockPresentationRequestResult.credential, mockPresentationRequestResult.request),
            mockPresentationRequestListResult
                to PresentationCredentialListScreenDestination(
                    mockPresentationRequestListResult.credentials.toTypedArray(),
                    mockPresentationRequestListResult.request,
                ),
        )
    }
}
