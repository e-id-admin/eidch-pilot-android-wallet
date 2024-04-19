package ch.admin.foitt.pilotwallet.platform.credential.domain.usecase.implementation

import ch.admin.foitt.pilotwallet.platform.credential.domain.model.CredentialPreview
import ch.admin.foitt.pilotwallet.platform.credential.domain.model.toCredentialPreview
import ch.admin.foitt.pilotwallet.platform.credential.domain.usecase.GetCredentialPreviewsFlow
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialDisplay
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialStatus
import ch.admin.foitt.pilotwallet.platform.ssi.domain.repository.CredentialRepo
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.GetAllLocalizedCredentialDisplaysFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

internal class GetCredentialPreviewsFlowImpl @Inject constructor(
    private val credentialRepo: CredentialRepo,
    private val getAllLocalizedCredentialDisplaysFlow: GetAllLocalizedCredentialDisplaysFlow
) : GetCredentialPreviewsFlow {

    override fun invoke(): Flow<List<CredentialPreview>> = combine(
        credentialRepo.getAllFlow(),
        getAllLocalizedCredentialDisplaysFlow(),
    ) { credentials, credentialDisplays ->
        if (credentials.isEmpty()) {
            return@combine emptyList()
        }
        val statuses = credentials.associate { credential ->
            credential.id to credential.status
        }
        credentialDisplays.toCredentialPreviews(statuses)
    }
        .distinctUntilChanged()

    private fun Collection<CredentialDisplay>.toCredentialPreviews(statuses: Map<Long, CredentialStatus>): List<CredentialPreview> =
        this.map { credentialDisplay ->
            credentialDisplay.toCredentialPreview(statuses[credentialDisplay.credentialId])
        }
}
