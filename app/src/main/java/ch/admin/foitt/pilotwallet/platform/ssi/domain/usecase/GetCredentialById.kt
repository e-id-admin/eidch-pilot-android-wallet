package ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase

import ch.admin.foitt.pilotwallet.platform.database.domain.model.Credential
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.GetCredentialByIdError
import com.github.michaelbull.result.Result

interface GetCredentialById {
    suspend operator fun invoke(credentialId: Long): Result<Credential?, GetCredentialByIdError>
}
