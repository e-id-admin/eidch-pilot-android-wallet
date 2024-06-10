package ch.admin.foitt.pilotwallet.platform.credential.domain.usecase.implementation

import ch.admin.foitt.openid4vc.domain.usecase.FetchIssuerPublicKeyInfo
import ch.admin.foitt.pilotwallet.platform.credential.domain.model.CredentialOfferError
import ch.admin.foitt.pilotwallet.platform.credential.domain.usecase.VerifyJwt
import ch.admin.foitt.pilotwallet.platform.credential.domain.usecase.VerifyJwtTimestamps
import ch.admin.foitt.pilotwallet.platform.credential.domain.usecase.implementation.mock.JwtTimestamps
import ch.admin.foitt.pilotwallet.platform.credential.domain.usecase.implementation.mock.MockIssuer
import ch.admin.foitt.pilotwallet.util.assertOk
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.getError
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.CredentialOfferError as OpenIdCredentialOfferError

class VerifyJwtTest {

    @MockK
    private lateinit var mockFetchIssuerPublicKeyInfo: FetchIssuerPublicKeyInfo

    @MockK
    private lateinit var mockVerifyJwtTimestamps: VerifyJwtTimestamps

    private lateinit var verifyJwt: VerifyJwt

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        verifyJwt = VerifyJwtImpl(
            mockFetchIssuerPublicKeyInfo,
            mockVerifyJwtTimestamps,
        )

        coEvery {
            mockVerifyJwtTimestamps(any())
        } returns Ok(true)
    }

    @Test
    fun `JWT signed by valid issuer, verified against valid issuer public key returns true`() = runTest {
        val issuerKeyPair = MockIssuer.createValidKeyPair()
        val issuerPublicKeyInfo = MockIssuer.createPublicKeyInfo(issuerKeyPair)
        val validSignedJwt = MockIssuer.issueCredential(
            keyPair = issuerKeyPair,
            payload = "credential"
        )

        coEvery {
            mockFetchIssuerPublicKeyInfo(any())
        } returns Ok(issuerPublicKeyInfo)

        val result = verifyJwt("", validSignedJwt).assertOk()
        assertTrue(result)
    }

    @Test
    fun `JWT signature does not match the issuer public key, returns false`() = runTest {
        val issuerKeyPair = MockIssuer.createValidKeyPair()
        val issuerPublicKeyInfo = MockIssuer.createPublicKeyInfo(issuerKeyPair)

        val maliciousIssuerKeyPair = MockIssuer.createValidKeyPair()

        val validSignedJwt = MockIssuer.issueCredential(
            keyPair = maliciousIssuerKeyPair,
            payload = "credential"
        )

        coEvery {
            mockFetchIssuerPublicKeyInfo(any())
        } returns Ok(issuerPublicKeyInfo)

        val result = verifyJwt("", validSignedJwt).assertOk()
        assertFalse(result)
    }

    @Test
    fun `error during fetch of issuer public key config returns error`() = runTest {
        coEvery {
            mockFetchIssuerPublicKeyInfo(any())
        } returns Err(OpenIdCredentialOfferError.Unexpected(null))

        val result = verifyJwt("", JwtTimestamps.VALID_JWT)

        assertNotNull(result.getError())
        assertTrue(result.getError() is CredentialOfferError.Unexpected)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }
}
