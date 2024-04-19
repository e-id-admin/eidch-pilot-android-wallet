package ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.implementation

import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialRaw
import ch.admin.foitt.pilotwallet.platform.ssi.domain.repository.CredentialRawRepo
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.GetCredentialRaw
import com.github.michaelbull.result.getOrElse
import timber.log.Timber
import javax.inject.Inject

class GetCredentialRawImpl @Inject constructor(
    private val credentialRawRepo: CredentialRawRepo,
) : GetCredentialRaw {
    override suspend fun invoke(credentialId: Long): List<CredentialRaw> =
        credentialRawRepo.getByCredentialId(credentialId).getOrElse { _ ->
            Timber.e("No credential raw for credential $credentialId")
            emptyList()
        }
}
