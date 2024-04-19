package ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.implementation

import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialDisplay
import ch.admin.foitt.pilotwallet.platform.locale.domain.usecase.GetLocalizedDisplay
import ch.admin.foitt.pilotwallet.platform.ssi.domain.repository.CredentialDisplayRepo
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.GetAllLocalizedCredentialDisplaysFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAllLocalizedCredentialDisplaysFlowImpl @Inject constructor(
    private val credentialDisplayRepo: CredentialDisplayRepo,
    private val getLocalizedDisplay: GetLocalizedDisplay
) : GetAllLocalizedCredentialDisplaysFlow {

    override fun invoke(): Flow<List<CredentialDisplay>> =
        credentialDisplayRepo.getAllGroupedCredentialDisplaysFlow().map { groupedCredentials ->
            groupedCredentials.mapNotNull { entry ->
                if (entry.value.isNotEmpty()) getLocalizedDisplay(entry.value) else null
            }
        }
            .distinctUntilChanged()
}
