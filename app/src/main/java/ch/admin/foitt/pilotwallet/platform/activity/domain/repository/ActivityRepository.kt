package ch.admin.foitt.pilotwallet.platform.activity.domain.repository

import ch.admin.foitt.pilotwallet.platform.activity.domain.model.ActivityRepositoryError
import ch.admin.foitt.pilotwallet.platform.database.domain.model.Activity
import ch.admin.foitt.pilotwallet.platform.database.domain.model.ActivityDetail
import ch.admin.foitt.pilotwallet.platform.database.domain.model.ActivityVerifier
import ch.admin.foitt.pilotwallet.platform.database.domain.model.ActivityWithVerifier
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.CompatibleCredential
import com.github.michaelbull.result.Result
import kotlinx.coroutines.flow.Flow

interface ActivityRepository {
    suspend fun saveActivity(activity: Activity): Result<Long, ActivityRepositoryError>
    suspend fun saveActivityVerifier(activityVerifier: ActivityVerifier): Result<Long, ActivityRepositoryError>
    suspend fun saveActivityVerifierCredentialClaimsSnapshot(
        compatibleCredential: CompatibleCredential,
        activityVerifierId: Long,
    ): Result<Unit, ActivityRepositoryError>
    suspend fun deleteActivity(id: Long): Result<Unit, ActivityRepositoryError>
    fun getActivityDetailFlowById(activityId: Long): Flow<Result<ActivityDetail, ActivityRepositoryError>>
    fun getLatestActivityFlow(): Flow<Result<ActivityWithVerifier?, ActivityRepositoryError>>
    fun getLastNActivitiesForCredentialFlow(
        credentialId: Long,
        amount: Int
    ): Flow<Result<List<ActivityWithVerifier>, ActivityRepositoryError>>
    fun getActivitiesForCredentialFlow(credentialId: Long): Flow<Result<List<ActivityWithVerifier>, ActivityRepositoryError>>
}
