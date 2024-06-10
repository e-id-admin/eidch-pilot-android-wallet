package ch.admin.foitt.pilotwallet.platform.activity.data.repository

import ch.admin.foitt.pilotwallet.platform.activity.domain.model.ActivityError
import ch.admin.foitt.pilotwallet.platform.activity.domain.model.ActivityRepositoryError
import ch.admin.foitt.pilotwallet.platform.activity.domain.repository.ActivityRepository
import ch.admin.foitt.pilotwallet.platform.database.data.dao.ActivityDao
import ch.admin.foitt.pilotwallet.platform.database.data.dao.ActivityVerifierCredentialClaimDao
import ch.admin.foitt.pilotwallet.platform.database.data.dao.ActivityVerifierCredentialClaimDisplayDao
import ch.admin.foitt.pilotwallet.platform.database.data.dao.ActivityVerifierDao
import ch.admin.foitt.pilotwallet.platform.database.data.dao.CredentialClaimDao
import ch.admin.foitt.pilotwallet.platform.database.data.dao.CredentialClaimDisplayDao
import ch.admin.foitt.pilotwallet.platform.database.domain.model.Activity
import ch.admin.foitt.pilotwallet.platform.database.domain.model.ActivityDetail
import ch.admin.foitt.pilotwallet.platform.database.domain.model.ActivityVerifier
import ch.admin.foitt.pilotwallet.platform.database.domain.model.ActivityWithVerifier
import ch.admin.foitt.pilotwallet.platform.database.domain.model.toActivityVerifierCredentialClaim
import ch.admin.foitt.pilotwallet.platform.database.domain.model.toActivityVerifierCredentialClaimDisplay
import ch.admin.foitt.pilotwallet.platform.database.domain.repository.DatabaseRepository
import ch.admin.foitt.pilotwallet.platform.di.IoDispatcher
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.CompatibleCredential
import ch.admin.foitt.pilotwallet.platform.utils.catchAndMap
import ch.admin.foitt.pilotwallet.platform.utils.suspendUntilNonNull
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.mapError
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class ActivityRepositoryImpl @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    databaseRepository: DatabaseRepository,
) : ActivityRepository {

    override suspend fun saveActivity(activity: Activity): Result<Long, ActivityRepositoryError> = runSuspendCatching {
        withContext(ioDispatcher) {
            activityDao().insert(activity)
        }
    }.mapError(::toActivityRepositoryError)

    override suspend fun saveActivityVerifier(
        activityVerifier: ActivityVerifier,
    ): Result<Long, ActivityRepositoryError> = runSuspendCatching {
        withContext(ioDispatcher) {
            activityVerifierDao().insert(activityVerifier)
        }
    }.mapError(::toActivityRepositoryError)

    override suspend fun saveActivityVerifierCredentialClaimsSnapshot(
        compatibleCredential: CompatibleCredential,
        activityVerifierId: Long
    ): Result<Unit, ActivityRepositoryError> = runSuspendCatching {
        withContext(ioDispatcher) {
            val claims = credentialClaimDao().getByCredentialId(compatibleCredential.credentialId)
            val requestedClaims = claims.filter { claim -> compatibleCredential.requestedFields.any { field -> field.key == claim.key } }

            requestedClaims.forEach { claim ->
                val activityClaimId = activityVerifierCredentialClaimDao().insert(
                    claim.toActivityVerifierCredentialClaim(activityVerifierId)
                )
                val claimDisplays = credentialClaimDisplayDao().getByClaimId(claim.id)
                claimDisplays.forEach { display ->
                    activityVerifierCredentialClaimDisplayDao().insert(display.toActivityVerifierCredentialClaimDisplay(activityClaimId))
                }
            }
        }
    }.mapError(::toActivityRepositoryError)

    override suspend fun deleteActivity(id: Long) = runSuspendCatching {
        withContext(ioDispatcher) {
            activityDao().deleteById(id)
        }
    }.mapError(::toActivityRepositoryError)

    override fun getActivityDetailFlowById(activityId: Long): Flow<Result<ActivityDetail, ActivityRepositoryError>> =
        activityDaoFlow.flatMapLatest { dao ->
            dao?.getActivityDetailFlow(activityId)
                ?.catchAndMap(::toActivityRepositoryError) ?: emptyFlow()
        }

    override fun getLatestActivityFlow(): Flow<Result<ActivityWithVerifier?, ActivityRepositoryError>> =
        activityDaoFlow.flatMapLatest { activityDao ->
            activityDao?.getLatestActivityFlow()
                ?.catchAndMap(::toActivityRepositoryError) ?: emptyFlow()
        }

    override fun getLastNActivitiesForCredentialFlow(
        credentialId: Long,
        amount: Int
    ): Flow<Result<List<ActivityWithVerifier>, ActivityRepositoryError>> =
        activityDaoFlow.flatMapLatest { activityDao ->
            activityDao?.getLastNActivitiesForCredentialFlow(credentialId, amount)
                ?.catchAndMap(::toActivityRepositoryError) ?: emptyFlow()
        }

    override fun getActivitiesForCredentialFlow(credentialId: Long): Flow<Result<List<ActivityWithVerifier>, ActivityRepositoryError>> =
        activityDaoFlow.flatMapLatest { activityDao ->
            activityDao?.getActivitiesForCredentialFlow(credentialId)
                ?.catchAndMap(::toActivityRepositoryError) ?: emptyFlow()
        }

    private fun toActivityRepositoryError(throwable: Throwable): ActivityRepositoryError {
        Timber.e(throwable)
        return ActivityError.Unexpected(throwable)
    }

    //region DAOs
    private suspend fun activityDao(): ActivityDao = suspendUntilNonNull { activityDaoFlow.value }
    private val activityDaoFlow = databaseRepository.activityDao

    private suspend fun activityVerifierDao(): ActivityVerifierDao = suspendUntilNonNull { activityVerifierDaoFlow.value }
    private val activityVerifierDaoFlow = databaseRepository.activityVerifierDao

    private suspend fun activityVerifierCredentialClaimDao(): ActivityVerifierCredentialClaimDao = suspendUntilNonNull {
        activityVerifierCredentialClaimDaoFlow.value
    }
    private val activityVerifierCredentialClaimDaoFlow = databaseRepository.activityVerifierCredentialClaimDao

    private suspend fun activityVerifierCredentialClaimDisplayDao(): ActivityVerifierCredentialClaimDisplayDao = suspendUntilNonNull {
        activityVerifierCredentialClaimDisplayDaoFlow.value
    }
    private val activityVerifierCredentialClaimDisplayDaoFlow = databaseRepository.activityVerifierCredentialClaimDisplayDao

    private suspend fun credentialClaimDao(): CredentialClaimDao = suspendUntilNonNull {
        credentialClaimDaoFlow.value
    }
    private val credentialClaimDaoFlow = databaseRepository.credentialClaimDao

    private suspend fun credentialClaimDisplayDao(): CredentialClaimDisplayDao = suspendUntilNonNull {
        credentialClaimDisplayDaoFlow.value
    }
    private val credentialClaimDisplayDaoFlow = databaseRepository.credentialClaimDisplayDao
    //endregion
}
