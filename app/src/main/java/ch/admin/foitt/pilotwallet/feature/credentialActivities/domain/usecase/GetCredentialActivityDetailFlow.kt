package ch.admin.foitt.pilotwallet.feature.credentialActivities.domain.usecase

import ch.admin.foitt.pilotwallet.feature.credentialActivities.domain.model.CredentialActivityDetail
import ch.admin.foitt.pilotwallet.feature.credentialActivities.domain.model.GetCredentialActivityDetailFlowError
import com.github.michaelbull.result.Result
import kotlinx.coroutines.flow.Flow

interface GetCredentialActivityDetailFlow {
    operator fun invoke(activityId: Long): Flow<Result<CredentialActivityDetail, GetCredentialActivityDetailFlowError>>
}
