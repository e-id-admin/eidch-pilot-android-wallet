package ch.admin.foitt.pilotwallet.platform.credential.domain.usecase

import ch.admin.foitt.openid4vc.domain.model.VerifiableCredential
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.IssuerCredentialInformation
import ch.admin.foitt.pilotwallet.platform.credential.domain.model.SaveCredentialError
import com.github.michaelbull.result.Result

fun interface SaveCredential {
    suspend operator fun invoke(
        issuerInfo: IssuerCredentialInformation,
        verifiableCredential: VerifiableCredential,
        credentialIdentifier: String
    ): Result<Long, SaveCredentialError>
}
