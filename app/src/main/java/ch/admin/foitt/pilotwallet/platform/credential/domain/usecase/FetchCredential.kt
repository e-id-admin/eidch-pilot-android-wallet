package ch.admin.foitt.pilotwallet.platform.credential.domain.usecase

import ch.admin.foitt.openid4vc.domain.model.credentialoffer.CredentialOffer
import ch.admin.foitt.pilotwallet.platform.credential.domain.model.FetchCredentialError
import com.github.michaelbull.result.Result

fun interface FetchCredential {
    suspend operator fun invoke(
        credentialOffer: CredentialOffer,
    ): Result<Long, FetchCredentialError>
}
