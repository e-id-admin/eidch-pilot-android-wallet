package ch.admin.foitt.pilotwallet.platform.sdjwt.domain.usecase

import ch.admin.foitt.pilotwallet.platform.sdjwt.domain.model.CreateDisclosedSdJwtError
import com.github.michaelbull.result.Result

fun interface CreateDisclosedSdJwt {
    suspend operator fun invoke(
        sdJwtString: String,
        attributesKey: List<String>,
    ): Result<String, CreateDisclosedSdJwtError>
}
