package ch.admin.foitt.pilotwallet.feature.credentialActivities.domain.usecase

import ch.admin.foitt.pilotwallet.feature.credentialActivities.domain.model.GetActivitiesForCredentialFlowError
import ch.admin.foitt.pilotwallet.platform.database.domain.model.ActivityWithVerifier
import com.github.michaelbull.result.Result
import kotlinx.coroutines.flow.Flow

fun interface GetActivitiesForCredentialFlow {
    operator fun invoke(credentialId: Long): Flow<Result<Map<String, List<ActivityWithVerifier>>, GetActivitiesForCredentialFlowError>>
}
