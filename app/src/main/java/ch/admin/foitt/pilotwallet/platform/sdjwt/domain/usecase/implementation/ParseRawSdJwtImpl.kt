package ch.admin.foitt.pilotwallet.platform.sdjwt.domain.usecase.implementation

import ch.admin.foitt.openid4vc.di.DefaultDispatcher
import ch.admin.foitt.pilotwallet.platform.sdjwt.domain.model.ParseRawSdJwtError
import ch.admin.foitt.pilotwallet.platform.sdjwt.domain.model.SdJwt
import ch.admin.foitt.pilotwallet.platform.sdjwt.domain.model.SdJwtClaim
import ch.admin.foitt.pilotwallet.platform.sdjwt.domain.usecase.ParseRawSdJwt
import ch.admin.foitt.pilotwallet.platform.utils.base64StringToByteArray
import ch.admin.foitt.pilotwallet.platform.utils.toBase64String
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.mapError
import com.nimbusds.jwt.SignedJWT
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import timber.log.Timber
import java.security.MessageDigest
import javax.inject.Inject

internal class ParseRawSdJwtImpl @Inject constructor(
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : ParseRawSdJwt {
    override suspend fun invoke(sdJwt: String): Result<SdJwt, ParseRawSdJwtError> =
        withContext(defaultDispatcher) {
            coroutineBinding {
                val (jwt, disclosures) = parseSdJwt(sdJwt).bind()
                val signedJwt = parseJwt(jwt).bind()
                val digestAlgorithm = parseDigestAlgorithm(signedJwt.payload.toString()).bind()
                val claims = parseDisclosedClaims(disclosures, digestAlgorithm).bind()
                SdJwt(
                    signedJwt = signedJwt,
                    claims = claims
                )
            }
        }

    private fun parseSdJwt(sdJwt: String): Result<Pair<String, String?>, ParseRawSdJwtError> = runSuspendCatching {
        val matchResult = Regex(SD_JWT_PATTERN).matchEntire(sdJwt)
        matchResult?.groups?.let { groupCollection ->
            val groups = groupCollection as MatchNamedGroupCollection
            val jwt = groups[JWT]?.value ?: ""
            val disclosures = groups[DISCLOSURES]?.value
            jwt to disclosures
        } ?: ("" to null)
    }.mapError { throwable ->
        Timber.e(throwable)
        ParseRawSdJwtError.Unexpected(throwable)
    }

    private fun parseJwt(signedJwt: String): Result<SignedJWT, ParseRawSdJwtError> = runSuspendCatching {
        SignedJWT.parse(signedJwt)
    }.mapError { throwable ->
        Timber.e(throwable)
        ParseRawSdJwtError.InvalidJwt
    }

    private fun parseDigestAlgorithm(jwt: String): Result<String, ParseRawSdJwtError> = runSuspendCatching {
        val algorithm = Json.parseToJsonElement(jwt).jsonObject[ALGORITHM_KEY]
        algorithm?.jsonPrimitive?.content ?: "SHA-256"
    }.mapError { throwable ->
        Timber.e(throwable)
        ParseRawSdJwtError.Unexpected(throwable)
    }

    private suspend fun parseDisclosedClaims(
        disclosuresString: String?,
        digestAlgorithm: String
    ): Result<Map<String, SdJwtClaim>, ParseRawSdJwtError> = coroutineBinding {
        val disclosures = disclosuresString?.trim(SD_JWT_SEPARATOR)?.split(SD_JWT_SEPARATOR) ?: emptyList()
        disclosures.toSet().associate { disclosure ->
            val digest = disclosure.createDigest(digestAlgorithm)
            val (key, value) = parseDisclosure(disclosure).bind()
            val claim = SdJwtClaim(
                key = key,
                value = value,
                disclosure = disclosure,
            )
            digest to claim
        }
    }

    private fun String.createDigest(algorithm: String): String {
        val digest = MessageDigest.getInstance(algorithm)
        val bytes = digest.digest(toByteArray(Charsets.US_ASCII))
        return bytes.toBase64String()
    }

    private fun parseDisclosure(disclosure: String): Result<Pair<String, JsonElement>, ParseRawSdJwtError> {
        val decoded = disclosure.base64StringToByteArray()
        val jsonString = String(decoded)
        val array = Json.parseToJsonElement(jsonString).jsonArray
        if (array.size != 3) {
            return Err(ParseRawSdJwtError.InvalidDisclosure)
        }
        return Ok(array[1].jsonPrimitive.content to array[2])
    }

    companion object {
        private const val JWT = "jwt"
        private const val ALGORITHM_KEY = "_sd_alg"
        private const val DISCLOSURES = "disclosures"
        private const val KEYBINDING_JWT = "keyBindingJwt"
        private const val SD_JWT_SEPARATOR = '~'
        private const val SD_JWT_PATTERN = "^" +
            "(?<$JWT>(?<header>[A-Za-z0-9-_]+)\\.(?<body>[A-Za-z0-9-_]+)\\.(?<signature>[A-Za-z0-9-_]+))" + // 1 issuer-signed JWT
            "($SD_JWT_SEPARATOR?" + // 0..1 separators
            "(?<$DISCLOSURES>(([A-Za-z0-9-_]+)$SD_JWT_SEPARATOR)+)?" + // 0..* Disclosures + "~"
            "(?<$KEYBINDING_JWT>([A-Za-z0-9-_]+)\\.([A-Za-z0-9-_]+)\\.([A-Za-z0-9-_]+))?" + // 0..1 Key Binding JWT
            ")\$"
    }
}
