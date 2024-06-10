package ch.admin.foitt.openid4vc.domain.usecase.implementation

import ch.admin.foitt.openid4vc.domain.model.credentialoffer.CredentialOfferError
import ch.admin.foitt.openid4vc.domain.usecase.CreateES512KeyPair
import ch.admin.foitt.openid4vc.domain.usecase.GenerateKeyPair
import ch.admin.foitt.openid4vc.domain.usecase.implementation.mock.MockCredentialOffer.validKeyPair
import ch.admin.foitt.openid4vc.domain.usecase.implementation.mock.MockSupportedCredential.SUPPORTED_CRYPTOGRAPHIC_SUITE
import ch.admin.foitt.openid4vc.domain.usecase.implementation.mock.MockSupportedCredential.supportedCredential
import ch.admin.foitt.openid4vc.domain.usecase.implementation.mock.MockSupportedCredential.supportedCredentialWithMultipleCryptographicSuites
import ch.admin.foitt.openid4vc.domain.usecase.implementation.mock.MockSupportedCredential.supportedCredentialWithOtherCryptographicSuite
import ch.admin.foitt.openid4vc.domain.usecase.implementation.mock.MockSupportedCredential.supportedCredentialWithoutCryptographicSuite
import ch.admin.foitt.openid4vc.util.assertErrorType
import ch.admin.foitt.openid4vc.util.assertOk
import com.github.michaelbull.result.Ok
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GenerateKeyPairImplTest {

    @MockK
    private lateinit var mockCreateES512KeyPair: CreateES512KeyPair

    private lateinit var generateKeyPair: GenerateKeyPair

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        generateKeyPair = GenerateKeyPairImpl(mockCreateES512KeyPair)

        coEvery { mockCreateES512KeyPair(any(), any()) } returns Ok(validKeyPair)
    }

    @Test
    fun `valid supported credential returns key pair and binding method`() = runTest {
        generateKeyPair(supportedCredential).assertOk()

        coVerify(exactly = 1) {
            mockCreateES512KeyPair(any(), any())
        }
    }

    @Test
    fun `use first cryptographic suite when multiple are available`() = runTest {
        val result = generateKeyPair(supportedCredentialWithMultipleCryptographicSuites).assertOk()

        assertEquals(SUPPORTED_CRYPTOGRAPHIC_SUITE, result.jwsAlgorithm.name)

        coVerify(exactly = 1) {
            mockCreateES512KeyPair(any(), any())
        }
    }

    @Test
    fun `no supported cryptographic suite returns an unsupported cryptographic suite error`() = runTest {
        generateKeyPair(
            supportedCredentialWithoutCryptographicSuite
        ).assertErrorType(CredentialOfferError.UnsupportedCryptographicSuite::class)

        coVerify(exactly = 0) {
            mockCreateES512KeyPair(any(), any())
        }
    }

    @Test
    fun `unsupported cryptographic suite returns an unsupported cryptographic suite error`() = runTest {
        generateKeyPair(
            supportedCredentialWithOtherCryptographicSuite
        ).assertErrorType(CredentialOfferError.UnsupportedCryptographicSuite::class)

        coVerify(exactly = 0) {
            mockCreateES512KeyPair(any(), any())
        }
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }
}
