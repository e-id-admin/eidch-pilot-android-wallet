package ch.admin.foitt.pilotwallet.platform.activity.domain.usecase.implementation

import ch.admin.foitt.pilotwallet.platform.activity.domain.model.ActivityRepositoryError
import ch.admin.foitt.pilotwallet.platform.activity.domain.model.SaveActivityVerifierError
import ch.admin.foitt.pilotwallet.platform.activity.domain.model.toSaveActivityVerifierError
import ch.admin.foitt.pilotwallet.platform.activity.domain.repository.ActivityRepository
import ch.admin.foitt.pilotwallet.platform.activity.domain.usecase.SaveActivityVerifier
import ch.admin.foitt.pilotwallet.platform.database.domain.model.ActivityVerifier
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.mapError
import javax.inject.Inject

class SaveActivityVerifierImpl @Inject constructor(
    private val activityRepository: ActivityRepository,
) : SaveActivityVerifier {
    override suspend fun invoke(activityVerifier: ActivityVerifier): Result<Long, SaveActivityVerifierError> = coroutineBinding {
        activityRepository.saveActivityVerifier(activityVerifier)
            .mapError(ActivityRepositoryError::toSaveActivityVerifierError)
            .bind()
    }
}
