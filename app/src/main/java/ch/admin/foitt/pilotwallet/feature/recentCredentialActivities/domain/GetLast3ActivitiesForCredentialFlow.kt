package ch.admin.foitt.pilotwallet.feature.recentCredentialActivities.domain

import ch.admin.foitt.pilotwallet.feature.recentCredentialActivities.domain.model.GetLast3ActivitiesForCredentialFlowError
import ch.admin.foitt.pilotwallet.platform.database.domain.model.ActivityWithVerifier
import com.github.michaelbull.result.Result
import kotlinx.coroutines.flow.Flow

interface GetLast3ActivitiesForCredentialFlow {
    operator fun invoke(credentialId: Long): Flow<Result<List<ActivityWithVerifier>, GetLast3ActivitiesForCredentialFlowError>>
}
