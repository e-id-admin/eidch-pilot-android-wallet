package ch.admin.foitt.pilotwallet.feature.credentialActivities.domain.model

import ch.admin.foitt.pilotwallet.platform.activity.domain.model.ActivityError
import ch.admin.foitt.pilotwallet.platform.activity.domain.model.ActivityRepositoryError
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.MapToCredentialClaimDataError
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.SsiError

internal interface CredentialActivitiesError {
    data class Unexpected(val throwable: Throwable?) :
        GetCredentialActivityDetailFlowError,
        GetActivitiesForCredentialFlowError
}

sealed interface GetCredentialActivityDetailFlowError
sealed interface GetActivitiesForCredentialFlowError

internal fun ActivityRepositoryError.toGetCredentialActivityDetailFlowError(): GetCredentialActivityDetailFlowError = when (this) {
    is ActivityError.Unexpected -> CredentialActivitiesError.Unexpected(throwable)
}

internal fun ActivityRepositoryError.toGetActivitiesForCredentialFlowError(): GetActivitiesForCredentialFlowError = when (this) {
    is ActivityError.Unexpected -> CredentialActivitiesError.Unexpected(throwable)
}

internal fun MapToCredentialClaimDataError.toGetCredentialActivityDetailFlowError(): GetCredentialActivityDetailFlowError = when (this) {
    is SsiError.Unexpected -> CredentialActivitiesError.Unexpected(cause)
}
