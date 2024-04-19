package ch.admin.foitt.pilotwallet.platform.credential.domain.usecase

import androidx.annotation.CheckResult
import ch.admin.foitt.pilotwallet.platform.credential.domain.model.VerifyJwtError
import com.github.michaelbull.result.Result

interface VerifyJwt {
    @CheckResult
    suspend operator fun invoke(jwksUri: String, credential: String): Result<Boolean, VerifyJwtError>
}
