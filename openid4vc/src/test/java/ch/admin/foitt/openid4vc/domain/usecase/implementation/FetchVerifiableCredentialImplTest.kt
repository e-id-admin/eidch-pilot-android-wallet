package ch.admin.foitt.openid4vc.domain.usecase.implementation

import android.annotation.SuppressLint
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.CredentialOfferError
import ch.admin.foitt.openid4vc.domain.repository.CredentialOfferRepository
import ch.admin.foitt.openid4vc.domain.usecase.CreateCredentialRequestProofJwt
import ch.admin.foitt.openid4vc.domain.usecase.CreateDidJwk
import ch.admin.foitt.openid4vc.domain.usecase.DeleteKeyPair
import ch.admin.foitt.openid4vc.domain.usecase.GenerateKeyPair
import ch.admin.foitt.openid4vc.domain.usecase.implementation.mock.MockCredentialOffer.jwtProof
import ch.admin.foitt.openid4vc.domain.usecase.implementation.mock.MockCredentialOffer.offerWithEmptyCredentials
import ch.admin.foitt.openid4vc.domain.usecase.implementation.mock.MockCredentialOffer.offerWithPreAuthorizedCode
import ch.admin.foitt.openid4vc.domain.usecase.implementation.mock.MockCredentialOffer.offerWithUnsupportedCredentialType
import ch.admin.foitt.openid4vc.domain.usecase.implementation.mock.MockCredentialOffer.offerWithoutPreAuthorizedCode
import ch.admin.foitt.openid4vc.domain.usecase.implementation.mock.MockCredentialOffer.validCredentialResponse
import ch.admin.foitt.openid4vc.domain.usecase.implementation.mock.MockCredentialOffer.validIssuerConfig
import ch.admin.foitt.openid4vc.domain.usecase.implementation.mock.MockCredentialOffer.validIssuerCredentialInformation
import ch.admin.foitt.openid4vc.domain.usecase.implementation.mock.MockCredentialOffer.validIssuerCredentialInformationWithUnsupportedProofType
import ch.admin.foitt.openid4vc.domain.usecase.implementation.mock.MockCredentialOffer.validTokenResponse
import ch.admin.foitt.openid4vc.domain.usecase.implementation.mock.MockCredentialOffer.validVerifiableCredential
import ch.admin.foitt.openid4vc.domain.usecase.implementation.mock.MockKeyPairs.VALID_KEY_PAIR
import ch.admin.foitt.openid4vc.util.assertErrorType
import ch.admin.foitt.openid4vc.util.assertOk
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.mockk.MockKAnnotations
import io.mockk.Ordering
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class FetchVerifiableCredentialImplTest {

    @MockK
    private lateinit var mockCredentialOfferRepository: CredentialOfferRepository

    @MockK
    private lateinit var mockGenerateKeyPair: GenerateKeyPair

    @MockK
    private lateinit var mockCreateDidJwk: CreateDidJwk

    @MockK
    private lateinit var mockCreateCredentialRequestProofJwt: CreateCredentialRequestProofJwt

    @MockK
    private lateinit var mockDeleteKeyPair: DeleteKeyPair

    private lateinit var fetchCredentialUseCase: FetchVerifiableCredentialImpl

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        success()

        fetchCredentialUseCase = FetchVerifiableCredentialImpl(
            mockCredentialOfferRepository,
            mockGenerateKeyPair,
            mockCreateDidJwk,
            mockCreateCredentialRequestProofJwt,
            mockDeleteKeyPair,
        )
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @SuppressLint("CheckResult")
    @Test
    fun `valid credential offer returns a verifiable credential`() = runTest {
        val credential = fetchCredentialUseCase(
            offerWithPreAuthorizedCode,
            validIssuerConfig,
            validIssuerCredentialInformation,
        ).assertOk()

        assertEquals(validVerifiableCredential, credential)

        coVerify(ordering = Ordering.SEQUENCE) {
            mockGenerateKeyPair(any())
            mockCreateDidJwk(any(), any())
            mockCredentialOfferRepository.fetchAccessToken(any(), any())
            mockCreateCredentialRequestProofJwt(any(), any(), any(), any())
            mockCredentialOfferRepository.fetchCredential(any(), any(), any(), any(), any())
        }

        coVerify(exactly = 0) {
            mockDeleteKeyPair(any())
        }
    }

    @SuppressLint("CheckResult")
    @Test
    fun `credential offer with empty credentials returns unsupported credential type error, access token not fetched`() = runTest {
        fetchCredentialUseCase(
            offerWithEmptyCredentials,
            validIssuerConfig,
            validIssuerCredentialInformation,
        ).assertErrorType(CredentialOfferError.UnsupportedCredentialType::class)

        coVerify(exactly = 0) {
            mockCredentialOfferRepository.fetchAccessToken(any(), any())
        }
    }

    @SuppressLint("CheckResult")
    @Test
    fun `credential offer with unsupported credential type should return an invalid credential type error, access token not fetched`() = runTest {
        fetchCredentialUseCase(
            offerWithUnsupportedCredentialType,
            validIssuerConfig,
            validIssuerCredentialInformation,
        ).assertErrorType(CredentialOfferError.UnsupportedCredentialType::class)

        coVerify(exactly = 0) {
            mockCredentialOfferRepository.fetchAccessToken(any(), any())
        }
    }

    @SuppressLint("CheckResult")
    @Test
    fun `when the proof type is not supported return an unsupported proof type error, access token not fetched`() = runTest {
        fetchCredentialUseCase(
            offerWithPreAuthorizedCode,
            validIssuerConfig,
            validIssuerCredentialInformationWithUnsupportedProofType,
        ).assertErrorType(CredentialOfferError.UnsupportedProofType::class)

        coVerify(exactly = 0) {
            mockCredentialOfferRepository.fetchAccessToken(any(), any())
        }
    }

    @SuppressLint("CheckResult")
    @Test
    fun `when generating the key pair fails return an unsupported cryptographic suite error, access token not fetched`() = runTest {
        coEvery {
            mockGenerateKeyPair(any())
        } returns Err(CredentialOfferError.UnsupportedCryptographicSuite)

        fetchCredentialUseCase(
            offerWithPreAuthorizedCode,
            validIssuerConfig,
            validIssuerCredentialInformation,
        ).assertErrorType(CredentialOfferError.UnsupportedCryptographicSuite::class)

        coVerify(exactly = 0) {
            mockCredentialOfferRepository.fetchAccessToken(any(), any())
        }
    }

    @SuppressLint("CheckResult")
    @Test
    fun `when creating the did jwk fails return an unsupported cryptographic suite error and delete the key pair, token not fetched`() = runTest {
        coEvery {
            mockCreateDidJwk(any(), any())
        } returns Err(CredentialOfferError.UnsupportedCryptographicSuite)

        fetchCredentialUseCase(
            offerWithPreAuthorizedCode,
            validIssuerConfig,
            validIssuerCredentialInformation,
        ).assertErrorType(CredentialOfferError.UnsupportedCryptographicSuite::class)

        coVerify {
            mockDeleteKeyPair(any())
        }

        coVerify(exactly = 0) {
            mockCredentialOfferRepository.fetchAccessToken(any(), any())
        }
    }

    @SuppressLint("CheckResult")
    @Test
    fun `credential offer without pre-authorized code should return an unsupported grant type error and delete the key pair, token not fetched`() = runTest {
        fetchCredentialUseCase(
            offerWithoutPreAuthorizedCode,
            validIssuerConfig,
            validIssuerCredentialInformation,
        ).assertErrorType(CredentialOfferError.UnsupportedGrantType::class)

        coVerify {
            mockDeleteKeyPair(any())
        }

        coVerify(exactly = 0) {
            mockCredentialOfferRepository.fetchAccessToken(any(), any())
        }
    }

    @SuppressLint("CheckResult")
    @Test
    fun `when fetching the token fails return an invalid credential offer error and delete the key pair`() = runTest {
        coEvery {
            mockCredentialOfferRepository.fetchAccessToken(any(), any())
        } returns Err(CredentialOfferError.InvalidCredentialOffer)

        fetchCredentialUseCase(
            offerWithPreAuthorizedCode,
            validIssuerConfig,
            validIssuerCredentialInformation,
        ).assertErrorType(CredentialOfferError.InvalidCredentialOffer::class)

        coVerify {
            mockDeleteKeyPair(any())
        }
    }

    @SuppressLint("CheckResult")
    @Test
    fun `if an error is thrown when creating a proof, it should return an unexpected error and delete the key pair`() = runTest {
        coEvery {
            mockCreateCredentialRequestProofJwt(any(), any(), any(), any())
        } returns Err(CredentialOfferError.Unexpected(IllegalStateException()))

        fetchCredentialUseCase(
            offerWithPreAuthorizedCode,
            validIssuerConfig,
            validIssuerCredentialInformation,
        ).assertErrorType(CredentialOfferError.Unexpected::class)

        coVerify {
            mockDeleteKeyPair(any())
        }
    }

    @SuppressLint("CheckResult")
    @Test
    fun `failed fetch of credential should return a network error and delete the key pair`() = runTest {
        coEvery {
            mockCredentialOfferRepository.fetchCredential(any(), any(), any(), any(), any())
        } returns Err(CredentialOfferError.NetworkInfoError)

        fetchCredentialUseCase(
            offerWithPreAuthorizedCode,
            validIssuerConfig,
            validIssuerCredentialInformation,
        ).assertErrorType(CredentialOfferError.NetworkInfoError::class)

        coVerify {
            mockDeleteKeyPair(any())
        }
    }

    private fun success() {
        coEvery {
            mockCredentialOfferRepository.fetchIssuerConfiguration(any())
        } returns Ok(validIssuerConfig)

        coEvery {
            mockCredentialOfferRepository.fetchIssuerCredentialInformation(any())
        } returns Ok(validIssuerCredentialInformation)

        coEvery {
            mockGenerateKeyPair(any())
        } returns Ok(VALID_KEY_PAIR)

        coEvery {
            mockCreateDidJwk(any(), any())
        } returns Ok("did:jwk:publicKey")

        coEvery {
            mockCreateCredentialRequestProofJwt(any(), any(), any(), any())
        } returns Ok(jwtProof)

        coEvery {
            mockCredentialOfferRepository.fetchAccessToken(any(), any())
        } returns Ok(validTokenResponse)

        coEvery {
            mockCredentialOfferRepository.fetchCredential(any(), any(), any(), any(), any())
        } returns Ok(validCredentialResponse)

        coEvery {
            mockDeleteKeyPair(any())
        } returns Ok(Unit)
    }
}
