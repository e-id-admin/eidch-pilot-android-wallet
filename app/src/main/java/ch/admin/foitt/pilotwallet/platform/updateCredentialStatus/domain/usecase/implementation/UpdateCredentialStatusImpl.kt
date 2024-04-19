package ch.admin.foitt.pilotwallet.platform.updateCredentialStatus.domain.usecase.implementation

import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialStatus
import ch.admin.foitt.pilotwallet.platform.di.IoDispatcher
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.GetCredentialByIdError
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.GetCredentialById
import ch.admin.foitt.pilotwallet.platform.updateCredentialStatus.domain.model.UpdateCredentialStatusError
import ch.admin.foitt.pilotwallet.platform.updateCredentialStatus.domain.model.UpdateOneCredentialStatusError
import ch.admin.foitt.pilotwallet.platform.updateCredentialStatus.domain.model.toUpdateOneCredentialError
import ch.admin.foitt.pilotwallet.platform.updateCredentialStatus.domain.usecase.GetAndRefreshCredentialValidity
import ch.admin.foitt.pilotwallet.platform.updateCredentialStatus.domain.usecase.UpdateCredentialStatus
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.toErrorIfNull
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UpdateCredentialStatusImpl @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val getCredentialById: GetCredentialById,
    private val getAndRefreshCredentialValidity: GetAndRefreshCredentialValidity,
) : UpdateCredentialStatus {

    override suspend fun invoke(credentialId: Long): Result<CredentialStatus, UpdateOneCredentialStatusError> = withContext(ioDispatcher) {
        coroutineBinding {
            val credential = getCredentialById(credentialId)
                .mapError(GetCredentialByIdError::toUpdateOneCredentialError)
                .toErrorIfNull {
                    UpdateCredentialStatusError.Unexpected(null)
                }.bind()
            getAndRefreshCredentialValidity(credential)
                .mapError {
                    it as UpdateOneCredentialStatusError
                }.bind()
        }
    }
}
