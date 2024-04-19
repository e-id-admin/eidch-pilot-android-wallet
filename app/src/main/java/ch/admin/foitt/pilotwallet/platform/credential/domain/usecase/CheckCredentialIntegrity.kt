package ch.admin.foitt.pilotwallet.platform.credential.domain.usecase

import androidx.annotation.CheckResult
import ch.admin.foitt.openid4vc.domain.model.VerifiableCredential
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata.IssuerConfiguration
import ch.admin.foitt.pilotwallet.platform.credential.domain.model.CheckCredentialIntegrityError
import com.github.michaelbull.result.Result

interface CheckCredentialIntegrity {
    @CheckResult
    suspend operator fun invoke(
        issuerConfiguration: IssuerConfiguration,
        verifiableCredential: VerifiableCredential
    ): Result<Boolean, CheckCredentialIntegrityError>
}
