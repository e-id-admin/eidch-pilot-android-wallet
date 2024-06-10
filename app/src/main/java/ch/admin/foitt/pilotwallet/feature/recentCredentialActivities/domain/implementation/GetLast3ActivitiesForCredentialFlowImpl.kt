package ch.admin.foitt.pilotwallet.feature.recentCredentialActivities.domain.implementation

import ch.admin.foitt.pilotwallet.feature.recentCredentialActivities.domain.GetLast3ActivitiesForCredentialFlow
import ch.admin.foitt.pilotwallet.feature.recentCredentialActivities.domain.model.GetLast3ActivitiesForCredentialFlowError
import ch.admin.foitt.pilotwallet.feature.recentCredentialActivities.domain.model.toGetLast3ActivitiesForCredentialFlowError
import ch.admin.foitt.pilotwallet.platform.activity.domain.model.ActivityRepositoryError
import ch.admin.foitt.pilotwallet.platform.activity.domain.repository.ActivityRepository
import ch.admin.foitt.pilotwallet.platform.database.domain.model.ActivityWithVerifier
import ch.admin.foitt.pilotwallet.platform.utils.mapError
import com.github.michaelbull.result.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLast3ActivitiesForCredentialFlowImpl @Inject constructor(
    private val activityRepository: ActivityRepository,
) : GetLast3ActivitiesForCredentialFlow {
    override fun invoke(
        credentialId: Long,
    ): Flow<Result<List<ActivityWithVerifier>, GetLast3ActivitiesForCredentialFlowError>> =
        activityRepository.getLastNActivitiesForCredentialFlow(
            credentialId = credentialId,
            amount = 3
        ).mapError(ActivityRepositoryError::toGetLast3ActivitiesForCredentialFlowError)
}
