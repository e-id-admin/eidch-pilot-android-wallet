package ch.admin.foitt.pilotwallet.platform.activity.domain.usecase.implementation

import ch.admin.foitt.pilotwallet.platform.activity.domain.model.ActivityRepositoryError
import ch.admin.foitt.pilotwallet.platform.activity.domain.model.DeleteActivityError
import ch.admin.foitt.pilotwallet.platform.activity.domain.model.toDeleteActivityError
import ch.admin.foitt.pilotwallet.platform.activity.domain.repository.ActivityRepository
import ch.admin.foitt.pilotwallet.platform.activity.domain.usecase.DeleteActivity
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.mapError
import javax.inject.Inject

class DeleteActivityImpl @Inject constructor(
    private val activityRepository: ActivityRepository,
) : DeleteActivity {
    override suspend fun invoke(activityId: Long): Result<Unit, DeleteActivityError> =
        activityRepository.deleteActivity(activityId)
            .mapError(ActivityRepositoryError::toDeleteActivityError)
}
