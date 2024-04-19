package ch.admin.foitt.pilotwallet.platform.sdjwt.domain.usecase

import ch.admin.foitt.pilotwallet.platform.sdjwt.domain.model.ParseRawSdJwtError
import ch.admin.foitt.pilotwallet.platform.sdjwt.domain.model.SdJwt
import com.github.michaelbull.result.Result

fun interface ParseRawSdJwt {
    suspend operator fun invoke(sdJwt: String): Result<SdJwt, ParseRawSdJwtError>
}
