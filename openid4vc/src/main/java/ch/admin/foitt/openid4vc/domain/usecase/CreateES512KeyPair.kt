package ch.admin.foitt.openid4vc.domain.usecase

import androidx.annotation.CheckResult
import ch.admin.foitt.openid4vc.domain.model.CreateES512KeyPairError
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.JWSKeyPair
import com.github.michaelbull.result.Result
import com.nimbusds.jose.JWSAlgorithm

internal interface CreateES512KeyPair {
    @CheckResult
    suspend operator fun invoke(
        jwsAlgorithm: JWSAlgorithm,
        provider: String,
    ): Result<JWSKeyPair, CreateES512KeyPairError>
}
