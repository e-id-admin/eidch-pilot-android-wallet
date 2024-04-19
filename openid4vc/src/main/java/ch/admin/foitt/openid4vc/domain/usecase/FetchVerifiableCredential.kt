package ch.admin.foitt.openid4vc.domain.usecase

import androidx.annotation.CheckResult
import ch.admin.foitt.openid4vc.domain.model.VerifiableCredential
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.CredentialOffer
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.FetchVerifiableCredentialError
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.IssuerConfiguration
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.IssuerCredentialInformation
import com.github.michaelbull.result.Result

interface FetchVerifiableCredential {
    @CheckResult
    suspend operator fun invoke(
        credentialOffer: CredentialOffer,
        issuerConfiguration: IssuerConfiguration,
        credentialInformation: IssuerCredentialInformation
    ): Result<VerifiableCredential, FetchVerifiableCredentialError>
}
