package ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.implementation

import ch.admin.foitt.pilotwallet.platform.database.domain.model.Credential
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.GetAllCredentialsError
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.toGetAllCredentialsError
import ch.admin.foitt.pilotwallet.platform.ssi.domain.repository.CredentialRepo
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.GetAllCredentials
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.mapError
import javax.inject.Inject

class GetAllCredentialsImpl @Inject constructor(
    private val credentialRepo: CredentialRepo,
) : GetAllCredentials {
    override suspend fun invoke(): Result<List<Credential>, GetAllCredentialsError> = coroutineBinding {
        val credentials = credentialRepo.getAll()
            .mapError { error ->
                error.toGetAllCredentialsError()
            }.bind()
        credentials
    }
}
