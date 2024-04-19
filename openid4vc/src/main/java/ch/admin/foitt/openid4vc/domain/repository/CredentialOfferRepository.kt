package ch.admin.foitt.openid4vc.domain.repository

import androidx.annotation.CheckResult
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.CredentialRequestProof
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.CredentialResponse
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.FetchIssuerConfigurationError
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.FetchIssuerCredentialInformationError
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.FetchIssuerPublicKeyInfoError
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.FetchVerifiableCredentialError
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.TokenResponse
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.IssuerConfiguration
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.IssuerCredentialInformation
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.IssuerPublicKeyInfo
import com.github.michaelbull.result.Result

internal interface CredentialOfferRepository {

    @CheckResult
    suspend fun fetchIssuerCredentialInformation(
        issuerEndpoint: String
    ): Result<IssuerCredentialInformation, FetchIssuerCredentialInformationError>

    @CheckResult
    suspend fun fetchIssuerConfiguration(issuerEndpoint: String): Result<IssuerConfiguration, FetchIssuerConfigurationError>

    @CheckResult
    suspend fun fetchIssuerPublicKeyInfo(url: String): Result<IssuerPublicKeyInfo, FetchIssuerPublicKeyInfoError>

    @CheckResult
    suspend fun fetchAccessToken(
        tokenEndpoint: String,
        preAuthorizedCode: String
    ): Result<TokenResponse, FetchVerifiableCredentialError>

    @CheckResult
    suspend fun fetchCredential(
        issuerEndpoint: String,
        credentialId: String,
        credentialFormat: String,
        proof: CredentialRequestProof,
        accessToken: String
    ): Result<CredentialResponse, FetchVerifiableCredentialError>
}
