package ch.admin.foitt.pilotwallet.platform.credential.domain.usecase.implementation

import ch.admin.foitt.pilotwallet.platform.credential.domain.model.VerifySdJwtError
import ch.admin.foitt.pilotwallet.platform.credential.domain.model.toVerifySdJwtError
import ch.admin.foitt.pilotwallet.platform.credential.domain.usecase.VerifySdJwt
import ch.admin.foitt.pilotwallet.platform.sdjwt.domain.usecase.ParseSdJwt
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.mapError
import javax.inject.Inject

class VerifySdJwtImpl @Inject constructor(
    private val parseSdJwt: ParseSdJwt,
) : VerifySdJwt {
    override suspend fun invoke(sdjwt: String): Result<Boolean, VerifySdJwtError> = coroutineBinding {
        parseSdJwt(sdjwt)
            .mapError { error ->
                error.toVerifySdJwtError()
            }.bind()

        true
    }
}
