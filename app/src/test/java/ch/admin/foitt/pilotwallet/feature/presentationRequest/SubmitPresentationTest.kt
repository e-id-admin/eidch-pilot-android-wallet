package ch.admin.foitt.pilotwallet.feature.presentationRequest

import ch.admin.foitt.openid4vc.domain.model.presentationRequest.Format
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.JwtVc
import ch.admin.foitt.openid4vc.domain.usecase.CreatePresentationRequestBody
import ch.admin.foitt.openid4vc.domain.usecase.CreateVerifiablePresentationToken
import ch.admin.foitt.openid4vc.domain.usecase.SendPresentation
import ch.admin.foitt.pilotwallet.feature.presentationRequest.domain.model.PresentationConfig
import ch.admin.foitt.pilotwallet.feature.presentationRequest.domain.model.PresentationRequestError
import ch.admin.foitt.pilotwallet.feature.presentationRequest.domain.usecase.SubmitPresentation
import ch.admin.foitt.pilotwallet.feature.presentationRequest.domain.usecase.implementation.SubmitPresentationImpl
import ch.admin.foitt.pilotwallet.feature.presentationRequest.mock.MockCompatibleCredentials
import ch.admin.foitt.pilotwallet.feature.presentationRequest.mock.MockPresentationValues
import ch.admin.foitt.pilotwallet.platform.sdjwt.domain.model.CreateDisclosedSdJwtError
import ch.admin.foitt.pilotwallet.platform.sdjwt.domain.usecase.CreateDisclosedSdJwt
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.SsiError
import ch.admin.foitt.pilotwallet.platform.ssi.domain.repository.CredentialRawRepo
import ch.admin.foitt.pilotwallet.util.assertErrorType
import ch.admin.foitt.pilotwallet.util.assertOk
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import java.net.URL
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.PresentationRequestError as OpenIdPresentationRequestError

class SubmitPresentationTest {

    @MockK
    private lateinit var mockCredentialRawRepo: CredentialRawRepo

    @MockK
    private lateinit var mockCreateVerifiablePresentationToken: CreateVerifiablePresentationToken

    @MockK
    private lateinit var mockCreateDisclosedSdJwt: CreateDisclosedSdJwt

    @MockK
    private lateinit var mockSendPresentation: SendPresentation

    @MockK
    private lateinit var mockCreatePresentationRequestBody: CreatePresentationRequestBody

    private val presentationConfig = PresentationConfig()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var submitPresentationUseCase: SubmitPresentation

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        submitPresentationUseCase = SubmitPresentationImpl(
            credentialRawRepo = mockCredentialRawRepo,
            createVerifiablePresentationToken = mockCreateVerifiablePresentationToken,
            createDisclosedSdJwt = mockCreateDisclosedSdJwt,
            sendPresentation = mockSendPresentation,
            presentationConfig = presentationConfig,
            createPresentationRequestBody = mockCreatePresentationRequestBody,
            ioDispatcher = testDispatcher,
        )

        coEvery { mockCredentialRawRepo.getByCredentialId(any()) } returns Ok(listOf(MockPresentationValues.credential))
        coEvery { mockCreateDisclosedSdJwt.invoke(any(), any()) } returns Ok(MockPresentationValues.disclosedSdJwt)
        coEvery {
            mockCreateVerifiablePresentationToken(
                keyAlias = any(),
                nonce = any(),
                disclosedSdJwt = any(),
                config = any(),
            )
        } returns Ok(MockPresentationValues.presentationToken)

        coEvery {
            mockCreatePresentationRequestBody(
                vpToken = any(),
                presentationRequest = any(),
                presentationConfig = any(),
            )
        } returns Ok(MockPresentationValues.presentationRequestBody)

        coEvery { mockSendPresentation(any(), any()) } returns Ok(Unit)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `submitting a presentation successfully follows specific steps`() = runTest(testDispatcher) {
        val testCredential = MockCompatibleCredentials.compatibleCredential
        val testRequest = MockPresentationValues.presentationRequest
        val expectedFieldList = testCredential.requestedFields.map { it.key }
        val expectedNonce = testRequest.nonce
        val expectedUrl = URL(testRequest.responseUri)

        val result = submitPresentationUseCase(
            presentationRequest = testRequest,
            compatibleCredential = testCredential,
        )
        result.assertOk()

        coVerifyOrder {
            mockCredentialRawRepo.getByCredentialId(credentialId = testCredential.credentialId)
            mockCreateDisclosedSdJwt.invoke(any(), attributesKey = expectedFieldList)
            mockCreateVerifiablePresentationToken.invoke(any(), nonce = expectedNonce, any(), any())
            mockCreatePresentationRequestBody.invoke(any(), any(), any())
            mockSendPresentation.invoke(url = expectedUrl, any())
        }
    }

    @Test
    fun `submitting a presentation with no input descriptor should fail`() = runTest(testDispatcher) {
        val presentationRequest = MockPresentationValues.presentationRequest.copy(
            presentationDefinition = MockPresentationValues.presentationDefinition.copy(inputDescriptors = listOf())
        )

        val result = submitPresentationUseCase(
            presentationRequest = presentationRequest,
            compatibleCredential = MockCompatibleCredentials.compatibleCredential,
        )

        result.assertErrorType(PresentationRequestError.Unexpected::class)

        coVerify(exactly = 0) {
            mockCredentialRawRepo.getByCredentialId(any())
            mockCreateDisclosedSdJwt.invoke(any(), any())
            mockCreateVerifiablePresentationToken.invoke(any(), any(), any(), any())
            mockCreatePresentationRequestBody.invoke(any(), any(), any())
            mockSendPresentation.invoke(any(), any())
        }
    }

    @Test
    @Ignore(
        "Should be taken into account when the issuers/verifiers actually provide/ask for various algorithms"
    )
    fun `submitting a presentation where the required algorithm do not match the one from the credential should fail`() = runTest(
        testDispatcher
    ) {
        val presentationRequest = MockPresentationValues.presentationRequest.copy(
            presentationDefinition = MockPresentationValues.presentationDefinition.copy(
                inputDescriptors = listOf(
                    MockPresentationValues.inputDescriptor.copy(
                        format = Format(jwtVc = JwtVc("ESXXX"))
                    )
                )
            )
        )

        val result = submitPresentationUseCase(
            presentationRequest = presentationRequest,
            compatibleCredential = MockCompatibleCredentials.compatibleCredential,
        )

        result.assertErrorType(PresentationRequestError.Unexpected::class)

        coVerify(exactly = 0) {
            mockCredentialRawRepo.getByCredentialId(any())
            mockCreateDisclosedSdJwt.invoke(any(), any())
            mockCreateVerifiablePresentationToken.invoke(any(), any(), any(), any())
            mockCreatePresentationRequestBody.invoke(any(), any(), any())
            mockSendPresentation.invoke(any(), any())
        }
    }

    @Test
    fun `submitting a presentation when the credential repo fails should fail`() = runTest(testDispatcher) {
        coEvery { mockCredentialRawRepo.getByCredentialId(any()) } returns Err(SsiError.Unexpected(testException))

        val result = submitPresentationUseCase(
            presentationRequest = MockPresentationValues.presentationRequest,
            compatibleCredential = MockCompatibleCredentials.compatibleCredential,
        )

        result.assertErrorType(PresentationRequestError.Unexpected::class)

        coVerifyOrder {
            mockCredentialRawRepo.getByCredentialId(any())
        }

        coVerify(exactly = 0) {
            mockCreateDisclosedSdJwt.invoke(any(), any())
            mockCreateVerifiablePresentationToken.invoke(any(), any(), any(), any())
            mockCreatePresentationRequestBody.invoke(any(), any(), any())
            mockSendPresentation.invoke(any(), any())
        }
    }

    @Test
    fun `submitting a presentation where the raw credential is missing should fail`() = runTest(testDispatcher) {
        coEvery { mockCredentialRawRepo.getByCredentialId(any()) } returns Ok(listOf())

        val result = submitPresentationUseCase(
            presentationRequest = MockPresentationValues.presentationRequest,
            compatibleCredential = MockCompatibleCredentials.compatibleCredential,
        )

        result.assertErrorType(PresentationRequestError.CredentialNoFound::class)

        coVerifyOrder {
            mockCredentialRawRepo.getByCredentialId(any())
        }

        coVerify(exactly = 0) {
            mockCreateDisclosedSdJwt.invoke(any(), any())
            mockCreateVerifiablePresentationToken.invoke(any(), any(), any(), any())
            mockCreatePresentationRequestBody.invoke(any(), any(), any())
            mockSendPresentation.invoke(any(), any())
        }
    }

    @Test
    fun `submitting a presentation where the disclosedSdJwt is invalid should fail`() = runTest(testDispatcher) {
        coEvery { mockCreateDisclosedSdJwt.invoke(any(), any()) } returns Err(CreateDisclosedSdJwtError.InvalidSdJwt)

        val result = submitPresentationUseCase(
            presentationRequest = MockPresentationValues.presentationRequest,
            compatibleCredential = MockCompatibleCredentials.compatibleCredential,
        )

        result.assertErrorType(PresentationRequestError.RawSdJwtParsingError::class)

        coVerifyOrder {
            mockCredentialRawRepo.getByCredentialId(any())
            mockCreateDisclosedSdJwt.invoke(any(), any())
        }

        coVerify(exactly = 0) {
            mockCreateVerifiablePresentationToken.invoke(any(), any(), any(), any())
            mockCreatePresentationRequestBody.invoke(any(), any(), any())
            mockSendPresentation.invoke(any(), any())
        }
    }

    @Test
    fun `submitting a presentation where the vpToken creation fail should fail`() = runTest(testDispatcher) {
        coEvery {
            mockCreateVerifiablePresentationToken.invoke(any(), any(), any(), any())
        } returns Err(OpenIdPresentationRequestError.Unexpected(testException))

        val result = submitPresentationUseCase(
            presentationRequest = MockPresentationValues.presentationRequest,
            compatibleCredential = MockCompatibleCredentials.compatibleCredential,
        )

        result.assertErrorType(PresentationRequestError.Unexpected::class)

        coVerifyOrder {
            mockCredentialRawRepo.getByCredentialId(any())
            mockCreateDisclosedSdJwt.invoke(any(), any())
            mockCreateVerifiablePresentationToken.invoke(any(), any(), any(), any())
        }

        coVerify(exactly = 0) {
            mockCreatePresentationRequestBody.invoke(any(), any(), any())
            mockSendPresentation.invoke(any(), any())
        }
    }

    @Test
    fun `submitting a presentation where the presentationRequestBody creation fail should fail`() = runTest(testDispatcher) {
        coEvery {
            mockCreatePresentationRequestBody.invoke(any(), any(), any())
        } returns Err(OpenIdPresentationRequestError.Unexpected(testException))

        val result = submitPresentationUseCase(
            presentationRequest = MockPresentationValues.presentationRequest,
            compatibleCredential = MockCompatibleCredentials.compatibleCredential,
        )

        result.assertErrorType(PresentationRequestError.Unexpected::class)

        coVerifyOrder {
            mockCredentialRawRepo.getByCredentialId(any())
            mockCreateDisclosedSdJwt.invoke(any(), any())
            mockCreateVerifiablePresentationToken.invoke(any(), any(), any(), any())
            mockCreatePresentationRequestBody.invoke(any(), any(), any())
        }

        coVerify(exactly = 0) {
            mockSendPresentation.invoke(any(), any())
        }
    }

    @Test
    fun `submitting a presentation where the presentation sending fail should fail with specific errors`() = runTest(testDispatcher) {
        coEvery { mockSendPresentation.invoke(any(), any()) } returns Err(OpenIdPresentationRequestError.ValidationError)

        val result = submitPresentationUseCase(
            presentationRequest = MockPresentationValues.presentationRequest,
            compatibleCredential = MockCompatibleCredentials.compatibleCredential,
        )

        result.assertErrorType(PresentationRequestError.ValidationError::class)

        coVerifyOrder {
            mockCredentialRawRepo.getByCredentialId(any())
            mockCreateDisclosedSdJwt.invoke(any(), any())
            mockCreateVerifiablePresentationToken.invoke(any(), any(), any(), any())
            mockCreatePresentationRequestBody.invoke(any(), any(), any())
            mockSendPresentation.invoke(any(), any())
        }
    }

    @Test
    fun `submitting a presentation where the response uri is invalid should fail`() = runTest(testDispatcher) {
        val presentationRequest = MockPresentationValues.presentationRequest.copy(
            responseUri = "thisIsNotAnUrl"
        )

        val result = submitPresentationUseCase(
            presentationRequest = presentationRequest,
            compatibleCredential = MockCompatibleCredentials.compatibleCredential,
        )

        result.assertErrorType(PresentationRequestError.InvalidUrl::class)

        coVerifyOrder {
            mockCredentialRawRepo.getByCredentialId(any())
            mockCreateDisclosedSdJwt.invoke(any(), any())
            mockCreateVerifiablePresentationToken.invoke(any(), any(), any(), any())
            mockCreatePresentationRequestBody.invoke(any(), any(), any())
        }

        coVerify(exactly = 0) {
            mockSendPresentation.invoke(any(), any())
        }
    }

    companion object {
        private val testException = Exception("crash")
    }
}
