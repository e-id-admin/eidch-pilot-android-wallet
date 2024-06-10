package ch.admin.foitt.pilotwallet.feature.credentialActivities.domain.usecase.implementation

import ch.admin.foitt.pilotwallet.feature.credentialActivities.domain.model.GetActivitiesForCredentialFlowError
import ch.admin.foitt.pilotwallet.feature.credentialActivities.domain.model.toGetActivitiesForCredentialFlowError
import ch.admin.foitt.pilotwallet.feature.credentialActivities.domain.usecase.GetActivitiesForCredentialFlow
import ch.admin.foitt.pilotwallet.platform.activity.domain.model.ActivityRepositoryError
import ch.admin.foitt.pilotwallet.platform.activity.domain.repository.ActivityRepository
import ch.admin.foitt.pilotwallet.platform.database.domain.model.ActivityWithVerifier
import ch.admin.foitt.pilotwallet.platform.locale.domain.usecase.GetCurrentAppLocale
import ch.admin.foitt.pilotwallet.platform.utils.asMonthYear
import ch.admin.foitt.pilotwallet.platform.utils.epochSecondsToZonedDateTime
import ch.admin.foitt.pilotwallet.platform.utils.mapError
import ch.admin.foitt.pilotwallet.platform.utils.mapOk
import com.github.michaelbull.result.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetActivitiesForCredentialFlowImpl @Inject constructor(
    private val activityRepository: ActivityRepository,
    private val getCurrentAppLocale: GetCurrentAppLocale,
) : GetActivitiesForCredentialFlow {
    override fun invoke(credentialId: Long): Flow<Result<Map<String, List<ActivityWithVerifier>>, GetActivitiesForCredentialFlowError>> =
        activityRepository.getActivitiesForCredentialFlow(credentialId)
            .mapError(ActivityRepositoryError::toGetActivitiesForCredentialFlowError)
            .mapOk { activityWithVerifierList ->
                activityWithVerifierList.groupBy {
                    val createdAt = it.activity.createdAt
                    val zonedDateTime = createdAt.epochSecondsToZonedDateTime()
                    zonedDateTime.asMonthYear(getCurrentAppLocale())
                }
            }
}
