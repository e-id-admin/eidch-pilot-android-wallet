package ch.admin.foitt.openid4vc.domain.usecase

import androidx.annotation.CheckResult
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.FetchVerifiableCredentialError
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.JWSKeyPair
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.SupportedCredential
import com.github.michaelbull.result.Result

interface CreateDidJwk {
    @CheckResult
    suspend operator fun invoke(
        supportedCredential: SupportedCredential,
        keyPair: JWSKeyPair,
    ): Result<String, FetchVerifiableCredentialError>
}
