package ch.admin.foitt.pilotwallet.platform.credential.domain.usecase

import androidx.annotation.CheckResult
import ch.admin.foitt.pilotwallet.platform.credential.domain.model.CredentialPreview
import kotlinx.coroutines.flow.Flow

fun interface GetCredentialPreviewsFlow {

    @CheckResult
    operator fun invoke(): Flow<List<CredentialPreview>>
}
