package ch.admin.foitt.pilotwallet.feature.recentCredentialActivities.domain.model

import ch.admin.foitt.pilotwallet.platform.activity.domain.model.ActivityError
import ch.admin.foitt.pilotwallet.platform.activity.domain.model.ActivityRepositoryError

internal interface RecentCredentialActivitiesError {
    data class Unexpected(val throwable: Throwable?) : GetLast3ActivitiesForCredentialFlowError
}

sealed interface GetLast3ActivitiesForCredentialFlowError

internal fun ActivityRepositoryError.toGetLast3ActivitiesForCredentialFlowError(): GetLast3ActivitiesForCredentialFlowError = when (this) {
    is ActivityError.Unexpected -> RecentCredentialActivitiesError.Unexpected(throwable)
}
