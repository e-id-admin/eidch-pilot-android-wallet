package ch.admin.foitt.openid4vc.domain.usecase.implementation.mock

import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.SupportedCredential
import ch.admin.foitt.openid4vc.domain.usecase.implementation.mock.MockCredentialOffer.JWT_VC_FORMAT
import io.mockk.mockk

object MockSupportedCredential {
    const val SUPPORTED_CRYPTOGRAPHIC_SUITE = "ES512"
    const val SUPPORTED_CRYPTOGRAPHIC_BINDING_METHOD = "supportedCryptographicBindingMethod"
    private const val SUPPORTED_PROOF_TYPE = "jwt"
    const val JWT_KID = "did:jwk:base64EncodedPublicKey"
    val supportedCredential = SupportedCredential(
        credentialDefinition = mockk(),
        cryptographicSuitesSupported = listOf(SUPPORTED_CRYPTOGRAPHIC_SUITE),
        cryptographicBindingMethodsSupported = listOf(SUPPORTED_CRYPTOGRAPHIC_BINDING_METHOD),
        format = JWT_VC_FORMAT,
        proofTypesSupported = listOf(SUPPORTED_PROOF_TYPE)
    )
    val supportedCredentialWithMultipleCryptographicSuites = supportedCredential.copy(
        cryptographicSuitesSupported = listOf(
            SUPPORTED_CRYPTOGRAPHIC_SUITE,
            "otherCryptographicSuite"
        ),
    )
    val supportedCredentialWithoutCryptographicSuite = supportedCredential.copy(
        cryptographicSuitesSupported = listOf(),
    )
    val supportedCredentialWithOtherCryptographicSuite = supportedCredential.copy(
        cryptographicSuitesSupported = listOf("otherCryptographicSuite"),
    )
    val supportedCredentialWithMultipleCryptographicBindingMethods = supportedCredential.copy(
        cryptographicBindingMethodsSupported = listOf(
            SUPPORTED_CRYPTOGRAPHIC_BINDING_METHOD,
            "otherCryptographicBindingMethod"
        )
    )
    val supportedCredentialWithoutCryptographicBindingMethods = supportedCredential.copy(
        cryptographicBindingMethodsSupported = listOf()
    )
    val supportedCredentialWithOtherProofType = supportedCredential.copy(
        proofTypesSupported = listOf("otherProofType")
    )
}
