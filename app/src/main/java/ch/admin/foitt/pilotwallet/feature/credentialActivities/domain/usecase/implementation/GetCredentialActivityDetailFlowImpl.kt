package ch.admin.foitt.pilotwallet.feature.credentialActivities.domain.usecase.implementation

import ch.admin.foitt.pilotwallet.feature.credentialActivities.domain.model.CredentialActivitiesError
import ch.admin.foitt.pilotwallet.feature.credentialActivities.domain.model.CredentialActivityDetail
import ch.admin.foitt.pilotwallet.feature.credentialActivities.domain.model.GetCredentialActivityDetailFlowError
import ch.admin.foitt.pilotwallet.feature.credentialActivities.domain.model.toGetCredentialActivityDetailFlowError
import ch.admin.foitt.pilotwallet.feature.credentialActivities.domain.usecase.GetCredentialActivityDetailFlow
import ch.admin.foitt.pilotwallet.platform.activity.domain.model.ActivityRepositoryError
import ch.admin.foitt.pilotwallet.platform.activity.domain.repository.ActivityRepository
import ch.admin.foitt.pilotwallet.platform.credential.domain.model.CredentialPreview
import ch.admin.foitt.pilotwallet.platform.credential.domain.model.toCredentialPreview
import ch.admin.foitt.pilotwallet.platform.database.domain.model.ActivityDetail
import ch.admin.foitt.pilotwallet.platform.database.domain.model.ActivityVerifierCredentialClaimWithDisplays
import ch.admin.foitt.pilotwallet.platform.database.domain.model.LocalizedDisplay
import ch.admin.foitt.pilotwallet.platform.locale.domain.usecase.GetLocalizedDisplay
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.CredentialClaimData
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.MapToCredentialClaimDataError
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.MapToCredentialClaimData
import ch.admin.foitt.pilotwallet.platform.utils.andThen
import ch.admin.foitt.pilotwallet.platform.utils.mapError
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.map
import com.github.michaelbull.result.mapError
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCredentialActivityDetailFlowImpl @Inject constructor(
    private val activityRepository: ActivityRepository,
    private val getLocalizedDisplay: GetLocalizedDisplay,
    private val mapToCredentialClaimData: MapToCredentialClaimData,
) : GetCredentialActivityDetailFlow {
    override fun invoke(activityId: Long): Flow<Result<CredentialActivityDetail, GetCredentialActivityDetailFlowError>> =
        activityRepository.getActivityDetailFlowById(activityId)
            .mapError(ActivityRepositoryError::toGetCredentialActivityDetailFlowError)
            .andThen { detail ->
                coroutineBinding {
                    val display = getDisplay(detail.credentialDisplays).bind()
                    val preview = display.toCredentialPreview(detail.activity.credentialSnapshotStatus)
                    detail.toCredentialActivityDetail(preview).bind()
                }
            }

    private suspend fun ActivityDetail.toCredentialActivityDetail(
        credentialPreview: CredentialPreview
    ): Result<CredentialActivityDetail, GetCredentialActivityDetailFlowError> =
        getCredentialClaimData(verifier.claims).map { claims ->
            CredentialActivityDetail(
                id = activity.id,
                actor = verifier.verifier.name,
                actorLogo = verifier.verifier.logo,
                credentialPreview = credentialPreview,
                createdAt = activity.createdAt,
                type = activity.type,
                claims = claims,
            )
        }

    private suspend fun getCredentialClaimData(
        claims: List<ActivityVerifierCredentialClaimWithDisplays>
    ): Result<List<CredentialClaimData>, GetCredentialActivityDetailFlowError> = coroutineBinding {
        claims.map { claimWithDisplays ->
            val claim = claimWithDisplays.claim
            mapToCredentialClaimData(
                claim,
                claimWithDisplays.displays
            ).mapError(MapToCredentialClaimDataError::toGetCredentialActivityDetailFlowError).bind()
        }
    }

    private fun <T : LocalizedDisplay> getDisplay(displays: List<T>): Result<T, GetCredentialActivityDetailFlowError> =
        getLocalizedDisplay(displays)?.let { Ok(it) }
            ?: Err(CredentialActivitiesError.Unexpected(IllegalStateException("No localized display found")))
}
