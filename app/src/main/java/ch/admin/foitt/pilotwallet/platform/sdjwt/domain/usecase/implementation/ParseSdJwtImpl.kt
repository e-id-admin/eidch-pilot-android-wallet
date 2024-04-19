package ch.admin.foitt.pilotwallet.platform.sdjwt.domain.usecase.implementation

import ch.admin.foitt.openid4vc.di.DefaultDispatcher
import ch.admin.foitt.pilotwallet.platform.sdjwt.domain.model.ParseSdJwtError
import ch.admin.foitt.pilotwallet.platform.sdjwt.domain.model.ParsedSdJwt
import ch.admin.foitt.pilotwallet.platform.sdjwt.domain.model.SdJwt
import ch.admin.foitt.pilotwallet.platform.sdjwt.domain.model.SdJwtClaim
import ch.admin.foitt.pilotwallet.platform.sdjwt.domain.model.toParseSdJwtError
import ch.admin.foitt.pilotwallet.platform.sdjwt.domain.usecase.ParseRawSdJwt
import ch.admin.foitt.pilotwallet.platform.sdjwt.domain.usecase.ParseSdJwt
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.toErrorIfNull
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Inject

internal class ParseSdJwtImpl @Inject constructor(
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
    private val parseRawSdJwt: ParseRawSdJwt
) : ParseSdJwt {

    override suspend fun invoke(sdJwtString: String): Result<ParsedSdJwt, ParseSdJwtError> =
        withContext(defaultDispatcher) {
            coroutineBinding {
                val sdJwt = parseRawSdJwt(sdJwtString)
                    .mapError { error ->
                        error.toParseSdJwtError()
                    }.bind()
                ParsedSdJwt(
                    signedJwt = sdJwt.signedJwt.parsedString,
                    jsonWithActualValues = replaceDigestsWithClaims(sdJwt).bind()
                )
            }
        }

    private suspend fun replaceDigestsWithClaims(sdJwt: SdJwt): Result<JsonElement, ParseSdJwtError> = coroutineBinding {
        val jwtPayload = sdJwt.signedJwt.payload.toString()
        val jsonElement = Json.parseToJsonElement(jwtPayload) as? JsonObject
        jsonElement?.let {
            replaceDigestsWithClaims(jsonElement, sdJwt.claims).bind()
        }
    }.toErrorIfNull {
        ParseSdJwtError.InvalidJwt
    }

    private suspend fun replaceDigestsWithClaims(
        key: String,
        jsonElement: JsonElement,
        claims: Map<String, SdJwtClaim>
    ): Result<JsonElement, ParseSdJwtError> = coroutineBinding {
        when (jsonElement) {
            is JsonObject -> replaceDigestsWithClaims(jsonElement, claims).bind()
            is JsonArray -> JsonArray(
                jsonElement.map { element ->
                    // TODO: support disclosed array elements
                    replaceDigestsWithClaims(key, element, claims).bind()
                }
            )

            else -> jsonElement
        }
    }

    private suspend fun replaceDigestsWithClaims(
        jsonObject: JsonObject,
        claims: Map<String, SdJwtClaim>
    ): Result<JsonElement, ParseSdJwtError> = coroutineBinding {
        val digestsJsonElement = jsonObject.firstNotNullOfOrNull { element ->
            if (element.key == DIGESTS_KEY) element else null
        }?.let { entry ->
            val digests = parseDigestArray(entry.value).bind()
            replaceDigestsAndFindClaims(digests, claims).bind()
        }

        val otherElements = jsonObject.toMutableMap() - DIGESTS_KEY
        val otherElementsWithClaims = otherElements.mapValues { element ->
            replaceDigestsWithClaims(element.key, element.value, claims).bind()
        }
        JsonObject(otherElementsWithClaims + digestsJsonElement.orEmpty())
    }

    private suspend fun replaceDigestsAndFindClaims(
        digests: List<String>,
        claims: Map<String, SdJwtClaim>
    ): Result<JsonObject, ParseSdJwtError> = coroutineBinding {
        val elementsWithClaims = digests.mapNotNull { digest -> claims[digest] }
            .associate { claim ->
                claim.key to replaceDigestsWithClaims(claim.key, claim.value, claims).bind()
            }
        JsonObject(elementsWithClaims)
    }

    private fun parseDigestArray(digests: JsonElement): Result<List<String>, ParseSdJwtError> =
        if (digests is JsonArray) {
            Ok(digests.jsonArray.map { digest -> digest.jsonPrimitive.content })
        } else {
            Err(ParseSdJwtError.InvalidDigestArray)
        }

    companion object {
        private const val DIGESTS_KEY = "_sd"
    }
}
