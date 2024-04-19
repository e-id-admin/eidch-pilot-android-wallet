package ch.admin.foitt.openid4vc.data

import ch.admin.foitt.openid4vc.domain.model.vcStatus.VCStatusError
import ch.admin.foitt.openid4vc.domain.repository.VCStatusRepository
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.mapError
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.contentType
import java.io.IOException
import java.net.URL
import javax.inject.Inject
import javax.net.ssl.SSLHandshakeException

internal class VCStatusRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
) : VCStatusRepository {
    override suspend fun fetchVCStatus(url: URL): Result<String, VCStatusError> =
        runSuspendCatching<String> {
            httpClient.get(url) {
                contentType(ContentType.Application.Json)
            }.body()
        }.mapError(Throwable::toFetchVCStatusError)
}

private fun Throwable.toFetchVCStatusError(): VCStatusError = when (this) {
    is SSLHandshakeException -> VCStatusError.CertificateNotPinnedError
    is IOException -> VCStatusError.NetworkError
    else -> VCStatusError.Unexpected(this)
}
