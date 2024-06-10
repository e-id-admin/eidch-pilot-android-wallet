package ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase

import ch.admin.foitt.pilotwallet.platform.database.domain.model.Claim
import ch.admin.foitt.pilotwallet.platform.database.domain.model.ClaimDisplay
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.CredentialClaimData
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.MapToCredentialClaimDataError
import com.github.michaelbull.result.Result

interface MapToCredentialClaimData {
    suspend operator fun <T : Claim, U : ClaimDisplay> invoke(
        claim: T,
        displays: List<U>
    ): Result<CredentialClaimData, MapToCredentialClaimDataError>
}
