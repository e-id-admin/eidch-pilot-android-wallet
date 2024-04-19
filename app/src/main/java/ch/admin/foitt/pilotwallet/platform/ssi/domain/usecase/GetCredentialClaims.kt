package ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase

import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialClaim
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.GetCredentialClaimsError
import com.github.michaelbull.result.Result

interface GetCredentialClaims {
    suspend operator fun invoke(credentialId: Long): Result<List<CredentialClaim>, GetCredentialClaimsError>
}
