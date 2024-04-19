package ch.admin.foitt.openid4vc.domain.usecase

import ch.admin.foitt.openid4vc.domain.model.VerifiableCredentialConfig
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.CreateVerifiablePresentationTokenError
import com.github.michaelbull.result.Result

fun interface CreateVerifiablePresentationToken {
    suspend operator fun invoke(
        keyAlias: String,
        nonce: String,
        disclosedSdJwt: String,
        config: VerifiableCredentialConfig,
    ): Result<String, CreateVerifiablePresentationTokenError>
}
