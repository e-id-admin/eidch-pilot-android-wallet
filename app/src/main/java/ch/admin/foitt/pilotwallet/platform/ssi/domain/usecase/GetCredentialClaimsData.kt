package ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase

import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.CredentialClaimData
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.GetCredentialClaimDataError
import com.github.michaelbull.result.Result

fun interface GetCredentialClaimsData {
    suspend operator fun invoke(credentialId: Long): Result<List<CredentialClaimData>, GetCredentialClaimDataError>
}
