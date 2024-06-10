package ch.admin.foitt.pilotwallet.feature.home.domain.model

import ch.admin.foitt.pilotwallet.platform.activity.domain.model.ActivityError
import ch.admin.foitt.pilotwallet.platform.activity.domain.model.ActivityRepositoryError

internal interface HomeError {
    data class Unexpected(val throwable: Throwable?) : GetLatestActivityFlowError
}

sealed interface GetLatestActivityFlowError

internal fun ActivityRepositoryError.toGetLatestActivityFlowError(): GetLatestActivityFlowError = when (this) {
    is ActivityError.Unexpected -> HomeError.Unexpected(throwable)
}
