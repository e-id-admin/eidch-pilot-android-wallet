package ch.admin.foitt.pilotwallet.platform.invitation

import ch.admin.foitt.openid4vc.domain.model.presentationRequest.PresentationDefinition
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.PresentationRequest
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.PresentationRequestError
import ch.admin.foitt.openid4vc.domain.usecase.FetchPresentationRequest
import ch.admin.foitt.pilotwallet.platform.invitation.domain.model.InvitationError
import ch.admin.foitt.pilotwallet.platform.invitation.domain.usecase.GetPresentationRequestFromUri
import ch.admin.foitt.pilotwallet.platform.invitation.domain.usecase.impl.GetPresentationRequestFromUriImpl
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import io.mockk.MockKAnnotations
import io.mockk.Ordering
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.net.URI

class GetPresentationRequestFromUriTest {

    @MockK
    private lateinit var mockFetchPresentationRequest: FetchPresentationRequest

    private val emptyPresentationRequest = PresentationRequest(
        nonce = "",
        presentationDefinition = PresentationDefinition(
            id = "",
            inputDescriptors = listOf()
        ),
        responseUri = "",
        responseMode = "",
        state = null,
        clientMetaData = null,
    )

    private val validPresentationUrl =
        URI("https://example.org/get_request_object/88cf0d95-54c9-465f-9b97-0ba782314700")

    private lateinit var getPresentationRequestFromUri: GetPresentationRequestFromUri

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        getPresentationRequestFromUri = GetPresentationRequestFromUriImpl(
            fetchPresentationRequest = mockFetchPresentationRequest,
        )

        coEvery { mockFetchPresentationRequest.invoke(url = any()) } returns Ok(emptyPresentationRequest)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `A valid presentation request should return a success`() = runTest {
        val useCaseResult = getPresentationRequestFromUri(
            uri = validPresentationUrl,
        )

        coVerify(ordering = Ordering.ORDERED) {
            mockFetchPresentationRequest.invoke(url = any())
        }

        Assert.assertTrue(useCaseResult.get() is PresentationRequest)
    }

    @Test
    fun `A repository network error should return an error`() = runTest {
        coEvery { mockFetchPresentationRequest.invoke(url = any()) } returns Err(PresentationRequestError.NetworkError)

        val useCaseResult = getPresentationRequestFromUri(
            uri = validPresentationUrl,
        )

        coVerify(ordering = Ordering.ORDERED) {
            mockFetchPresentationRequest.invoke(url = any())
        }

        Assert.assertTrue(useCaseResult.getError() is InvitationError.NetworkError)
    }

    @Test
    fun `A presentation unexpected error should return an InvalidInvitation error`() = runTest {
        coEvery {
            mockFetchPresentationRequest.invoke(url = any())
        } returns Err(PresentationRequestError.Unexpected(Exception()))

        val useCaseResult = getPresentationRequestFromUri(
            uri = validPresentationUrl,
        )

        coVerify(ordering = Ordering.ORDERED) {
            mockFetchPresentationRequest.invoke(url = any())
        }

        Assert.assertTrue(useCaseResult.getError() is InvitationError.InvalidInvitation)
    }

    @Test
    fun `An invalid URL should return an error`() = runTest {
        val useCaseResult = getPresentationRequestFromUri(
            uri = URI("invalid://invalid.com"),
        )

        coVerify(exactly = 0) {
            mockFetchPresentationRequest.invoke(url = any())
        }

        Assert.assertTrue(useCaseResult.getError() is InvitationError.InvalidUri)
    }
}
