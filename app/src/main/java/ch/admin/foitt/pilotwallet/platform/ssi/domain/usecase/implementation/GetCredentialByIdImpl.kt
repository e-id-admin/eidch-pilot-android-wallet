package ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.implementation

import ch.admin.foitt.pilotwallet.platform.database.domain.model.Credential
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.GetCredentialByIdError
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.toGetCredentialByIdError
import ch.admin.foitt.pilotwallet.platform.ssi.domain.repository.CredentialRepo
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.GetCredentialById
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.mapError
import javax.inject.Inject

class GetCredentialByIdImpl @Inject constructor(
    private val credentialRepo: CredentialRepo,
) : GetCredentialById {
    override suspend fun invoke(credentialId: Long): Result<Credential?, GetCredentialByIdError> = coroutineBinding {
        val credential = credentialRepo.getById(credentialId)
            .mapError { error ->
                error.toGetCredentialByIdError()
            }.bind()
        credential
    }
}
