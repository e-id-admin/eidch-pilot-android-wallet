package ch.admin.foitt.pilotwallet.platform.activity.domain.usecase

import ch.admin.foitt.pilotwallet.platform.activity.domain.model.SaveActivityVerifierError
import ch.admin.foitt.pilotwallet.platform.database.domain.model.ActivityVerifier
import com.github.michaelbull.result.Result

interface SaveActivityVerifier {
    suspend operator fun invoke(activityVerifier: ActivityVerifier): Result<Long, SaveActivityVerifierError>
}
