package ch.admin.foitt.pilotwallet.platform.credential.domain.usecase

import ch.admin.foitt.pilotwallet.platform.credential.domain.model.CredentialPreview
import kotlinx.coroutines.flow.Flow

fun interface GetCredentialPreviewFlow {
    operator fun invoke(credentialId: Long): Flow<CredentialPreview>
}
