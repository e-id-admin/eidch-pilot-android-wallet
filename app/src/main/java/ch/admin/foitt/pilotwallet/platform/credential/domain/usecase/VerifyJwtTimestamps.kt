package ch.admin.foitt.pilotwallet.platform.credential.domain.usecase

import androidx.annotation.CheckResult
import ch.admin.foitt.pilotwallet.platform.credential.domain.model.VerifyJwtTimestampsError
import com.github.michaelbull.result.Result
import com.nimbusds.jwt.SignedJWT

interface VerifyJwtTimestamps {
    @CheckResult
    suspend operator fun invoke(signedJwt: SignedJWT): Result<Boolean, VerifyJwtTimestampsError>
}
