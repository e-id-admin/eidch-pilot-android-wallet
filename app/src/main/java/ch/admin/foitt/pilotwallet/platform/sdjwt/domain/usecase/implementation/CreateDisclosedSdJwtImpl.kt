package ch.admin.foitt.pilotwallet.platform.sdjwt.domain.usecase.implementation

import ch.admin.foitt.openid4vc.di.DefaultDispatcher
import ch.admin.foitt.pilotwallet.platform.sdjwt.domain.model.CreateDisclosedSdJwtError
import ch.admin.foitt.pilotwallet.platform.sdjwt.domain.model.ParseRawSdJwtError
import ch.admin.foitt.pilotwallet.platform.sdjwt.domain.model.SdJwt
import ch.admin.foitt.pilotwallet.platform.sdjwt.domain.model.toCreateDisclosedSdJwtError
import ch.admin.foitt.pilotwallet.platform.sdjwt.domain.usecase.CreateDisclosedSdJwt
import ch.admin.foitt.pilotwallet.platform.sdjwt.domain.usecase.ParseRawSdJwt
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.mapError
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

internal class CreateDisclosedSdJwtImpl @Inject constructor(
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
    private val parseRawSdJwt: ParseRawSdJwt
) : CreateDisclosedSdJwt {
    override suspend fun invoke(
        sdJwtString: String,
        attributesKey: List<String>,
    ): Result<String, CreateDisclosedSdJwtError> =
        withContext(defaultDispatcher) {
            coroutineBinding {
                val sdJwt = parseRawSdJwt(sdJwtString)
                    .mapError(ParseRawSdJwtError::toCreateDisclosedSdJwtError).bind()
                createDisclosedSdJwt(sdJwt, attributesKey).bind()
            }
        }

    private fun createDisclosedSdJwt(sdJwt: SdJwt, attributesKey: List<String>) = runSuspendCatching {
        val disclosures = sdJwt.claims.values.filter { claim ->
            claim.key in attributesKey
        }.map { claim ->
            claim.disclosure
        }

        StringBuilder(sdJwt.signedJwt.parsedString).apply {
            append(SEPARATOR)
            if (disclosures.isNotEmpty()) {
                append(disclosures.joinToString(SEPARATOR))
                append(SEPARATOR)
            } else {
                Timber.w(message = "No disclosure for this verification")
            }
        }.toString()
    }.mapError { throwable ->
        CreateDisclosedSdJwtError.Unexpected(throwable)
    }

    companion object {
        private const val SEPARATOR = "~"
    }
}
