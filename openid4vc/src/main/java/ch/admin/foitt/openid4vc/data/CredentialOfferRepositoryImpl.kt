package ch.admin.foitt.openid4vc.data

import ch.admin.foitt.openid4vc.domain.model.HttpErrorBody
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.CredentialOfferError
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.CredentialRequest
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
import ch.admin.foitt.openid4vc.domain.repository.CredentialOfferRepository
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.getOr
import com.github.michaelbull.result.mapError
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.utils.io.errors.IOException
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.net.ssl.SSLHandshakeException

internal class CredentialOfferRepositoryImpl @Inject constructor(private val httpClient: HttpClient) :
    CredentialOfferRepository {

    override suspend fun fetchIssuerCredentialInformation(issuerEndpoint: String) =
        runSuspendCatching<IssuerCredentialInformation> {
            httpClient.get("$issuerEndpoint/.well-known/openid-credential-issuer") {
                contentType(ContentType.Application.Json)
            }.body()
        }.mapError(Throwable::toFetchIssuerCredentialInformationError)

    override suspend fun fetchIssuerConfiguration(issuerEndpoint: String) =
        runSuspendCatching<IssuerConfiguration> {
            httpClient.get("$issuerEndpoint/.well-known/openid-configuration") {
                contentType(ContentType.Application.Json)
            }.body()
        }.mapError(Throwable::toFetchIssuerConfigurationError)

    override suspend fun fetchIssuerPublicKeyInfo(url: String) =
        runSuspendCatching<IssuerPublicKeyInfo> {
            httpClient.get(url) {
                contentType(ContentType.Application.Json)
            }.body()
        }.mapError(Throwable::toFetchIssuerPublicKeyError)

    override suspend fun fetchAccessToken(
        tokenEndpoint: String,
        preAuthorizedCode: String
    ) = runSuspendCatching<TokenResponse> {
        httpClient.post(tokenEndpoint) {
            url {
                parameters.append("grant_type", PRE_AUTHORIZED_KEY)
                parameters.append("pre-authorized_code", preAuthorizedCode)
            }
            contentType(ContentType.Application.Json)
        }.body()
    }.mapError { throwable ->
        when (throwable) {
            is ClientRequestException -> handleClientRequestException(throwable)
            else -> throwable.toFetchVerifiableCredentialError()
        }
    }

    override suspend fun fetchCredential(
        issuerEndpoint: String,
        credentialId: String,
        credentialFormat: String,
        proof: CredentialRequestProof,
        accessToken: String
    ) = runSuspendCatching<CredentialResponse> {
        val definition = CredentialRequest.CredentialDefinition(listOf(credentialId))
        val credentialRequest = CredentialRequest(
            credentialDefinition = definition,
            format = credentialFormat,
            proof = proof,
        )
        httpClient.post(issuerEndpoint) {
            contentType(ContentType.Application.Json)
            header("Authorization", "BEARER $accessToken")
            setBody(credentialRequest)
        }.body()
    }.mapError { throwable ->
        when (throwable) {
            is ClientRequestException -> handleClientRequestException(throwable)
            else -> throwable.toFetchVerifiableCredentialError()
        }
    }

    companion object {
        private const val PRE_AUTHORIZED_KEY =
            "urn:ietf:params:oauth:grant-type:pre-authorized_code"
    }
}

private suspend fun handleClientRequestException(clientRequestException: ClientRequestException): FetchVerifiableCredentialError =
    when (clientRequestException.response.status) {
        HttpStatusCode.BadRequest -> parseError(clientRequestException)
        else -> CredentialOfferError.InvalidCredentialOffer
    }

private suspend fun parseError(clientRequestException: ClientRequestException): FetchVerifiableCredentialError =
    runSuspendCatching {
        val errorBodyString = clientRequestException.response.bodyAsText()
        val errorBody = Json.decodeFromString<HttpErrorBody>(errorBodyString)
        handleErrorBody(errorBody)
    }.getOr(CredentialOfferError.InvalidCredentialOffer)

private fun handleErrorBody(errorBody: HttpErrorBody): FetchVerifiableCredentialError = when (errorBody.error) {
    "invalid_grant" -> CredentialOfferError.InvalidGrant
    else -> CredentialOfferError.InvalidCredentialOffer
}

private fun Throwable.toFetchVerifiableCredentialError(): FetchVerifiableCredentialError =
    when (this) {
        is SSLHandshakeException -> CredentialOfferError.CertificateNotPinnedInfoError
        is IOException -> CredentialOfferError.NetworkInfoError
        else -> CredentialOfferError.Unexpected(this)
    }

private fun Throwable.toFetchIssuerCredentialInformationError(): FetchIssuerCredentialInformationError =
    when (this) {
        is SSLHandshakeException -> CredentialOfferError.CertificateNotPinnedInfoError
        is IOException -> CredentialOfferError.NetworkInfoError
        else -> CredentialOfferError.Unexpected(this)
    }

private fun Throwable.toFetchIssuerConfigurationError(): FetchIssuerConfigurationError =
    when (this) {
        is SSLHandshakeException -> CredentialOfferError.CertificateNotPinnedInfoError
        is IOException -> CredentialOfferError.NetworkInfoError
        else -> CredentialOfferError.Unexpected(this)
    }

private fun Throwable.toFetchIssuerPublicKeyError(): FetchIssuerPublicKeyInfoError =
    when (this) {
        is SSLHandshakeException -> CredentialOfferError.CertificateNotPinnedInfoError
        is IOException -> CredentialOfferError.NetworkInfoError
        else -> CredentialOfferError.Unexpected(this)
    }
