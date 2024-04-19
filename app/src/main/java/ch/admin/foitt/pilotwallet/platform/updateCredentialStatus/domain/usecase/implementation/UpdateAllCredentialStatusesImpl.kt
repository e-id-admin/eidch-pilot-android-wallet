package ch.admin.foitt.pilotwallet.platform.updateCredentialStatus.domain.usecase.implementation

import ch.admin.foitt.pilotwallet.platform.database.domain.model.Credential
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.GetAllCredentials
import ch.admin.foitt.pilotwallet.platform.updateCredentialStatus.domain.model.UpdateCredentialStatusError
import ch.admin.foitt.pilotwallet.platform.updateCredentialStatus.domain.usecase.GetAndRefreshCredentialValidity
import ch.admin.foitt.pilotwallet.platform.updateCredentialStatus.domain.usecase.UpdateAllCredentialStatuses
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import timber.log.Timber
import javax.inject.Inject

class UpdateAllCredentialStatusesImpl @Inject constructor(
    private val getAllCredentials: GetAllCredentials,
    private val getAndRefreshCredentialValidity: GetAndRefreshCredentialValidity,
) : UpdateAllCredentialStatuses {

    override suspend fun invoke() {
        getAllCredentials()
            .onSuccess { credentials ->
                checkCredentials(credentials)
            }.onFailure {
                Timber.e(message = "Could not get credentials for credential status update")
            }
        // silently fail
    }

    private suspend fun checkCredentials(credentials: List<Credential>) {
        credentials.forEach { credential ->
            getAndRefreshCredentialValidity(credential)
                .onFailure {
                    when (val error = it) {
                        is UpdateCredentialStatusError.Unexpected ->
                            Timber.e(error.cause, "Could not update credential status for credential")
                    }
                }
        }
    }
}
