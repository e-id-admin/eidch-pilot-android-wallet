package ch.admin.foitt.pilotwallet.platform.activity.domain.usecase

import ch.admin.foitt.pilotwallet.platform.activity.domain.model.SaveActivityError
import ch.admin.foitt.pilotwallet.platform.database.domain.model.Activity
import com.github.michaelbull.result.Result

interface SaveActivity {
    suspend operator fun invoke(activity: Activity): Result<Long, SaveActivityError>
}
