package ch.admin.foitt.pilotwallet.platform.credential.domain.usecase.implementation

import ch.admin.foitt.pilotwallet.platform.credential.domain.model.CredentialOfferError
import ch.admin.foitt.pilotwallet.platform.credential.domain.model.VerifyJwtTimestampsError
import ch.admin.foitt.pilotwallet.platform.credential.domain.usecase.VerifyJwtTimestamps
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.mapError
import com.nimbusds.jwt.SignedJWT
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.time.Instant
import javax.inject.Inject

class VerifyJwtTimestampsImpl @Inject constructor() : VerifyJwtTimestamps {
    override suspend fun invoke(signedJwt: SignedJWT): Result<Boolean, VerifyJwtTimestampsError> = coroutineBinding {
        runSuspendCatching {
            // see section 6.1.2.4 of
            // https://www.ietf.org/archive/id/draft-ietf-oauth-selective-disclosure-jwt-05.html#name-verification-of-the-sd-jwt
            val payload = signedJwt.payload.toString()
            val jsonPayload = Json.parseToJsonElement(payload)
            val exp = jsonPayload.jsonObject[EXP_KEY]?.jsonPrimitive?.contentOrNull
            val iat = jsonPayload.jsonObject[IAT_KEY]?.jsonPrimitive?.contentOrNull
            val nbf = jsonPayload.jsonObject[NBF_KEY]?.jsonPrimitive?.contentOrNull

            val now = Instant.now()

            val isExpirationValid = exp?.let {
                val expTime = Instant.ofEpochSecond(exp.toLong())
                now.isBefore(expTime.plusSeconds(BUFFER)) // add 15 sec buffer
            } ?: true

            val isIssuedAtValid = iat?.let {
                val iatTime = Instant.ofEpochSecond(iat.toLong())
                !now.isBefore(iatTime.minusSeconds(BUFFER)) // add 15 sec buffer
            } ?: true

            val isNotBeforeValid = nbf?.let {
                val nbfTime = Instant.ofEpochSecond(nbf.toLong())
                !now.isBefore(nbfTime.minusSeconds(BUFFER)) // add 15 sec buffer
            } ?: true

            isExpirationValid && isIssuedAtValid && isNotBeforeValid
        }.mapError {
            CredentialOfferError.Unexpected(it)
        }.bind()
    }

    companion object {
        private const val EXP_KEY = "exp"
        private const val IAT_KEY = "iat"
        private const val NBF_KEY = "nbf"

        private const val BUFFER = 15L
    }
}
