package ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.implementation

import ch.admin.foitt.openid4vc.utils.Constants.ANDROID_KEY_STORE
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.CredentialRepositoryError
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.DeleteCredentialError
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.toDeleteCredentialError
import ch.admin.foitt.pilotwallet.platform.ssi.domain.repository.CredentialRepo
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.DeleteCredential
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.GetCredentialRaw
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.getOrElse
import com.github.michaelbull.result.mapError
import timber.log.Timber
import java.security.KeyStore
import javax.inject.Inject

class DeleteCredentialImpl @Inject constructor(
    private val credentialRepo: CredentialRepo,
    private val getCredentialRaw: GetCredentialRaw,
) : DeleteCredential {
    override suspend fun invoke(credentialId: Long): Result<Unit, DeleteCredentialError> = coroutineBinding {
        deleteKeyStoreEntries(credentialId)
        credentialRepo.deleteById(credentialId)
            .mapError(CredentialRepositoryError::toDeleteCredentialError).bind()
    }

    private suspend fun deleteKeyStoreEntries(credentialId: Long) = runSuspendCatching {
        val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
        keyStore.load(null)
        getCredentialRaw(credentialId).map { credential -> credential.keyIdentifier }
            .forEach { keyIdentifier ->
                keyStore.deleteEntry(keyIdentifier)
            }
    }.getOrElse { throwable ->
        Timber.e("Could not delete all key stores entries for credential with id '$credentialId'", throwable)
    }
}
