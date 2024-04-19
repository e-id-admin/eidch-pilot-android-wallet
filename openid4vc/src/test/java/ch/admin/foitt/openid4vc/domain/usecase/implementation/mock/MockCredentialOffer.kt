package ch.admin.foitt.openid4vc.domain.usecase.implementation.mock

import ch.admin.foitt.openid4vc.domain.model.VerifiableCredential
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.CredentialOffer
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.CredentialRequestProofJwt
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.CredentialResponse
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.Grant
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.JWSKeyPair
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.PreAuthorizedContent
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.TokenResponse
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.IssuerConfiguration
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.IssuerCredentialInformation
import ch.admin.foitt.openid4vc.domain.usecase.implementation.mock.MockSupportedCredential.supportedCredential
import ch.admin.foitt.openid4vc.domain.usecase.implementation.mock.MockSupportedCredential.supportedCredentialWithOtherProofType
import com.nimbusds.jose.JWSAlgorithm
import io.mockk.mockk
import java.security.KeyPair

internal object MockCredentialOffer {
    const val CREDENTIAL_ISSUER = "credentialIssuer"
    private const val CREDENTIAL_TYPE = "credentialType"
    private val CREDENTIALS = listOf(CREDENTIAL_TYPE)
    private const val PRE_AUTHORIZED_CODE = "preAuthorizedCode"
    val offerWithPreAuthorizedCode = CredentialOffer(
        credentialIssuer = CREDENTIAL_ISSUER,
        credentials = CREDENTIALS,
        grants = Grant(PreAuthorizedContent(PRE_AUTHORIZED_CODE))
    )
    val offerWithoutPreAuthorizedCode = offerWithPreAuthorizedCode.copy(grants = Grant())
    val offerWithUnsupportedCredentialType =
        offerWithPreAuthorizedCode.copy(credentials = listOf("otherCredentialType"))
    val offerWithEmptyCredentials =
        offerWithPreAuthorizedCode.copy(credentials = listOf())

    private const val ACCESS_TOKEN = "accessToken"
    const val C_NONCE = "cNonce"
    private const val C_NONCE_EXPIRES_IN = 1
    private const val EXPIRES_IN = 2
    private const val TOKEN_TYPE = "tokenType"
    val validTokenResponse = TokenResponse(
        accessToken = ACCESS_TOKEN,
        cNonce = C_NONCE,
        cNonceExpiresIn = C_NONCE_EXPIRES_IN,
        expiresIn = EXPIRES_IN,
        tokenType = TOKEN_TYPE
    )

    private const val TOKEN_ENDPOINT = "tokenEndpoint"
    private const val JWKS_URI = "jwks_uri"
    private const val AUTHORIZATION_ENDPOINT = "authorizationEndpoint"
    private const val PUSHED_AUTHORIZATION_REQUEST_ENDPOINT = "pushedAuthorizationRequestEndpoint"
    private const val IS_REQUEST_URI_PARAMETER_SUPPORTED = true
    val validIssuerConfig = IssuerConfiguration(
        issuer = CREDENTIAL_ISSUER,
        jwksUri = JWKS_URI,
        authorizationEndpoint = AUTHORIZATION_ENDPOINT,
        tokenEndpoint = TOKEN_ENDPOINT,
        responseTypesSupported = listOf(),
        supportedSigningAlgorithms = listOf(),
        pushedAuthorizationRequestEndpoint = PUSHED_AUTHORIZATION_REQUEST_ENDPOINT,
        isRequestUriParameterSupported = IS_REQUEST_URI_PARAMETER_SUPPORTED
    )

    private const val CREDENTIAL_ENDPOINT = "credentialEndpoint"
    val validIssuerCredentialInformation = IssuerCredentialInformation(
        credentialEndpoint = CREDENTIAL_ENDPOINT,
        credentialIssuer = CREDENTIAL_ISSUER,
        supportedCredentials = mapOf(CREDENTIAL_TYPE to supportedCredential),
        display = listOf()
    )
    val validIssuerCredentialInformationWithUnsupportedProofType = validIssuerCredentialInformation.copy(
        supportedCredentials = mapOf(CREDENTIAL_TYPE to supportedCredentialWithOtherProofType)
    )

    const val KEY_ID = "keyId"
    private const val PROOF_JWT = "proofJwt"
    val jwtProof = CredentialRequestProofJwt(PROOF_JWT)

    private val JWS_ALGORITHM = JWSAlgorithm.ES512
    private val mockKeyPair = mockk<KeyPair>()

    val validKeyPair = JWSKeyPair(
        jwsAlgorithm = JWS_ALGORITHM,
        keyPair = mockKeyPair,
        keyId = KEY_ID,
    )

    private const val CREDENTIAL = "credential"
    const val JWT_VC_FORMAT = "jwt_vc"
    val validCredentialResponse = CredentialResponse(
        credential = CREDENTIAL,
        format = JWT_VC_FORMAT
    )

    val validVerifiableCredential = VerifiableCredential(
        format = JWT_VC_FORMAT,
        credential = CREDENTIAL,
        signingKeyId = KEY_ID
    )
}
