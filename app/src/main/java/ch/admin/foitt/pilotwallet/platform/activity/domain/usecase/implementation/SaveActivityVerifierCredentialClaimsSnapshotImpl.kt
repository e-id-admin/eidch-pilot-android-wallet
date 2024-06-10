package ch.admin.foitt.pilotwallet.platform.activity.domain.usecase.implementation

import ch.admin.foitt.pilotwallet.platform.activity.domain.model.ActivityRepositoryError
import ch.admin.foitt.pilotwallet.platform.activity.domain.model.SaveActivityVerifierCredentialClaimSnapshotError
import ch.admin.foitt.pilotwallet.platform.activity.domain.model.toSaveActivityVerifierCredentialClaimSnapshotError
import ch.admin.foitt.pilotwallet.platform.activity.domain.repository.ActivityRepository
import ch.admin.foitt.pilotwallet.platform.activity.domain.usecase.SaveActivityVerifierCredentialClaimsSnapshot
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.CompatibleCredential
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.mapError
import javax.inject.Inject

class SaveActivityVerifierCredentialClaimsSnapshotImpl @Inject constructor(
    private val activityRepository: ActivityRepository,
) : SaveActivityVerifierCredentialClaimsSnapshot {
    override suspend fun invoke(
        compatibleCredential: CompatibleCredential,
        activityVerifierId: Long
    ): Result<Unit, SaveActivityVerifierCredentialClaimSnapshotError> = coroutineBinding {
        activityRepository.saveActivityVerifierCredentialClaimsSnapshot(compatibleCredential, activityVerifierId)
            .mapError(ActivityRepositoryError::toSaveActivityVerifierCredentialClaimSnapshotError)
            .bind()
    }
}
