package ch.admin.foitt.openid4vc.domain.usecase.implementation

import ch.admin.foitt.openid4vc.domain.model.credentialoffer.CredentialOfferError
import ch.admin.foitt.openid4vc.domain.usecase.CreateDidJwk
import ch.admin.foitt.openid4vc.domain.usecase.implementation.mock.MockKeyPairs.UNSUPPORTED_KEY_PAIR
import ch.admin.foitt.openid4vc.domain.usecase.implementation.mock.MockKeyPairs.VALID_KEY_PAIR
import ch.admin.foitt.openid4vc.domain.usecase.implementation.mock.MockSupportedCredential.SUPPORTED_CRYPTOGRAPHIC_BINDING_METHOD
import ch.admin.foitt.openid4vc.domain.usecase.implementation.mock.MockSupportedCredential.supportedCredential
import ch.admin.foitt.openid4vc.domain.usecase.implementation.mock.MockSupportedCredential.supportedCredentialWithMultipleCryptographicBindingMethods
import ch.admin.foitt.openid4vc.domain.usecase.implementation.mock.MockSupportedCredential.supportedCredentialWithoutCryptographicBindingMethods
import ch.admin.foitt.openid4vc.util.assertErrorType
import ch.admin.foitt.openid4vc.util.assertOk
import com.nimbusds.jose.JWSAlgorithm
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CreateDidJwkImplTest {

    private lateinit var createDidJwk: CreateDidJwk

    @BeforeEach
    fun setUp() {
        createDidJwk = CreateDidJwkImpl()
    }

    @Test
    fun `use did jwk cryptographic binding method when none is given`() = runTest {
        val keyPair = VALID_KEY_PAIR

        val result = createDidJwk(
            supportedCredential = supportedCredentialWithoutCryptographicBindingMethods,
            keyPair = keyPair,
        ).assertOk()

        assertTrue(result.startsWith("did:jwk"))
    }

    @Test
    fun `use first cryptographic binding method when multiple are available`() = runTest {
        val keyPair = VALID_KEY_PAIR

        val result = createDidJwk(
            supportedCredential = supportedCredentialWithMultipleCryptographicBindingMethods,
            keyPair = keyPair,
        ).assertOk()

        assertTrue(result.startsWith(SUPPORTED_CRYPTOGRAPHIC_BINDING_METHOD))
    }

    @Test
    fun `creating a did jwk with an unsupported key pair should return an invalid cryptographic suite error`() = runTest {
        val keyPair = UNSUPPORTED_KEY_PAIR

        createDidJwk(
            supportedCredential = supportedCredential,
            keyPair = keyPair,
        ).assertErrorType(
            CredentialOfferError.UnsupportedCryptographicSuite::class
        )
    }

    @Test
    fun `creating a did jwk with a wrong curve should return an unexpected error`() = runTest {
        val keyPair = VALID_KEY_PAIR.copy(jwsAlgorithm = JWSAlgorithm.ES256)

        createDidJwk(
            supportedCredential = supportedCredential,
            keyPair = keyPair,
        ).assertErrorType(
            CredentialOfferError.Unexpected::class
        )
    }
}
