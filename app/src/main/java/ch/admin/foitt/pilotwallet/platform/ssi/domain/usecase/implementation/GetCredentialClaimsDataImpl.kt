package ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.implementation

import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialClaim
import ch.admin.foitt.pilotwallet.platform.sdjwt.domain.model.ParsedSdJwt
import ch.admin.foitt.pilotwallet.platform.sdjwt.domain.usecase.ParseSdJwt
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.CredentialClaimData
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.GetCredentialClaimDataError
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.toGetCredentialClaimDataError
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.GetCredentialClaimData
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.GetCredentialClaims
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.GetCredentialClaimsData
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.GetCredentialRaw
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.getOr
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.mapOr
import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.JsonPath
import kotlinx.serialization.json.JsonPrimitive
import javax.inject.Inject

class GetCredentialClaimsDataImpl @Inject constructor(
    private val getCredentialClaims: GetCredentialClaims,
    private val getCredentialClaimData: GetCredentialClaimData,
    private val getCredentialRaw: GetCredentialRaw,
    private val parseSdJwt: ParseSdJwt,
) : GetCredentialClaimsData {
    override suspend fun invoke(credentialId: Long): Result<List<CredentialClaimData>, GetCredentialClaimDataError> = coroutineBinding {
        val claims = getCredentialClaims(credentialId)
            .mapError { error ->
                error.toGetCredentialClaimDataError()
            }.bind()
        claims
            .filterPoliceQRImageForELFA(credentialId)
            .map { credentialClaim ->
                getCredentialClaimData(credentialClaim).bind()
            }
    }

    private suspend fun List<CredentialClaim>.filterPoliceQRImageForELFA(credentialId: Long): List<CredentialClaim> {
        val credentialRaw = getCredentialRaw(credentialId)
        val isELFA = credentialRaw.any {
            parseSdJwt(it.payload).mapOr(false) { sdJwt ->
                "ELFA" in parseTypes(sdJwt)
            }
        }
        return if (isELFA) {
            filterNot { it.key == "policeQRImage" }
        } else {
            this
        }
    }

    private fun parseTypes(sdJwt: ParsedSdJwt): List<String> =
        runSuspendCatching {
            JsonPath.using(Configuration.defaultConfiguration())
                .parse(sdJwt.jsonWithActualValues)
                .read<List<JsonPrimitive>?>("$.vc.type") ?: emptyList()
        }.getOr(emptyList())
            .map { it.content }
}
