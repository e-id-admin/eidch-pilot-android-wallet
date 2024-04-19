package ch.admin.foitt.pilotwallet.platform.credential.domain.usecase.implementation

import ch.admin.foitt.pilotwallet.platform.credential.domain.model.CredentialPreview
import ch.admin.foitt.pilotwallet.platform.credential.domain.model.toCredentialPreview
import ch.admin.foitt.pilotwallet.platform.credential.domain.usecase.GetCredentialPreviewFlow
import ch.admin.foitt.pilotwallet.platform.ssi.domain.repository.CredentialRepo
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.GetLocalizedCredentialDisplayFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import javax.inject.Inject

internal class GetCredentialPreviewFlowImpl @Inject constructor(
    private val credentialRepo: CredentialRepo,
    private val getLocalizedCredentialDisplayFlow: GetLocalizedCredentialDisplayFlow,
) : GetCredentialPreviewFlow {
    override fun invoke(credentialId: Long): Flow<CredentialPreview> = combine(
        credentialRepo.getByIdFlow(credentialId),
        getLocalizedCredentialDisplayFlow(credentialId),
    ) { credential, credentialDisplay ->
        if (credential == null) {
            return@combine null
        }
        credentialDisplay.toCredentialPreview(credential.status)
    }.filterNotNull()
}
