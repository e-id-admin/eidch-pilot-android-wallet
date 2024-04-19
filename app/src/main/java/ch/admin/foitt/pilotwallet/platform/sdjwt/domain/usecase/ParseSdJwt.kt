package ch.admin.foitt.pilotwallet.platform.sdjwt.domain.usecase

import ch.admin.foitt.pilotwallet.platform.sdjwt.domain.model.ParseSdJwtError
import ch.admin.foitt.pilotwallet.platform.sdjwt.domain.model.ParsedSdJwt
import com.github.michaelbull.result.Result

fun interface ParseSdJwt {
    suspend operator fun invoke(sdJwtString: String): Result<ParsedSdJwt, ParseSdJwtError>
}
