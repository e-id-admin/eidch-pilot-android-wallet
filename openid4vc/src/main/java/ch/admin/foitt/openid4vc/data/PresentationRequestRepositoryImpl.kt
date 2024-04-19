package ch.admin.foitt.openid4vc.data

import ch.admin.foitt.openid4vc.domain.model.HttpErrorBody
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.FetchPresentationRequestError
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.PresentationRequest
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.PresentationRequestBody
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.PresentationRequestError
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.PresentationRequestErrorBody
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.SendPresentationError
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.SubmitPresentationErrorError
import ch.admin.foitt.openid4vc.domain.repository.PresentationRequestRepository
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.mapError
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.parameters
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException
import java.net.URL
import javax.inject.Inject
import javax.inject.Named
import javax.net.ssl.SSLHandshakeException

internal class PresentationRequestRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
    @Named("jsonSerializer") private val json: Json,
) : PresentationRequestRepository {
    override suspend fun fetchPresentationRequest(url: URL) =
        runSuspendCatching<PresentationRequest> {
            httpClient.get(url) {
                contentType(ContentType.Application.Json)
            }.body()
        }.mapError(Throwable::toFetchPresentationRequestError)

    override suspend fun submitPresentation(
        url: URL,
        presentationRequestBody: PresentationRequestBody,
    ) = runSuspendCatching<Unit> {
        val jsonString = json.encodeToString(presentationRequestBody.presentationSubmission)
        httpClient.submitForm(
            url = url.toExternalForm(),
            formParameters = parameters {
                append("vp_token", presentationRequestBody.vpToken)
                append("presentation_submission", jsonString)
            }
        )
    }.mapError { throwable ->
        when (throwable) {
            is ClientRequestException -> handleClientRequestException(throwable)
            else -> throwable.toSubmitPresentationError()
        }
    }

    override suspend fun submitPresentationError(
        url: String,
        body: PresentationRequestErrorBody,
    ) = runSuspendCatching<Unit> {
        httpClient.submitForm(
            url = url,
            formParameters = parameters {
                append("error", body.error.key)
                body.errorDescription?.let { append("error_description", body.errorDescription) }
            }
        )
    }.mapError(Throwable::toSubmitPresentationErrorError)
}

private fun Throwable.toFetchPresentationRequestError(): FetchPresentationRequestError = when (this) {
    is SSLHandshakeException -> PresentationRequestError.CertificateNotPinnedError
    is IOException -> PresentationRequestError.NetworkError
    else -> PresentationRequestError.Unexpected(this)
}

private fun Throwable.toSubmitPresentationError(): SendPresentationError =
    when (this) {
        is SSLHandshakeException -> PresentationRequestError.CertificateNotPinnedError
        is IOException -> PresentationRequestError.NetworkError
        else -> PresentationRequestError.Unexpected(this)
    }

private suspend fun handleClientRequestException(clientRequestException: ClientRequestException): SendPresentationError =
    when (clientRequestException.response.status) {
        HttpStatusCode.BadRequest -> parseError(clientRequestException)
        else -> PresentationRequestError.Unexpected(clientRequestException)
    }

private suspend fun parseError(clientRequestException: ClientRequestException): SendPresentationError =
    try {
        val errorBodyString = clientRequestException.response.bodyAsText()
        val errorBody = Json.decodeFromString<HttpErrorBody>(errorBodyString)
        when {
            errorBody.isValidationError() -> PresentationRequestError.ValidationError
            else -> PresentationRequestError.Unexpected(clientRequestException)
        }
    } catch (e: Exception) {
        PresentationRequestError.Unexpected(e)
    }

private fun HttpErrorBody.isValidationError(): Boolean =
    error == "invalid_request" && (errorCode == "credential_revoked" || errorCode == "credential_suspended")

private fun Throwable.toSubmitPresentationErrorError(): SubmitPresentationErrorError = when (this) {
    is SSLHandshakeException -> PresentationRequestError.CertificateNotPinnedError
    is IOException -> PresentationRequestError.NetworkError
    else -> PresentationRequestError.Unexpected(this)
}
