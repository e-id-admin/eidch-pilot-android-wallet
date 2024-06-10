package ch.admin.foitt.pilotwallet.platform.activity.domain.usecase.implementation

import ch.admin.foitt.pilotwallet.platform.activity.domain.model.ActivityRepositoryError
import ch.admin.foitt.pilotwallet.platform.activity.domain.model.SaveActivityError
import ch.admin.foitt.pilotwallet.platform.activity.domain.model.toSaveActivityError
import ch.admin.foitt.pilotwallet.platform.activity.domain.repository.ActivityRepository
import ch.admin.foitt.pilotwallet.platform.activity.domain.usecase.SaveActivity
import ch.admin.foitt.pilotwallet.platform.database.domain.model.Activity
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.mapError
import javax.inject.Inject

class SaveActivityImpl @Inject constructor(
    private val activityRepository: ActivityRepository,
) : SaveActivity {
    override suspend fun invoke(activity: Activity): Result<Long, SaveActivityError> = coroutineBinding {
        activityRepository.saveActivity(activity)
            .mapError(ActivityRepositoryError::toSaveActivityError)
            .bind()
    }
}
