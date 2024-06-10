package ch.admin.foitt.pilotwallet.platform.activity.domain.usecase

import ch.admin.foitt.pilotwallet.platform.activity.domain.model.DeleteActivityError
import com.github.michaelbull.result.Result

interface DeleteActivity {
    suspend operator fun invoke(activityId: Long): Result<Unit, DeleteActivityError>
}
