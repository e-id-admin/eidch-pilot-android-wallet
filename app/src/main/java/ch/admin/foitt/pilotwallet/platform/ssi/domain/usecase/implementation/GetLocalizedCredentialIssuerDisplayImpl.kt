package ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.implementation

import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialIssuerDisplay
import ch.admin.foitt.pilotwallet.platform.locale.domain.usecase.GetLocalizedDisplay
import ch.admin.foitt.pilotwallet.platform.ssi.domain.repository.CredentialIssuerDisplayRepo
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.GetLocalizedCredentialIssuerDisplay
import javax.inject.Inject

class GetLocalizedCredentialIssuerDisplayImpl @Inject constructor(
    private val credentialIssuerDisplayRepo: CredentialIssuerDisplayRepo,
    private val getLocalizedDisplay: GetLocalizedDisplay
) : GetLocalizedCredentialIssuerDisplay {

    override suspend fun invoke(credentialId: Long): CredentialIssuerDisplay? {
        val displays = credentialIssuerDisplayRepo.getByCredentialId(credentialId)
        return if (displays.isNotEmpty()) {
            getLocalizedDisplay(displays)
        } else {
            null
        }
    }
}
