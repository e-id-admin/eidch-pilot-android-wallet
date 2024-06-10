package ch.admin.foitt.pilotwallet.platform.activity.domain.usecase

import ch.admin.foitt.pilotwallet.platform.activity.domain.model.SaveActivityVerifierCredentialClaimSnapshotError
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.CompatibleCredential
import com.github.michaelbull.result.Result

interface SaveActivityVerifierCredentialClaimsSnapshot {
    suspend operator fun invoke(
        compatibleCredential: CompatibleCredential,
        activityVerifierId: Long
    ): Result<Unit, SaveActivityVerifierCredentialClaimSnapshotError>
}
