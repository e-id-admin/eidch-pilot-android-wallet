package ch.admin.foitt.openid4vc.domain.usecase.implementation

import ch.admin.foitt.openid4vc.domain.model.vcStatus.CredentialStatusResponse
import ch.admin.foitt.openid4vc.domain.model.vcStatus.PrepareStatusListError
import ch.admin.foitt.openid4vc.domain.model.vcStatus.toPrepareStatusListError
import ch.admin.foitt.openid4vc.domain.repository.VCStatusRepository
import ch.admin.foitt.openid4vc.domain.usecase.PrepareStatusList
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.mapError
import com.nimbusds.jwt.SignedJWT
import kotlinx.serialization.json.Json
import java.io.ByteArrayInputStream
import java.net.URL
import java.util.Base64
import java.util.zip.GZIPInputStream
import javax.inject.Inject
import javax.inject.Named

class PrepareStatusListImpl @Inject constructor(
    @Named("jsonSerializer") private val json: Json,
    private val vcStatusRepository: VCStatusRepository,
) : PrepareStatusList {
    override suspend fun invoke(url: URL): Result<ByteArray, PrepareStatusListError> = coroutineBinding {
        val jwt = vcStatusRepository.fetchVCStatus(url).mapError { error ->
            error.toPrepareStatusListError()
        }.bind()

        val payload = SignedJWT.parse(jwt).payload
        val credentialStatusResponse = json.decodeFromString<CredentialStatusResponse>(payload.toString())
        val encodedStatusList = credentialStatusResponse.vc.credentialSubject.encodedList

        val compressedStatusList = runSuspendCatching {
            Base64.getUrlDecoder().decode(encodedStatusList)
        }.mapError { throwable ->
            throwable.toPrepareStatusListError()
        }.bind()

        val decompressedStatusList = runSuspendCatching {
            val byteArrayInputStream = ByteArrayInputStream(compressedStatusList)
            GZIPInputStream(byteArrayInputStream).use { it.readBytes() }
        }.mapError { throwable ->
            throwable.toPrepareStatusListError()
        }.bind()

        decompressedStatusList
    }
}
