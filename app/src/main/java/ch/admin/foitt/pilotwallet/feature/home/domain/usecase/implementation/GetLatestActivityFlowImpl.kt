package ch.admin.foitt.pilotwallet.feature.home.domain.usecase.implementation

import ch.admin.foitt.pilotwallet.feature.home.domain.model.GetLatestActivityFlowError
import ch.admin.foitt.pilotwallet.feature.home.domain.model.toGetLatestActivityFlowError
import ch.admin.foitt.pilotwallet.feature.home.domain.usecase.GetLatestActivityFlow
import ch.admin.foitt.pilotwallet.platform.activity.domain.model.ActivityRepositoryError
import ch.admin.foitt.pilotwallet.platform.activity.domain.repository.ActivityRepository
import ch.admin.foitt.pilotwallet.platform.database.domain.model.ActivityWithVerifier
import ch.admin.foitt.pilotwallet.platform.utils.mapError
import com.github.michaelbull.result.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLatestActivityFlowImpl @Inject constructor(
    private val activityRepository: ActivityRepository,
) : GetLatestActivityFlow {
    override fun invoke(): Flow<Result<ActivityWithVerifier?, GetLatestActivityFlowError>> =
        activityRepository.getLatestActivityFlow()
            .mapError(ActivityRepositoryError::toGetLatestActivityFlowError)
}
