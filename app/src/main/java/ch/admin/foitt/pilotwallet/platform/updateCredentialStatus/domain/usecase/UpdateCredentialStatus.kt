package ch.admin.foitt.pilotwallet.platform.updateCredentialStatus.domain.usecase

import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialStatus
import ch.admin.foitt.pilotwallet.platform.updateCredentialStatus.domain.model.UpdateOneCredentialStatusError
import com.github.michaelbull.result.Result

interface UpdateCredentialStatus {
    suspend operator fun invoke(credentialId: Long): Result<CredentialStatus, UpdateOneCredentialStatusError>
}
